package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.FormationEntity;
import com.cie.hr.infrastructure.repository.FormationJpaRepository;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
@Order(11)
@Component
public class FormationBootstrapCommandLineRunner implements CommandLineRunner {

    private final FormationJpaRepository formationJpaRepository;

    public FormationBootstrapCommandLineRunner(FormationJpaRepository formationJpaRepository) {
        this.formationJpaRepository = formationJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        var formationsList = new ArrayList<FormationEntity>() {{
           add(new FormationEntity(Generators.timeBasedEpochGenerator().generate(), "Reconversion"));
            add(new FormationEntity(Generators.timeBasedEpochGenerator().generate(), "Perfectionnement"));
            add(new FormationEntity(Generators.timeBasedEpochGenerator().generate(), "Adaptation au poste"));
            add(new FormationEntity(Generators.timeBasedEpochGenerator().generate(), "Développement"));
        }};

        if (formationJpaRepository.count() == 0) {
            formationJpaRepository.saveAll(formationsList);
        }
    }
}
