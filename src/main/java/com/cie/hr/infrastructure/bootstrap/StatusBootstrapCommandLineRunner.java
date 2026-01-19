package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.StatusEntity;
import com.cie.hr.infrastructure.repository.StatusJpaRepository;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
@Order(7)
@Component
public class StatusBootstrapCommandLineRunner implements CommandLineRunner {

    private final StatusJpaRepository statusJpaRepository;

    public StatusBootstrapCommandLineRunner(StatusJpaRepository statusJpaRepository) {
        this.statusJpaRepository = statusJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        var statusList = new ArrayList<StatusEntity>() {{
            var notStarted = StatusEntity.builder().name("notStarted").code("0").build();
            notStarted.setId(Generators.timeBasedEpochGenerator().generate());
            add(notStarted);
            var inProgress = StatusEntity.builder().name("inProgress").code("1").build();
            inProgress.setId(Generators.timeBasedEpochGenerator().generate());
            add(inProgress);
            var closed = StatusEntity.builder().name("closed").code("2").build();
            closed.setId(Generators.timeBasedEpochGenerator().generate());
            add(closed);
            var evaluated = StatusEntity.builder().name("evaluated").code("3").build();
            evaluated.setId(Generators.timeBasedEpochGenerator().generate());
            add(evaluated);
            var dispute = StatusEntity.builder().name("dispute").code("4").build();
            dispute.setId(Generators.timeBasedEpochGenerator().generate());
            add(dispute);
        }};

        var cleanStatusList = new ArrayList<StatusEntity>();
        statusList.forEach(statusEntity -> {
            var checkStatus = statusJpaRepository.findByCode(statusEntity.getCode());
            if (checkStatus.isEmpty()) {
                cleanStatusList.add(statusEntity);
            }
        });
        statusJpaRepository.saveAll(cleanStatusList);
    }
}
