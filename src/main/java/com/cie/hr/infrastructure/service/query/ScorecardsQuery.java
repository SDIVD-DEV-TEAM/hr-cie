package com.cie.hr.infrastructure.service.query;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.utils.ExcelExportService;
import com.cie.hr.common.utils.PdfGeneratorService;
import com.cie.hr.common.utils.ScoreCalculator;
import com.cie.hr.infrastructure.entity.ScorecardEntity;
import com.cie.hr.infrastructure.repository.ScorecardJpaRepository;
import com.cie.hr.infrastructure.service.viewmodel.EmployeeListVm;
import com.cie.hr.infrastructure.service.viewmodel.ExportManagerNoteVm;
import com.cie.hr.infrastructure.service.viewmodel.ExportScorecardVm;
import com.cie.hr.infrastructure.valueobject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 20/09/2024
 * @project hr-cie
 */
@Service
public class ScorecardsQuery {

    private final ScorecardJpaRepository scorecardJpaRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final ExcelExportService excelExportService;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmployeeQuery employeeQuery;
    private final CampaignQuery campaignQuery;

    public ScorecardsQuery(ScorecardJpaRepository scorecardJpaRepository,
                           ExcelExportService excelExportService,
                           PdfGeneratorService pdfGeneratorService,
                           EmployeeQuery employeeQuery, CampaignQuery campaignQuery) {
        this.scorecardJpaRepository = scorecardJpaRepository;
        this.excelExportService = excelExportService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.employeeQuery = employeeQuery;
        this.campaignQuery = campaignQuery;
    }

    public byte[] downloadScorecardToExcel(UUID campaignId) {
        try {
            List<ScorecardEntity> scorecardEntityList = scorecardJpaRepository.findByDeletedFalseAndCampaignId(campaignId);
            List<ExportScorecardVm> exportScorecardVmList = new ArrayList<>();
            scorecardEntityList.forEach(scorecard -> {
                EvaluationScorecardManager managerTemplate = scorecard.getManagerTemplate();
                EvaluationScorecardExpert expertTemplate = scorecard.getExpertTemplate();
                double totalCoefficientA = 0;
                double totalCoefficientB = 0;
                double totalCoefficientC = 0;
                double totalCoefficientD = 0;
                double totalNoteA = 0;
                double totalNoteB = 0;
                double totalNoteC = 0;
                double totalNoteD = 0;
                double totalWeightedNoteA = 0;
                double totalWeightedNoteB = 0;
                double totalWeightedNoteC = 0;
                double totalWeightedNoteD = 0;
                double noteA = 0;
                double noteB = 0;
                double noteC = 0;
                double noteD = 0;
                double coefficientA = 0;
                double coefficientB = 0;
                double coefficientC = 0;
                double coefficientD = 0;
                FormPropositionLine formPropositionMobility = null;
                FormPropositionLine formPropositionFormation = null;
                FormAnalyseLine formAnalyseOne = null;
                FormAnalyseLine formAnalyseTwo = null;
                FormAnalyseLine formAnalyseThree = null;
                if (managerTemplate != null) {
                    totalCoefficientA = ScoreCalculator.calculateTotalCoefficientStandard(managerTemplate.forms().sectionA().lines());
                    totalCoefficientB = ScoreCalculator.calculateTotalCoefficientStandard(managerTemplate.forms().sectionB().lines());
                    totalCoefficientC = ScoreCalculator.calculateTotalCoefficientStandard(managerTemplate.forms().sectionC().lines());
                    totalCoefficientD = ScoreCalculator.calculateTotalCoefficientSpecial(managerTemplate.forms().sectionD().lines());

                    totalNoteA = ScoreCalculator.calculateTotalNoteStandard(managerTemplate.forms().sectionA().lines());
                    totalNoteB = ScoreCalculator.calculateTotalNoteStandard(managerTemplate.forms().sectionB().lines());
                    totalNoteC = ScoreCalculator.calculateTotalNoteStandard(managerTemplate.forms().sectionC().lines());
                    totalNoteD = ScoreCalculator.calculateTotalNoteSpecial(managerTemplate.forms().sectionD().lines());

                    totalWeightedNoteA = ScoreCalculator.calculateTotalNoteWeightedStandard(managerTemplate.forms().sectionA().lines());
                    totalWeightedNoteB = ScoreCalculator.calculateTotalNoteWeightedStandard(managerTemplate.forms().sectionB().lines());
                    totalWeightedNoteC = ScoreCalculator.calculateTotalNoteWeightedStandard(managerTemplate.forms().sectionC().lines());
                    totalWeightedNoteD = ScoreCalculator.calculateTotalNoteWeightedSpecial(managerTemplate.forms().sectionD().lines());

                    noteA = ScoreCalculator.calculateNote(totalCoefficientA, totalWeightedNoteA);
                    noteB = ScoreCalculator.calculateNote(totalCoefficientB, totalWeightedNoteB);
                    noteC = ScoreCalculator.calculateNote(totalCoefficientC, totalWeightedNoteC);
                    noteD = ScoreCalculator.calculateNote(totalCoefficientD, totalWeightedNoteD);

                    coefficientA = managerTemplate.forms().sectionA().coefficient();
                    coefficientB = managerTemplate.forms().sectionB().coefficient();
                    coefficientC = managerTemplate.forms().sectionC().coefficient();
                    coefficientD = managerTemplate.forms().sectionD().coefficient();

                    formPropositionMobility = managerTemplate.forms().sectionE().lines().stream().filter(elt -> elt.title().equals("Mobilité")).findFirst().orElse(null);
                    formPropositionFormation = managerTemplate.forms().sectionE().lines().stream().filter(elt -> elt.title().equals("Formation")).findFirst().orElse(null);
                    formAnalyseOne = managerTemplate.forms().sectionF().lines().isEmpty() ? null : managerTemplate.forms().sectionF().lines().get(0);
                    formAnalyseTwo = managerTemplate.forms().sectionF().lines().size() > 1 ? managerTemplate.forms().sectionF().lines().get(1) : null;
                    formAnalyseThree = managerTemplate.forms().sectionF().lines().size() > 2 ? managerTemplate.forms().sectionF().lines().get(2) : null;
                }

                if (expertTemplate != null) {
                    totalCoefficientA = ScoreCalculator.calculateTotalCoefficientStandard(expertTemplate.forms().sectionA().lines());
                    totalCoefficientB = ScoreCalculator.calculateTotalCoefficientSpecial(expertTemplate.forms().sectionB().lines());

                    totalNoteA += ScoreCalculator.calculateTotalNoteStandard(expertTemplate.forms().sectionA().lines());
                    totalNoteB += ScoreCalculator.calculateTotalNoteSpecial(expertTemplate.forms().sectionB().lines());

                    totalWeightedNoteA = ScoreCalculator.calculateTotalNoteWeightedStandard(expertTemplate.forms().sectionA().lines());
                    totalWeightedNoteB = ScoreCalculator.calculateTotalNoteWeightedSpecial(expertTemplate.forms().sectionB().lines());

                    noteA = ScoreCalculator.calculateNote(totalCoefficientA, totalWeightedNoteA);
                    noteB = ScoreCalculator.calculateNote(totalCoefficientB, totalWeightedNoteB);

                    coefficientA = expertTemplate.forms().sectionA().coefficient();
                    coefficientB = expertTemplate.forms().sectionB().coefficient();

                    formPropositionMobility = expertTemplate.forms().sectionC().lines().stream().filter(elt -> elt.title().equals("Mobilité")).findFirst().orElse(null);
                    formPropositionFormation = expertTemplate.forms().sectionC().lines().stream().filter(elt -> elt.title().equals("Formation")).findFirst().orElse(null);
                    formAnalyseOne = expertTemplate.forms().sectionD().lines().isEmpty() ? null : expertTemplate.forms().sectionD().lines().get(0);
                    formAnalyseTwo = expertTemplate.forms().sectionD().lines().size() > 1 ? expertTemplate.forms().sectionD().lines().get(1) : null;
                    formAnalyseThree = expertTemplate.forms().sectionD().lines().size() > 2 ? expertTemplate.forms().sectionD().lines().get(2) : null;
                }
                LOGGER.info("Scorecard - {}", scorecard.getManagerTemplate());
                ExportScorecardVm exportScorecard = createExportScorecardVm(
                        scorecard,
                        totalNoteA, totalCoefficientA, totalWeightedNoteA, coefficientA, noteA,
                        totalNoteB, totalCoefficientB, totalWeightedNoteB, coefficientB, noteB,
                        totalNoteC, totalCoefficientC, totalWeightedNoteC, coefficientC, noteC,
                        totalNoteD, totalCoefficientD, totalWeightedNoteD, coefficientD, noteD,
                        formPropositionMobility, formPropositionFormation,
                        formAnalyseOne, formAnalyseTwo, formAnalyseThree
                );
                exportScorecardVmList.add(exportScorecard);
            });
            // Generate Excel file
            return excelExportService.exportScorecardListToExcel(exportScorecardVmList);
        } catch (Exception ex) {
            LOGGER.error("Error while downloading scorecard to Excel", ex);
            throw new ApplicationException("Erreur survenue lors de la génération du fichier Excel");
        }
    }

    public byte[] generateScorecardPdf(UUID scorecardId) throws IOException {
        ScorecardEntity scorecardEntity = scorecardJpaRepository.findById(scorecardId).orElseThrow(
                () -> new ApplicationException("Scorecard not found")
        );
        return pdfGeneratorService.generatePdfFromHtml(scorecardEntity);
    }

    private ExportScorecardVm createExportScorecardVm(
            ScorecardEntity scorecard,
            double totalNoteA, double totalCoefficientA, double totalWeightedNoteA, double coefficientA, double noteA,
            double totalNoteB, double totalCoefficientB, double totalWeightedNoteB, double coefficientB, double noteB,
            double totalNoteC, double totalCoefficientC, double totalWeightedNoteC, double coefficientC, double noteC,
            double totalNoteD, double totalCoefficientD, double totalWeightedNoteD, double coefficientD, double noteD,
            FormPropositionLine formPropositionMobility, FormPropositionLine formPropositionFormation,
            FormAnalyseLine formAnalyseOne, FormAnalyseLine formAnalyseTwo, FormAnalyseLine formAnalyseThree) {

        return new ExportScorecardVm(
                scorecard.getAssessed().getEmployeeNumber(),
                scorecard.getAssessed().getLastname(),
                scorecard.getAssessed().getFirstname(),
                scorecard.getJob() == null ? "" : scorecard.getJob().getOrganization(),
                scorecard.getJob() == null ? "" : scorecard.getJob().getTitle(),
                scorecard.getJob() == null ? "" : scorecard.getJob().getCode(),
                totalNoteA,
                totalCoefficientA,
                totalWeightedNoteA,
                coefficientA * 100,
                noteA,
                totalNoteB,
                totalCoefficientB,
                totalWeightedNoteB,
                coefficientB * 100,
                noteB,
                totalNoteC,
                totalCoefficientC,
                totalWeightedNoteC,
                coefficientC * 100,
                noteC,
                totalNoteD,
                totalCoefficientD,
                totalWeightedNoteD,
                coefficientD * 100,
                noteD,
                totalWeightedNoteA + totalWeightedNoteB + totalWeightedNoteC + totalWeightedNoteD,
                totalCoefficientA + totalCoefficientB + totalCoefficientC + totalCoefficientD,
                noteA * coefficientA + noteB * coefficientB + noteC * coefficientC + noteD * coefficientD,
                formPropositionMobility != null ? formPropositionMobility.proposition_value() : "",
                formPropositionMobility != null ? formPropositionMobility.proposition_reason() : "",
                formPropositionFormation != null ? formPropositionFormation.proposition_value() : "",
                formPropositionFormation != null ? formPropositionFormation.proposition_reason() : "",
                formAnalyseOne != null ? formAnalyseOne.reason() : "",
                formAnalyseOne != null ? formAnalyseOne.main_difficulty() : "",
                formAnalyseOne != null ? formAnalyseOne.manager_comment() : "",
                formAnalyseOne != null ? formAnalyseOne.possible_improvements() : "",
                formAnalyseTwo != null ? formAnalyseTwo.reason() : "",
                formAnalyseTwo != null ? formAnalyseTwo.main_difficulty() : "",
                formAnalyseTwo != null ? formAnalyseTwo.manager_comment() : "",
                formAnalyseTwo != null ? formAnalyseTwo.possible_improvements() : "",
                formAnalyseThree != null ? formAnalyseThree.reason() : "",
                formAnalyseThree != null ? formAnalyseThree.main_difficulty() : "",
                formAnalyseThree != null ? formAnalyseThree.manager_comment() : "",
                formAnalyseThree != null ? formAnalyseThree.possible_improvements() : "",
                scorecard.getManager() != null ? scorecard.getManager().getEmployeeNumber() : "",
                scorecard.getManager() != null ? scorecard.getManager().getLastname() : "",
                scorecard.getManager() != null ? scorecard.getManager().getFirstname() : "",
                scorecard.getStatus().getName()
        );
    }

    private ExportManagerNoteVm createExportManagerNoteVm(String employeeNumber, String lastname, String firstname, String jobTitle, double totalNote) {
        return new ExportManagerNoteVm(employeeNumber, lastname, firstname, jobTitle, totalNote);
    }

    public byte[] downloadManagerToExcelNote() {
        try {
            var campaign = campaignQuery.readCampaignByStatusCode("1");
            if (campaign.isEmpty()) {
                throw new ApplicationException("No campaign in progress");
            }
            return excelExportService.exportManagerNote(
                    getListExportManagerNoteVm(campaign.get().id())
            );
        } catch (Exception ex) {
            LOGGER.error("Error while downloading manager note to Excel", ex);
            throw new ApplicationException("Erreur survenue lors de la génération du fichier Excel");
        }
    }

    private List<ExportManagerNoteVm> getListExportManagerNoteVm(UUID campaignId) {
        Optional<List<EmployeeListVm>> elements = employeeQuery.collaboratorsEvaluationScorecards(campaignId);

        if (elements.isEmpty()) {
            return new ArrayList<>();
        }

        List<ExportManagerNoteVm> exportManagerNoteVmList = new ArrayList<>();
        elements.get().forEach(element -> {
            ExportManagerNoteVm exportManagerNoteVm = createExportManagerNoteVm(
                    element.employee_number(),
                    element.lastname(),
                    element.firstname(),
                    element.job(),
                    element.note()
            );
            exportManagerNoteVmList.add(exportManagerNoteVm);
        });
        return exportManagerNoteVmList;
    }
}
