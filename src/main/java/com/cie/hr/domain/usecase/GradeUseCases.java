package com.cie.hr.domain.usecase;

import com.cie.hr.application.command.CreateGradeCommand;
import com.cie.hr.application.command.UpdateGradeCommand;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 03/05/2023
 * @project hr
 */
public interface GradeUseCases {
    UUID createGrade(CreateGradeCommand command);
    UUID updateGrade(UpdateGradeCommand command);
}
