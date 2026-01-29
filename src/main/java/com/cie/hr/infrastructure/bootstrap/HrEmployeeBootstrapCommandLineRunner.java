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
 * Bootstrap pour les employés HR (Direction Commerciale et Opérations)
 * Exécuté après HrJobBootstrapCommandLineRunner (Order=17)
 * 
 * IMPORTANT: Ne publie PAS l'événement CreateEmployeeEvent
 * Les emails seront envoyés via l'endpoint dédié /api/hr/employees/send-welcome-emails
 */
@Order(17)
@Component
public class HrEmployeeBootstrapCommandLineRunner implements CommandLineRunner {

    private final EmployeeJpaRepository employeeJpaRepository;
    private final ProfileJpaRepository profileJpaRepository;
    private final JobJpaRepository jobJpaRepository;

    // Données des employés HR
    private static final List<EmployeData> EMPLOYES_DATA = List.of(
            new EmployeData("020122X", "DOH", "MARIUS", "mariusdoh@cie.ci"),
            new EmployeData("008400F", "SEKONGO", "PEHA", "psekongo@cie.ci"),
            new EmployeData("020118S", "BAMBA", "YACOUBA", "bamyacouba@cie.ci"),
            new EmployeData("020123Y", "KOUASSI KONAN BLE N'GUESSAN DESIREE DOMI", "Epse YAO", "deskouassi@cie.ci"),
            new EmployeData("020127C", "SIAHE", "NEAMIN JEROME", "jneasiahe@cie.ci"),
            new EmployeData("021263M", "COULIBALY", "FONA IBRAHIMA", "ficoulibaly@cie.ci"),
            new EmployeData("021265P", "N'GORAN", "KOUASSI YANNICK", "kyngoran@cie.ci"),
            new EmployeData("021266Q", "KOUAME", "KOUAKOU AUGUSTE", "aukkouame@cie.ci"),
            new EmployeData("021267R", "DIRABOUT", "ABLE GUY BERENGER", "adirabout@cie.ci"),
            new EmployeData("021408V", "KOUASSI", "EPSE AHOUEGNY ANNICK ROSINE", "rokouassi@cie.ci"),
            new EmployeData("022434K", "KOUAME", "ATTIMAN YVES", "attikouame@cie.ci"),
            new EmployeData("022435L", "EHOUMAN", "BRUCE FERNAND", "behouman@cie.ci"),
            new EmployeData("022940K", "EDI", "STEPHANE", "sedi@cie.ci"),
            new EmployeData("022942M", "OUATTARA", "POGADJOUFOUGOU", "pouattara@cie.ci"),
            new EmployeData("023027E", "DIDO", "GUY MARC WILFRIED", "gmdido@cie.ci"),
            new EmployeData("023028F", "LOUALOU", "LOUA MARIUS", "mloualou@cie.ci"),
            new EmployeData("023035N", "OUATTARA", "KINIDINNIN RAISSA EPSE KOUA", "kinouattara@cie.ci"),
            new EmployeData("023092A", "OUEHI", "SCEANSIEHI FABIEN RODRIGUE", "souehi@cie.ci"),
            new EmployeData("023116B", "GAUDJI", "DJAHI ARMEL", "dagaudji@cie.ci")
    );

    // Mapping employé (NOM PRÉNOMS normalisé) -> code poste
    private static final Map<String, String> EMPLOYE_TO_JOB = Map.ofEntries(
            Map.entry("DOH MARIUS", "DCCMO01"),
            Map.entry("SEKONGO PEHA", "DCOA0001"),
            Map.entry("BAMBA YACOUBA", "DCOI0001"),
            Map.entry("KOUASSI KONAN BLE N'GUESSAN DESIREE DOMINIQUE EPSE YAO", "DRAS0001"),
            Map.entry("KOUAME KOUAKOU AUGUSTE", "DRYOP0001"),
            Map.entry("KOUASSI ANNICK ROSINE EPSE AHOUEGNY", "DRAN0001"),
            Map.entry("COULIBALY FONA IBRAHIMA", "DRABO0001"),
            Map.entry("DIRABOUT ABLE GUY BERENGER", "DCOA0001_AST"),
            Map.entry("OUATTARA KINIDINNIN RAISSA EPSE KOUA", "DRE0001"),
            Map.entry("DIDO GUY MARC WILFRIED", "DRSO0001"),
            Map.entry("EDI STEPHANE", "DRBC0001"),
            Map.entry("N'GORAN KOUASSI YANNICK", "DRSE0001"),
            Map.entry("EHOUMAN BRUCE FERNAND", "DRLO0001"),
            Map.entry("OUEHI SCEANSIEHI FABIEN RODRIGUE", "DRC0001"),
            Map.entry("OUATTARA POGADJOUFOUGOU", "DRCO0001"),
            Map.entry("SIAHE NEAMIN JEROME", "DRN0001"),
            Map.entry("LOUALOU LOUA MARIUS", "DRO0001"),
            Map.entry("KOUAME ATTIMAN YVES", "DRCS0001"),
            Map.entry("GAUDJI DJAHI ARMEL", "DCOI0004")
    );

    public HrEmployeeBootstrapCommandLineRunner(EmployeeJpaRepository employeeJpaRepository,
                                                 ProfileJpaRepository profileJpaRepository,
                                                 JobJpaRepository jobJpaRepository) {
        this.employeeJpaRepository = employeeJpaRepository;
        this.profileJpaRepository = profileJpaRepository;
        this.jobJpaRepository = jobJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("------ HR EMPLOYEE BOOTSTRAP: Démarrage");

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

        System.out.println("------ HR EMPLOYEE BOOTSTRAP: Terminé");
        System.out.println("------ INFO: Les emails ne sont PAS envoyés automatiquement.");
        System.out.println("------ INFO: Utilisez POST /api/hr/employees/send-welcome-emails pour déclencher l'envoi.");
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
                .sendAccountIdEmail(false) // Marquer comme email non envoyé
                .build();
        employee.setId(Generators.timeBasedEpochGenerator().generate());

        employeeJpaRepository.save(employee);
        System.out.println("------ Employé " + data.matricule() + " créé: " + data.nom() + " " + data.prenoms());

        // NOTE: Pas de publication de CreateEmployeeEvent ici
        // L'email sera envoyé via l'endpoint dédié

        return employee;
    }

    private void linkEmployeesToJobs(Map<String, EmployeeEntity> employeeCache) {
        for (Map.Entry<String, String> entry : EMPLOYE_TO_JOB.entrySet()) {
            String employeeName = entry.getKey();
            String jobCode = entry.getValue();

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
