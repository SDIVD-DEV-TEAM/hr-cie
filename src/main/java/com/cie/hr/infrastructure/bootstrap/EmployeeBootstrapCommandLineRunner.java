package com.cie.hr.infrastructure.bootstrap;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.cie.hr.common.event.CreateEmployeeEvent;
import com.cie.hr.common.event.publish.CreateEmployeeRequestMessagePublisher;
import com.cie.hr.infrastructure.entity.EmployeeEntity;
import com.cie.hr.infrastructure.entity.JobEntity;
import com.cie.hr.infrastructure.mapper.EmployeeMapper;
import com.cie.hr.infrastructure.repository.EmployeeJpaRepository;
import com.cie.hr.infrastructure.repository.JobJpaRepository;
import com.cie.hr.infrastructure.repository.ProfileJpaRepository;
import com.fasterxml.uuid.Generators;

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
        // Création des employés RH
        var profileRH = profileJpaRepository.findFirstByCode("RH");
        if (profileRH.isPresent()) {
            // Employé RH 1: Raymond Ano
            var checkEmployeeRH1 = employeeJpaRepository.findFirstByEmployeeNumber("0000");
            if (checkEmployeeRH1.isEmpty()) {
                var employee1 = EmployeeEntity.builder()
                        .profile(profileRH.get())
                        .email("staffdctd@gmail.com")
                        .firstname("Raymond")
                        .lastname("Ano")
                        .employeeNumber("0000")
                        .accessLevel(1)
                        .active(true)
                        .isNotLocked(true)
                        .isFirstConnect(true).build();
                employee1.setId(Generators.timeBasedEpochGenerator().generate());

                System.out.println("------ BOOTSTRAP Employee RH: Raymond Ano");
                employeeJpaRepository.save(employee1);
                var event1 = new CreateEmployeeEvent(EmployeeMapper.toEmployeeDomain(employee1), ZonedDateTime.now(ZoneId.of("UTC")));
                createEmployeeRequestMessagePublisher.publish(event1);
            } else {
                System.out.println("------ L'employé RH Raymond Ano existe déjà");
            }

            // Employé RH 2: AGOUA JEAN ABEL
            var checkEmployeeRH2 = employeeJpaRepository.findFirstByEmployeeNumber("022742V");
            if (checkEmployeeRH2.isEmpty()) {
                var employee2 = EmployeeEntity.builder()
                        .profile(profileRH.get())
                        .email("jagoua@cie.ci")
                        .firstname("JEAN ABEL")
                        .lastname("AGOUA")
                        .employeeNumber("022742V")
                        .accessLevel(1)
                        .active(true)
                        .isNotLocked(true)
                        .isFirstConnect(true).build();
                employee2.setId(Generators.timeBasedEpochGenerator().generate());

                System.out.println("------ BOOTSTRAP Employee RH: AGOUA JEAN ABEL");
                employeeJpaRepository.save(employee2);
                var event2 = new CreateEmployeeEvent(EmployeeMapper.toEmployeeDomain(employee2), ZonedDateTime.now(ZoneId.of("UTC")));
                createEmployeeRequestMessagePublisher.publish(event2);
            } else {
                System.out.println("------ L'employé RH AGOUA JEAN ABEL existe déjà");
            }

            // Employé RH 3: ADOUAKOUA KROU ESTELLE
            var checkEmployeeRH3 = employeeJpaRepository.findFirstByEmployeeNumber("020201H");
            if (checkEmployeeRH3.isEmpty()) {
                var employee3 = EmployeeEntity.builder()
                        .profile(profileRH.get())
                        .email("eadouakoua@cie.ci")
                        .firstname("KROU ESTELLE")
                        .lastname("ADOUAKOUA")
                        .employeeNumber("020201H")
                        .accessLevel(1)
                        .active(true)
                        .isNotLocked(true)
                        .isFirstConnect(true).build();
                employee3.setId(Generators.timeBasedEpochGenerator().generate());

                System.out.println("------ BOOTSTRAP Employee RH: ADOUAKOUA KROU ESTELLE");
                employeeJpaRepository.save(employee3);
                var event3 = new CreateEmployeeEvent(EmployeeMapper.toEmployeeDomain(employee3), ZonedDateTime.now(ZoneId.of("UTC")));
                createEmployeeRequestMessagePublisher.publish(event3);
            } else {
                System.out.println("------ L'employé RH ADOUAKOUA KROU ESTELLE existe déjà");
            }
        }

        var checkEmployeeDG = jobJpaRepository.findByCode("DG");
        if (checkEmployeeDG.isPresent()) {
            JobEntity jobDG = checkEmployeeDG.get();
            if (jobDG.getEmployee() == null) {
                var profile = profileJpaRepository.findFirstByCode("USR");
                if (profile.isPresent()) {
                    var employeeDG = EmployeeEntity.builder()
                            .profile(profile.get())
                            .email("info@dctd-cie.com")
                            .firstname("Jean-Christian")
                            .lastname("Turkson")
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
