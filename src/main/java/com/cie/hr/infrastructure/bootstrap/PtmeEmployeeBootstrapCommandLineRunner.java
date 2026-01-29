package com.cie.hr.infrastructure.bootstrap;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.cie.hr.infrastructure.entity.EmployeeEntity;
import com.cie.hr.infrastructure.entity.JobEntity;
import com.cie.hr.infrastructure.entity.ProfileEntity;
import com.cie.hr.infrastructure.repository.EmployeeJpaRepository;
import com.cie.hr.infrastructure.repository.JobJpaRepository;
import com.cie.hr.infrastructure.repository.ProfileJpaRepository;
import com.fasterxml.uuid.Generators;

/**
 * Bootstrap pour les employés PTME (Production Transport Mouvements d'Énergie)
 * Exécuté après PtmeJobBootstrapCommandLineRunner (Order=20)
 * 
 * IMPORTANT: Ne publie PAS l'événement CreateEmployeeEvent
 * Les emails seront envoyés automatiquement au démarrage d'une campagne
 * ou via l'endpoint dédié /api/hr/employees/send-welcome-emails
 */
@Order(20)
@Component
public class PtmeEmployeeBootstrapCommandLineRunner implements CommandLineRunner {

    private final EmployeeJpaRepository employeeJpaRepository;
    private final ProfileJpaRepository profileJpaRepository;
    private final JobJpaRepository jobJpaRepository;

    // Données des employés PTME
    private static final List<EmployeData> EMPLOYES_DATA = List.of(
            new EmployeData("008306D", "DON", "GOSSAN SALOMON", "gdon@cie.ci"),
            new EmployeData("008240G", "BERETE", "AMAKO EPSE ADEYEMI", "aadeyemi@cie.ci"),
            new EmployeData("008391W", "ELIAKA", "ELIAKA ERNEST DIDIER", "deliaka@cie.ci"),
            new EmployeData("008430N", "GNABRO", "TOTI LUCIEN", "tgnabro@cie.ci"),
            new EmployeData("008604C", "AISSI", "CHRISTEL FIDELE", "caissi@cie.ci"),
            new EmployeData("008958M", "KOFFI", "KOUASSI THOMAS", "kthkoffi@cie.ci"),
            new EmployeData("008980L", "AKPOUE", "ERICKSON", "eakpoue@cie.ci"),
            new EmployeData("008984Q", "TOURE", "IBRAHIM", "itoure@cie.ci"),
            new EmployeData("020131G", "AKA", "ALHOUA DESIREE ANGE", "adaka@cie.ci"),
            new EmployeData("020237X", "DJE", "BI LEZIE", "lezibidje@cie.ci"),
            new EmployeData("020294J", "OBOUMOU", "KEVIN", "kevoboumou@cie.ci"),
            new EmployeData("020591G", "SABANA", "ABDOULAYE", "absabana@cie.ci"),
            new EmployeData("021213H", "KONE", "ALY", "alykone@cie.ci"),
            new EmployeData("021361T", "AHOGO", "ARISTIDE-SIMON KOUAKOU", "aahogo@cie.ci"),
            new EmployeData("021366Z", "FOUA", "GOURI FRANCK MICHAEL", "ffoua@cie.ci"),
            new EmployeData("021391B", "SORO", "PEFOUNGODJOMON YACOUBA", "pefsoro@cie.ci"),
            new EmployeData("021587P", "YAO", "KOUAME LENOIR", "lenyao@cie.ci"),
            new EmployeData("021676L", "YEO", "YEKORIBE", "yyeo@cie.ci"),
            new EmployeData("022951X", "YAPI", "YAPO FABRICE", "yoyapi@cie.ci"),
            new EmployeData("000278C", "SYLLA", "DAOUDA", "daouda.sylla@cie.ci"),
            new EmployeData("008751M", "SANOGO", "SEYDOU", "sesanogo@cie.ci"),
            new EmployeData("020262Z", "TRAORE", "KARNON ADELPHE", "adkatraore@cie.ci"),
            new EmployeData("020317J", "KOUASSI", "ADOU APPA JEAN JONAS", "jonkouassi@cie.ci"),
            new EmployeData("022231P", "DIE", "DECA KOUAKOU HONORAT", "dkdie@cie.ci"),
            new EmployeData("008415X", "KOFFI", "KOUADIO EDOUARD", "chkoffiko@cie.ci"),
            new EmployeData("008696C", "BROU", "APPIA BRNARD", "bappiabrou@cie.ci"),
            new EmployeData("008921X", "ADOU", "OKONI GERARD-PHILIPPE", "oadou@cie.ci"),
            new EmployeData("020296L", "LADJI", "KONE VACABA", "lvkone@cie.ci"),
            new EmployeData("020297M", "DIABATE", "ADAMA", "adadiabate@cie.ci"),
            new EmployeData("020300Q", "COMOE", "YAO GILBERT", "ygcomoe@cie.ci"),
            new EmployeData("020543E", "YEO", "VALY", "valyeo@cie.ci"),
            new EmployeData("021139C", "KONE", "ALAMADOGO", "alamkone@cie.ci"),
            new EmployeData("021399K", "DOUMBOUYA", "MOHAMADOU", "mdoumbouya@cie.ci")
    );

    // Mapping employé (NOM PRÉNOMS normalisé) -> code poste
    private static final Map<String, String> EMPLOYE_TO_JOB = Map.ofEntries(
            Map.entry("DON GOSSAN SALOMON", "DGAPTME01_JOB"), // Job racine à créer manuellement
            Map.entry("BERETE AMAKO EPSE ADEYEMI", "DGAP0001"),
            Map.entry("BERTE AMAKO EPSE ADEYEMI", "DGAP0001"), // Variante orthographique
            Map.entry("ELIAKA ELIAKA ERNEST DIDIER", "DEMP0001"),
            Map.entry("ELIAKA ERNEST DIDIER", "DEMP0001"),
            Map.entry("GNABRO TOTI LUCIEN", "DTT0001"),
            Map.entry("AISSI CHRISTEL FIDELE", "DAOE0001"),
            Map.entry("KOFFI KOUASSI THOMAS", "DAOT0007"),
            Map.entry("AKPOUE ERICKSON", "DRTK0001"),
            Map.entry("TOURE IBRAHIM", "DEXT0001"),
            Map.entry("AKA ALHOUA DESIREE ANGE", "DGAP0002"),
            Map.entry("DJE BI LEZIE", "DAOT0025"),
            Map.entry("OBOUMOU KEVIN", "DRTE0001"),
            Map.entry("SABANA ABDOULAYE", "DEMP0002"),
            Map.entry("KONE ALY", "DAOT0002"),
            Map.entry("AHOGO ARISTIDE-SIMON KOUAKOU", "DEXT0011"),
            Map.entry("FOUA GOURI FRANCK MICHAEL", "DTT0002"),
            Map.entry("SORO PEFOUNGODJOMON YACOUBA", "DRTB0001"),
            Map.entry("YAO KOUAME LENOIR", "DRTA0001"),
            Map.entry("YEO YEKORIBE", "DRTS0001"),
            Map.entry("YAPI YAPO FABRICE", "DRTM0001"),
            Map.entry("SYLLA DAOUDA", "DME0001"),
            Map.entry("SANOGO SEYDOU", "DME0009"),
            Map.entry("TRAORE KARNON ADELPHE", "DME0006"),
            Map.entry("KOUASSI ADOU APPA JEAN JONAS", "DME0049"),
            Map.entry("KOUASSI ADOU APPA JONAS", "DME0049"),
            Map.entry("DIE DECA KOUAKOU HONORAT", "DME0003"),
            Map.entry("KOFFI KOUADIO EDOUARD", "DPE0001"),
            Map.entry("KOFFI KOUADIO EDOURD", "DPE0001"),
            Map.entry("BROU APPIA BERNARD", "DPE0053"),
            Map.entry("BROU APPIA BRNARD", "DPE0053"), // Variante orthographique
            Map.entry("ADOU OKONI GERARD-PHILIPPE", "DPE0026"),
            Map.entry("LADJI KONE VACABA", "DPE0023"),
            Map.entry("DIABATE ADAMA", "DPE0047"),
            Map.entry("COMOE YAO GILBERT", "DPE0033"),
            Map.entry("YEO VALY", "DPE0005"),
            Map.entry("KONE ALAMADOGO", "DPE0030"),
            Map.entry("DOUMBOUYA MOHAMADOU", "DPE0041")
    );

    public PtmeEmployeeBootstrapCommandLineRunner(EmployeeJpaRepository employeeJpaRepository,
                                                   ProfileJpaRepository profileJpaRepository,
                                                   JobJpaRepository jobJpaRepository) {
        this.employeeJpaRepository = employeeJpaRepository;
        this.profileJpaRepository = profileJpaRepository;
        this.jobJpaRepository = jobJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("------ PTME EMPLOYEE BOOTSTRAP: Démarrage");

        // Récupérer le profil USER
        Optional<ProfileEntity> profileUSR = profileJpaRepository.findFirstByCode("USR");
        if (profileUSR.isEmpty()) {
            System.out.println("------ ERREUR: Profil USR non trouvé");
            return;
        }

        // Cache pour les employés créés
        Map<String, EmployeeEntity> employeeCache = new HashMap<>();

        // Créer les employés
        for (EmployeData data : EMPLOYES_DATA) {
            EmployeeEntity employee = createEmployee(data, profileUSR.get());
            if (employee != null) {
                String fullName = normalizeString(data.nom() + " " + data.prenoms());
                employeeCache.put(fullName, employee);
            }
        }

        // Lier les employés à leurs postes
        linkEmployeesToJobs(employeeCache);

        System.out.println("------ PTME EMPLOYEE BOOTSTRAP: Terminé");
        System.out.println("------ INFO: Les emails d'identifiants seront envoyés au prochain démarrage de campagne.");
    }

    private EmployeeEntity createEmployee(EmployeData data, ProfileEntity profile) {
        // Vérifier si l'employé existe déjà (par matricule)
        Optional<EmployeeEntity> existing = employeeJpaRepository.findFirstByEmployeeNumber(data.matricule());

        if (existing.isPresent()) {
            System.out.println("------ Employé " + data.matricule() + " (" + data.nom() + " " + data.prenoms() + ") existe déjà");
            return existing.get();
        }

        // Créer l'employé
        EmployeeEntity employee = EmployeeEntity.builder()
                .employeeNumber(data.matricule())
                .lastname(data.nom())
                .firstname(data.prenoms())
                .email(data.email())
                .profile(profile)
                .active(true)
                .isNotLocked(true)
                .isFirstConnect(true)
                .sendAccountIdEmail(false) // IMPORTANT: Email non envoyé - sera envoyé au démarrage de campagne
                .build();
        employee.setId(Generators.timeBasedEpochGenerator().generate());

        employeeJpaRepository.save(employee);
        System.out.println("------ Employé " + data.matricule() + " créé: " + data.nom() + " " + data.prenoms());

        // NOTE: Pas de publication de CreateEmployeeEvent ici
        // L'email sera envoyé automatiquement au démarrage de campagne (via AsyncEmailBatchService)

        return employee;
    }

    private void linkEmployeesToJobs(Map<String, EmployeeEntity> employeeCache) {
        for (Map.Entry<String, String> entry : EMPLOYE_TO_JOB.entrySet()) {
            String employeeName = entry.getKey();
            String jobCode = entry.getValue();

            // Skip les références au job DGAPTME01 qui n'existe pas encore
            if (jobCode.equals("DGAPTME01_JOB")) {
                continue;
            }

            // Trouver l'employé par correspondance de nom
            EmployeeEntity employee = findEmployeeByNameMatch(employeeName, employeeCache);

            if (employee == null) {
                System.out.println("------ ATTENTION: Employé non trouvé pour " + employeeName);
                continue;
            }

            // Trouver le job
            Optional<JobEntity> job = jobJpaRepository.findByCode(jobCode);
            if (job.isEmpty()) {
                System.out.println("------ ATTENTION: Job " + jobCode + " non trouvé pour " + employeeName);
                continue;
            }

            // Vérifier si le job n'est pas déjà occupé
            JobEntity jobEntity = job.get();
            if (jobEntity.getEmployee() != null) {
                System.out.println("------ Job " + jobCode + " déjà occupé par " + 
                        jobEntity.getEmployee().getLastname() + " " + jobEntity.getEmployee().getFirstname());
                continue;
            }

            // Vérifier si l'employé n'est pas déjà assigné à un autre job
            Optional<JobEntity> existingJob = jobJpaRepository.findByEmployeeId(employee.getId());
            if (existingJob.isPresent()) {
                System.out.println("------ Employé " + employee.getLastname() + " " + employee.getFirstname() + 
                        " déjà affecté au poste " + existingJob.get().getCode());
                continue;
            }

            // Lier l'employé au job
            jobEntity.setEmployee(employee);
            jobJpaRepository.save(jobEntity);
            System.out.println("------ Employé " + employee.getLastname() + " " + employee.getFirstname() + 
                    " affecté au poste " + jobCode);
        }
    }

    private EmployeeEntity findEmployeeByNameMatch(String searchName, Map<String, EmployeeEntity> cache) {
        String normalizedSearch = normalizeString(searchName);

        // Recherche exacte d'abord
        for (Map.Entry<String, EmployeeEntity> entry : cache.entrySet()) {
            if (normalizedSearch.equals(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Recherche par sous-chaîne si pas de correspondance exacte
        for (Map.Entry<String, EmployeeEntity> entry : cache.entrySet()) {
            String cachedName = entry.getKey();
            if (normalizedSearch.contains(cachedName) || cachedName.contains(normalizedSearch)) {
                return entry.getValue();
            }
        }

        // Recherche par mots clés (nom de famille)
        String[] searchWords = normalizedSearch.split("\\s+");
        for (Map.Entry<String, EmployeeEntity> entry : cache.entrySet()) {
            String cachedName = entry.getKey();
            // Si le premier mot (nom) correspond
            if (cachedName.startsWith(searchWords[0] + " ")) {
                // Vérifier si au moins 2 mots correspondent
                int matchCount = 0;
                for (String word : searchWords) {
                    if (cachedName.contains(word)) {
                        matchCount++;
                    }
                }
                if (matchCount >= 2) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    /**
     * Normalise une chaîne: majuscules, sans accents, espaces uniques
     */
    private String normalizeString(String input) {
        if (input == null) return "";
        
        // Convertir en majuscules
        String result = input.toUpperCase();
        
        // Supprimer les accents
        result = Normalizer.normalize(result, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        result = pattern.matcher(result).replaceAll("");
        
        // Normaliser les espaces
        result = result.replaceAll("\\s+", " ").trim();
        
        return result;
    }

    // Record pour les données d'employé
    private record EmployeData(String matricule, String nom, String prenoms, String email) {}
}
