package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.adapter.AbstractEntity;
import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.Job;
import com.cie.hr.domain.port.JobRepositoryPort;
import com.cie.hr.infrastructure.entity.JobEntity;
import com.cie.hr.infrastructure.mapper.JobMapper;
import com.cie.hr.infrastructure.repository.JobJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 08/05/2023
 * @project hr
 */
@Transactional
@Service
public class JobRepositoryAdapter implements JobRepositoryPort {

    private final JobJpaRepository jobJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public JobRepositoryAdapter(JobJpaRepository jobJpaRepository) {
        this.jobJpaRepository = jobJpaRepository;
    }

    @Override
    public void save(Job job) {
        var newJob = JobMapper.toJobEntity(job);
        jobJpaRepository.save(newJob);
    }

    @Override
    public Optional<Job> findById(UUID id) {
        return jobJpaRepository.findById(id).map(JobMapper::toJobDomain);
    }

    @Override
    public void updateAndSave(Job domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                JobEntity jobEntity = jobJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Ce poste n'existe pas"));
                JobMapper.updateAndSave(domain, jobEntity);
                jobJpaRepository.save(jobEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update job after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating job", e);
                throw e;
            }
        }
    }

    @Override
    public boolean existsByCode(String code) {
        return jobJpaRepository.existsByCode(code);
    }

    @Override
    public Optional<Boolean> existsById(UUID id) {
        return Optional.of(jobJpaRepository.existsById(id));
    }

    @Override
    public Optional<UUID> findByCode(String code) {
        return jobJpaRepository.findByCode(code).map(AbstractEntity::getId);
    }

    @Override
    public List<Job> findByParentId(UUID id) {
        return jobJpaRepository.findByParentId(id).stream().map(JobMapper::toJobDomain).toList();
    }

    @Override
    public List<Job> findAllJobsWithEmployees() {
        return jobJpaRepository.findAllByDeletedFalseAndEmployeeIdIsNotNull().stream().map(JobMapper::toJobDomain).toList();
    }

    @Override
    public Optional<Job> findByEmployeeId(UUID employeeId) {
        return jobJpaRepository.findByEmployeeId(employeeId).map(JobMapper::toJobDomain);
    }
}
