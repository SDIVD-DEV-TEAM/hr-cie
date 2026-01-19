package com.cie.hr.common.utils;

import com.cie.hr.common.security.port.CustomAuthenticationManager;
import com.cie.hr.domain.entity.EmployeeDomain;
import com.cie.hr.domain.port.EmployeeRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * @author Alexis TAMBIE
 * @created 30/05/2023
 * @project hr-cie
 */
@Configuration
public class CheckRHEmployee {
    private final CustomAuthenticationManager authenticationManager;
    private final EmployeeRepositoryPort employeeRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String USER_PROFILE_CODE = "USR";

    public CheckRHEmployee(CustomAuthenticationManager authenticationManager, EmployeeRepositoryPort employeeRepository) {
        this.authenticationManager = authenticationManager;
        this.employeeRepository = employeeRepository;
    }

    private boolean employeeIsRH() {
        String currentUser = fetchCurrentUser();
        Optional<EmployeeDomain> checkRights = fetchEmployeeRights(currentUser);
        return decideIfUserIsRH(checkRights);
    }

    public boolean employeeIsNotRH() {
        return !employeeIsRH();
    }

    private String fetchCurrentUser() {
        return authenticationManager.getCurrentUser();
    }

    private Optional<EmployeeDomain> fetchEmployeeRights(String currentUser) {
        return employeeRepository.findByEmail(currentUser);
    }

    private boolean decideIfUserIsRH(Optional<EmployeeDomain> checkRights) {
        if (checkRights.isPresent()) {
            EmployeeDomain currentEmployee = checkRights.get();
            LOGGER.info("User Profile is {}", currentEmployee.profile().getCode());
            return isUserProfile(currentEmployee);
        } else {
            LOGGER.info("User is None");
            return true;
        }
    }

    private boolean isUserProfile(EmployeeDomain currentEmployee) {
        return !currentEmployee.profile().getCode().equals(USER_PROFILE_CODE);
    }
}
