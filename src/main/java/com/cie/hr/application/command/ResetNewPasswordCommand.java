package com.cie.hr.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author Alexis TAMBIE
 * @created 19/05/2023
 * @project hr-cie
 */
public record ResetNewPasswordCommand(
        @NotBlank(message = "Le mot de passe à modifier est obligatoire!")
        String password,
        @NotNull(message = "L'adresse e-mail est obligatoire")
        @Email
        String email
)  {
}
