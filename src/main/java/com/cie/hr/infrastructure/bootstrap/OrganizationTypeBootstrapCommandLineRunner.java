package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.OrganizationTypeEntity;
import com.cie.hr.infrastructure.repository.OrganizationTypeJpaRepository;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Order(2)
@Component
public class OrganizationTypeBootstrapCommandLineRunner implements CommandLineRunner {

    private final OrganizationTypeJpaRepository organizationTypeJpaRepository;

    public OrganizationTypeBootstrapCommandLineRunner(OrganizationTypeJpaRepository organizationTypeJpaRepository) {
        this.organizationTypeJpaRepository = organizationTypeJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<OrganizationTypeEntity> organizationTypeEntities = new ArrayList<>() {{
            var organisationP = OrganizationTypeEntity.builder().name("Pôle").code("1").gradeCode("DG").build();
            organisationP.setId(Generators.timeBasedEpochGenerator().generate());
            var organisationDC = OrganizationTypeEntity.builder().name("Direction centrale").code("2").gradeCode("DC").build();
            organisationDC.setId(Generators.timeBasedEpochGenerator().generate());
            var organisationD = OrganizationTypeEntity.builder().name("Direction").code("3").gradeCode("D").build();
            organisationD.setId(Generators.timeBasedEpochGenerator().generate());
            var organisationDA = OrganizationTypeEntity.builder().name("Direction Adjointe").code("4").gradeCode("DA").build();
            organisationDA.setId(Generators.timeBasedEpochGenerator().generate());
            var organisationSD = OrganizationTypeEntity.builder().name("Sous Direction").code("5").gradeCode("SD").build();
            organisationSD.setId(Generators.timeBasedEpochGenerator().generate());
            var organisationDR = OrganizationTypeEntity.builder().name("Direction Régionale").code("6").gradeCode("DR").build();
            organisationDR.setId(Generators.timeBasedEpochGenerator().generate());
            var organisationAS = OrganizationTypeEntity.builder().name("Assistant").code("7").gradeCode("AS").build();
            organisationAS.setId(Generators.timeBasedEpochGenerator().generate());

            add(organisationP);
            add(organisationDC);
            add(organisationD);
            add(organisationDA);
            add(organisationSD);
            add(organisationDR);
            add(organisationAS);
        }};
        if (organizationTypeJpaRepository.findAll().isEmpty())
            organizationTypeJpaRepository.saveAll(organizationTypeEntities);
    }
}
