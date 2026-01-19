package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.NoteDistributionEntity;
import com.cie.hr.infrastructure.entity.ScoreEntity;
import com.cie.hr.infrastructure.entity.ScoreNoteDistributionEntity;
import com.cie.hr.infrastructure.repository.NoteDistributionJpaRepository;
import com.cie.hr.infrastructure.repository.ScoreJpaRepository;
import com.cie.hr.infrastructure.repository.ScoreNoteDistributionJpaRepository;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
@Order(14)
@Component
public class ScoreNoteDistributionCommandLineRunner implements CommandLineRunner {

    private final ScoreNoteDistributionJpaRepository scoreNoteDistributionJpaRepository;
    private final ScoreJpaRepository scoreJpaRepository;
    private final NoteDistributionJpaRepository noteDistributionJpaRepository;

    public ScoreNoteDistributionCommandLineRunner(ScoreNoteDistributionJpaRepository scoreNoteDistributionJpaRepository, ScoreJpaRepository scoreJpaRepository, NoteDistributionJpaRepository noteDistributionJpaRepository) {
        this.scoreNoteDistributionJpaRepository = scoreNoteDistributionJpaRepository;
        this.scoreJpaRepository = scoreJpaRepository;
        this.noteDistributionJpaRepository = noteDistributionJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        List<ScoreNoteDistributionEntity> list = new ArrayList<>();

        // Find Note
        Optional<NoteDistributionEntity> noteDistribution = getNoteDistributionEntity("Q1");
        noteDistribution.ifPresent(note -> {
            // Find Score
            Optional<ScoreEntity> score = getScoreEntity(1);
            score.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(1.00f)
                    .maxInterval(79.99f)
                    .noteDistribution(noteDistribution.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score2 = getScoreEntity(2);
            score2.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(80.00f)
                    .maxInterval(94.99f)
                    .noteDistribution(noteDistribution.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score3 = getScoreEntity(3);
            score3.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(95.00f)
                    .maxInterval(100.00f)
                    .noteDistribution(noteDistribution.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score4 = getScoreEntity(4);
            score4.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(100.01f)
                    .maxInterval(105.00f)
                    .noteDistribution(noteDistribution.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score5 = getScoreEntity(5);
            score5.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(105.01f)
                    .maxInterval(1000)
                    .noteDistribution(noteDistribution.get())
                    .score(scoreEntity)
                    .build()));
        });

        // Find Note
        Optional<NoteDistributionEntity> noteDistributionQ2 = getNoteDistributionEntity("Q2");
        noteDistributionQ2.ifPresent(note -> {
            // Find Score
            Optional<ScoreEntity> score = getScoreEntity(1);
            score.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(1.00f)
                    .maxInterval(74.99f)
                    .noteDistribution(noteDistributionQ2.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score2 = getScoreEntity(2);
            score2.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(75.00f)
                    .maxInterval(79.99f)
                    .noteDistribution(noteDistributionQ2.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score3 = getScoreEntity(3);
            score3.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(80.00f)
                    .maxInterval(94.99f)
                    .noteDistribution(noteDistributionQ2.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score4 = getScoreEntity(4);
            score4.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(95.00f)
                    .maxInterval(99.99f)
                    .noteDistribution(noteDistributionQ2.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score5 = getScoreEntity(5);
            score5.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(100)
                    .maxInterval(100)
                    .noteDistribution(noteDistributionQ2.get())
                    .score(scoreEntity)
                    .build()));
        });

        // Find Note
        Optional<NoteDistributionEntity> noteDistributionQ3 = getNoteDistributionEntity("Q3");
        noteDistributionQ3.ifPresent(note -> {
            // Find Score
            Optional<ScoreEntity> score = getScoreEntity(1);
            score.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(1.00f)
                    .maxInterval(69.99f)
                    .noteDistribution(noteDistributionQ3.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score2 = getScoreEntity(2);
            score2.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(70.00f)
                    .maxInterval(79.99f)
                    .noteDistribution(noteDistributionQ3.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score3 = getScoreEntity(3);
            score3.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(80.00f)
                    .maxInterval(89.99f)
                    .noteDistribution(noteDistributionQ3.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score4 = getScoreEntity(4);
            score4.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(90.00f)
                    .maxInterval(94.99f)
                    .noteDistribution(noteDistributionQ3.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score5 = getScoreEntity(5);
            score5.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(95.00f)
                    .maxInterval(105.00f)
                    .noteDistribution(noteDistributionQ3.get())
                    .score(scoreEntity)
                    .build()));

            score.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(105.01f)
                    .maxInterval(1000)
                    .noteDistribution(noteDistributionQ3.get())
                    .score(scoreEntity)
                    .build()));
        });

        // Find Note
        Optional<NoteDistributionEntity> noteDistributionQ4 = getNoteDistributionEntity("Q4");
        noteDistributionQ4.ifPresent(note -> {
            Optional<ScoreEntity> score = getScoreEntity(1);
            score.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(100.01f)
                    .maxInterval(1000)
                    .noteDistribution(noteDistributionQ4.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score2 = getScoreEntity(2);
            score2.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(99.99f)
                    .maxInterval(105.00f)
                    .noteDistribution(noteDistributionQ4.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score3 = getScoreEntity(3);
            score3.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(95.00f)
                    .maxInterval(100.00f)
                    .noteDistribution(noteDistributionQ4.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score4 = getScoreEntity(4);
            score4.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(80.00f)
                    .maxInterval(94.99f)
                    .noteDistribution(noteDistributionQ4.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score5 = getScoreEntity(5);
            score5.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(1)
                    .maxInterval(79.99f)
                    .noteDistribution(noteDistributionQ4.get())
                    .score(scoreEntity)
                    .build()));
        });


        // Find Note
        Optional<NoteDistributionEntity> noteDistributionQ5 = getNoteDistributionEntity("Q5");
        noteDistributionQ5.ifPresent(note -> {
            Optional<ScoreEntity> score = getScoreEntity(1);
            score.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(0.01f)
                    .maxInterval(1000)
                    .noteDistribution(noteDistributionQ5.get())
                    .score(scoreEntity)
                    .build()));

            Optional<ScoreEntity> score5 = getScoreEntity(5);
            score5.ifPresent(scoreEntity -> list.add(ScoreNoteDistributionEntity.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .registeredAt(LocalDateTime.now())
                    .minInterval(0.00f)
                    .maxInterval(0.00f)
                    .noteDistribution(noteDistributionQ5.get())
                    .score(scoreEntity)
                    .build()));
        });

        if (scoreNoteDistributionJpaRepository.count() == 0) {
            scoreNoteDistributionJpaRepository.saveAll(list);
        }
    }

    private Optional<ScoreEntity> getScoreEntity(int score) {
        return scoreJpaRepository.findFirstByScore(score);
    }

    private Optional<NoteDistributionEntity> getNoteDistributionEntity(String code) {
        return noteDistributionJpaRepository.findFirstByCode(code);
    }
}
