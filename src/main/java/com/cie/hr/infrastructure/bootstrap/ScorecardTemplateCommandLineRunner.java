package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.ScorecardExpertTemplateEntity;
import com.cie.hr.infrastructure.entity.ScorecardManagerTemplateEntity;
import com.cie.hr.infrastructure.entity.ScorecardTemplateEntity;
import com.cie.hr.infrastructure.repository.ScorecardTemplateJpaRepository;
import com.cie.hr.infrastructure.valueobject.*;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author Koty BLEU
 * @created 09/05/2023
 * @project hr
 */

@Order(6)
@Component
public class ScorecardTemplateCommandLineRunner implements CommandLineRunner {

    private final ScorecardTemplateJpaRepository scorecardTemplateJpaRepository;

    public ScorecardTemplateCommandLineRunner(ScorecardTemplateJpaRepository scorecardTemplateJpaRepository) {
        this.scorecardTemplateJpaRepository = scorecardTemplateJpaRepository;
    }

    private ScorecardTemplateEntity buildManagerForm() {
        var linesSectionA = new ArrayList<FormStandardLine>() {{
            add(getFormStandardLine("A1- Maîtrise et respect des règles et procédure", 3d));
            add(getFormStandardLine("A2- Communication, exercice de l’autorité et pilotage de l’Unité", 3d));
            add(getFormStandardLine("A3- Planification du travail, gestion des projets et contribution à la RSE", 4d));
            add(getFormStandardLine("A4- Rigueur et probité", 5d));
            add(getFormStandardLine("A5- Analyse, diagnostic et esprit d’initiative", 4d));
            add(getFormStandardLine("A6- Méthode, organisation du travail et maintien de la cohésion", 3d));
        }};
        var sectionA = getFormStandardSection("A- Management et Leadership", linesSectionA, 0.35d);

        // Section B
        var linesSectionB = new ArrayList<FormStandardLine>() {{
            add(getFormStandardLine("B1- Participation du personnel aux visites médicales", 2d));
            add(getFormStandardLine("B2- Participation des collaborateurs aux activités de formation ", 4d));
            add(getFormStandardLine("B3- Implication dans le processus de recrutement", 3d));

        }};
        var sectionB = getFormStandardSection("B- Aptitudes de renforcement des performances sur les axes prioritaires fixés par la  DG", linesSectionB, .1d);

        // Section C
        var linesSectionC = new ArrayList<FormStandardLine>() {{
            add(getFormStandardLine("C1- Esprit de service", 4d));
            add(getFormStandardLine("C2- Relations humaines", 4d));
            add(getFormStandardLine("C3- Disponibilité", 4d));
            add(getFormStandardLine("C4- Environnement et sécurité au travail", 3d));
        }};
        var sectionC = getFormStandardSection("C- Habiletés comportementales", linesSectionC, .05d);

        // Section D
        var linesSectionD = new ArrayList<FormSpecialLine>();
        var sectionD = getFormSpecialSection("D- Objectifs opérationnels", linesSectionD, 0.5d);

        // Section E
        var linesSectionE = new ArrayList<FormPropositionLine>() {{
            add(new FormPropositionLine("Mobilité", false, "", ""));
            add(new FormPropositionLine("Formation", false, "", ""));
        }};
        var sectionE = getFormPropositionSection("E- Propositions", linesSectionE);

        // Section F
        var linesSectionF = new ArrayList<FormAnalyseLine>() {{
            add(new FormAnalyseLine("", "", "", ""));
            add(new FormAnalyseLine("", "", "", ""));
            add(new FormAnalyseLine("", "", "", ""));
        }};
        var sectionF = getFormAnalyseSection("F- Analyse des 3 principales difficultés ayant affectées la performance", linesSectionF);

        var formTemplate = new ScorecardForManagerForm(sectionA, sectionB, sectionC, sectionD, sectionE, sectionF);
        ScorecardManagerTemplateEntity scorecardManagerTemplateEntity;
        scorecardManagerTemplateEntity = ScorecardManagerTemplateEntity.builder().values(formTemplate).build();
        scorecardManagerTemplateEntity.setId(Generators.timeBasedEpochGenerator().generate());
        scorecardManagerTemplateEntity.setTitle("Fiche d'évaluation - Manager de direction");
        scorecardManagerTemplateEntity.setDeleted(false);
        scorecardManagerTemplateEntity.setActive(true);
        return scorecardManagerTemplateEntity;
    }

    private ScorecardTemplateEntity buildExpertForm() {
        // Section A
        var linesSectionA = new ArrayList<FormStandardLine>() {{
            add(getFormStandardLine("A1- Esprit de service", 1d));
            add(getFormStandardLine("A2- Relations humaines", 1d));
            add(getFormStandardLine("A3- Disponibilité", 1d));
            add(getFormStandardLine("A4- Environnement et sécurité au travail", 1d));
        }};
        var sectionA = getFormStandardSection("A- Aptitudes comportementales", linesSectionA, 0.15d);

        // Section B
        var linesSectionB = new ArrayList<FormSpecialLine>();
        var sectionB = getFormSpecialSection("B- Objectifs opérationnels", linesSectionB, 0.85d);

        // Section C
        var linesSectionC = new ArrayList<FormPropositionLine>() {{
            add(new FormPropositionLine("Mobilité", false, "", ""));
            add(new FormPropositionLine("Formation", false, "", ""));
        }};
        var sectionC = getFormPropositionSection("C- Propositions", linesSectionC);

        // Section D
        var linesSectionD = new ArrayList<FormAnalyseLine>() {{
            add(new FormAnalyseLine("", "", "", ""));
            add(new FormAnalyseLine("", "", "", ""));
            add(new FormAnalyseLine("", "", "", ""));
        }};
        var sectionD = getFormAnalyseSection("D- Analyse des 3 principales difficultés ayant affectées la performance", linesSectionD);

        var formTemplate = new ScorecardForExpert(sectionA, sectionB, sectionC, sectionD);
        ScorecardExpertTemplateEntity scorecardExpertTemplateEntity;

        scorecardExpertTemplateEntity = ScorecardExpertTemplateEntity.builder().values(formTemplate).build();
        scorecardExpertTemplateEntity.setId(Generators.timeBasedEpochGenerator().generate());
        scorecardExpertTemplateEntity.setTitle("Fiche d'évaluation - Expert & Conseiller");
        scorecardExpertTemplateEntity.setDeleted(false);
        scorecardExpertTemplateEntity.setActive(true);
        return scorecardExpertTemplateEntity;
    }

    @Override
    public void run(String... args) throws Exception {
        var forms = new ArrayList<ScorecardTemplateEntity>() {{
            add(buildManagerForm());
            add(buildExpertForm());
        }};

        if (scorecardTemplateJpaRepository.count() == 0) {
            scorecardTemplateJpaRepository.saveAll(forms);
        }
    }

    private FormStandardSection getFormStandardSection(String title, ArrayList<FormStandardLine> lines, double weight) {
        return new FormStandardSection(title, "normal", lines, weight, 0.0, false, null);
    }

    private FormSpecialSection getFormSpecialSection(String title, ArrayList<FormSpecialLine> lines, double weight) {
        return new FormSpecialSection(title, 0.0, "normal", lines, weight, false);
    }

    private FormPropositionSection getFormPropositionSection(String title, ArrayList<FormPropositionLine> lines) {
        return new FormPropositionSection(title, "proposition", lines, false);
    }

    private FormAnalyseSection getFormAnalyseSection(String title, ArrayList<FormAnalyseLine> lines) {
        return new FormAnalyseSection(title, "analyse", lines, false);
    }

    private FormStandardLine getFormStandardLine(String title, double maxScore) {
        return new FormStandardLine(title, maxScore, 0.0, "");
    }
}
