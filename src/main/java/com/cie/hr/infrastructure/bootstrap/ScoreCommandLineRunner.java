package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.ScoreEntity;
import com.cie.hr.infrastructure.repository.ScoreJpaRepository;
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
@Order(13)
@Component
public class ScoreCommandLineRunner implements CommandLineRunner {

    private final ScoreJpaRepository scoreJpaRepository;

    public ScoreCommandLineRunner(ScoreJpaRepository scoreJpaRepository) {
        this.scoreJpaRepository = scoreJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        var scoreList = new ArrayList<ScoreEntity>() {{
            add(new ScoreEntity(Generators.timeBasedEpochGenerator().generate(), 1));
            add(new ScoreEntity(Generators.timeBasedEpochGenerator().generate(), 2));
            add(new ScoreEntity(Generators.timeBasedEpochGenerator().generate(), 3));
            add(new ScoreEntity(Generators.timeBasedEpochGenerator().generate(), 4));
            add(new ScoreEntity(Generators.timeBasedEpochGenerator().generate(), 5));
        }};

        if (scoreJpaRepository.count() == 0) {
            scoreJpaRepository.saveAll(scoreList);
        }
    }
}
