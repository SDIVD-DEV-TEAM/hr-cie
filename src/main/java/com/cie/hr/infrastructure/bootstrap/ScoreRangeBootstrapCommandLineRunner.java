package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.ScoreRangeEntity;
import com.cie.hr.infrastructure.repository.ScoreRangeJpaRepository;
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
@Order(12)
@Component
public class ScoreRangeBootstrapCommandLineRunner implements CommandLineRunner {

    private final ScoreRangeJpaRepository scoreRangeJpaRepository;

    public ScoreRangeBootstrapCommandLineRunner(ScoreRangeJpaRepository scoreRangeJpaRepository) {
        this.scoreRangeJpaRepository = scoreRangeJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        var scoreRangeList = new ArrayList<ScoreRangeEntity>() {{
            add(new ScoreRangeEntity(Generators.timeBasedEpochGenerator().generate(), "Performance exceptionnelle [18 - 20]", 20, 18));
            add(new ScoreRangeEntity(Generators.timeBasedEpochGenerator().generate(), "Performance très satisfaisante [15 - 18[", 17, 15));
            add(new ScoreRangeEntity(Generators.timeBasedEpochGenerator().generate(), "Performance acceptable [13 - 15[", 14, 13));
            add(new ScoreRangeEntity(Generators.timeBasedEpochGenerator().generate(), "Performance moyenne [11 - 13[", 12, 11));
            add(new ScoreRangeEntity(Generators.timeBasedEpochGenerator().generate(), "Performance faible [0 - 11[", 10, 0));
        }};

        if (scoreRangeJpaRepository.count() == 0) {
            scoreRangeJpaRepository.saveAll(scoreRangeList);
        }
    }
}
