package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.ProfileEntity;
import com.cie.hr.infrastructure.repository.ProfileJpaRepository;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Order(1)
@Component
public class ProfileBootstrapCommandLineRunner implements CommandLineRunner {

    private final ProfileJpaRepository profileJpaRepository;

    public ProfileBootstrapCommandLineRunner(ProfileJpaRepository profileJpaRepository) {
        this.profileJpaRepository = profileJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<ProfileEntity> profileEntities = new ArrayList<>() {{
            var profileUser = ProfileEntity.builder()
                    .code("USR")
                    .name("User")
                    .build();
            profileUser.setId(Generators.timeBasedEpochGenerator().generate());

            var profileRH = ProfileEntity.builder()
                    .code("RH")
                    .name("Employé RH")
                    .build();
            profileRH.setId(Generators.timeBasedEpochGenerator().generate());

            var profileFull = ProfileEntity.builder()
                    .code("RHU")
                    .name("Employé RH & User")
                    .build();
            profileFull.setId(Generators.timeBasedEpochGenerator().generate());
            add(profileUser);
            add(profileRH);
            add(profileFull);
        }};

        var cleanProfileList = new ArrayList<ProfileEntity>();
        profileEntities.forEach(profileEntity -> {
            var checkProfile = profileJpaRepository.findFirstByCode(profileEntity.getCode());
            if (checkProfile.isEmpty()) {
                cleanProfileList.add(profileEntity);
            }
        });

        if (!cleanProfileList.isEmpty()) {
            System.out.println("------ BOOTSTRAP Profiles");
            profileJpaRepository.saveAll(cleanProfileList);
        }
    }
}
