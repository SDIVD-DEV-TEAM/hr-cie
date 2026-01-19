package com.cie.hr.common.security.port;

/**
 * @author Alexis TAMBIE
 * @created 05/05/2023
 * @project hr-cie
 */
public interface LoginAttemptChecker {
    void evictUserFromLoginAttemptCache(String username);

    void addUserToLoginAttemptCache(String username);

    boolean hasExceededMaxAttempts(String username);

    boolean isBlocked(String username);

}
