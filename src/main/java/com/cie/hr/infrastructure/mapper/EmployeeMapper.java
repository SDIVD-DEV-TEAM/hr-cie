package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.EmployeeDomain;
import com.cie.hr.domain.entity.PasswordStore;
import com.cie.hr.domain.valueobject.EmployeeId;
import com.cie.hr.infrastructure.entity.EmployeeEntity;
import com.cie.hr.infrastructure.entity.PasswordStoreEntity;
import com.cie.hr.infrastructure.entity.ProfileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Alexis TAMBIE
 * @created 04/05/2023
 * @project hr-cie
 */
public class EmployeeMapper {

    public static EmployeeDomain toEmployeeDomain(EmployeeEntity employeeEntity) {
        if (employeeEntity == null) {
            return null;
        }
        EmployeeDomain.Builder employeeDomain = EmployeeDomain.newBuilder();

        employeeDomain.token(employeeEntity.getToken());
        employeeDomain.changeExpiredDate(employeeEntity.getChangeExpiredDate() == null ? null : employeeEntity.getChangeExpiredDate());
        employeeDomain.firstname(employeeEntity.getFirstname());
        employeeDomain.lastname(employeeEntity.getLastname());
        employeeDomain.email(employeeEntity.getEmail());
        employeeDomain.password(employeeEntity.getPassword());
        employeeDomain.employeeNumber(employeeEntity.getEmployeeNumber());
        employeeDomain.isFirstConnect(employeeEntity.getIsFirstConnect() != null && employeeEntity.getIsFirstConnect());
        employeeDomain.active(employeeEntity.getActive() != null && employeeEntity.getActive());
        employeeDomain.isNotLocked(employeeEntity.getIsNotLocked() != null && employeeEntity.getIsNotLocked());
        employeeDomain.profile(ProfileMapper.toDomain(employeeEntity.getProfile()));
        employeeDomain.accessLevel(employeeEntity.getAccessLevel());
        employeeDomain.passwordStores(employeeEntity.getPasswordStores() == null ? new ArrayList<>() :
                employeeEntity.getPasswordStores().stream().map(e -> PasswordStore.newBuilder()
                        .id(e.getId())
                        .storageDate(e.getCreatedAt())
                        .lastPassword(e.getPassword())
                        .build()).collect(Collectors.toList()));
        employeeDomain.employeeId(new EmployeeId(employeeEntity.getId()));
        employeeDomain.isDeleted(employeeEntity.isDeleted());
        employeeDomain.sendAccountIdEmail(employeeEntity.getSendAccountIdEmail() != null && employeeEntity.getSendAccountIdEmail());
        return employeeDomain.build();
    }

    public static EmployeeEntity toEmployeeEntity(EmployeeDomain employeeDomain) {
        if (employeeDomain == null) {
            return null;
        }

        List<PasswordStoreEntity> passwordsStores;

        var employeeEntity = EmployeeEntity.builder()
                .firstname(employeeDomain.firstname())
                .lastname(employeeDomain.lastname())
                .email(employeeDomain.email())
                .active(employeeDomain.isActive())
                .employeeNumber(employeeDomain.employeeNumber())
                .profile(ProfileMapper.toEntity(employeeDomain.profile()))
                .password(employeeDomain.password())
                .isNotLocked(employeeDomain.isNotLocked())
                .token(employeeDomain.token())
                .changeExpiredDate(employeeDomain.changeExpiredDate())
                .accessLevel(employeeDomain.accessLevel())
                .isFirstConnect(employeeDomain.isFirstConnect())
                .build();

        if (employeeDomain.passwordStores() != null) {
            passwordsStores = employeeDomain.passwordStores().stream()
                    .map(e -> PasswordStoreEntity.builder()
                            .id(e.getId())
                            .password(e.getLastPassword())
                            .createdAt(e.getStorageDate())
                            .build()).collect(Collectors.toList());
            employeeEntity.setPasswordStores(passwordsStores);
        }

        employeeEntity.setId(employeeDomain.getId().getValue());
        employeeEntity.setDeleted(employeeDomain.isDeleted());
        employeeEntity.setSendAccountIdEmail(employeeDomain.sendAccountIdEmail());
        return employeeEntity;
    }

    public  static void updateAndSave(EmployeeDomain domain, EmployeeEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!Objects.equals(domain.firstname(), entity.getFirstname())) {
            entity.setFirstname(domain.firstname());
        }

        if (!Objects.equals(domain.lastname(), entity.getLastname())) {
            entity.setLastname(domain.lastname());
        }

        if (!Objects.equals(domain.email(), entity.getEmail())) {
            entity.setEmail(domain.email());
        }

        if (!Objects.equals(domain.password(), entity.getPassword())) {
            entity.setPassword(domain.password());
        }

        if (!Objects.equals(domain.employeeNumber(), entity.getEmployeeNumber())) {
            entity.setEmployeeNumber(domain.employeeNumber());
        }

        if (!Objects.equals(domain.isActive(), entity.getActive())) {
            entity.setActive(domain.isActive());
        }

        if (!Objects.equals(domain.isFirstConnect(), entity.getIsFirstConnect())) {
            entity.setIsFirstConnect(domain.isFirstConnect());
        }

        if (!Objects.equals(domain.isNotLocked(), entity.getIsNotLocked())) {
            entity.setIsNotLocked(domain.isNotLocked());
        }

        ProfileEntity profileEntity = ProfileMapper.toEntity(domain.profile());
        if (profileEntity != null && (entity.getProfile() == null ||
                !Objects.equals(entity.getProfile(), profileEntity))) {
            entity.setProfile(profileEntity);
        }


        if (domain.passwordStores() != null) {
            entity.setPasswordStores(domain.passwordStores().stream()
                    .map(e -> PasswordStoreEntity.builder()
                            .id(e.getId())
                            .password(e.getLastPassword())
                            .createdAt(e.getStorageDate())
                            .build()).collect(Collectors.toList()));
        }

        if (!Objects.equals(domain.token(), entity.getToken())) {
            entity.setToken(domain.token());
        }

        if (!Objects.equals(domain.changeExpiredDate(), entity.getChangeExpiredDate())) {
            entity.setChangeExpiredDate(domain.changeExpiredDate());
        }

        if (!Objects.equals(domain.accessLevel(), entity.getAccessLevel())) {
            entity.setAccessLevel(domain.accessLevel());
        }

        if (!Objects.equals(domain.isDeleted(), entity.isDeleted())) {
            entity.setDeleted(domain.isDeleted());
        }

        if (!Objects.equals(domain.sendAccountIdEmail(), entity.getSendAccountIdEmail())) {
            entity.setSendAccountIdEmail(domain.sendAccountIdEmail());
        }
    }
}
