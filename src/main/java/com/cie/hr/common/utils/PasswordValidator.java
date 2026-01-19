package com.cie.hr.common.utils;

/**
 * @author Alexis TAMBIE
 * @created 20/05/2023
 * @project hr-cie
 */
public class PasswordValidator {

    public static boolean isStrongPassword(String password) {
        // Check if length more than 8 character
        if (password.length() < 8) {
            return false;
        }

        // Check for uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // Check for lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // Check for number
        if (!password.matches(".*\\d.*")) {
            return false;
        }

        // Check for special character
        return password.matches(".*[^a-zA-Z0-9 ].*");
    }
}
