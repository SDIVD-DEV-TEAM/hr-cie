package com.cie.hr.common.security.port;

/**
 * @author Alexis TAMBIE
 * @created 04/05/2023
 * @project hr-cie
 */
public interface CustomAuthenticationManager {
    Boolean authenticate(String username, String password);

    String encodePassword(String password);

    String getCurrentUser();

    Boolean matches(String password, String newPassword);
}
