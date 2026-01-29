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
 * Bootstrap pour les organisations PTME (Production Transport Mouvements d'Énergie)
 * Exécuté après HrEmployeeBootstrapCommandLineRunner (Order=18)
 * Fait un upsert: crée si n'existe pas, met à jour sinon
 */
@Order(18)
@Component
public class PtmeOrganisationBootstrapCommandLineRunner implements CommandLineRunner {

    private final OrganizationJpaRepository organizationJpaRepository;
    private final OrganizationTypeJpaRepository organizationTypeJpaRepository;

    // Mapping des types d'organisation vers les codes existants
    private static final Map<String, String> TYPE_CODE_MAPPING = Map.of(
            "DIRECTION CENTRALE", "2",
            "DIRECTION", "3",
            "DIRECTION USINE", "3",        // Direction usine = Direction
            "DIRECTION REGIONALE", "6",
            "SOUS DIRECTION", "5",
            "SOUS-DIRECTION", "5"
    );

    // Données des organisations PTME
    private static final List<OrganisationData> ORGANISATION_DATA = List.of(
            // Organisations rattachées à DGAPTME01
            new OrganisationData("DGAP0001", "SOUS DIRECTION MOYENS GENERAUX ET RESSOURCES PTME", "SOUS DIRECTION", "DGAPTME01"),
            new OrganisationData("DEMP0001", "DIRECTION DES ETUDES METHODES ET PROJETS", "DIRECTION", "DGAPTME01"),
            new OrganisationData("DGAP0002", "SOUS DIRECTION EN CHARGE DU CONTROLE DE GESTION", "SOUS DIRECTION", "DGAPTME01"),
            new OrganisationData("DEMP0002", "DIRECTION ADJOINT CHARGE DU MANAGEMENT DES PROJETS", "DIRECTION", "DGAPTME01"),
            new OrganisationData("DME0001", "DIRECTION DES MOUVEMENTS D'ENERGIE AVEC RANG DE DIRECTEUR CENTRAL", "DIRECTION", "DGAPTME01"),
            new OrganisationData("DPE0001", "DIRECTION DE LA PRODUCTION D'ELECTRICITE", "DIRECTION", "DGAPTME01"),
            
            // Organisations rattachées à DCTET01 (Transport d'Énergie)
            new OrganisationData("DTT0001", "DIRECTION TELECOMMUNICATION ET TELECONDUITE", "DIRECTION", "DCTET01"),
            new OrganisationData("DAOE0001", "DIRECTION APPUI OPERATIONNEL TRANSPORT D'ENERGIE", "DIRECTION", "DCTET01"),
            new OrganisationData("DEXT0001", "DIRECTION EXPLOITATION DU TRANSPORT D'ENERGIE", "DIRECTION", "DCTET01"),
            
            // Sous-directions de DAOE0001
            new OrganisationData("DAOT0007", "SOUS DIRECTION MAINTENANCE", "SOUS DIRECTION", "DAOE0001"),
            new OrganisationData("DAOT0002", "SOUS DIRECTION ETUDES ET TRAVAUX DAOTEL", "SOUS DIRECTION", "DAOE0001"),
            
            // Sous-directions de DTT0001
            new OrganisationData("DAOT0025", "SOUS DIRECTION TELECOMMUNICATION", "SOUS DIRECTION", "DTT0001"),
            new OrganisationData("DTT0002", "SOUS DIRECTION TELECOMMUNICATION ET TELECONDUITE", "SOUS DIRECTION", "DTT0001"),
            
            // Directions régionales de DEXT0001
            new OrganisationData("DRTK0001", "DIRECTION REGIONAL DU TRANSPORT D'ENERGIE ET DE LA TELECOMMUNICATION KORHOGO", "DIRECTION REGIONALE", "DEXT0001"),
            new OrganisationData("DRTE0001", "DIRECTION REGIONAL DU TRANSPORT D'ENERGIE ET DE LA TELECOMMUNICATION ABENGOUROU", "DIRECTION REGIONALE", "DEXT0001"),
            new OrganisationData("DRTB0001", "DIRECTION REGIONAL DU TRANSPORT D'ENERGIE ET TELECOMMUNICATIONS BOUAKE", "DIRECTION REGIONALE", "DEXT0001"),
            new OrganisationData("DRTA0001", "DIRECTION REGIONAL DU TRANSPORT D'ENERGIE ET TELECOMMUNICATIONS ABIDJAN", "DIRECTION REGIONALE", "DEXT0001"),
            new OrganisationData("DRTS0001", "DIRECTION REGIONAL DU TRANSPORT D'ENERGIE ET DE LA TELECOMMUNICATION SOUBRE", "DIRECTION REGIONALE", "DEXT0001"),
            new OrganisationData("DRTM0001", "DIRECTION REGIONAL DU TRANSPORT D'ENERGIE ET TELECOMMUNICATIONS MAN", "DIRECTION REGIONALE", "DEXT0001"),
            new OrganisationData("DEXT0011", "SOUS DIRECTION EXPLOITATION TRANSPORT", "SOUS DIRECTION", "DEXT0001"),
            
            // Sous-directions de DME0001 (Mouvements d'Énergie)
            new OrganisationData("DME0009", "SOUS DIRECTION EXPLOITATION DU SYSTEME ELECTRIQUE SDESE", "SOUS DIRECTION", "DME0001"),
            new OrganisationData("DME0006", "SOUS DIRECTION MOYENS TECHNIQUES SDMT", "SOUS DIRECTION", "DME0001"),
            new OrganisationData("DME0049", "DIRECTION ADJOINTE DES MOUVEMENTS D'ENERGIE EN CHARGE DU DISPATCHING", "DIRECTION", "DME0001"),
            new OrganisationData("DME0003", "SOUS DIRECTEUR ETUDES RETOUR D'EXPERIENCE SDERE", "SOUS DIRECTION", "DME0001"),
            
            // Sous-directions et usines de DPE0001 (Production d'Électricité)
            new OrganisationData("DPE0053", "DIRECTION ADJOINT DE LA PRODUCTION D'ELECTRICITE", "DIRECTION", "DPE0001"),
            new OrganisationData("DPE0026", "SOUS DIRECTION APPUI OPERATIONNEL SDAO", "SOUS DIRECTION", "DPE0001"),
            new OrganisationData("DPE0023", "SOUS DIRECTION INGENIERIE ET RETOUR D'EXPERIENCE", "SOUS DIRECTION", "DPE0001"),
            new OrganisationData("DPE0047", "DIRECTION USINE HYDROELECTRIQUE TAABO", "DIRECTION USINE", "DPE0001"),
            new OrganisationData("DPE0033", "DIRECTION DES USINES DE BUYO ET FAYE", "DIRECTION USINE", "DPE0001"),
            new OrganisationData("DPE0005", "DIRECTION D'USINE HYDROELECTRIQUE AYAME", "DIRECTION USINE", "DPE0001"),
            new OrganisationData("DPE0030", "DIRECTION D'USINE TURBINE A GAZ VRIDI 1", "DIRECTION USINE", "DPE0001"),
            new OrganisationData("DPE0041", "DIRECTION D'USINE HYDROELECTRIQUE KOSSOU", "DIRECTION USINE", "DPE0001")
    );

    public PtmeOrganisationBootstrapCommandLineRunner(OrganizationJpaRepository organizationJpaRepository,
                                                       OrganizationTypeJpaRepository organizationTypeJpaRepository) {
        this.organizationJpaRepository = organizationJpaRepository;
        this.organizationTypeJpaRepository = organizationTypeJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("------ PTME ORGANISATION BOOTSTRAP: Démarrage");

        // Cache pour les organisations créées/trouvées
        Map<String, OrganizationEntity> organisationCache = new HashMap<>();

        // Charger les types d'organisation
        Map<String, OrganizationTypeEntity> typeCache = loadOrganizationTypes();

        // Créer les organisations parentes si elles n'existent pas
        ensureParentOrganizationsExist(typeCache, organisationCache);

        // Traiter chaque organisation
        for (OrganisationData data : ORGANISATION_DATA) {
            upsertOrganisation(data, typeCache, organisationCache);
        }

        System.out.println("------ PTME ORGANISATION BOOTSTRAP: Terminé");
    }

    private Map<String, OrganizationTypeEntity> loadOrganizationTypes() {
        Map<String, OrganizationTypeEntity> cache = new HashMap<>();
        for (String typeCode : TYPE_CODE_MAPPING.values()) {
            organizationTypeJpaRepository.findByCode(typeCode)
                    .ifPresent(type -> cache.put(typeCode, type));
        }
        return cache;
    }

    private void ensureParentOrganizationsExist(Map<String, OrganizationTypeEntity> typeCache,
                                                 Map<String, OrganizationEntity> organisationCache) {
        // DGAPTME01 - DGA Production Transport Mouvements d'Énergie
        ensureOrganizationExists("DGAPTME01", "DIRECTION GENERALE ADJOINTE PRODUCTION TRANSPORT MOUVEMENTS D'ENERGIE",
                "2", null, typeCache, organisationCache);

        // DCTET01 - Direction Centrale Transport d'Énergie et Télécommunication
        ensureOrganizationExists("DCTET01", "DIRECTION CENTRALE TRANSPORT D'ENERGIE ET TELECOMMUNICATION",
                "2", "DGAPTME01", typeCache, organisationCache);
    }

    private void ensureOrganizationExists(String code, String name, String typeCode, String parentCode,
                                           Map<String, OrganizationTypeEntity> typeCache,
                                           Map<String, OrganizationEntity> organisationCache) {
        Optional<OrganizationEntity> existing = organizationJpaRepository.findFirstByCode(code);

        if (existing.isPresent()) {
            organisationCache.put(code, existing.get());
            System.out.println("------ Organisation parent " + code + " existe déjà");
        } else {
            OrganizationTypeEntity type = typeCache.get(typeCode);
            OrganizationEntity parent = parentCode != null ? resolveParent(parentCode, organisationCache) : null;

            if (type != null) {
                OrganizationEntity newOrg = OrganizationEntity.builder()
                        .code(code)
                        .name(name)
                        .type(type)
                        .parent(parent)
                        .build();
                newOrg.setId(Generators.timeBasedEpochGenerator().generate());
                organizationJpaRepository.save(newOrg);
                organisationCache.put(code, newOrg);
                System.out.println("------ Organisation parent " + code + " créée");
            }
        }
    }

    private void upsertOrganisation(OrganisationData data,
                                    Map<String, OrganizationTypeEntity> typeCache,
                                    Map<String, OrganizationEntity> organisationCache) {
        // Récupérer le type
        String typeCode = TYPE_CODE_MAPPING.get(data.type());
        if (typeCode == null) {
            System.out.println("------ ERREUR: Type d'organisation non mappé: " + data.type());
            return;
        }
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
