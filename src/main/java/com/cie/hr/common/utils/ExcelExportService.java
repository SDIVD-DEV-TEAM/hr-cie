package com.cie.hr.common.utils;

import com.cie.hr.infrastructure.service.viewmodel.ExportManagerNoteVm;
import com.cie.hr.infrastructure.service.viewmodel.ExportScorecardVm;
import com.cie.hr.infrastructure.service.viewmodel.NoteDistributionVm;
import com.cie.hr.infrastructure.service.viewmodel.UnitsVM;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Alexis TAMBIE
 * @created 20/09/2024
 * @project hr-cie
 */
@Service
public class ExcelExportService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public byte[] exportScorecardListToExcel(List<ExportScorecardVm> records) throws IOException {
        // Create a new workbook
        Workbook workbook = new XSSFWorkbook();

        // Create a sheet
        Sheet sheet = workbook.createSheet("Statistique");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.ORANGE.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Matricule", "Nom employé", "Prénom employé", "Unité Organisationnelle", "Libellé du poste", "Code Poste", "A-Total Note (1-4)", "A-Tot. Coefficient", "A-Tot Note pondéré", "A-Total Note", "Coefficient-A", "B-Total Note (1-4)", "B-Total Coefficient", "B-Tot Note pondéré", "B-Total Note", "Coefficient-B", "C-Total Note (1-4)", "C-Tot. Coefficient", "C-Tot Note pondéré", "C-Total Note", "Coefficient-C", "D-Total Note (1-4)", "D-Tot. Coefficient", "D-Tot Note pondéré", "D-Total Note", "Coefficient-D", "Total Points", "Total Coefficient", "Note Générale", "Status Mobilité", "Raison Mobilité", "Status Formation", "Raison Formation", "Analyse 1 - Difficultés", "Analyse 1 - Causes", "Analyse 1 - Améliorations envisageables", "Analyse 1 - Commentaires du manager", "Analyse 2 - Difficultés", "Analyse 2 - Causes", "Analyse 2 - Améliorations envisageables", "Analyse 2 - Commentaires du manager", "Analyse 3 - Difficultés", "Analyse 3 - Causes", "Analyse 3 - Améliorations envisageables", "Analyse 3 - Commentaires du manager", "Matricule Évaluateur", "Nom  Évaluateur", "Prénom Évaluateur", "Status de l'évaluation"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create Other rows and cells with employees data
        int rowIdx = 1;
        for (ExportScorecardVm record : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(record.employee_number());
            row.createCell(1).setCellValue(record.lastname());
            row.createCell(2).setCellValue(record.firstname());
            row.createCell(3).setCellValue(record.organization_code());
            row.createCell(4).setCellValue(record.job_title());
            row.createCell(5).setCellValue(record.job_code());
            row.createCell(6).setCellValue(record.section_a_sum_note());
            row.createCell(7).setCellValue(record.section_a_sum_coefficient());
            row.createCell(8).setCellValue(record.section_a_note_weighted());
            row.createCell(9).setCellValue(record.section_a_note());
            row.createCell(10).setCellValue(record.section_a_coefficient());
            row.createCell(11).setCellValue(record.section_b_sum_note());
            row.createCell(12).setCellValue(record.section_b_sum_coefficient());
            row.createCell(13).setCellValue(record.section_b_note_weighted());
            row.createCell(14).setCellValue(record.section_b_note());
            row.createCell(15).setCellValue(record.section_b_coefficient());
            row.createCell(16).setCellValue(record.section_c_sum_note());
            row.createCell(17).setCellValue(record.section_c_sum_coefficient());
            row.createCell(18).setCellValue(record.section_c_note_weighted());
            row.createCell(19).setCellValue(record.section_c_note());
            row.createCell(20).setCellValue(record.section_c_coefficient());
            row.createCell(21).setCellValue(record.section_d_sum_note());
            row.createCell(22).setCellValue(record.section_d_sum_coefficient());
            row.createCell(23).setCellValue(record.section_d_note_weighted());
            row.createCell(24).setCellValue(record.section_d_note());
            row.createCell(25).setCellValue(record.section_d_coefficient());
            row.createCell(26).setCellValue(record.total_note());
            row.createCell(27).setCellValue(record.total_coefficient());
            row.createCell(28).setCellValue(record.total_note_weighted());
            row.createCell(29).setCellValue(record.mobility_status());
            row.createCell(30).setCellValue(record.mobility_reason());
            row.createCell(31).setCellValue(record.formation_status());
            row.createCell(32).setCellValue(record.formation_reason());
            row.createCell(33).setCellValue(record.analyse_one_reason());
            row.createCell(34).setCellValue(record.analyse_one_main_difficulty());
            row.createCell(35).setCellValue(record.analyse_one_manager_comment());
            row.createCell(36).setCellValue(record.analyse_one_possible_improvements());
            row.createCell(37).setCellValue(record.analyse_two_reason());
            row.createCell(38).setCellValue(record.analyse_two_main_difficulty());
            row.createCell(39).setCellValue(record.analyse_two_manager_comment());
            row.createCell(40).setCellValue(record.analyse_two_possible_improvements());
            row.createCell(41).setCellValue(record.analyse_three_reason());
            row.createCell(42).setCellValue(record.analyse_three_main_difficulty());
            row.createCell(43).setCellValue(record.analyse_three_manager_comment());
            row.createCell(44).setCellValue(record.analyse_three_possible_improvements());
            row.createCell(45).setCellValue(record.manager_employee_number());
            row.createCell(46).setCellValue(record.manager_lastname());
            row.createCell(47).setCellValue(record.manager_firstname());
            row.createCell(48).setCellValue(record.status());
        }

        // Write the workbook content to a byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    public byte[] exportScorecardTemplate(List<UnitsVM> unitsVMList, List<NoteDistributionVm> notes) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            List<String> unites = unitsVMList.stream().map(UnitsVM::label).toList();

            // Première feuille : lister toutes les unités
            Sheet sheet1 = workbook.createSheet("Unités");
            Row headerRow = sheet1.createRow(0);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Unité");

            int rowIdx = 1;
            for (String unite : unites) {
                Row row = sheet1.createRow(rowIdx++);
                Cell cell = row.createCell(0);
                cell.setCellValue(unite);
            }

            // Protéger la première feuille (sans mots de passe)
            sheet1.protectSheet("idfFKI8m0n3iDhy0");

            // Deuxième feuille : Tableau de distribution des notes
            Sheet sheet2 = workbook.createSheet("Formules de calcul");
            Row headerRow1 = sheet2.createRow(0);
            String[] noteHeaders = {"Code", "Description"};
            for (int i = 0; i < noteHeaders.length; i++) {
                Cell cell = headerRow1.createCell(i);
                cell.setCellValue(noteHeaders[i]);
            }

            // Resize columns to fit the content
            for (int i = 0; i < noteHeaders.length; i++) {
                sheet2.autoSizeColumn(i);
            }

            // Ajouter les notes et leurs descriptions
            notes.forEach(note -> {
                Row row = sheet2.createRow(notes.indexOf(note) + 1);
                row.createCell(0).setCellValue(note.code());
                row.createCell(1).setCellValue(note.description());
            });

            // Protéger la feuille 2 (sans mots de passe)
            sheet2.protectSheet("idfFKI8m0n3iDhy0");

            // Create a Font for styling header cells
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontName("Century Gothic");
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.ORANGE.getIndex());

            // Style de cellule avec bordure et centrage
            CellStyle borderedStyle = workbook.createCellStyle();
            borderedStyle.setBorderBottom(BorderStyle.THIN);
            borderedStyle.setBorderTop(BorderStyle.THIN);
            borderedStyle.setBorderLeft(BorderStyle.THIN);
            borderedStyle.setBorderRight(BorderStyle.THIN);

            // Centrage du texte dans les cellules
            CellStyle centeredStyle = workbook.createCellStyle();
            centeredStyle.setAlignment(HorizontalAlignment.CENTER);
            centeredStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            centeredStyle.cloneStyleFrom(borderedStyle); // Appliquer aussi les bordures
            centeredStyle.setFont(headerFont);


            // Troisième feuille : Tableau avec choix d'unités
            Sheet sheet3 = workbook.createSheet("Contrat Objectifs");
            Row headerRow2 = sheet3.createRow(0);
            String[] headers = {"INDICATEURS", "COEFFICIENT DE PONDÉRATION", "UNITÉ", "OBJECTIF", "FORMULE DE CALCUL"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow2.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(centeredStyle);
            }

            // Resize columns to fit the content
            for (int i = 0; i < headers.length; i++) {
                sheet3.autoSizeColumn(i);
            }

            // Ajouter la liste déroulante des unités dans la troisième colonne
            for (int i = 1; i <= 50; i++) {
                Row row = sheet3.createRow(i);
                row.createCell(0).setCellValue("");
                row.createCell(4).setCellValue("");

                for (int j = 1; j <= 3; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(centeredStyle); // Appliquer le centrage et les bordures
                }

                // Ajouter la validation pour les colonnes 2 et 4 (nombres uniquement)
                DataValidationHelper validationHelper = sheet3.getDataValidationHelper();
                DataValidationConstraint numericConstraint = validationHelper.createNumericConstraint(DataValidationConstraint.ValidationType.DECIMAL,
                        DataValidationConstraint.OperatorType.BETWEEN, "-1E+30", "1E+30"); // Accepter des nombres sans limites spécifiques

                // Appliquer la validation aux colonnes 2 (index 1) et 4 (index 4)
                CellRangeAddressList numericAddressList2 = new CellRangeAddressList(i, i, 1, 1); // Colonne B
                DataValidation numericValidation2 = validationHelper.createValidation(numericConstraint, numericAddressList2);
                sheet3.addValidationData(numericValidation2);

                CellRangeAddressList numericAddressList4 = new CellRangeAddressList(i, i, 3, 3); // Colonne E
                DataValidation numericValidation4 = validationHelper.createValidation(numericConstraint, numericAddressList4);
                sheet3.addValidationData(numericValidation4);

                // Cellule de la liste déroulante des unités dans la 4ᵉ colonne
                Cell choiceCell = row.createCell(3);
                choiceCell.setCellStyle(centeredStyle); // Centrer et bordurer la cellule

                // Cellule de la liste déroulante des notes dans la 5ᵉ colonne
                Cell noteCell = row.createCell(4);
                noteCell.setCellStyle(centeredStyle); // Centrer et bordurer la cell

                // Ajouter la validation pour la colonne C (Choix Unité)
                DataValidationHelper listConstraint = sheet3.getDataValidationHelper();
                DataValidationConstraint constraint = listConstraint.createExplicitListConstraint(unites.toArray(new String[0]));
                CellRangeAddressList addressList = new CellRangeAddressList(i, i, 2, 2);
                DataValidation validation = listConstraint.createValidation(constraint, addressList);
                sheet3.addValidationData(validation);

                // Ajouter une validation pour la colonne E (Formule de calcul) basé sur les notes
                List<String> noteCodes = notes.stream().map(NoteDistributionVm::code).toList();
                LOGGER.info("Note codes: {}", noteCodes);
                DataValidationConstraint noteConstraint = listConstraint.createExplicitListConstraint(noteCodes.toArray(new String[0]));
                CellRangeAddressList noteAddressList = new CellRangeAddressList(i, i, 4, 4);
                DataValidation noteValidation = listConstraint.createValidation(noteConstraint, noteAddressList);
                sheet3.addValidationData(noteValidation);

                for (int j = 0; j < 5; j++) {
                    row.getCell(j).setCellStyle(borderedStyle);
                }
            }

            // Préparer le fichier Excel pour téléchargement
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            return out.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Erreur lors de la génération du fichier Excel", e);
            return null;
        }
    }

    public byte[] exportManagerNote(List<ExportManagerNoteVm> records) throws IOException {
        // Create a new workbook
        Workbook workbook = new XSSFWorkbook();

        // Create a sheet
        Sheet sheet = workbook.createSheet("Statistique");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.ORANGE.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Matricule", "Nom employé", "Prénom employé", "Libellé du poste", "Note Générale"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create Other rows and cells with employees data
        int rowIdx = 1;
        for (ExportManagerNoteVm record : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(record.employee_number());
            row.createCell(1).setCellValue(record.lastname());
            row.createCell(2).setCellValue(record.firstname());
            row.createCell(3).setCellValue(record.job_title());
            row.createCell(4).setCellValue(record.total_note());
        }

        // Write the workbook content to a byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
}
