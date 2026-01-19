package com.cie.hr.common.utils;

import com.cie.hr.domain.valueobject.AccessLevelEnum;
import org.springframework.context.annotation.Configuration;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;


/**
 * @author Alexis TAMBIE
 * @created 08/05/2023
 * @project hr-cie
 */
@Converter(autoApply = true)
@Configuration
public class AccessLevelConverter implements AttributeConverter<AccessLevelEnum, String> {
    @Override
    public String convertToDatabaseColumn(AccessLevelEnum accessLevelEnum) {
        if (accessLevelEnum == null) {
            return null;
        }
        return accessLevelEnum.getLabel();
    }

    @Override
    public AccessLevelEnum convertToEntityAttribute(final String s) {
        if (s == null) {
            return null;
        }
        return Stream.of(AccessLevelEnum.values())
                .filter(c -> c.getLabel().equals(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
