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
 * Bootstrap pour les postes PTME (Production Transport Mouvements d'Énergie)
 * Exécuté après PtmeOrganisationBootstrapCommandLineRunner (Order=19)
 */
@Order(19)
@Component
public class PtmeJobBootstrapCommandLineRunner implements CommandLineRunner {

    private final JobJpaRepository jobJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final GradeJpaRepository gradeJpaRepository;

    // Données des postes PTME
    private static final List<PosteData> POSTES_DATA = List.of(
            // Postes rattachés à DGAPTME01
            new PosteData("DGAP0001", "DGAPTME01", "SOUS DIRECTEUR MOYENS GENERAUX ET RESSOURCES PTME", "BERTE AMAKO EPSE ADEYEMI", "DON GOSSAN SALOMON"),
            new PosteData("DEMP0001", "DGAPTME01", "DIRECTEUR DES ETUDES METHODES ET PROJETS", "ELIAKA ERNEST DIDIER", "DON GOSSAN SALOMON"),
            new PosteData("DGAP0002", "DGAPTME01", "SOUS DIRECTEUR EN CHARGE DU CONTROLE DE GESTION", "AKA ALHOUA DESIREE ANGE", "DON GOSSAN SALOMON"),
            new PosteData("DEMP0002", "DGAPTME01", "DIRECTEUR ADJOINT CHARGE DU MANAGEMENT DES PROJETS", "SABANA ABDOULAYE", "DON GOSSAN SALOMON"),
            new PosteData("DME0001", "DGAPTME01", "DIRECTEUR DES MOUVEMENTS D'ENERGIE AVEC RANG DE DIRECTEUR CENTRAL", "SYLLA DAOUDA", "DON GOSSAN"),
            new PosteData("DPE0001", "DGAPTME01", "DIRECTEUR DE LA PRODUCTION D'ELECTRICITE", "KOFFI KOUADIO EDOURD", "DON GOSSAN SALOMON"),
            
            // Postes rattachés à DCTET01
            new PosteData("DTT0001", "DCTET01", "DIRECTEUR TELECOMMUNICATION ET TELECONDUITE", "GNABRO TOTI LUCIEN", "GBEULY LEBATTO RENE"),
            new PosteData("DAOE0001", "DCTET01", "DIRECTEUR APPUI OPERATIONNEL TRANSPORT D'ENERGIE", "AISSI CHRISTEL FIDELE", "GBEULY LEBATTO RENE"),
            new PosteData("DEXT0001", "DCTET01", "DIRECTEUR EXPLOITATION DU TRANSPORT D'ENERGIE", "TOURE IBRAHIM", "GBEULY LEBATTO RENE"),
            
            // Postes rattachés à DAOE0001
            new PosteData("DAOT0007", "DAOE0001", "SOUS DIRECTEUR MAINTENANCE", "KOFFI KOUASSI THOMAS", "AISSI CHRISTEL"),
            new PosteData("DAOT0002", "DAOE0001", "SOUS DIRECTEUR ETUDES ET TRAVAUX DAOTEL", "KONE ALY", "AISSI CHRISTEL"),
            
            // Postes rattachés à DTT0001
            new PosteData("DAOT0025", "DTT0001", "SOUS DIRECTEUR TELECOMMUNICATION", "DJE BI LEZIE", "GNABRO TOTI LUCIEN"),
            new PosteData("DTT0002", "DTT0001", "SOUS DIRECTEUR TELECOMMUNICATION ET TELECONDUITE", "FOUA GOURI FRANCK MICHAEL", "GNABRO TOTI LUCIEN"),
            
            // Postes rattachés à DEXT0001
            new PosteData("DRTK0001", "DEXT0001", "DIRECTEUR REGIONAL DU TRANSPORT D'ENERGIE ET DE LA TELECOMMUNICATION KORHOGO", "AKPOUE ERICKSON", "TOURE IBRAHIM"),
            new PosteData("DRTE0001", "DEXT0001", "DIRECTEUR REGIONAL DU TRANSPORT D'ENERGIE ET DE LA TELECOMMUNICATION ABENGOUROU", "OBOUMOU KEVIN", "TOURE IBRAHIM"),
            new PosteData("DRTB0001", "DEXT0001", "DIRECTEUR REGIONAL DU TRANSPORT D'ENERGIE ET TELECOMMUNICATIONS BOUAKE", "SORO PEFOUNGODJOMON YACOUBA", "TOURE IBRAHIM"),
            new PosteData("DRTA0001", "DEXT0001", "DIRECTEUR REGIONAL DU TRANSPORT D'ENERGIE ET TELECOMMUNICATIONS ABIDJAN", "YAO KOUAME LENOIR", "TOURE IBRAHIM"),
            new PosteData("DRTS0001", "DEXT0001", "DIRECTEUR REGIONAL DU TRANSPORT D'ENERGIE ET DE LA TELECOMMUNICATION SOUBRE", "YEO YEKORIBE", "TOURE IBRAHIM"),
            new PosteData("DRTM0001", "DEXT0001", "DIRECTEUR REGIONAL DU TRANSPORT D'ENERGIE ET TELECOMMUNICATIONS MAN", "YAPI YAPO FABRICE", "TOURE IBRAHIM"),
            new PosteData("DEXT0011", "DEXT0001", "SOUS DIRECTEUR EXPLOITATION TRANSPORT", "AHOGO ARISTIDE-SIMON KOUAKOU", "TOURE IBRAHIM"),
            
            // Postes rattachés à DME0001
            new PosteData("DME0009", "DME0001", "SOUS DIRECTEUR EXPLOITATION DU SYSTEME ELECTRIQUE SDESE", "SANOGO SEYDOU", "SYLLA DADOUA"),
            new PosteData("DME0006", "DME0001", "SOUS DIRECTEUR MOYENS TECHNIQUES SDMT", "TRAORE KARNON ADELPHE", "SYLLA DADOUA"),
            new PosteData("DME0049", "DME0001", "DIRECTEUR ADJOINT DES MOUVEMENTS D'ENERGIE EN CHARGE DU DISP", "KOUASSI ADOU APPA JONAS", "SYLLA DADOUA"),
            new PosteData("DME0003", "DME0001", "SOUS DIRECTEUR ETUDES RETOUR D'EXPERIENCE SDERE", "DIE DECA KOUAKOU HONORAT", "SYLLA DADOUA"),
            
            // Postes rattachés à DPE0001
            new PosteData("DPE0053", "DPE0001", "DIRECTEUR ADJOINT DE LA PRODUCTION D'ELECTRICITE", "BROU APPIA BERNARD", "KOFFI KOUADIO EDOURD"),
            new PosteData("DPE0026", "DPE0001", "SOUS DIRECTEUR APPUI OPERATIONNEL SDAO", "ADOU OKONI GERARD-PHILIPPE", "KOFFI KOUADIO EDOURD"),
            new PosteData("DPE0023", "DPE0001", "SOUS DIRECTEUR INGENIERIE ET RETOUR D'EXPERIENCE", "LADJI KONE VACABA", "KOFFI KOUADIO EDOURD"),
            new PosteData("DPE0047", "DPE0001", "DIRECTEUR USINE HYDROELECTRIQUE TAABO", "DIABATE ADAMA", "KOFFI KOUADIO EDOURD"),
            new PosteData("DPE0033", "DPE0001", "DIRECTEUR DES USINES DE BUYO ET FAYE", "COMOE YAO GILBERT", "KOFFI KOUADIO EDOURD"),
            new PosteData("DPE0005", "DPE0001", "DIRECTEUR D'USINE HYDROELECTRIQUE AYAME", "YEO VALY", "KOFFI KOUADIO EDOURD"),
            new PosteData("DPE0030", "DPE0001", "DIRECTEUR D'USINE TURBINE A GAZ VRIDI 1", "KONE ALAMADOGO", "KOFFI KOUADIO EDOURD"),
            new PosteData("DPE0041", "DPE0001", "DIRECTEUR D'USINE HYDROELECTRIQUE KOSSOU", "DOUMBOUYA MOHAMADOU", "KOFFI KOUADIO EDOURD")
    );

    public PtmeJobBootstrapCommandLineRunner(JobJpaRepository jobJpaRepository,
                                              OrganizationJpaRepository organizationJpaRepository,
                                              GradeJpaRepository gradeJpaRepository) {
        this.jobJpaRepository = jobJpaRepository;
        this.organizationJpaRepository = organizationJpaRepository;
        this.gradeJpaRepository = gradeJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("------ PTME JOB BOOTSTRAP: Démarrage");

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

        System.out.println("------ PTME JOB BOOTSTRAP: Terminé");
    }

    private Map<String, GradeEntity> loadGrades() {
        Map<String, GradeEntity> cache = new HashMap<>();
        for (String gradeCode : List.of("DG", "DGA", "DC", "D", "DA", "SD", "DR", "AS")) {
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
        String libelleUpper = libelle.toUpperCase();
        
        // Déterminer le grade basé sur le libellé
        if (libelleUpper.contains("DIRECTEUR GENERAL ADJOINT") || libelleUpper.contains("DGA")) {
            return gradeCache.get("DGA");
        } else if (libelleUpper.contains("DIRECTEUR CENTRAL") || libelleUpper.contains("RANG DE DIRECTEUR CENTRAL")) {
            return gradeCache.get("DC");
        } else if (libelleUpper.contains("SOUS DIRECTEUR") || libelleUpper.contains("SOUS-DIRECTEUR")) {
            return gradeCache.get("SD");
        } else if (libelleUpper.contains("DIRECTEUR ADJOINT")) {
            return gradeCache.get("DA");
        } else if (libelleUpper.contains("DIRECTEUR REGIONAL") || libelleUpper.contains("DIRECTEUR USINE") || 
                   libelleUpper.contains("DIRECTION USINE") || libelleUpper.contains("D'USINE")) {
            return gradeCache.get("DR");
        } else if (libelleUpper.contains("DIRECTEUR")) {
            return gradeCache.get("D");
        }
        return gradeCache.get("SD"); // Par défaut
    }

    private void linkJobParents(Map<String, JobEntity> jobCache) {
        // Mapping des jobs enfants vers leurs parents basé sur le responsable
        // Le responsable du poste devient le poste parent
        Map<String, String> parentMapping = Map.ofEntries(
                // Postes sous DGAPTME01 - à lier au DGA-PTME si existe
                Map.entry("DGAP0001", "DGAPTME01_JOB"),
                Map.entry("DEMP0001", "DGAPTME01_JOB"),
                Map.entry("DGAP0002", "DGAPTME01_JOB"),
                Map.entry("DEMP0002", "DGAPTME01_JOB"),
                Map.entry("DME0001", "DGAPTME01_JOB"),
                Map.entry("DPE0001", "DGAPTME01_JOB"),
                
                // Postes sous DTT0001
                Map.entry("DAOT0025", "DTT0001"),
                Map.entry("DTT0002", "DTT0001"),
                
                // Postes sous DAOE0001
                Map.entry("DAOT0007", "DAOE0001"),
                Map.entry("DAOT0002", "DAOE0001"),
                
                // Postes sous DEXT0001
                Map.entry("DRTK0001", "DEXT0001"),
                Map.entry("DRTE0001", "DEXT0001"),
                Map.entry("DRTB0001", "DEXT0001"),
                Map.entry("DRTA0001", "DEXT0001"),
                Map.entry("DRTS0001", "DEXT0001"),
                Map.entry("DRTM0001", "DEXT0001"),
                Map.entry("DEXT0011", "DEXT0001"),
                
                // Postes sous DME0001
                Map.entry("DME0009", "DME0001"),
                Map.entry("DME0006", "DME0001"),
                Map.entry("DME0049", "DME0001"),
                Map.entry("DME0003", "DME0001"),
                
                // Postes sous DPE0001
                Map.entry("DPE0053", "DPE0001"),
                Map.entry("DPE0026", "DPE0001"),
                Map.entry("DPE0023", "DPE0001"),
                Map.entry("DPE0047", "DPE0001"),
                Map.entry("DPE0033", "DPE0001"),
                Map.entry("DPE0005", "DPE0001"),
                Map.entry("DPE0030", "DPE0001"),
                Map.entry("DPE0041", "DPE0001")
        );

        for (Map.Entry<String, String> entry : parentMapping.entrySet()) {
            String childCode = entry.getKey();
            String parentCode = entry.getValue();

            // Skip les références au job DGAPTME01 qui n'existe pas encore
            if (parentCode.equals("DGAPTME01_JOB")) {
                continue;
            }

            JobEntity child = jobCache.get(childCode);
            JobEntity parent = jobCache.get(parentCode);

            if (child != null && parent != null && child.getParent() == null) {
                child.setParent(parent);
                jobJpaRepository.save(child);
                System.out.println("------ Job " + childCode + " lié au parent " + parentCode);
            }
        }
    }

    // Record pour les données de poste (occupePar et responsable gardés pour référence future)
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private record PosteData(String code, String codeOrganisation, String libelle, String occupePar, String responsable) {}
}
