package com.cie.hr.common.security.service;

import com.cie.hr.common.security.port.LoginAttemptChecker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexis TAMBIE
 * @created 05/05/2023
 * @project hr-cie
 */
@Service
public class LoginAttemptService implements LoginAttemptChecker {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    private final LoadingCache<String, Integer> loginAttemptCache;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAttemptService.class);

    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(4, TimeUnit.HOURS)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(final String key) {
                        return 0;
                    }
                });
    }

    @Override
    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    @Override
    public void addUserToLoginAttemptCache(String username) {
        int attempts;
        try {
            attempts = loginAttemptCache.get(username);
        } catch (final ExecutionException e) {
            LOGGER.error("Error while adding user to login attempt cache", e);
            attempts = 0;
        }
        attempts++;
        loginAttemptCache.put(username, attempts);
    }

    @Override
    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            LOGGER.error("Error while checking if user has exceeded max attempts", e);
        }
        return false;
    }


    @Override
    public boolean isBlocked(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (final ExecutionException e) {
            LOGGER.error("Error while checking if user is blocked", e);
            return false;
        }
    }
}
