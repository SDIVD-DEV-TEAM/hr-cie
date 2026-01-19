package com.cie.hr.infrastructure.entity;

import com.cie.hr.infrastructure.valueobject.ScorecardForManagerForm;
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
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue(value = "1")
@Builder
public class ScorecardManagerTemplateEntity extends ScorecardTemplateEntity {
    @Type(JsonType.class)
    @Column(name = "template_form_direction_manager", columnDefinition = "jsonb")
    ScorecardForManagerForm values;
}
