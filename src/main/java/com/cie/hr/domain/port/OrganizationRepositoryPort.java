package com.cie.hr.domain.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.cie.hr.domain.entity.Job;
import com.cie.hr.domain.entity.Organization;

public interface OrganizationRepositoryPort extends AbstractRepository<Organization, UUID> {

    boolean checkNameOrCodeAlreadyExists(String name, String code);

    Job findChiefJob(UUID organizationId);

    /**
     * Trouve le manager pour un poste en remontant la hiérarchie organisationnelle.
     * Le manager est le chef de l'organisation parente qui a un employé assigné.
     * Si l'organisation parente n'a pas de chef avec employé, remonte au niveau supérieur.
     * 
     * @param organizationId L'ID de l'organisation du poste
     * @param excludeJobId L'ID du poste à exclure (pour éviter qu'un poste soit son propre manager)
     * @return Le Job du manager ou null si aucun trouvé
     */
    Job findManagerForJob(UUID organizationId, UUID excludeJobId);

    List<Organization> findByParentId(UUID parentId);

    Optional<Organization> findByCode(String code);
}
