package com.cie.hr.infrastructure.bootstrap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.cie.hr.infrastructure.entity.GradeEntity;
import com.cie.hr.infrastructure.entity.JobEntity;
import com.cie.hr.infrastructure.entity.OrganizationEntity;
import com.cie.hr.infrastructure.repository.GradeJpaRepository;
import com.cie.hr.infrastructure.repository.JobJpaRepository;
import com.cie.hr.infrastructure.repository.OrganizationJpaRepository;
import com.fasterxml.uuid.Generators;

/**
 * Bootstrap pour les postes HR (Direction Commerciale et Opérations)
 * Exécuté après HrOrganisationBootstrapCommandLineRunner (Order=16)
 */
@Order(16)
@Component
public class HrJobBootstrapCommandLineRunner implements CommandLineRunner {

    private final JobJpaRepository jobJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final GradeJpaRepository gradeJpaRepository;

    // Mapping des types d'organisation vers les grades
    private static final Map<String, String> TYPE_TO_GRADE = Map.of(
            "DIRECTION CENTRALE COMMERCIALE MARKETING ET OPERATIONS", "DC",
            "DIRECTION COMMERCIALE ET OPERATIONS ABIDJAN", "D",
            "DIRECTION COMMERCIALE ET OPERATIONS INTERIEUR", "D",
            "DIRECTION REGIONALE", "DR",
            "SOUS-DIRECTION", "SD",
            "ASSISTANT", "AS"
    );

    // Données des postes HR
    private static final List<PosteData> POSTES_DATA = List.of(
            new PosteData("DCCMO01", "DGADC01", "DIRECTION CENTRALE COMMERCIALE MARKETING ET OPERATIONS", "DOH MARIUS", "KOUASSI KONAN MATHIAS"),
            new PosteData("DCOA0001", "DCCMO01", "DIRECTION COMMERCIALE ET OPERATIONS ABIDJAN", "SEKONGO PEHA", "DOH MARIUS"),
            new PosteData("DCOI0001", "DCCMO01", "DIRECTION COMMERCIALE ET OPERATIONS INTERIEUR", "BAMBA YACOUBA", "DOH MARIUS"),
            new PosteData("DRAS0001", "DCOA0001", "DIRECTION REGIONALE ABIDJAN SUD", "KOUASSI KONAN BLE N'GUESSAN DESIREE DOMINIQUE Epse YAO", "SEKONGO PEHA"),
            new PosteData("DRYOP0001", "DCOA0001", "DIRECTION REGIONALE YOPOUGON", "KOUAME KOUAKOU AUGUSTE", "SEKONGO PEHA"),
            new PosteData("DRAN0001", "DCOA0001", "DIRECTION REGIONALE ABIDJAN NORD", "KOUASSI ANNICK ROSINE Epse AHOUEGNY", "SEKONGO PEHA"),
            new PosteData("DRABO0001", "DCOA0001", "DIRECTION REGIONALE ABOBO", "COULIBALY FONA IBRAHIMA", "SEKONGO PEHA"),
            new PosteData("DCOA0001_AST", "DCOA0001", "ASSISTANT DCOA", "DIRABOUT ABLE GUY BERENGER", "SEKONGO PEHA"),
            new PosteData("DRE0001", "DCOI0001", "DIRECTION REGIONALE EST", "OUATTARA KINIDINNIN RAISSA EPSE KOUA", "BAMBA YACOUBA"),
            new PosteData("DRSO0001", "DCOI0001", "DIRECTION REGIONALE SUD OUEST", "DIDO GUY MARC WILFRIED", "BAMBA YACOUBA"),
            new PosteData("DRBC0001", "DCOI0001", "DIRECTION REGIONALE BASSE CÔTE", "EDI STEPHANE", "BAMBA YACOUBA"),
            new PosteData("DRSE0001", "DCOI0001", "DIRECTION REGIONALE SUD EST", "N'GORAN KOUASSI YANNICK", "BAMBA YACOUBA"),
            new PosteData("DRLO0001", "DCOI0001", "DIRECTION REGIONALE LITTORAL OUEST", "EHOUMAN BRUCE FERNAND", "BAMBA YACOUBA"),
            new PosteData("DRC0001", "DCOI0001", "DIRECTION REGIONALE CENTRE", "OUEHI SCEANSIEHI FABIEN RODRIGUE", "BAMBA YACOUBA"),
            new PosteData("DRCO0001", "DCOI0001", "DIRECTION REGIONALE CENTRE OUEST", "OUATTARA POGADJOUFOUGOU", "BAMBA YACOUBA"),
            new PosteData("DRN0001", "DCOI0001", "DIRECTION REGIONALE NORD", "SIAHE NEAMIN JEROME", "BAMBA YACOUBA"),
            new PosteData("DRO0001", "DCOI0001", "DIRECTION REGIONALE OUEST", "LOUALOU LOUA MARIUS", "BAMBA YACOUBA"),
            new PosteData("DRCS0001", "DCOI0001", "DIRECTION REGIONALE CENTRE SUD", "KOUAME ATTIMAN YVES", "BAMBA YACOUBA"),
            new PosteData("DCOI0004", "DCOI0001", "SOUS-DIRECTION SUIVI EVALUATION PEPT", "GAUDJI DJAHI ARMEL", "BAMBA YACOUBA")
    );

    public HrJobBootstrapCommandLineRunner(JobJpaRepository jobJpaRepository,
                                           OrganizationJpaRepository organizationJpaRepository,
                                           GradeJpaRepository gradeJpaRepository) {
        this.jobJpaRepository = jobJpaRepository;
        this.organizationJpaRepository = organizationJpaRepository;
        this.gradeJpaRepository = gradeJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("------ HR JOB BOOTSTRAP: Démarrage");

        // Cache pour les jobs créés
        Map<String, JobEntity> jobCache = new HashMap<>();

        // Charger les grades
        Map<String, GradeEntity> gradeCache = loadGrades();

        // Traiter chaque poste
        for (PosteData data : POSTES_DATA) {
            createOrUpdateJob(data, gradeCache, jobCache);
        }

        // Établir les relations parent après création de tous les jobs
        linkJobParents(jobCache);

        System.out.println("------ HR JOB BOOTSTRAP: Terminé");
    }

    private Map<String, GradeEntity> loadGrades() {
        Map<String, GradeEntity> cache = new HashMap<>();
        for (String gradeCode : List.of("DC", "D", "DA", "SD", "DR", "AS")) {
            GradeEntity grade = gradeJpaRepository.findByCode(gradeCode);
            if (grade != null) {
                cache.put(gradeCode, grade);
            }
        }
        return cache;
    }

    private void createOrUpdateJob(PosteData data, Map<String, GradeEntity> gradeCache, Map<String, JobEntity> jobCache) {
        // Vérifier si le job existe déjà
        Optional<JobEntity> existing = jobJpaRepository.findByCode(data.code());

        if (existing.isPresent()) {
            jobCache.put(data.code(), existing.get());
            System.out.println("------ Job " + data.code() + " existe déjà");
            return;
        }

        // Récupérer l'organisation
        Optional<OrganizationEntity> organization = organizationJpaRepository.findFirstByCode(data.codeOrganisation());
        if (organization.isEmpty()) {
            System.out.println("------ ERREUR: Organisation " + data.codeOrganisation() + " non trouvée pour le job " + data.code());
            return;
        }

        // Déterminer le grade
        GradeEntity grade = determineGrade(data.libelle(), gradeCache);

        // Créer le job
        JobEntity newJob = JobEntity.builder()
                .code(data.code())
                .title(data.libelle())
                .organization(organization.get())
                .grade(grade)
                .build();
        newJob.setId(Generators.timeBasedEpochGenerator().generate());

        jobJpaRepository.save(newJob);
        jobCache.put(data.code(), newJob);
        System.out.println("------ Job " + data.code() + " créé: " + data.libelle());
    }

    private GradeEntity determineGrade(String libelle, Map<String, GradeEntity> gradeCache) {
        // Déterminer le grade basé sur le libellé
        if (libelle.contains("DIRECTION CENTRALE")) {
            return gradeCache.get("DC");
        } else if (libelle.contains("SOUS-DIRECTION")) {
            return gradeCache.get("SD");
        } else if (libelle.contains("DIRECTION REGIONALE")) {
            return gradeCache.get("DR");
        } else if (libelle.contains("ASSISTANT")) {
            return gradeCache.get("AS");
        } else if (libelle.contains("DIRECTION")) {
            return gradeCache.get("D");
        }
        return gradeCache.get("SD"); // Par défaut
    }

    private void linkJobParents(Map<String, JobEntity> jobCache) {
        // Établir les relations de hiérarchie entre jobs basées sur le responsable
        // Le job parent est celui du responsable

        // DCCMO01 (DOH) -> parent = job de KOUASSI KONAN MATHIAS (à chercher)
        // DCOA0001, DCOI0001 -> parent = DCCMO01
        // Directions régionales DCOA -> parent = DCOA0001
        // Directions régionales DCOI -> parent = DCOI0001

        Map<String, String> parentMapping = Map.ofEntries(
                Map.entry("DCOA0001", "DCCMO01"),
                Map.entry("DCOI0001", "DCCMO01"),
                Map.entry("DRAS0001", "DCOA0001"),
                Map.entry("DRYOP0001", "DCOA0001"),
                Map.entry("DRAN0001", "DCOA0001"),
                Map.entry("DRABO0001", "DCOA0001"),
                Map.entry("DCOA0001_AST", "DCOA0001"),
                Map.entry("DRE0001", "DCOI0001"),
                Map.entry("DRSO0001", "DCOI0001"),
                Map.entry("DRBC0001", "DCOI0001"),
                Map.entry("DRSE0001", "DCOI0001"),
                Map.entry("DRLO0001", "DCOI0001"),
                Map.entry("DRC0001", "DCOI0001"),
                Map.entry("DRCO0001", "DCOI0001"),
                Map.entry("DRN0001", "DCOI0001"),
                Map.entry("DRO0001", "DCOI0001"),
                Map.entry("DRCS0001", "DCOI0001"),
                Map.entry("DCOI0004", "DCOI0001")
        );

        for (Map.Entry<String, String> entry : parentMapping.entrySet()) {
            String childCode = entry.getKey();
            String parentCode = entry.getValue();

            JobEntity child = jobCache.get(childCode);
            JobEntity parent = jobCache.get(parentCode);

            if (child != null && parent != null && child.getParent() == null) {
                child.setParent(parent);
                jobJpaRepository.save(child);
                System.out.println("------ Job " + childCode + " lié au parent " + parentCode);
            }
        }

        // Lier DCCMO01 au DGA-DC si existe
        JobEntity dccmo = jobCache.get("DCCMO01");
        if (dccmo != null && dccmo.getParent() == null) {
            Optional<JobEntity> dgaDc = jobJpaRepository.findByCode("DGA-DC");
            if (dgaDc.isPresent()) {
                dccmo.setParent(dgaDc.get());
                jobJpaRepository.save(dccmo);
                System.out.println("------ Job DCCMO01 lié au parent DGA-DC");
            }
        }
    }

    // Record pour les données de poste
    private record PosteData(String code, String codeOrganisation, String libelle, String occupePar, String responsable) {}
}
