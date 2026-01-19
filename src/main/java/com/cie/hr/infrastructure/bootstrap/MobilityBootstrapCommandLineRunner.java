package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.MobilityEntity;
import com.cie.hr.infrastructure.repository.MobilityJpaRepository;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author Alexis TAMBIE
 * @created 03/07/2023
 * @project hr-cie
 */
@Order(10)
@Component
public class MobilityBootstrapCommandLineRunner implements CommandLineRunner {

    private final MobilityJpaRepository mobilityJpaRepository;

    public MobilityBootstrapCommandLineRunner(MobilityJpaRepository mobilityJpaRepository) {
        this.mobilityJpaRepository = mobilityJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        var mobilityList = new ArrayList<MobilityEntity>() {{
            add(new MobilityEntity(Generators.timeBasedEpochGenerator().generate(), "Mobilité verticale"));
            add(new MobilityEntity(Generators.timeBasedEpochGenerator().generate(), "Mobilité horizontale"));
            add(new MobilityEntity(Generators.timeBasedEpochGenerator().generate(), "Mutation"));
            add(new MobilityEntity(Generators.timeBasedEpochGenerator().generate(), "Maintien au poste"));
            add(new MobilityEntity(Generators.timeBasedEpochGenerator().generate(), "Reconversion"));
        }};

        if (mobilityJpaRepository.count() == 0) {
            mobilityJpaRepository.saveAll(mobilityList);
        }
    }
}
