package com.cie.hr.infrastructure.service.query;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.utils.ExcelExportService;
import com.cie.hr.infrastructure.mapper.UnitsMapper;
import com.cie.hr.infrastructure.repository.UnitsJpaRepository;
import com.cie.hr.infrastructure.service.viewmodel.UnitsVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexis TAMBIE
 * @created 14/06/2023
 * @project hr-cie
 */
@Service
public class UnitsQuery {

    private final UnitsJpaRepository unitsJpaRepository;
    private final ExcelExportService excelExportService;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final NoteDistributionQuery noteDistributionQuery;

    public UnitsQuery(UnitsJpaRepository unitsJpaRepository, ExcelExportService excelExportService, NoteDistributionQuery noteDistributionQuery) {
        this.unitsJpaRepository = unitsJpaRepository;
        this.excelExportService = excelExportService;
        this.noteDistributionQuery = noteDistributionQuery;
    }

    public List<UnitsVM> getAllUnits() {
        return unitsJpaRepository.findAll().stream().map(UnitsMapper::toUnitsVM).toList();
    }

    public byte[] exportToExcel() {
        try {
            return excelExportService.exportScorecardTemplate(getAllUnits(), noteDistributionQuery.readAllNotes());
        } catch (Exception e) {
            LOGGER.error("Error while downloading scorecard template to Excel", e);
            throw new ApplicationException("Erreur survenue lors de la génération du fichier Excel");
        }
    }
}
