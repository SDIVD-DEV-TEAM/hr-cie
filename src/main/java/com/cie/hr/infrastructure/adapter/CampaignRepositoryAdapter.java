package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.Campaign;
import com.cie.hr.domain.port.CampaignRepositoryPort;
import com.cie.hr.infrastructure.entity.CampaignEntity;
import com.cie.hr.infrastructure.mapper.CampaignMapper;
import com.cie.hr.infrastructure.repository.CampaignJpaRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class CampaignRepositoryAdapter implements CampaignRepositoryPort {

    private final CampaignJpaRepository campaignJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public CampaignRepositoryAdapter(CampaignJpaRepository campaignJpaRepository) {
        this.campaignJpaRepository = campaignJpaRepository;
    }

    @Override
    public Optional<Campaign> findById(UUID id) {
        return this.campaignJpaRepository.findById(id).map(CampaignMapper::toCampaign);
    }

    @Override
    public void updateAndSave(Campaign domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                CampaignEntity campaignEntity = campaignJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Cette campagne n'existe pas"));
                CampaignMapper.updateAndSave(domain, campaignEntity);
                campaignJpaRepository.save(campaignEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update campaign after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating campaign", e);
                throw e;
            }
        }
    }

    @Override
    public void save(Campaign campaign) {
        var campaignEntity = CampaignMapper.toCampaignEntity(campaign);
        this.campaignJpaRepository.save(campaignEntity);
    }

    @Override
    public boolean checkIfEndDateGreaterThan(Date date) {
        return campaignJpaRepository.existsByEndDateGreaterThan(date);
    }

    @Override
    public Optional<Campaign> findFirstByStatusCode(String code) {
        return campaignJpaRepository.findFirstByStatusCodeAndDeletedFalse(code).map(CampaignMapper::toCampaign);
    }

    @Override
    public List<Campaign> findByStatusCodeIn(List<String> code) {
        return campaignJpaRepository.findByStatusCodeIn(code).stream().map(CampaignMapper::toCampaign).toList();
    }

    @Override
    public Optional<Campaign> findFirstByStatusCodeAndDeletedFalseOrderByEndDateDesc(String code) {
        return campaignJpaRepository.findFirstByStatusCodeAndDeletedFalseOrderByEndDateDesc(code).map(CampaignMapper::toCampaign);
    }

}
