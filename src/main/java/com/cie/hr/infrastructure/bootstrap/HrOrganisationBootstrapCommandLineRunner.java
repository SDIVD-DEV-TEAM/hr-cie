package com.cie.hr.infrastructure.bootstrap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.cie.hr.infrastructure.entity.OrganizationEntity;
import com.cie.hr.infrastructure.entity.OrganizationTypeEntity;
import com.cie.hr.infrastructure.repository.OrganizationJpaRepository;
import com.cie.hr.infrastructure.repository.OrganizationTypeJpaRepository;
import com.fasterxml.uuid.Generators;

/**
 * Bootstrap pour les organisations HR (Direction Commerciale et Opérations)
 * Exécuté après les bootstraps existants (Order=15)
 * Fait un upsert: crée si n'existe pas, met à jour sinon
 */
@Order(15)
@Component
public class HrOrganisationBootstrapCommandLineRunner implements CommandLineRunner {

    private final OrganizationJpaRepository organizationJpaRepository;
    private final OrganizationTypeJpaRepository organizationTypeJpaRepository;

    // Mapping des types d'organisation vers les codes existants
    private static final Map<String, String> TYPE_CODE_MAPPING = Map.of(
            "DIRECTION CENTRALE", "2",      // Direction centrale
            "DIRECTION", "3",               // Direction
            "DIRECTION REGIONALE", "6",     // Direction Régionale
            "SOUS-DIRECTION", "5"           // Sous Direction
    );

    // Données des organisations HR
    private static final List<OrganisationData> ORGANISATION_DATA = List.of(
            new OrganisationData("DCCMO01", "DIRECTION CENTRALE COMMERCIALE MARKETING ET OPERATIONS", "DIRECTION CENTRALE", "DGADC01"),
            new OrganisationData("DCOA0001", "DIRECTION COMMERCIALE ET OPERATIONS ABIDJAN", "DIRECTION", "DCCMO01"),
            new OrganisationData("DCOI0001", "DIRECTION COMMERCIALE ET OPERATIONS INTERIEUR", "DIRECTION", "DCCMO01"),
            new OrganisationData("DRAS0001", "DIRECTION REGIONALE ABIDJAN SUD", "DIRECTION REGIONALE", "DCOA0001"),
            new OrganisationData("DRYOP0001", "DIRECTION REGIONALE YOPOUGON", "DIRECTION REGIONALE", "DCOA0001"),
            new OrganisationData("DRAN0001", "DIRECTION REGIONALE ABIDJAN NORD", "DIRECTION REGIONALE", "DCOA0001"),
            new OrganisationData("DRABO0001", "DIRECTION REGIONALE ABOBO", "DIRECTION REGIONALE", "DCOA0001"),
            new OrganisationData("DRE0001", "DIRECTION REGIONALE EST", "DIRECTION REGIONALE", "DCOI0001"),
            new OrganisationData("DRSO0001", "DIRECTION REGIONALE SUD OUEST", "DIRECTION REGIONALE", "DCOI0001"),
            new OrganisationData("DRBC0001", "DIRECTION REGIONALE BASSE CÔTE", "DIRECTION REGIONALE", "DCOI0001"),
            new OrganisationData("DRSE0001", "DIRECTION REGIONALE SUD EST", "DIRECTION REGIONALE", "DCOI0001"),
            new OrganisationData("DRLO0001", "DIRECTION REGIONALE LITTORAL OUEST", "DIRECTION REGIONALE", "DCOI0001"),
            new OrganisationData("DRC0001", "DIRECTION REGIONALE CENTRE", "DIRECTION REGIONALE", "DCOI0001"),
            new OrganisationData("DRCO0001", "DIRECTION REGIONALE CENTRE OUEST", "DIRECTION REGIONALE", "DCOI0001"),
            new OrganisationData("DRN0001", "DIRECTION REGIONALE NORD", "DIRECTION REGIONALE", "DCOI0001"),
            new OrganisationData("DRO0001", "DIRECTION REGIONALE OUEST", "DIRECTION REGIONALE", "DCOI0001"),
            new OrganisationData("DRCS0001", "DIRECTION REGIONALE CENTRE SUD", "DIRECTION REGIONALE", "DCOI0001"),
            new OrganisationData("DCOI0004", "SOUS-DIRECTION SUIVI EVALUATION PEPT", "SOUS-DIRECTION", "DCOI0001")
    );

    public HrOrganisationBootstrapCommandLineRunner(OrganizationJpaRepository organizationJpaRepository,
                                                     OrganizationTypeJpaRepository organizationTypeJpaRepository) {
        this.organizationJpaRepository = organizationJpaRepository;
        this.organizationTypeJpaRepository = organizationTypeJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("------ HR ORGANISATION BOOTSTRAP: Démarrage");

        // Cache pour les organisations créées/trouvées
        Map<String, OrganizationEntity> organisationCache = new HashMap<>();

        // Charger les types d'organisation
        Map<String, OrganizationTypeEntity> typeCache = loadOrganizationTypes();

        // Créer l'organisation parent DGADC01 si elle n'existe pas
        ensureParentOrganizationExists(typeCache, organisationCache);

        // Traiter chaque organisation
        for (OrganisationData data : ORGANISATION_DATA) {
            upsertOrganisation(data, typeCache, organisationCache);
        }

        System.out.println("------ HR ORGANISATION BOOTSTRAP: Terminé");
    }

    private Map<String, OrganizationTypeEntity> loadOrganizationTypes() {
        Map<String, OrganizationTypeEntity> cache = new HashMap<>();
        for (String typeCode : TYPE_CODE_MAPPING.values()) {
            organizationTypeJpaRepository.findByCode(typeCode)
                    .ifPresent(type -> cache.put(typeCode, type));
        }
        return cache;
    }

    private void ensureParentOrganizationExists(Map<String, OrganizationTypeEntity> typeCache,
                                                 Map<String, OrganizationEntity> organisationCache) {
        String parentCode = "DGADC01";
        Optional<OrganizationEntity> existing = organizationJpaRepository.findFirstByCode(parentCode);

        if (existing.isPresent()) {
            organisationCache.put(parentCode, existing.get());
            System.out.println("------ Organisation parent " + parentCode + " existe déjà");
        } else {
            // Créer DGADC01 comme direction centrale sous le Pôle DC
            var poleDC = organizationJpaRepository.findFirstByCode("Pôle DC");
            OrganizationTypeEntity dcType = typeCache.get("2"); // Direction centrale

            if (dcType != null) {
                OrganizationEntity dgadc = OrganizationEntity.builder()
                        .code(parentCode)
                        .name("DIRECTION GENERALE ADJOINTE DISTRIBUTION ET COMMERCIALISATION")
                        .type(dcType)
                        .parent(poleDC.orElse(null))
                        .build();
                dgadc.setId(Generators.timeBasedEpochGenerator().generate());
                organizationJpaRepository.save(dgadc);
                organisationCache.put(parentCode, dgadc);
                System.out.println("------ Organisation parent " + parentCode + " créée");
            }
        }
    }

    private void upsertOrganisation(OrganisationData data,
                                    Map<String, OrganizationTypeEntity> typeCache,
                                    Map<String, OrganizationEntity> organisationCache) {
        // Récupérer le type
        String typeCode = TYPE_CODE_MAPPING.get(data.type());
        OrganizationTypeEntity type = typeCache.get(typeCode);

        if (type == null) {
            System.out.println("------ ERREUR: Type d'organisation non trouvé pour " + data.type());
            return;
        }

        // Récupérer le parent
        OrganizationEntity parent = resolveParent(data.codeParent(), organisationCache);

        // Vérifier si l'organisation existe
        Optional<OrganizationEntity> existing = organizationJpaRepository.findFirstByCode(data.code());

        if (existing.isPresent()) {
            // UPDATE: mettre à jour le nom et le type si nécessaire
            OrganizationEntity org = existing.get();
            boolean updated = false;

            if (!org.getName().equals(data.libelle())) {
                org.setName(data.libelle());
                updated = true;
            }
            if (!org.getType().equals(type)) {
                org.setType(type);
                updated = true;
            }
            if (parent != null && (org.getParent() == null || !org.getParent().getCode().equals(data.codeParent()))) {
                org.setParent(parent);
                updated = true;
            }

            if (updated) {
                organizationJpaRepository.save(org);
                System.out.println("------ Organisation " + data.code() + " mise à jour");
            } else {
                System.out.println("------ Organisation " + data.code() + " existe déjà (pas de changement)");
            }
            organisationCache.put(data.code(), org);
        } else {
            // CREATE: créer la nouvelle organisation
            OrganizationEntity newOrg = OrganizationEntity.builder()
                    .code(data.code())
                    .name(data.libelle())
                    .type(type)
                    .parent(parent)
                    .build();
            newOrg.setId(Generators.timeBasedEpochGenerator().generate());
            organizationJpaRepository.save(newOrg);
            organisationCache.put(data.code(), newOrg);
            System.out.println("------ Organisation " + data.code() + " créée");
        }
    }

    private OrganizationEntity resolveParent(String codeParent, Map<String, OrganizationEntity> cache) {
        // D'abord chercher dans le cache
        if (cache.containsKey(codeParent)) {
            return cache.get(codeParent);
        }
        // Sinon chercher en base
        Optional<OrganizationEntity> parent = organizationJpaRepository.findFirstByCode(codeParent);
        parent.ifPresent(org -> cache.put(codeParent, org));
        return parent.orElse(null);
    }

    // Record pour les données d'organisation
    private record OrganisationData(String code, String libelle, String type, String codeParent) {}
}
