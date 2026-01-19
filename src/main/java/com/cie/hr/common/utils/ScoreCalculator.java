package com.cie.hr.common.utils;

import com.cie.hr.infrastructure.valueobject.FormSpecialLine;
import com.cie.hr.infrastructure.valueobject.FormStandardLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * @author Alexis TAMBIE
 * @created 09/12/2024
 * @project hr-cie
 */
public class ScoreCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScoreCalculator.class);

    public static double calculateTotalCoefficientStandard(List<FormStandardLine> lines) {
        LOGGER.info("Calculating total coefficient for standard lines {}", lines);
        return lines.isEmpty() ? 0 : lines.stream().map(FormStandardLine::coefficient).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
    }

    public static double calculateTotalCoefficientSpecial(List<FormSpecialLine> lines) {
        LOGGER.info("Calculating total coefficient for special lines {}", lines);
        return lines.isEmpty() ? 0 : lines.stream().map(FormSpecialLine::coefficient).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
    }

    public static double calculateTotalNoteStandard(List<FormStandardLine> lines) {
        LOGGER.info("Calculating total note for standard lines {}", lines);
        return lines.isEmpty() ? 0 : lines.stream().map(FormStandardLine::note).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
    }

    public static double calculateTotalNoteSpecial(List<FormSpecialLine> lines) {
        LOGGER.info("Calculating total note for special lines {}", lines);
        return lines.isEmpty() ? 0 : lines.stream().map(FormSpecialLine::note).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
    }

    public static double calculateTotalNoteWeightedStandard(List<? extends FormStandardLine> lines) {
        LOGGER.info("Calculating total weighted note for standard lines {}", lines);
        return lines.isEmpty() ? 0 : lines.stream()
                .filter(elt -> elt != null && elt.coefficient() != 0 && elt.note() != null)
                .mapToDouble(elt -> elt.coefficient() * elt.note())
                .sum();
    }

    public static double calculateTotalNoteWeightedSpecial(List<? extends FormSpecialLine> lines) {
        LOGGER.info("Calculating total weighted note for special lines {}", lines);
        return lines.isEmpty() ? 0 : lines.stream()
                .filter(elt -> elt != null && elt.coefficient() != 0 && elt.note() != null)
                .mapToDouble(elt -> elt.coefficient() * elt.note())
                .sum();
    }

    public static double calculateNote(double totalCoefficient, double totalWeightedNote) {
        return totalCoefficient == 0 ? 0 : ((totalWeightedNote / totalCoefficient) / 5) * 20;
    }
}
