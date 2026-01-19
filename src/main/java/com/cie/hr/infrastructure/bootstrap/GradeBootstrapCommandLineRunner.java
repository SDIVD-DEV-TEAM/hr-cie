package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.GradeEntity;
import com.cie.hr.infrastructure.repository.GradeJpaRepository;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Order(4)
@Component
public class GradeBootstrapCommandLineRunner implements CommandLineRunner {

    private final GradeJpaRepository gradeJpaRepository;

    public GradeBootstrapCommandLineRunner(GradeJpaRepository gradeJpaRepository) {
        this.gradeJpaRepository = gradeJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<GradeEntity> gradeEntities = new ArrayList<>() {{
            var gradeDG = GradeEntity.builder().name("Directeur Général").code("DG").description("Manager rang 5").active(true).rank(0).build();
            var gradeCE = GradeEntity.builder().name("Conseiller & Expert").code("CE").description("Manager hors hiérarchie").rank(1).active(true).build();
            var gradeDGA = GradeEntity.builder().name("Directeur Général Adjoint").code("DGA").description("Manager rang 4").active(true).rank(2).build();
            var gradeDC = GradeEntity.builder().name("Directeur Central").code("DC").description("Manager rang 3").active(true).rank(3).build();
            var gradeD = GradeEntity.builder().name("Directeur").code("D").description("Manager rang 2").active(true).rank(4).build();
            var gradeDA = GradeEntity.builder().name("Directeur Adjoint").code("DA").description("Manager rang 1").active(true).rank(5).build();
            var gradeSD = GradeEntity.builder().name("Sous-Directeur").code("SD").description("Manager rang 0").active(true).rank(6).build();
            var gradeDR = GradeEntity.builder().name("Directeur Régional").code("DR").description("Manager rang 0").active(true).rank(6).build();
            var gradeAS = GradeEntity.builder().name("Assistant").code("AS").description("Manager rang 0").active(true).rank(6).build();

            gradeDR.setId(Generators.timeBasedEpochGenerator().generate());
            gradeDG.setId(Generators.timeBasedEpochGenerator().generate());
            gradeDGA.setId(Generators.timeBasedEpochGenerator().generate());
            gradeDC.setId(Generators.timeBasedEpochGenerator().generate());
            gradeD.setId(Generators.timeBasedEpochGenerator().generate());
            gradeDA.setId(Generators.timeBasedEpochGenerator().generate());
            gradeSD.setId(Generators.timeBasedEpochGenerator().generate());
            gradeCE.setId(Generators.timeBasedEpochGenerator().generate());
            gradeAS.setId(Generators.timeBasedEpochGenerator().generate());

            add(gradeDG);
            add(gradeCE);
            add(gradeDGA);
            add(gradeDC);
            add(gradeD);
            add(gradeDA);
            add(gradeSD);
            add(gradeDR);
            add(gradeAS);

        }};

        if (gradeJpaRepository.findAll().isEmpty()) {
            gradeJpaRepository.saveAll(gradeEntities);
        }
    }
}
