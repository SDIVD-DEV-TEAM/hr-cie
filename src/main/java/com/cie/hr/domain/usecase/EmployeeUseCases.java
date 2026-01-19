package com.cie.hr.domain.usecase;

import com.cie.hr.application.command.*;
import com.cie.hr.domain.entity.EmployeeDomain;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
public interface EmployeeUseCases {
    UUID createEmployee(CreateEmployeeCommand command) throws MessagingException, IOException;

    EmployeeDomain updateEmployee(UpdateEmployeeCommand command);

    EmployeeDomain authentication(AuthenticationCommand command);

    UUID initChangePassword(ForgotPasswordCommand command) throws MessagingException, UnsupportedEncodingException;

    Boolean delegateNotation(DelegateNotationCommand command);

    UUID giveDerogation(GiveDerogationCommand command);

    Boolean removeDelegation(RemoveDelegationCommand command);

    Boolean removeDerogation(RemoveDerogationCommand command);

    UUID resetPassword(ResetPasswordCommand command);

    Boolean deleteEmployee(DeleteEmployeeCommand command);

    Boolean reactivateEmployee(ReactivateUserCommand command);
}
