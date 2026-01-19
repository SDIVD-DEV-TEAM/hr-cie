package com.cie.hr.common.utils;

import com.cie.hr.domain.entity.*;
import com.cie.hr.domain.port.ScoreCardRepositoryPort;
import com.cie.hr.domain.port.ScorecardExpertTemplateRepositoryPort;
import com.cie.hr.domain.port.ScorecardManagerTemplateRepositoryPort;
import com.cie.hr.domain.valueobject.JobEmbedded;
import com.cie.hr.infrastructure.valueobject.*;
import com.fasterxml.uuid.Generators;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author Alexis TAMBIE
 * @created 30/09/2024
 * @project hr-cie
 */
@Component
public class ScorecardUtils {

    private final ScorecardManagerTemplateRepositoryPort scorecardManagerTemplateRepositoryPort;
    private final ScorecardExpertTemplateRepositoryPort scorecardExpertTemplateRepositoryPort;
    private final ScoreCardRepositoryPort scoreCardRepositoryPort;

    public ScorecardUtils(ScorecardManagerTemplateRepositoryPort scorecardManagerTemplateRepositoryPort, ScorecardExpertTemplateRepositoryPort scorecardExpertTemplateRepositoryPort, ScoreCardRepositoryPort scoreCardRepositoryPort) {
        this.scorecardManagerTemplateRepositoryPort = scorecardManagerTemplateRepositoryPort;
        this.scorecardExpertTemplateRepositoryPort = scorecardExpertTemplateRepositoryPort;
        this.scoreCardRepositoryPort = scoreCardRepositoryPort;
    }

    public ScorecardDomain createScorecardForManager(Job job, Campaign campaign, EmployeeDomain employee, Status status) {
        Optional<ScorecardForManagerForm> scorecardTemplate = scorecardManagerTemplateRepositoryPort.findFirstByTypeAndActiveTrue(1);
        if (scorecardTemplate.isEmpty()) {
            return null;
        }

        ScorecardForManagerForm formManager = getScorecardForManagerForm(job, scorecardTemplate);

        EvaluationScorecardManager evaluationScorecardManager = new EvaluationScorecardManager(campaign.getId(), 0d, status.getName(), formManager);
        return createScorecard(campaign, employee, status, evaluationScorecardManager, null, job);
    }

    public ScorecardDomain createScorecardForExpert(Job job, Campaign campaign, EmployeeDomain employee, Status status) {
        Optional<ScorecardForExpert> scorecardForExpert = scorecardExpertTemplateRepositoryPort.findFirstByTypeAndActiveTrue(2);
        if (scorecardForExpert.isEmpty()) {
            return null;
        }

        ScorecardForExpert expertForm = getScorecardForExpert(job, scorecardForExpert);

        EvaluationScorecardExpert evaluationScorecardExpert = new EvaluationScorecardExpert(campaign.getId(), 0d, status.getName(), expertForm);
        return createScorecard(campaign, employee, status, null, evaluationScorecardExpert, job);
    }

    private ScorecardDomain createScorecard(Campaign campaign, EmployeeDomain employee, Status status, EvaluationScorecardManager managerDetails, EvaluationScorecardExpert expertDetails, Job job) {
        return ScorecardDomain.newBuilder()
                .campaign(campaign)
                .assessed(employee)
                .id(Generators.timeBasedEpochGenerator().generate())
                .manager(null)
                .scorecardForExpert(expertDetails)
                .scorecardForManagerForm(managerDetails)
                .job(new JobEmbedded(job.getTitle(), job.getCode(), job.getOrganizationId().getName(), job.getGrade().getName(), job.getOrganizationId().getType().getName()))
                .status(status)
                .build();
    }

    public void removeExistingScorecard(EmployeeDomain employee, Campaign campaign) {
        Optional<ScorecardDomain> existingScorecard = scoreCardRepositoryPort.findByAssessed_IdAndCampaignId(employee.id(), campaign.getId());
        existingScorecard.ifPresent(this.scoreCardRepositoryPort::delete);
    }

    private static ScorecardForExpert getScorecardForExpert(Job job, Optional<ScorecardForExpert> scorecardForExpert) {
        if (scorecardForExpert.isEmpty())
            return null;
        ScorecardForExpert expertForm = scorecardForExpert.get();
        FormSpecialSection specialSection = expertForm.sectionB();

        if (job.getJobTemplate() != null) {
            List<FormSpecialLine> jobLines = job.getJobTemplate().lines();
            FormSpecialSection section = new FormSpecialSection(specialSection.title(), specialSection.note(), specialSection.type(), jobLines, specialSection.coefficient(), false);
            expertForm = new ScorecardForExpert(expertForm.sectionA(), section, expertForm.sectionC(), expertForm.sectionD());
        }
        return expertForm;
    }

    private static ScorecardForManagerForm getScorecardForManagerForm(Job job, Optional<ScorecardForManagerForm> scorecardTemplate) {
        if (scorecardTemplate.isEmpty())
            return null;

        ScorecardForManagerForm formManager = scorecardTemplate.get();
        FormSpecialSection specialSection = formManager.sectionD();

        if (job.getJobTemplate() != null) {
            List<FormSpecialLine> jobLines = job.getJobTemplate().lines();
            FormSpecialSection section = new FormSpecialSection(specialSection.title(), specialSection.note(), specialSection.type(), jobLines, specialSection.coefficient(), false);
            formManager = new ScorecardForManagerForm(formManager.sectionA(), formManager.sectionB(), formManager.sectionC(), section, formManager.sectionE(), formManager.sectionF());
        }
        return formManager;
    }
}
