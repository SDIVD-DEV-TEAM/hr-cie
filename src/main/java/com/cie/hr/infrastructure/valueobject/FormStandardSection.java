package com.cie.hr.infrastructure.valueobject;

import java.util.List;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */

public record FormStandardSection(
        String title,
        String type,
        List<FormStandardLine> lines,
        Double coefficient,
        Double note,
        boolean completed,
        String comments
) implements ScorecardTemplate {
    public FormStandardSection(String title, String type, List<FormStandardLine> lines, Double coefficient, Double note, boolean completed, String comments) {
        this.title = title;
        this.type = type;
        this.lines = lines;
        this.coefficient = coefficient;
        this.note = sumCoefficient(lines) == 0 ? 0 : calc(lines)/sumCoefficient(lines);
        this.completed = completed;
        this.comments = comments;
    }

    static double calc(List<FormStandardLine> lines) {
        return lines.stream().mapToDouble(elt -> elt.note() * elt.coefficient()).sum();
    }

    static double sumCoefficient(List<FormStandardLine> lines){
        return lines.stream().mapToDouble(FormStandardLine::coefficient).sum();
    }
}
