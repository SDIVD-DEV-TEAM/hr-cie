package com.cie.hr.infrastructure.entity;

import com.cie.hr.infrastructure.valueobject.ScorecardForExpert;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.Type;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue(value = "2")
@Getter
@Setter
@Builder
public class ScorecardExpertTemplateEntity extends ScorecardTemplateEntity {
    @Type(JsonType.class)
    @Column(name = "template_form_expert", columnDefinition = "jsonb")
    ScorecardForExpert values;
}
