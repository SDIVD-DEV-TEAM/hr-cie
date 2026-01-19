package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.EmployeeUseCases;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */
public record GiveDerogationCommand(UUID employeeId, UUID campaignId, LocalDate expiredAt) implements Command<EmployeeUseCases, UUID> {
    @Override
    public UUID execute(EmployeeUseCases useCase) {
        return useCase.giveDerogation(this);
    }
}
