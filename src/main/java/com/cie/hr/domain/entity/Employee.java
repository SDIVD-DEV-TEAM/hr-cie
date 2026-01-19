package com.cie.hr.domain.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 06/05/2023
 * @project hr-cie
 */
public interface Employee {
    UUID id();

    String firstname();

    String lastname();

    String email();

    String employeeNumber();

    boolean isActive();

    Profile profile();

    boolean isNotLocked();

    String password();

    boolean sendAccountIdEmail();

    List<PasswordStore> passwordStores();

    String token();

    boolean isFirstConnect();

    boolean isDeleted();

    void setFirstConnect(Boolean firstConnect);

    long getPeriodBetweenPasswordRegisterDayAndNow(String password);

    void setPassword(String newPassword);

    void addNewPasswordToAStore(String newPassword);

    void saveToken(String token);

    void saveExpiredChangeInitializeTokenDate();

    void setLocked(boolean isLocked);

    void destroyExpireDate();

    LocalDateTime changeExpiredDate();

    Integer accessLevel();

    void setIsDeleted(boolean isDeleted);

    void sendAccountIdEmail(boolean sendAccountIdEmail);
}
