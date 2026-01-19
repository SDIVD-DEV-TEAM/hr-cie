package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.adapter.response.HttpResponse;
import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.security.model.CustomUser;
import com.cie.hr.common.security.port.LoginAttemptChecker;
import com.cie.hr.domain.entity.Employee;
import com.cie.hr.domain.entity.EmployeeDomain;
import com.cie.hr.domain.port.EmployeeRepositoryPort;
import com.cie.hr.infrastructure.entity.EmployeeEntity;
import com.cie.hr.infrastructure.mapper.EmployeeMapper;
import com.cie.hr.infrastructure.repository.EmployeeJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.cie.hr.common.constant.Constant.FOUND_USER_BY_USERNAME;
import static com.cie.hr.common.constant.Constant.NO_USER_FOUND_BY_USERNAME;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Transactional
@Service
@Qualifier("userDetailsService")
public class EmployeeRepositoryAdapter implements EmployeeRepositoryPort, UserDetailsService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final EmployeeJpaRepository employeeJpaRepository;

    private final HttpServletResponse response;

    private final LoginAttemptChecker loginAttemptPort;

    public EmployeeRepositoryAdapter(EmployeeJpaRepository employeeJpaRepository, HttpServletResponse response, LoginAttemptChecker loginAttemptPort) {
        this.employeeJpaRepository = employeeJpaRepository;
        this.response = response;
        this.loginAttemptPort = loginAttemptPort;
    }

    @Override
    public void save(EmployeeDomain employeeDomain) {
        EmployeeEntity employeeEntity = EmployeeMapper.toEmployeeEntity(employeeDomain);
        this.employeeJpaRepository.save(employeeEntity);
    }

    @Override
    public Optional<EmployeeDomain> findById(UUID employeeId) {
        var employeeEntity = this.employeeJpaRepository.findById(employeeId);
        return employeeEntity.map(EmployeeMapper::toEmployeeDomain);
    }

    @Override
    public void updateAndSave(EmployeeDomain domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                EmployeeEntity employeeEntity = employeeJpaRepository.findByIdForWrite(domain.id()).orElseThrow(() -> new ApplicationException("Cet employé n'existe pas"));
                EmployeeMapper.updateAndSave(domain, employeeEntity);
                employeeJpaRepository.save(employeeEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update employee after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating employee", e);
                throw e;
            }
        }
    }

    @Override
    public void saveAll(List<EmployeeDomain> entities) {
        List<EmployeeEntity> employeeEntities;
        employeeEntities = entities.stream().map(EmployeeMapper::toEmployeeEntity).toList();
        this.employeeJpaRepository.saveAll(employeeEntities);
    }

    @Override
    public Optional<EmployeeDomain> findByEmail(String email) {
        var employeeEntity = this.employeeJpaRepository.findByEmailAndDeletedFalse(email);
        return employeeEntity.map(EmployeeMapper::toEmployeeDomain);
    }

    @Override
    public Optional<Boolean> existsById(UUID id) {
        return Optional.of(employeeJpaRepository.existsById(id));
    }

    @Override
    public Optional<EmployeeDomain> findByEmployeeNumber(String employeeNumber) {
        return this.employeeJpaRepository.findFirstByEmployeeNumber(employeeNumber).map(EmployeeMapper::toEmployeeDomain);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var employeeEntity = this.employeeJpaRepository.findByEmailAndDeletedFalse(username);
        if (employeeEntity.isEmpty()) {
            LOGGER.error(NO_USER_FOUND_BY_USERNAME + "{}", username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            if (loginAttemptPort.isBlocked(username)) {
                HttpResponse httpResponse = new HttpResponse(UNAUTHORIZED.value(), UNAUTHORIZED, UNAUTHORIZED.getReasonPhrase().toUpperCase(), "Votre compte est bloqué. Merci de réessayer plus tard");
                response.setContentType(APPLICATION_JSON_VALUE);
                response.setStatus(UNAUTHORIZED.value());
                OutputStream outputStream;
                try {
                    outputStream = response.getOutputStream();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(outputStream, httpResponse);
                    outputStream.flush();
                } catch (IOException e) {
                    throw new ApplicationException(e.getMessage());
                }
            }

            Employee employee = EmployeeMapper.toEmployeeDomain(employeeEntity.get());
            CustomUser customPrincipal = new CustomUser(employee);
            validateLoginAttempt(employeeEntity.get());
            employeeJpaRepository.save(employeeEntity.get());

            LOGGER.info(FOUND_USER_BY_USERNAME + "{}", username);
            return customPrincipal;
        }

    }

    private void validateLoginAttempt(EmployeeEntity employeeEntity) {
        if (Boolean.TRUE.equals(employeeEntity.getIsNotLocked())) {
            employeeEntity.setIsNotLocked(!loginAttemptPort.hasExceededMaxAttempts(employeeEntity.getEmail()));
        } else {
            loginAttemptPort.evictUserFromLoginAttemptCache(employeeEntity.getEmail());
        }
    }
}
