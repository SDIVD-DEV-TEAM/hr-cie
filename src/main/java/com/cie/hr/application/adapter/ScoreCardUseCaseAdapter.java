package com.cie.hr.application.adapter;

import com.cie.hr.application.command.CloseScoreCardCommand;
import com.cie.hr.application.command.CreateScorecardCommand;
import com.cie.hr.application.command.ReCreatedScorecardCommand;
import com.cie.hr.application.command.UpdateScoreCardCommand;
import com.cie.hr.common.event.ScorecardEvent;
import com.cie.hr.common.event.listeners.ScorecardEventListener;
import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.security.port.CustomAuthenticationManager;
import com.cie.hr.common.utils.CheckRHEmployee;
import com.cie.hr.common.utils.ScorecardUtils;
import com.cie.hr.domain.entity.*;
import com.cie.hr.domain.port.*;
import com.cie.hr.domain.usecase.ScorecardUseCases;
import com.cie.hr.domain.valueobject.JobEmbedded;
import com.cie.hr.infrastructure.valueobject.*;
import com.fasterxml.uuid.Generators;
import jakarta.mail.MessagingException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.time.*;
import java.util.*;

/**
 * @author Alexis TAMBIE
 * @created 11/05/2023
 * @project hr-cie
 */
@Component
public class ScoreCardUseCaseAdapter implements ScorecardUseCases {

    private final ScoreCardRepositoryPort scoreCardRepositoryPort;

    private final StatusRepositoryPort statusRepositoryPort;

    private final EmployeeRepositoryPort employeeRepository;

    private final CampaignRepositoryPort campaignRepositoryPort;

    private final ScorecardTemplateRepositoryPort scorecardTemplateRepositoryPort;

    private final CheckRHEmployee checkRHEmployee;

    private final CustomAuthenticationManager customAuthenticationManager;

    private final ScorecardEventListener scorecardEventListener;

    private final JobRepositoryPort jobRepositoryPort;

    private final NoteDistributionRepositoryPort noteDistributionRepositoryPort;

    private final ScoreNoteDistributionRepositoryPort scoreNoteDistributionRepositoryPort;

    private final DerogationRepositoryPort derogationRepositoryPort;

    private final ScorecardUtils scorecardUtils;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public ScoreCardUseCaseAdapter(ScoreCardRepositoryPort scoreCardRepositoryPort,
                                   StatusRepositoryPort statusRepositoryPort,
                                   EmployeeRepositoryPort employeeRepository,
                                   CampaignRepositoryPort campaignRepositoryPort,
                                   ScorecardTemplateRepositoryPort scorecardTemplateRepositoryPort,
                                   CheckRHEmployee checkRHEmployee,
                                   CustomAuthenticationManager customAuthenticationManager,
                                   ScorecardEventListener scorecardEventListener,
                                   JobRepositoryPort jobRepositoryPort,
                                   NoteDistributionRepositoryPort noteDistributionRepositoryPort,
                                   ScoreNoteDistributionRepositoryPort scoreNoteDistributionRepositoryPort,
                                   DerogationRepositoryPort derogationRepositoryPort, ScorecardUtils scorecardUtils) {
        this.scoreCardRepositoryPort = scoreCardRepositoryPort;
        this.statusRepositoryPort = statusRepositoryPort;
        this.employeeRepository = employeeRepository;
        this.campaignRepositoryPort = campaignRepositoryPort;
        this.scorecardTemplateRepositoryPort = scorecardTemplateRepositoryPort;
        this.checkRHEmployee = checkRHEmployee;
        this.customAuthenticationManager = customAuthenticationManager;
        this.scorecardEventListener = scorecardEventListener;
        this.jobRepositoryPort = jobRepositoryPort;
        this.noteDistributionRepositoryPort = noteDistributionRepositoryPort;
        this.scoreNoteDistributionRepositoryPort = scoreNoteDistributionRepositoryPort;
        this.derogationRepositoryPort = derogationRepositoryPort;
        this.scorecardUtils = scorecardUtils;
    }

    @Override
    public UUID createScorecard(CreateScorecardCommand command) {
        command.checkValidity();

        // Check if RH user
        if (checkRHEmployee.employeeIsNotRH()) {
            throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
        }

        // Get status
        var status = statusRepositoryPort.findById(command.status());
        if (status.isEmpty()) {
            throw new ApplicationException("Status introuvable");
        }

        // Get Assessed
        var assessed = employeeRepository.findById(command.assessedId());
        if (assessed.isEmpty()) {
            throw new ApplicationException("Cet employé n'existe pas");
        }

        // Get campaign
        var campaign = campaignRepositoryPort.findById(command.campaign());
        if (campaign.isEmpty()) {
            throw new ApplicationException("Campaign not found");
        }

        // Get template
        var template = scorecardTemplateRepositoryPort.findById(command.templateId());
        if (template.isEmpty()) {
            throw new ApplicationException("Template not found");
        }

        Profile profile = assessed.get().profile();

        var scoreCard = ScorecardDomain.newBuilder()
                .id(Generators.timeBasedEpochGenerator().generate())
                .assessed(assessed.get())
                .campaign(campaign.get())
                .manager(null)
                .status(status.get())
                .scorecardForExpert(profile.getCode().equals("CE") ? new EvaluationScorecardExpert(campaign.get().getId(), 0d, status.get().getName(), template.get().getFormExpert()) : null)
                .scorecardForManagerForm(!profile.getName().equals("CE") ? new EvaluationScorecardManager(campaign.get().getId(), 0d, status.get().getName(), template.get().getFormManager()) : null)
                .build();
        this.scoreCardRepositoryPort.save(scoreCard);
        return scoreCard.getId();
    }

    @Override
    public UUID updateScorecard(UpdateScoreCardCommand command) throws MessagingException, UnsupportedEncodingException {
        command.checkValidity();

        var currentUser = customAuthenticationManager.getCurrentUser();

        // Find logged user
        var findEmployee = employeeRepository.findByEmail(currentUser);

        if (findEmployee.isEmpty()) {
            throw new ApplicationException("Employée introuvable");
        }

        // Find template
        var findScorecard = scoreCardRepositoryPort.findById(command.scorecardId());

        if (findScorecard.isEmpty()) {
            throw new ApplicationException("Cette fiche de notation n'existe pas");
        }

        // Get Campaign
        Campaign campaign = findScorecard.get().campaign();

        if (campaign == null) {
            throw new ApplicationException("Cette campagne n'existe pas");
        }

        // Check if exist derogation
        List<Derogation> derogationList = derogationRepositoryPort.findByEmployeeIdAndCampaignId(
                findScorecard.get().assessed().id(),
                campaign.getId()
        );

        Derogation currentDerogation = derogationList.stream().filter(e -> !e.getDeleted()).findFirst().orElse(null);
        if (currentDerogation != null && !currentDerogation.getExpiredAt().isAfter(LocalDate.now())) {
            throw new ApplicationException("Votre délégation est expirée");
        }

        // Check if campaign is closed
        if (campaign.getStatus() != null && campaign.getStatus().getCode().equals("2") && currentDerogation == null) {
            throw new ApplicationException("Cette campagne est clôturée");
        }

        // Get Manager
        var managerJob = jobRepositoryPort.findByEmployeeId(command.managerId());
        if (managerJob.isEmpty()) {
            throw new ApplicationException("Ce manager n'existe pas");
        }

        EmployeeDomain employeeManager = managerJob.get().getEmployeeId();
        if (!employeeManager.id().equals(findEmployee.get().id())) {
            throw new ApplicationException("Cette fiche de notation semble ne pas être la vôtre");
        }

        // Check if scorecard can be updated
        Status status = findScorecard.get().status();
        if (status.getName().equals("closed") || status.getName().equals("evaluated")) {
            throw new ApplicationException("Il n'est plus possible d'apporter des modifications à cette fiche");
        }

        ScorecardDomain scorecard = findScorecard.get();

        // Find User Job
        var userJob = jobRepositoryPort.findByEmployeeId(scorecard.getAssessed().id());
        if (userJob.isEmpty()) {
            throw new ApplicationException("Cet employée n'existe pas");
        }

        scorecard.setManager(employeeManager);
        scorecard.setJob(
                new JobEmbedded(
                        userJob.get().getTitle(),
                        userJob.get().getCode(),
                        userJob.get().getOrganizationId().getName(),
                        userJob.get().getGrade().getName(),
                        userJob.get().getOrganizationId().getType().getName()
                )
        );
        Grade managerGrade = managerJob.get().getGrade();
        Grade employeeGrade = userJob.get().getGrade();

        if (managerGrade.getRank() > employeeGrade.getRank()) {
            throw new ApplicationException("Vous ne pouvez pas évaluer ce collaborateur");
        }

        int checkType;
        Optional<Status> checkStatus;
        if (scorecard.getEvaluationScorecardExpert() == null) {
            checkType = 2;
            EvaluationScorecardManager scorecardForm = command.scorecardManager();
            FormSpecialSection oldData = scorecardForm.forms().sectionD();
            List<FormSpecialLine> formSpecialLineList = addScores(oldData.lines());
            FormSpecialSection formSpecialSection = new FormSpecialSection(oldData.title(), oldData.note(), oldData.type(), formSpecialLineList, oldData.coefficient(), oldData.completed());

            double coefficient = sumCoefficient(formSpecialSection.lines());
            double note = coefficient == 0 ? 0.0 : calc(formSpecialSection.lines()) / coefficient;
            EvaluationScorecardManager evaluationScorecardManager = getEvaluationScorecardManager(formSpecialSection, note, scorecardForm);

            checkStatus = statusRepositoryPort.findByName(scorecardForm.status());
            if (checkStatus.isEmpty()) {
                throw new ApplicationException("Ce status n'existe pas");
            }
            scorecard.setStatus(checkStatus.get());
            scorecard.saveTemplate(evaluationScorecardManager, checkType);
        } else {
            checkType = 1;
            EvaluationScorecardExpert scorecardForm = command.scorecardExpert();
            FormSpecialSection oldData = scorecardForm.forms().sectionB();
            List<FormSpecialLine> formSpecialLineList = addScores(oldData.lines());
            FormSpecialSection formSpecialSection = new FormSpecialSection(oldData.title(), oldData.note(), oldData.type(), formSpecialLineList, oldData.coefficient(), oldData.completed());

            double coefficient = sumCoefficient(formSpecialSection.lines());
            double note = coefficient == 0 ? 0 : calc(formSpecialSection.lines()) / coefficient;

            EvaluationScorecardExpert evaluationScorecardExpert = getEvaluationScorecardExpert(formSpecialSection, note, scorecardForm);

            checkStatus = statusRepositoryPort.findByName(scorecardForm.status());
            if (checkStatus.isEmpty()) {
                throw new ApplicationException("Ce status n'existe pas");
            }
            scorecard.setStatus(checkStatus.get());
            scorecard.saveTemplate(evaluationScorecardExpert, checkType);
        }

        if (checkStatus.get().getName().equals("evaluated")) {
            scorecard.setEvaluatedAt(LocalDateTime.now());
            var event = new ScorecardEvent(scorecard.getAssessed(), ZonedDateTime.now(ZoneId.of("UTC")));
            scorecardEventListener.publishWithParam(false, event, "", false);
            var eventManager = new ScorecardEvent(scorecard.getManager(), ZonedDateTime.now(ZoneId.of("UTC")));
            String employeeName = String.format("%s %s", scorecard.getAssessed().lastname(), scorecard.getAssessed().firstname());
            scorecardEventListener.publishManagerWithParam(eventManager, employeeName);
        }
        scoreCardRepositoryPort.updateAndSave(scorecard);

        return scorecard.getId();
    }

    @NotNull
    private static EvaluationScorecardManager getEvaluationScorecardManager(FormSpecialSection sectionDWithScores, double note, EvaluationScorecardManager scorecardForm) {
        // Use the processed sectionD (with achieved values and calculated scores)
        FormSpecialSection formSpecialSection = new FormSpecialSection(sectionDWithScores.title(), note, sectionDWithScores.type(), sectionDWithScores.lines(), sectionDWithScores.coefficient(), sectionDWithScores.completed());
        ScorecardForManagerForm scorecardForManagerForm = new ScorecardForManagerForm(scorecardForm.forms().sectionA(), scorecardForm.forms().sectionB(), scorecardForm.forms().sectionC(), formSpecialSection, scorecardForm.forms().sectionE(), scorecardForm.forms().sectionF());
        return new EvaluationScorecardManager(scorecardForm.campaignId(), scorecardForm.note(), scorecardForm.status(), scorecardForManagerForm);
    }

    @NotNull
    private static EvaluationScorecardExpert getEvaluationScorecardExpert(FormSpecialSection oldData, double note, EvaluationScorecardExpert scorecardForm) {
        FormSpecialSection formSpecialSection = new FormSpecialSection(oldData.title(), note, oldData.type(), oldData.lines(), oldData.coefficient(), oldData.completed());
        ScorecardForExpert scorecardForExpert = new ScorecardForExpert(scorecardForm.forms().sectionA(), formSpecialSection, scorecardForm.forms().sectionC(), scorecardForm.forms().sectionD());
        return new EvaluationScorecardExpert(scorecardForm.campaignId(), scorecardForm.note(), scorecardForm.status(), scorecardForExpert);
    }

    @Override
    public UUID closeScorecard(CloseScoreCardCommand command) {
        command.checkValidity();

        var currentUser = customAuthenticationManager.getCurrentUser();

        // Find logged user
        var findEmployee = employeeRepository.findByEmail(currentUser);

        if (findEmployee.isEmpty()) {
            throw new ApplicationException("Employée introuvable");
        }

        // find scorecard
        var checkScorecard = scoreCardRepositoryPort.findById(command.scorecardId());
        if (checkScorecard.isEmpty()) {
            throw new ApplicationException("Cette fiche de notation n'existe pas");
        }

        EmployeeDomain scorecardEmployee = checkScorecard.get().assessed();
        if (scorecardEmployee == null || !scorecardEmployee.id().equals(findEmployee.get().id())) {
            throw new ApplicationException("Cette fiche de notation semble ne pas être la vôtre");
        }

        // check if scorecard can be closed
        Status status = checkScorecard.get().status();

        if (status.getName().equals("closed")) {
            throw new ApplicationException("Cette fiche est déjà clôturée");
        } else if (status.getName().equals("evaluated") || status.getName().equals("dispute")) {
            Optional<Status> checkStatus = statusRepositoryPort.findByName("closed");
            ScorecardDomain scorecard = checkScorecard.get();
            if (checkStatus.isEmpty()) {
                throw new ApplicationException("Ce status n'existe pas");
            }
            scorecard.setStatus(checkStatus.get());
            scorecard.setAutomaticClosed(false);
            scoreCardRepositoryPort.save(scorecard);

            return scorecard.getId();
        } else {
            throw new ApplicationException("Cette fiche ne peut pas être clôturée");
        }
    }

    @Override
    public UUID reCreatedScorecard(ReCreatedScorecardCommand command) {
        command.checkValidity();

        var currentUser = customAuthenticationManager.getCurrentUser();

        // Find logged user
        var findEmployee = employeeRepository.findByEmail(currentUser);

        if (findEmployee.isEmpty()) {
            throw new ApplicationException("Employée introuvable");
        }

        // Get Assessed
        LOGGER.info("Command assessed id: {}", command.assessedId());
        LOGGER.info("Command campaign id: {}", command.campaign());

        var assessed = employeeRepository.findById(command.assessedId());
        if (assessed.isEmpty()) {
            throw new ApplicationException("Cet employé n'existe pas");
        }

        // Get campaign
        var campaign = campaignRepositoryPort.findById(command.campaign());
        if (campaign.isEmpty()) {
            throw new ApplicationException("Campaign not found");
        }

        // Find scorecard
        var checkScorecard = scoreCardRepositoryPort.findByAssessed_IdAndCampaignId(command.assessedId(), command.campaign());
        if (checkScorecard.isPresent()) {
            throw new ApplicationException("Cette fiche de notation existe déjà");
        }

        var statusNotStarted = statusRepositoryPort.findByCode("0");

        if (statusNotStarted.isEmpty()) {
            throw new ApplicationException("Ce statut n'existe pas");
        }

        var job = jobRepositoryPort.findByEmployeeId(assessed.get().id());
        if (job.isEmpty()) {
            throw new ApplicationException("Cet employé n'a pas de poste");
        }

        var grade = job.get().getGrade().getCode();
        boolean isManager = !grade.equals("CE");
        ScorecardDomain scorecard = isManager ? scorecardUtils.createScorecardForManager(job.get(), campaign.get(), assessed.get(), statusNotStarted.get()) : scorecardUtils.createScorecardForExpert(job.get(), campaign.get(), assessed.get(), statusNotStarted.get());
        this.scoreCardRepositoryPort.save(scorecard);
        return scorecard.getId();
    }

    double calc(List<FormSpecialLine> lines) {
        return lines.parallelStream().map(e -> e.note() * e.coefficient()).mapToDouble(Double::doubleValue).sum();
    }

    List<FormSpecialLine> addScores(List<FormSpecialLine> lines) {
        return new ArrayList<>() {{
            lines.forEach(elt -> {
                int note = 0;
                double achieved = elt.achieved() == null ? 0.0 : elt.achieved();
                if (elt.noteId() != null) {
                    var noteDescription = noteDistributionRepositoryPort.findById(elt.noteId());
                    if (noteDescription.isPresent()) {
                        String way = noteDescription.get().getWay();
                        if (noteDescription.get().isAllOrNothing()) {
                            if (way.equals("Down") && achieved == 0.0 || way.equals("Up") && achieved == elt.objective()) {
                                note = 5;
                            } else {
                                note = 1;
                            }
                        } else {
                            double percent = elt.objective() == 0 ? 0 : achieved / elt.objective();
                            percent = Math.round(percent * 100.0);
                            var range = scoreNoteDistributionRepositoryPort.findFirstByValueInRangeAndNoteDistributionId(percent, noteDescription.get().getId());
                            if (range.isEmpty()) {
                                if (way.equals("Down")) {
                                    note = 1;
                                } else {
                                    note = 5;
                                }
                            } else {
                                note = (int) range.get().getScore().score();
                            }
                        }
                    }
                }
                add(new FormSpecialLine(elt.title(), elt.coefficient(), (double) note, elt.objective(), achieved, elt.unit(), elt.noteId()));
            });
        }};
    }

    static double sumCoefficient(List<FormSpecialLine> lines) {
        return lines.stream().mapToDouble(FormSpecialLine::coefficient).sum();
    }
}
