package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.common.event.CreateEmployeeEvent;
import com.cie.hr.common.event.publish.CreateEmployeeRequestMessagePublisher;
import com.cie.hr.infrastructure.entity.EmployeeEntity;
import com.cie.hr.infrastructure.entity.JobEntity;
import com.cie.hr.infrastructure.mapper.EmployeeMapper;
import com.cie.hr.infrastructure.repository.EmployeeJpaRepository;
import com.cie.hr.infrastructure.repository.JobJpaRepository;
import com.cie.hr.infrastructure.repository.ProfileJpaRepository;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Order(8)
@Component
public class EmployeeBootstrapCommandLineRunner implements CommandLineRunner {

    private final EmployeeJpaRepository employeeJpaRepository;
    private final ProfileJpaRepository profileJpaRepository;
    private final JobJpaRepository jobJpaRepository;
    private final CreateEmployeeRequestMessagePublisher createEmployeeRequestMessagePublisher;

    public EmployeeBootstrapCommandLineRunner(EmployeeJpaRepository employeeJpaRepository, ProfileJpaRepository profileJpaRepository, JobJpaRepository jobJpaRepository, CreateEmployeeRequestMessagePublisher createEmployeeRequestMessagePublisher) {
        this.employeeJpaRepository = employeeJpaRepository;
        this.profileJpaRepository = profileJpaRepository;
        this.jobJpaRepository = jobJpaRepository;
        this.createEmployeeRequestMessagePublisher = createEmployeeRequestMessagePublisher;
    }

    @Override
    public void run(String... args) throws Exception {
        var checkEmployeeRH = employeeJpaRepository.findFirstByProfileCode("RH");
        if (checkEmployeeRH.isEmpty()) {
            var checkEmployeeRHU = employeeJpaRepository.findFirstByProfileCode("RHU");
            if (checkEmployeeRHU.isEmpty()) {
                var profile = profileJpaRepository.findFirstByCode("RH");
                if (profile.isPresent()) {
                    var employee = EmployeeEntity.builder()
                            .profile(profile.get())
                            .email("virtus225one@gmail.com")
                            .firstname("Yoan")
                            .lastname("Virtus")
                            .employeeNumber("0000")
                            .accessLevel(1)
                            .active(true)
                            .isNotLocked(true)
                            .isFirstConnect(true).build();
                    employee.setId(Generators.timeBasedEpochGenerator().generate());

                    System.out.println("------ BOOTSTRAP Employee");
                    employeeJpaRepository.save(employee);
                    var event = new CreateEmployeeEvent(EmployeeMapper.toEmployeeDomain(employee), ZonedDateTime.now(ZoneId.of("UTC")));
                    createEmployeeRequestMessagePublisher.publish(event);
                }
            }
        } else {
            System.out.println("------ Il existe déjà un employée RH");
        }

        var checkEmployeeDG = jobJpaRepository.findByCode("DG");
        if (checkEmployeeDG.isPresent()) {
            JobEntity jobDG = checkEmployeeDG.get();
            if (jobDG.getEmployee() == null) {
                var profile = profileJpaRepository.findFirstByCode("USR");
                if (profile.isPresent()) {
                    var employeeDG = EmployeeEntity.builder()
                            .profile(profile.get())
                            .email("kysaymeric@gmail.com")
                            .firstname("Aymeric")
                            .lastname("Rico")
                            .employeeNumber("0001")
                            .accessLevel(null)
                            .active(true)
                            .isNotLocked(true)
                            .isFirstConnect(true).build();
                    employeeDG.setId(Generators.timeBasedEpochGenerator().generate());

                    System.out.println("------ BOOTSTRAP Employee DG");
                    employeeJpaRepository.save(employeeDG);

                    jobDG.setEmployee(employeeDG);
                    jobJpaRepository.save(jobDG);

                    var event = new CreateEmployeeEvent(EmployeeMapper.toEmployeeDomain(employeeDG), ZonedDateTime.now(ZoneId.of("UTC")));
                    createEmployeeRequestMessagePublisher.publish(event);
                }
            } else {
                System.out.println("------ Le poste de DG est déjà occupé par un employée");
            }

        } else {
            System.out.println("------ Il existe déjà un employée DG");
        }
    }
}
