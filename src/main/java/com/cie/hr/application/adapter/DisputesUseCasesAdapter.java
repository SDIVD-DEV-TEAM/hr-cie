package com.cie.hr.application.adapter;

import com.cie.hr.application.command.CreateDisputeCommand;
import com.cie.hr.application.command.UpdateDisputesCommand;
import com.cie.hr.common.event.ScorecardEvent;
import com.cie.hr.common.event.listeners.ScorecardEventListener;
import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.security.port.CustomAuthenticationManager;
import com.cie.hr.domain.entity.Disputes;
import com.cie.hr.domain.entity.EmployeeDomain;
import com.cie.hr.domain.entity.ScorecardDomain;
import com.cie.hr.domain.entity.Status;
import com.cie.hr.domain.port.DisputeRepositoryPort;
import com.cie.hr.domain.port.EmployeeRepositoryPort;
import com.cie.hr.domain.port.ScoreCardRepositoryPort;
import com.cie.hr.domain.port.StatusRepositoryPort;
import com.cie.hr.domain.usecase.DisputesUseCases;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardExpert;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardManager;
import com.fasterxml.uuid.Generators;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 22/06/2023
 * @project hr-cie
 */
@Component
public class DisputesUseCasesAdapter implements DisputesUseCases {

    private final DisputeRepositoryPort disputeRepositoryPort;
    private final EmployeeRepositoryPort employeeRepository;
    private final ScoreCardRepositoryPort scoreCardRepositoryPort;
    private final StatusRepositoryPort statusRepositoryPort;
    private final CustomAuthenticationManager customAuthenticationManager;
    private final ScorecardEventListener scorecardEventListener;

    public DisputesUseCasesAdapter(DisputeRepositoryPort disputeRepositoryPort,
                                   EmployeeRepositoryPort employeeRepository,
                                   ScoreCardRepositoryPort scoreCardRepositoryPort,
                                   StatusRepositoryPort statusRepositoryPort,
                                   CustomAuthenticationManager customAuthenticationManager,
                                   ScorecardEventListener scorecardEventListener) {
        this.disputeRepositoryPort = disputeRepositoryPort;
        this.employeeRepository = employeeRepository;
        this.scoreCardRepositoryPort = scoreCardRepositoryPort;
        this.statusRepositoryPort = statusRepositoryPort;
        this.customAuthenticationManager = customAuthenticationManager;
        this.scorecardEventListener = scorecardEventListener;
    }

    @Override
    public UUID createDispute(CreateDisputeCommand command) throws MessagingException, UnsupportedEncodingException {
        command.checkValidity();
        var currentUser = customAuthenticationManager.getCurrentUser();

        var loggedUser = employeeRepository.findByEmail(currentUser);
        if (loggedUser.isEmpty()) {
            throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
        }

        // Find Employee
        var checkEmployee = employeeRepository.findById(command.employeeId());
        if (checkEmployee.isEmpty()) {
            throw new ApplicationException("Cet employé n'existe pas");
        }

        if (!loggedUser.get().id().equals(checkEmployee.get().id())) {
            throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
        }

        // Find Scorecard
        var checkScorecard = scoreCardRepositoryPort.findById(command.scorecardId());
        if (checkScorecard.isEmpty()) {
            throw new ApplicationException("Cette fiche de notation n'existe pas");
        }

        ScorecardDomain scorecardDomain = checkScorecard.get();

        // Check Status
        Status status = scorecardDomain.status();
        if (!status.getName().equals("evaluated")) {
            throw new ApplicationException("Cette action n'est possible que pour les fiches déjà évaluée");
        }
        var findDisputeStatus = statusRepositoryPort.findByName("dispute");
        if (findDisputeStatus.isEmpty()) {
            throw new ApplicationException("Ce status n'existe pas");
        }

        // Find if scorecard is already disputed
        var checkDispute = disputeRepositoryPort.findByScorecardId(command.scorecardId());
        UUID disputeId = Generators.timeBasedEpochGenerator().generate();
        if (checkDispute.isEmpty()) {
            Disputes disputes = Disputes.newBuilder()
                    .id(disputeId)
                    .employee(checkEmployee.get())
                    .scorecard(checkScorecard.get())
                    .build();

            disputes.addNewMessageToAStore(command.subject().strip(), command.message(), false);
            disputeRepositoryPort.save(disputes);
            scorecardDomain.setStatus(findDisputeStatus.get());
            scoreCardRepositoryPort.updateAndSave(scorecardDomain);

            if (scorecardDomain.getManager() != null) {
                var event = new ScorecardEvent(scorecardDomain.manager(), ZonedDateTime.now());
                String evaluatedName = String.format("%s %s", scorecardDomain.assessed().lastname(), scorecardDomain.assessed().firstname());
                scorecardEventListener.publishWithParam(true, event, evaluatedName, false);
            }
        } else {
            Disputes disputes = checkDispute.get();
            EmployeeDomain employeeDomain = disputes.getEmployee();

            if (status.getName().equals("evaluated") || status.getName().equals("dispute")) {
                int checkType;
                if (scorecardDomain.getEvaluationScorecardExpert() == null) {
                    checkType = 2;
                    EvaluationScorecardManager scorecardForm = scorecardDomain.scorecardForManagerForm();
                    EvaluationScorecardManager scorecardManager = new EvaluationScorecardManager(
                            scorecardForm.campaignId(),
                            scorecardForm.note(),
                            findDisputeStatus.get().getName(),
                            scorecardForm.forms()
                    );
                    scorecardDomain.saveTemplate(scorecardManager, checkType);
                } else {
                    checkType = 1;
                    EvaluationScorecardExpert scorecardForm = scorecardDomain.scorecardForExpert();
                    EvaluationScorecardExpert scorecardExpert = new EvaluationScorecardExpert(
                            scorecardForm.campaignId(),
                            scorecardForm.note(),
                            findDisputeStatus.get().getName(),
                            scorecardForm.forms()
                    );
                    scorecardDomain.saveTemplate(scorecardExpert, checkType);
                }

                disputes.addNewMessageToAStore(command.subject(), command.message(), false);
                disputeRepositoryPort.updateAndSave(disputes);
                scorecardDomain.setStatus(findDisputeStatus.get());
                scoreCardRepositoryPort.updateAndSave(scorecardDomain);

                ScorecardEvent event;
                if (loggedUser.get().id().equals(employeeDomain.id())) {
                    event = new ScorecardEvent(loggedUser.get(), ZonedDateTime.now());
                    String evaluatedName = String.format("%s %s", scorecardDomain.assessed().lastname(), scorecardDomain.assessed().firstname());
                    scorecardEventListener.publishWithParam(true, event, evaluatedName, false);
                } else {
                    event = new ScorecardEvent(employeeDomain, ZonedDateTime.now());
                    scorecardEventListener.publishWithParam(false, event, "", false);
                }

                disputeId = disputes.getId();
            } else {
                throw new ApplicationException("Cette action n'est possible que pour les fiches déjà évaluée");
            }
        }
        return disputeId;
    }

    @Override
    public UUID updateDispute(UpdateDisputesCommand command) throws MessagingException, UnsupportedEncodingException {
        command.checkValidity();

        var currentUser = customAuthenticationManager.getCurrentUser();

        var loggedUser = employeeRepository.findByEmail(currentUser);
        if (loggedUser.isEmpty()) {
            throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
        }

        // Find Dispute
        var checkDispute = disputeRepositoryPort.findById(command.disputeId());
        if (checkDispute.isEmpty()) {
            throw new ApplicationException("Ce litige n'existe pas");
        }

        Disputes disputes = checkDispute.get();
        EmployeeDomain employeeDomain = disputes.getEmployee();
        ScorecardDomain scorecardDomain = disputes.getScorecard();

        // Check Status
        Status status = disputes.getScorecard().status();
        if (status.getName().equals("evaluated") || status.getName().equals("dispute")) {
            Optional<Status> findDisputeStatus;
            if (command.isRejected()) {
                findDisputeStatus = statusRepositoryPort.findByName("evaluated");

            } else {
                findDisputeStatus = statusRepositoryPort.findByName("dispute");
            }

            if (findDisputeStatus.isEmpty()) {
                throw new ApplicationException("Ce status n'existe pas");
            }

            int checkType;
            if (scorecardDomain.getEvaluationScorecardExpert() == null) {
                checkType = 2;
                EvaluationScorecardManager scorecardForm = scorecardDomain.scorecardForManagerForm();
                EvaluationScorecardManager scorecardManager = new EvaluationScorecardManager(
                        scorecardForm.campaignId(),
                        scorecardForm.note(),
                        findDisputeStatus.get().getName(),
                        scorecardForm.forms()
                );
                scorecardDomain.saveTemplate(scorecardManager, checkType);
            } else {
                checkType = 1;
                EvaluationScorecardExpert scorecardForm = scorecardDomain.scorecardForExpert();
                EvaluationScorecardExpert scorecardExpert = new EvaluationScorecardExpert(
                        scorecardForm.campaignId(),
                        scorecardForm.note(),
                        findDisputeStatus.get().getName(),
                        scorecardForm.forms()
                );
                scorecardDomain.saveTemplate(scorecardExpert, checkType);
            }

            disputes.addNewMessageToAStore(command.subject(), command.message(), command.isRejected());
            disputeRepositoryPort.updateAndSave(disputes);
            scorecardDomain.setStatus(findDisputeStatus.get());
            scoreCardRepositoryPort.updateAndSave(scorecardDomain);

            ScorecardEvent event;
            if (loggedUser.get().id().equals(employeeDomain.id())) {
                event = new ScorecardEvent(loggedUser.get(), ZonedDateTime.now());
                String evaluatedName = String.format("%s %s", scorecardDomain.assessed().lastname(), scorecardDomain.assessed().firstname());
                scorecardEventListener.publishWithParam(true, event, evaluatedName, command.isRejected());
            } else {
                event = new ScorecardEvent(employeeDomain, ZonedDateTime.now());
                scorecardEventListener.publishWithParam(false, event, "", command.isRejected());
            }

            return disputes.getId();
        }
        throw new ApplicationException("Cette action n'est possible que pour les fiches déjà évaluée");
    }

}
