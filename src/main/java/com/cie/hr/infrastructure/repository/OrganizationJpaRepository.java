package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.OrganizationEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
@Repository
public interface OrganizationJpaRepository extends JpaRepository<OrganizationEntity, UUID> {
    List<OrganizationEntity> findByNameOrCode(String name, String code);

    List<OrganizationEntity> findByTypeName(String name);

    Optional<OrganizationEntity> findFirstByCode(String code);

    List<OrganizationEntity> findByParentId(UUID parent);

    List<OrganizationEntity> findByParentIdAndTypeNameIn(UUID id, List<String> codes);

    List<OrganizationEntity> findByDeletedFalseOrderByCreatedDesc();

    List<OrganizationEntity> findAllByTypeGradeCodeAndDeletedFalse(String gradeCode);

    @Query(value = """
            WITH RECURSIVE OrganizationHierarchy AS (
                SELECT * FROM organizations WHERE id = :parentId
                UNION ALL
                SELECT org.* FROM organizations org
                INNER JOIN OrganizationHierarchy h ON org.parent_id = h.id
            )
            SELECT * FROM OrganizationHierarchy""", nativeQuery = true)
    List<OrganizationEntity> findChildrenAndGrandChildren(@Param("parentId") UUID parentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM OrganizationEntity e WHERE e.id = :id")
    Optional<OrganizationEntity> findByIdForWrite(@Param("id") UUID id);
}
