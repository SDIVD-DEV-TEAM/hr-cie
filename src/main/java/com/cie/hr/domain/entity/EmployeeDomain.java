package com.cie.hr.domain.entity;

import com.cie.hr.common.entity.BaseEntity;
import com.cie.hr.domain.valueobject.EmployeeId;
import com.fasterxml.uuid.Generators;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
public class EmployeeDomain extends BaseEntity<EmployeeId> implements Employee {
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String employeeNumber;
    private final boolean active;
    private final Profile profile;
    private boolean isNotLocked;
    private boolean isDeleted;
    private boolean isFirstConnect;
    private String token;
    private String password;
    private LocalDateTime changeExpiredDate;
    private final List<PasswordStore> passwordStores;
    private final Integer accessLevel;
    private boolean sendAccountIdEmail;

    private EmployeeDomain(Builder builder) {
        setId(builder.employeeId);
        firstname = builder.firstname;
        lastname = builder.lastname;
        email = builder.email;
        employeeNumber = builder.employeeNumber;
        active = builder.active;
        profile = builder.profile;
        password = builder.password;
        isNotLocked = builder.isNotLocked;
        token = builder.token;
        changeExpiredDate = builder.changeExpiredDate;
        isFirstConnect = builder.isFirstConnect;
        passwordStores = builder.passwordStores;
        accessLevel = builder.accessLevel;
        isDeleted = builder.isDeleted;
        sendAccountIdEmail = builder.sendAccountIdEmail;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public UUID id() {
        return getId().getValue();
    }

    @Override
    public String firstname() {
        return firstname;
    }

    @Override
    public String lastname() {
        return lastname;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String employeeNumber() {
        return employeeNumber;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Profile profile() {
        return profile;
    }

    @Override
    public boolean isNotLocked() {
        return isNotLocked;
    }

    @Override
    public String password() {
        return password;
    }

    @Override
    public boolean sendAccountIdEmail() {
        return sendAccountIdEmail;
    }

    @Override
    public List<PasswordStore> passwordStores() {
        return passwordStores;
    }

    @Override
    public String token() {
        return token;
    }


    @Override
    public boolean isFirstConnect() {
        return isFirstConnect;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public void setFirstConnect(Boolean firstConnect) {
        this.isFirstConnect = firstConnect;
    }

    @Override
    public long getPeriodBetweenPasswordRegisterDayAndNow(String password) {
        if (!passwordStores.isEmpty()) {
            PasswordStore existPassword = passwordStores.stream().filter(passwordStore ->
                            passwordStore.getLastPassword().equals(password))
                    .findFirst().orElse(null);
            if (existPassword != null) {
                return ChronoUnit.MONTHS.
                        between(existPassword.getStorageDate(),
                                LocalDateTime.now());
            }
        }
        return 0;
    }

    @Override
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    @Override
    public void addNewPasswordToAStore(String newPassword) {
        if (this.passwordStores.isEmpty() || this.passwordStores.size() < 3) {
            this.passwordStores.add(PasswordStore.newBuilder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .lastPassword(newPassword)
                    .storageDate(LocalDateTime.now())
                    .build());
        } else if (this.passwordStores.size() == 3) {
            this.passwordStores.sort(new PasswordStore.SortByDate());
            var lastPasswordId = passwordStores.get(2).getId();
            this.passwordStores.remove(passwordStores.get(2));
            this.passwordStores.add(PasswordStore.newBuilder()
                    .id(lastPasswordId)
                    .lastPassword(newPassword)
                    .storageDate(LocalDateTime.now())
                    .build());
        }
    }

    @Override
    public void saveToken(String token) {
        this.token = token;
    }

    @Override
    public void saveExpiredChangeInitializeTokenDate() {
        this.changeExpiredDate = LocalDateTime.now().plusMinutes(10);
    }

    @Override
    public void setLocked(boolean isLocked) {
        this.isNotLocked = isLocked;
    }

    @Override
    public void destroyExpireDate() {
        this.changeExpiredDate = null;
    }

    @Override
    public LocalDateTime changeExpiredDate() {
        return changeExpiredDate;
    }

    @Override
    public Integer accessLevel() {
        return accessLevel;
    }

    @Override
    public void setIsDeleted(boolean val) {
        isDeleted = val;
    }

    @Override
    public void sendAccountIdEmail(boolean sendAccountIdEmail) {
        this.sendAccountIdEmail = sendAccountIdEmail;
    }

    public static final class Builder {
        private String token;
        private LocalDateTime changeExpiredDate;
        private boolean isFirstConnect;
        private String firstname;
        private String lastname;
        private String email;
        private String employeeNumber;
        private boolean active;
        private Profile profile;
        private String password;
        private boolean isNotLocked;
        private EmployeeId employeeId;
        private List<PasswordStore> passwordStores;
        private Integer accessLevel;
        private boolean isDeleted;
        private boolean sendAccountIdEmail;

        public Builder token(String val) {
            token = val;
            return this;
        }

        public Builder employeeId(EmployeeId val) {
            employeeId = val;
            return this;
        }

        public void changeExpiredDate(LocalDateTime val) {
            changeExpiredDate = val;
        }

        public Builder firstname(String val) {
            firstname = val;
            return this;
        }

        public Builder lastname(String val) {
            lastname = val;
            return this;
        }

        public Builder email(String val) {
            email = val;
            return this;
        }

        public Builder employeeNumber(String val) {
            employeeNumber = val;
            return this;
        }

        public Builder isFirstConnect(boolean val) {
            isFirstConnect = val;
            return this;
        }

        public Builder active(boolean val) {
            active = val;
            return this;
        }

        public Builder isDeleted(boolean val) {
            isDeleted = val;
            return this;
        }
        public Builder isNotLocked(boolean val) {
            isNotLocked = val;
            return this;
        }

        public EmployeeDomain build() {
            return new EmployeeDomain(this);
        }

        public Builder profile(Profile val) {
            profile = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public void passwordStores(List<PasswordStore> val) {
            passwordStores = val;
        }

        public Builder accessLevel(Integer val) {
            accessLevel = val;
            return this;
        }

        public Builder sendAccountIdEmail(boolean val) {
            sendAccountIdEmail = val;
            return this;
        }

        private Builder() {
            passwordStores = new ArrayList<>();
        }

    }

    @Override
    public String toString() {
        return "EmployeeDomain{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", active=" + active +
                ", profile=" + profile +
                ", isNotLocked=" + isNotLocked +
                ", isDeleted=" + isDeleted +
                ", isFirstConnect=" + isFirstConnect +
                ", token='" + token + '\'' +
                ", password='" + password + '\'' +
                ", changeExpiredDate=" + changeExpiredDate +
                ", passwordStores=" + passwordStores +
                ", accessLevel=" + accessLevel +
                ", sendAccountIdEmail=" + sendAccountIdEmail +
                '}';
    }
}
