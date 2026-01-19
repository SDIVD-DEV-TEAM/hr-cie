package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.ScorecardTemplate;
import com.cie.hr.infrastructure.entity.ScorecardExpertTemplateEntity;
import com.cie.hr.infrastructure.entity.ScorecardManagerTemplateEntity;
import com.cie.hr.infrastructure.entity.ScorecardTemplateEntity;
import com.cie.hr.infrastructure.service.viewmodel.ScorecardExpertTemplateVm;
import com.cie.hr.infrastructure.service.viewmodel.ScorecardManagerTemplateVm;
import com.cie.hr.infrastructure.service.viewmodel.ScorecardTemplateVm;
import com.cie.hr.infrastructure.valueobject.ScorecardForExpert;
import com.cie.hr.infrastructure.valueobject.ScorecardForManagerForm;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
public class ScorecardTemplateMapper {
    public static ScorecardForExpert toExpertDomain(ScorecardExpertTemplateEntity scorecardExpertTemplateEntity) {
        if (scorecardExpertTemplateEntity == null) {
            return null;
        }
        return scorecardExpertTemplateEntity.getValues();
    }

    public static ScorecardForManagerForm toManagerDomain(ScorecardManagerTemplateEntity scorecardManagerTemplateEntity) {
        if (scorecardManagerTemplateEntity == null) {
            return null;
        }
        return scorecardManagerTemplateEntity.getValues();
    }

    public static ScorecardTemplateEntity toEntity(ScorecardTemplate scorecardTemplate) {
        if (scorecardTemplate == null) {
            return null;
        }

        if (scorecardTemplate.getFormExpert() != null) {
            ScorecardExpertTemplateEntity scorecardExpertTemplateEntity;
            scorecardExpertTemplateEntity = ScorecardExpertTemplateEntity.builder()
                    .values(scorecardTemplate.getFormExpert())
                    .build();
            scorecardExpertTemplateEntity.setId(scorecardTemplate.getId());
            scorecardExpertTemplateEntity.setTitle(scorecardTemplate.getTitle());
            scorecardExpertTemplateEntity.setActive(scorecardTemplate.getActive());
            scorecardExpertTemplateEntity.setDeleted(scorecardTemplate.getDeleted());
            return scorecardExpertTemplateEntity;
        } else {
            ScorecardManagerTemplateEntity scorecardManagerTemplateEntity;
            scorecardManagerTemplateEntity = ScorecardManagerTemplateEntity.builder()
                    .values(scorecardTemplate.getFormManager())
                    .build();
            scorecardManagerTemplateEntity.setId(scorecardTemplate.getId());
            scorecardManagerTemplateEntity.setTitle(scorecardTemplate.getTitle());
            scorecardManagerTemplateEntity.setActive(scorecardTemplate.getActive());
            scorecardManagerTemplateEntity.setDeleted(scorecardTemplate.getDeleted());
            return scorecardManagerTemplateEntity;
        }
    }

    public static void updateAndSave(ScorecardTemplate domain, ScorecardTemplateEntity entity) {
        if (domain.getFormExpert() != null) {
            ScorecardExpertTemplateEntity scorecardExpertTemplateEntity = (ScorecardExpertTemplateEntity) entity;
            scorecardExpertTemplateEntity.setValues(domain.getFormExpert());
            scorecardExpertTemplateEntity.setTitle(domain.getTitle());
            scorecardExpertTemplateEntity.setActive(domain.getActive());
            scorecardExpertTemplateEntity.setDeleted(domain.getDeleted());
        } else {
            ScorecardManagerTemplateEntity scorecardManagerTemplateEntity = (ScorecardManagerTemplateEntity) entity;
            scorecardManagerTemplateEntity.setValues(domain.getFormManager());
            scorecardManagerTemplateEntity.setTitle(domain.getTitle());
            scorecardManagerTemplateEntity.setActive(domain.getActive());
            scorecardManagerTemplateEntity.setDeleted(domain.getDeleted());
        }
    }


    public static ScorecardTemplateVm toVm(ScorecardTemplateEntity scorecardTemplateEntity) {
        var type = scorecardTemplateEntity.getType() == 1 ? "Manager de direction" : "Conseiller & Expert";
        return new ScorecardTemplateVm(scorecardTemplateEntity.getId(), scorecardTemplateEntity.getTitle(), type);
    }

    public static ScorecardManagerTemplateVm toVm(ScorecardManagerTemplateEntity scorecardManagerTemplateEntity) {
        return new ScorecardManagerTemplateVm(scorecardManagerTemplateEntity.getId(), scorecardManagerTemplateEntity.getTitle(), "Manager de direction", scorecardManagerTemplateEntity.getValues());
    }

    public static ScorecardExpertTemplateVm toVm(ScorecardExpertTemplateEntity scorecardExpertTemplateEntity) {
        return new ScorecardExpertTemplateVm(scorecardExpertTemplateEntity.getId(), scorecardExpertTemplateEntity.getTitle(), "Manager de direction", scorecardExpertTemplateEntity.getValues());
    }

}
