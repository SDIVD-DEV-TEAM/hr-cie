package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.NoteDistributionEntity;
import com.cie.hr.infrastructure.repository.NoteDistributionJpaRepository;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
@Order(12)
@Component
public class NoteDistributionCommandLineRunner implements CommandLineRunner {

    private final NoteDistributionJpaRepository noteDistributionJpaRepository;

    public NoteDistributionCommandLineRunner(NoteDistributionJpaRepository noteDistributionJpaRepository) {
        this.noteDistributionJpaRepository = noteDistributionJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        var notesList = new ArrayList<NoteDistributionEntity>() {{
            add(NoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .way("Up")
                    .code("Q1")
                    .description("Les indicateurs croissants dont le réalisé peut excéder l'objectif")
                    .category("Taux proportionnel à la note")
                    .maxWhenMayExceed(100)
                    .mayExceed(true)
                    .allOrNothing(false)
                    .build());

            add(NoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .way("Up")
                    .code("Q2")
                    .description("Les indicateurs croissants dont le réalisé ne peut excéder l'objectif")
                    .category("Taux proportionnel à la note")
                    .maxWhenMayExceed(100)
                    .mayExceed(false)
                    .allOrNothing(false)
                    .build());

            add(NoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .way("Up")
                    .code("Q3")
                    .description("Les indicateurs croissants liés au budget")
                    .category("Taux proportionnel à la note")
                    .maxWhenMayExceed(105)
                    .mayExceed(true)
                    .allOrNothing(false)
                    .build());

            add(NoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .way("Down")
                    .code("Q4")
                    .description("Les indicateurs décroissants lié au délai, durée, nombre, etc...")
                    .category("Taux inversement proportionnel à la note")
                    .maxWhenMayExceed(100)
                    .mayExceed(true)
                    .allOrNothing(false)
                    .build());

            add(NoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .way("Down")
                    .code("Q5")
                    .description("Tout ou rien (Négatif) liés à la sécurité ou autre loi du tout ou rien")
                    .category("Taux inversement proportionnel à la note")
                    .maxWhenMayExceed(100)
                    .mayExceed(true)
                    .allOrNothing(true)
                    .build());
        }};

        if (noteDistributionJpaRepository.count() == 0) {
            noteDistributionJpaRepository.saveAll(notesList);
        }
    }
}
