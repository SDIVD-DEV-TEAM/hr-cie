package com.cie.hr.application.adapter;

import com.cie.hr.application.command.CreateGradeCommand;
import com.cie.hr.application.command.UpdateGradeCommand;
import com.cie.hr.domain.entity.Grade;
import com.cie.hr.domain.port.GradeRepositoryPort;
import com.cie.hr.domain.usecase.GradeUseCases;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 03/05/2023
 * @project hr
 */
@Component
public class GradeUseCaseAdapter implements GradeUseCases {

    private final GradeRepositoryPort gradeRepositoryPort;

    public GradeUseCaseAdapter(GradeRepositoryPort gradeRepositoryPort) {
        this.gradeRepositoryPort = gradeRepositoryPort;
    }

    @Override
    public UUID createGrade(CreateGradeCommand command) {
        command.checkValidity();
        var grade = new Grade(command.name(), command.code(), command.description(), command.rank(), command.active());
        grade.checksBusinessRules(gradeRepositoryPort);
        gradeRepositoryPort.save(grade);
        return grade.getId();
    }

    @Override
    public UUID updateGrade(UpdateGradeCommand command) {
        return null;
    }
}
