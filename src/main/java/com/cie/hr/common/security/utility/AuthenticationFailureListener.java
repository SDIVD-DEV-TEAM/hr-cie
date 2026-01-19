package com.cie.hr.common.security.utility;

import com.cie.hr.common.security.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

/**
 * @author Alexis TAMBIE
 * @created 09/05/2023
 * @project hr-cie
 */
@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private final HttpServletRequest request;
    private final LoginAttemptService loginAttemptService;

    public AuthenticationFailureListener(HttpServletRequest request, LoginAttemptService loginAttemptService) {
        this.request = request;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onApplicationEvent(@NonNull AuthenticationFailureBadCredentialsEvent e) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        try {
            Object principal = e.getAuthentication().getPrincipal();
            if (principal instanceof String) {
                String username = (String) e.getAuthentication().getPrincipal();
                loginAttemptService.addUserToLoginAttemptCache(username);
            } else {
                if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
                    loginAttemptService.addUserToLoginAttemptCache(request.getRemoteAddr());
                } else {
                    loginAttemptService.addUserToLoginAttemptCache(xfHeader.split(",")[0]);
                }
            }
        } catch (Exception ex) {
            if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
                loginAttemptService.addUserToLoginAttemptCache(request.getRemoteAddr());
            } else {
                loginAttemptService.addUserToLoginAttemptCache(xfHeader.split(",")[0]);
            }
        }
    }

}
