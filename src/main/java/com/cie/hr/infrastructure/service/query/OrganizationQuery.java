package com.cie.hr.infrastructure.service.query;

import com.cie.hr.infrastructure.entity.EmployeeEntity;
import com.cie.hr.infrastructure.entity.JobEntity;
import com.cie.hr.infrastructure.entity.OrganizationEntity;
import com.cie.hr.infrastructure.mapper.JobMapper;
import com.cie.hr.infrastructure.mapper.OrganizationMapper;
import com.cie.hr.infrastructure.repository.GradeJpaRepository;
import com.cie.hr.infrastructure.repository.JobJpaRepository;
import com.cie.hr.infrastructure.repository.OrganizationJpaRepository;
import com.cie.hr.infrastructure.repository.ScorecardJpaRepository;
import com.cie.hr.infrastructure.service.viewmodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Service
public class OrganizationQuery {

    private final OrganizationJpaRepository organizationJpaRepository;

    private final ScorecardJpaRepository scorecardJpaRepository;

    private final JobJpaRepository jobJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final GradeJpaRepository gradeJpaRepository;

    public OrganizationQuery(OrganizationJpaRepository organizationJpaRepository,
                             ScorecardJpaRepository scorecardJpaRepository,
                             JobJpaRepository jobJpaRepository, GradeJpaRepository gradeJpaRepository) {
        this.organizationJpaRepository = organizationJpaRepository;
        this.scorecardJpaRepository = scorecardJpaRepository;
        this.jobJpaRepository = jobJpaRepository;
        this.gradeJpaRepository = gradeJpaRepository;
    }

    public List<OrganizationVm> getAllOrganizations() {
        return organizationJpaRepository.findByDeletedFalseOrderByCreatedDesc().stream().map(OrganizationMapper::toOrganizationVm).toList();
    }

    public OrganizationHierarchicalVm readAllPole() {
        List<OrganizationEntity> organizationVms = organizationJpaRepository.findByTypeName("Pôle");
        List<OrganizationListDetails> details = new ArrayList<>();
        OrganizationHierarchicalVm element = null;
        Optional<OrganizationEntity> parentPole = organizationVms.stream().filter(e -> e.getParent() == null).findFirst();

        organizationVms.forEach(organization -> {
            if (organization.getParent() != null) {
                var directions = organizationJpaRepository.findByParentId(organization.getId());
                EmployeeEntity employee;
                int toEvaluate = 0;
                AtomicInteger evaluated = new AtomicInteger();
                if (organization.getChiefJob() != null && organization.getChiefJob().getEmployee() != null) {
                    employee = organization.getChiefJob().getEmployee();
                    var scorecardList = scorecardJpaRepository.findAllByManagerId(employee.getId());
                    toEvaluate = scorecardList.stream().filter(e -> !e.isDeleted()).toList().size();
                    scorecardList.forEach(s -> {
                        if (s.getStatus().getCode().equals("2")) {
                            evaluated.getAndIncrement();
                        }
                    });
                }

                OrganizationListDetails d = new OrganizationListDetails(
                        organization.getId(),
                        organization.getName(),
                        organization.getCode(),
                        organization.getParent() == null ? null : organization.getParent().getId(),
                        organization.getParent() == null ? null : organization.getParent().getName(),
                        organization.getType().getId(),
                        organization.getType().getName(),
                        organization.getChiefJob() == null ? "" : organization.getChiefJob().getEmployee() == null ? null : organization.getChiefJob().getEmployee().getFullName(),
                        organization.getChiefJob() == null ? "" : organization.getChiefJob().getTitle(),
                        directions.size(),
                        toEvaluate,
                        evaluated.get(),
                        true,
                        false
                );
                details.add(d);
            }

        });

        if (parentPole.isPresent()) {
            OrganizationEntity parentOrganization = parentPole.get();
            EmployeeEntity employee;
            int toEvaluate = 0;
            AtomicInteger evaluated = new AtomicInteger();
            if (parentOrganization.getChiefJob() != null && parentOrganization.getChiefJob().getEmployee() != null) {
                employee = parentOrganization.getChiefJob().getEmployee();
                var scorecardList = scorecardJpaRepository.findAllByManagerId(employee.getId());
                toEvaluate = scorecardList.stream().filter(e -> !e.isDeleted()).toList().size();
                scorecardList.forEach(s -> {
                    if (s.getStatus().getCode().equals("2")) {
                        evaluated.getAndIncrement();
                    }
                });
            }
            element = new OrganizationHierarchicalVm(
                    parentOrganization.getId(),
                    parentOrganization.getName(),
                    parentOrganization.getCode(),
                    null,
                    null,
                    parentOrganization.getType().getId(),
                    parentOrganization.getType().getName(),
                    parentOrganization.getChiefJob() == null ? "" : parentOrganization.getChiefJob().getEmployee() == null ? null : parentOrganization.getChiefJob().getEmployee().getFullName(),
                    parentOrganization.getChiefJob() == null ? "" : parentOrganization.getChiefJob().getTitle(),
                    0,
                    toEvaluate,
                    evaluated.get(),
                    true,
                    details,
                    parentOrganization.getCostCenter()
            );
        }
        return element;
    }

    public OrganizationCompleteVM readCompleteViewDetails(UUID organizationId) {
        var organization = organizationJpaRepository.findById(organizationId);
        if (organization.isEmpty()) return null;

        List<JobEntity> jobs = jobJpaRepository.findAllByDeletedFalseAndOrganizationId(organizationId);

        var organizationChildrenList = organizationJpaRepository.findByParentId(organizationId).stream().map(OrganizationMapper::toOrganizationVm).toList();
        var organizationJobs = jobs.stream().map(JobMapper::toJobVm).toList();

        return new OrganizationCompleteVM(OrganizationMapper.toOrganizationVm(organization.get()), organizationJobs, organizationChildrenList);
    }

    public List<OrganizationVm> organizationByDirection() {
        return this.organizationJpaRepository.findByTypeName("Direction").stream().map(OrganizationMapper::toOrganizationVm).toList();
    }

    public List<OrganizationLightVm> organizationByGrade(UUID grade) {
        var currentGrade = gradeJpaRepository.findById(grade);
        if (currentGrade.isEmpty()) {
            return new ArrayList<>();
        }
        List<OrganizationLightVm> organizationLightVms;

        if (currentGrade.get().getCode().equals("DG") || currentGrade.get().getCode().equals("DGA")) {
            organizationLightVms = this.organizationJpaRepository.findAllByTypeGradeCodeAndDeletedFalse("DG").stream().map(OrganizationMapper::toOrganizationLightVm).toList();
        } else {
            LOGGER.info("Grade {}", currentGrade.get().getCode());
            organizationLightVms = this.organizationJpaRepository.findAllByTypeGradeCodeAndDeletedFalse(currentGrade.get().getCode()).stream().map(OrganizationMapper::toOrganizationLightVm).toList();
        }
        LOGGER.info("Nombre d'élément dans organizations {}", organizationLightVms.size());
        LOGGER.info("Nombre d'élément dans organizations {}", organizationLightVms);
        return organizationLightVms;
    }

    public OrganizationHierarchicalVm organizationHierarchicById(UUID id) {
        var organization = organizationJpaRepository.findById(id);
        OrganizationHierarchicalVm employeesHierarchicalVm;
        if (organization.isEmpty()) {
            return null;
        } else {
            List<String> codes;
            codes = List.of("Direction centrale", "Direction");

            OrganizationEntity organizationEntity = organization.get();
            List<OrganizationEntity> allOrganizationUnder = organizationJpaRepository.findByParentId(organizationEntity.getId());
            List<OrganizationListDetails> details = new ArrayList<>();
            allOrganizationUnder.forEach(org -> {
                var findDirection = organizationJpaRepository.findByParentIdAndTypeNameIn(org.getId(), codes);
                List<JobEntity> jobs = jobJpaRepository.findAllByDeletedFalseAndOrganizationId(org.getId());
                jobs.removeIf(e -> e.getCode().equals(org.getChiefJob().getCode()));
                EmployeeEntity employee;
                int countDirections;
                int toEvaluate = 0;
                AtomicInteger evaluated = new AtomicInteger();
                if (findDirection.isEmpty()) {
                    countDirections = org.getJobs().size();
                } else {
                    var directions = organizationJpaRepository.findByParentId(org.getId());
                    countDirections = directions.size();
                }
                if (org.getChiefJob() != null && org.getChiefJob().getEmployee() != null) {
                    employee = org.getChiefJob().getEmployee();
                    var scorecardList = scorecardJpaRepository.findAllByManagerId(employee.getId());
                    toEvaluate = scorecardList.stream().filter(e -> !e.isDeleted()).toList().size();
                    scorecardList.forEach(s -> {
                        if (s.getStatus().getCode().equals("2")) {
                            evaluated.getAndIncrement();
                        }
                    });
                }

                OrganizationListDetails d = new OrganizationListDetails(
                        org.getId(),
                        org.getName(),
                        org.getCode(),
                        org.getParent() == null ? null : org.getParent().getId(),
                        org.getParent() == null ? null : org.getParent().getName(),
                        org.getType().getId(),
                        org.getType().getName(),
                        org.getChiefJob() == null ? "" : org.getChiefJob().getEmployee() == null ? null : org.getChiefJob().getEmployee().getFullName(),
                        org.getChiefJob() == null ? "" : org.getChiefJob().getTitle(),
                        countDirections,
                        toEvaluate,
                        evaluated.get(),
                        !findDirection.isEmpty(),
                        jobs.isEmpty()
                );
                details.add(d);
            });

            // Parent Element
            var findDirection = organizationJpaRepository.findByParentIdAndTypeNameIn(organizationEntity.getId(), codes);
            EmployeeEntity employee;
            int countDirections;
            int toEvaluate = 0;
            AtomicInteger evaluated = new AtomicInteger();
            if (findDirection.isEmpty()) {
                countDirections = organizationEntity.getJobs().size();
            } else {
                var directions = organizationJpaRepository.findByParentId(organizationEntity.getId());
                countDirections = directions.size();
            }
            if (organizationEntity.getChiefJob() != null && organizationEntity.getChiefJob().getEmployee() != null) {
                employee = organization.get().getChiefJob().getEmployee();
                var scorecardList = scorecardJpaRepository.findAllByManagerId(employee.getId());
                toEvaluate = scorecardList.stream().filter(e -> !e.isDeleted()).toList().size();
                scorecardList.forEach(s -> {
                    if (s.getStatus().getCode().equals("2")) {
                        evaluated.getAndIncrement();
                    }
                });
            }

            employeesHierarchicalVm = new OrganizationHierarchicalVm(
                    organizationEntity.getId(),
                    organizationEntity.getName(),
                    organizationEntity.getCode(),
                    organizationEntity.getParent() == null ? null : organizationEntity.getParent().getId(),
                    organizationEntity.getParent() == null ? null : organizationEntity.getParent().getName(),
                    organizationEntity.getType().getId(),
                    organizationEntity.getType().getName(),
                    organizationEntity.getChiefJob() == null ? "" : organizationEntity.getChiefJob().getEmployee() == null ? null : organizationEntity.getChiefJob().getEmployee().getFullName(),
                    organizationEntity.getChiefJob() == null ? "" : organizationEntity.getChiefJob().getTitle(),
                    countDirections,
                    toEvaluate,
                    evaluated.get(),
                    !findDirection.isEmpty(),
                    details,
                    organizationEntity.getCostCenter()
            );
            return employeesHierarchicalVm;
        }
    }

    public OrganizationListVm organizationById(UUID id) {
        var organization = organizationJpaRepository.findById(id);
        if (organization.isEmpty()) {
            return null;
        } else {
            List<String> codes;
            codes = List.of("Pôle", "Direction centrale", "Direction");
            OrganizationEntity organizationEntity = organization.get();
            List<JobEntity> jobOnOrganization = organizationEntity.getJobs();
            List<OrganizationEntity> allOrganizationUnder = organizationJpaRepository.findByParentId(organization.get().getId());
            List<OrganizationListDetails> details = new ArrayList<>();
            List<JobWithEmployeeInfoVm> jobsList = new ArrayList<>();
            List<UUID> uuids = new ArrayList<>();
            LOGGER.info("Nombre d'élément dans la liste {}", jobOnOrganization.size());
            jobOnOrganization.forEach(job -> {
                JobWithEmployeeInfoVm d = new JobWithEmployeeInfoVm(
                        job.getEmployee() == null ? null : job.getEmployee().getId(),
                        job.getEmployee() == null ? null : job.getEmployee().getFirstname(),
                        job.getEmployee() == null ? null : job.getEmployee().getLastname(),
                        job.getOrganization().getId(),
                        job.getOrganization().getCode(),
                        job.getOrganization().getName(),
                        job.getId(),
                        job.getTitle(),
                        job.getCode(),
                        job.getGrade().getName(),
                        job.getJobTemplate()
                );
                jobsList.add(d);
            });

            allOrganizationUnder.forEach(org -> {
                var findDirection = organizationJpaRepository.findByParentIdAndTypeNameIn(org.getId(), codes);
                List<JobEntity> jobs = jobJpaRepository.findAllByDeletedFalseAndOrganizationId(org.getId());
                jobs.removeIf(e -> e.getCode().equals(org.getChiefJob().getCode()));
                int countDirections = 0;
                int toEvaluate = 0;
                int evaluated = 0;
                if (!findDirection.isEmpty()) {
                    var directions = organizationJpaRepository.findByParentId(org.getId());
                    countDirections = directions.size();
                }

                if (org.getChiefJob() != null && org.getChiefJob().getEmployee() != null) {
                    evaluated = scorecardJpaRepository.findAllOrganizationScorecard(org.getId()).size();
                    toEvaluate = jobs.size();
                }

                if (!uuids.contains(org.getId())) {
                    OrganizationListDetails d = new OrganizationListDetails(
                            org.getId(),
                            org.getName(),
                            org.getCode(),
                            org.getParent() == null ? null : org.getParent().getId(),
                            org.getParent() == null ? null : org.getParent().getName(),
                            org.getType().getId(),
                            org.getType().getName(),
                            org.getChiefJob() == null ? "" : org.getChiefJob().getEmployee() == null ? null : org.getChiefJob().getEmployee().getFullName(),
                            org.getChiefJob() == null ? "" : org.getChiefJob().getTitle(),
                            countDirections,
                            toEvaluate,
                            evaluated,
                            !findDirection.isEmpty(),
                            false
                    );
                    details.add(d);
                    uuids.add(org.getId());
                }
            });
            return new OrganizationListVm(organizationEntity.getId(), organizationEntity.getName(), organizationEntity.getCode(), organizationEntity.getType().getId(), organizationEntity.getType().getName(), details, jobsList);
        }
    }

    public List<OrganizationVm> organizationByDirectionCentral() {
        return this.organizationJpaRepository.findByTypeName("Direction centrale").stream().map(OrganizationMapper::toOrganizationVm).toList();
    }

    public List<OrganizationVm> organizationByDirectionAdjointe() {
        return this.organizationJpaRepository.findByTypeName("Direction Adjointe").stream().map(OrganizationMapper::toOrganizationVm).toList();
    }
}
