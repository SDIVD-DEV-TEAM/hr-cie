package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.JobEntity;
import com.cie.hr.infrastructure.repository.GradeJpaRepository;
import com.cie.hr.infrastructure.repository.JobJpaRepository;
import com.cie.hr.infrastructure.repository.OrganizationJpaRepository;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Koty BLEU
 * @created 08/05/2023
 * @project hr
 */
@Order(5)
@Component
public class JobBootstrapCommandLineRunner implements CommandLineRunner {

    private final JobJpaRepository jobJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final GradeJpaRepository gradeJpaRepository;

    public JobBootstrapCommandLineRunner(JobJpaRepository jobJpaRepository, OrganizationJpaRepository organizationJpaRepository, GradeJpaRepository gradeJpaRepository) {
        this.jobJpaRepository = jobJpaRepository;
        this.organizationJpaRepository = organizationJpaRepository;
        this.gradeJpaRepository = gradeJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Direction Générale
        var checkDG = jobJpaRepository.findByCode("DG");
        var checkDGA = jobJpaRepository.findByCode("DGA-DC");
        var checkDGAPTME = jobJpaRepository.findByCode("DI0046");

        JobEntity newJob;
        JobEntity jobDGA;
        JobEntity jobDGAPTME;

        var organizationPole = organizationJpaRepository.findByTypeName("Pôle").stream().filter(elt -> elt.getParent() == null).findFirst().orElse(null);
        if (checkDG.isEmpty()) {
            var gradeDGEntity = gradeJpaRepository.findAll().stream().filter(elt -> elt.getCode().equals("DG")).findFirst().orElse(null);
            newJob = JobEntity.builder().organization(organizationPole).title("Directeur Général").code("DG").grade(gradeDGEntity).build();
            newJob.setId(Generators.timeBasedEpochGenerator().generate());

        } else {
            newJob = checkDG.get();
        }

        var gradeDGAEntity = gradeJpaRepository.findByCode("DGA");
        if (checkDGA.isEmpty()) {
            jobDGA = JobEntity.builder().organization(organizationPole).title("DGA Pôle Distribution et Commercialisation").code("DGA-DC").grade(gradeDGAEntity).parent(newJob).build();
            jobDGA.setId(Generators.timeBasedEpochGenerator().generate());

        } else {
            jobDGA = checkDGA.get();
        }

        if (checkDGAPTME.isEmpty()) {
            jobDGAPTME = JobEntity.builder().organization(organizationPole).title("DGA Pôle Production Transport et Mouvement d'Energie").code("DI0046").grade(gradeDGAEntity).parent(newJob).build();
            jobDGAPTME.setId(Generators.timeBasedEpochGenerator().generate());
        } else {
            jobDGAPTME = checkDGAPTME.get();
        }


        //DSTD
        var findDSTD = organizationJpaRepository.findFirstByCode("DSTD");

        //DCOA
        var findDCCMO = organizationJpaRepository.findFirstByCode("DCCMO");

        // PTME
        var findDCTET = organizationJpaRepository.findFirstByCode("DCTET01");

        //DME
        var findDME = organizationJpaRepository.findFirstByCode("DME0001");

        //DPE
        var findDPE = organizationJpaRepository.findFirstByCode("DPE0001");

        //DEMP
        var findDEMP = organizationJpaRepository.findFirstByCode("DEMP0001");


        JobEntity finalNewJob = newJob;
        JobEntity finalJobDGA = jobDGA;
        JobEntity finaljobDGAPTME = jobDGAPTME;

        var gradeDC = gradeJpaRepository.findByCode("DC");
        var gradeD = gradeJpaRepository.findByCode("D");
        var gradeSD = gradeJpaRepository.findByCode("SD");
        var gradeDA = gradeJpaRepository.findByCode("DA");
        var gradeDR = gradeJpaRepository.findByCode("DR");

        List<JobEntity> jobEntities = new ArrayList<>() {{
            if (findDSTD.isPresent()) {
                // Add Directeur
                var gradeDirecteur = gradeJpaRepository.findByCode("DC");
                var job = JobEntity.builder().organization(findDSTD.get()).title("Directeur de la Stratégie et de la Transformation Digitale").parent(finalNewJob).grade(gradeDirecteur).code("DSTD").build();
                job.setId(Generators.timeBasedEpochGenerator().generate());
                add(job);

                var gradeDA = gradeJpaRepository.findByCode("DA");
                var jobIA = JobEntity.builder().organization(findDSTD.get()).title("Directeur Adjoint - Intelligence Artificielle").parent(job).grade(gradeDA).code("DSTD - DAIA").build();
                jobIA.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobIA);

                var jobDA = JobEntity.builder().organization(findDSTD.get()).title("Directeur Adjoint - Data & Analytics").parent(job).grade(gradeDA).code("DSTD DADA").build();
                jobDA.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDA);

                var jobDP = JobEntity.builder().organization(findDSTD.get()).title("Directeur Adjoint - Project Management").parent(job).grade(gradeDA).code("DSTD DADP").build();
                jobDP.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDP);

                var gradeSD = gradeJpaRepository.findByCode("SD");
                var jobSDPD = JobEntity.builder().organization(findDSTD.get()).title("Sous-directeur Process Digitalization").parent(job).grade(gradeSD).code("DSTD SDPD").build();
                jobSDPD.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDPD);

                var jobSDIA = JobEntity.builder().organization(findDSTD.get()).title("Sous-directeur Intelligence Artificielle").parent(jobIA).grade(gradeSD).code("DSTD SDIA").build();
                jobSDIA.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDIA);

                var jobSDCERIA = JobEntity.builder().organization(findDSTD.get()).title("Sous-directeur CERIA").parent(jobIA).grade(gradeSD).code("DSTD SDCERIA").build();
                jobSDCERIA.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDCERIA);

                var jobSDDA = JobEntity.builder().organization(findDSTD.get()).title("Sous-directeur Data & Analytics").parent(jobDA).grade(gradeSD).code("DSTD SDDA").build();
                jobSDDA.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDDA);

                var jobSDFC = JobEntity.builder().organization(findDSTD.get()).title("Sous-directeur Finance et Controle").parent(jobDA).grade(gradeSD).code("DSTD SDFC").build();
                jobSDFC.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDFC);

                var jobSDPM = JobEntity.builder().organization(findDSTD.get()).title("Sous-directeur Project Management").parent(jobDP).grade(gradeSD).code("DSTD SDPM").build();
                jobSDPM.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDPM);

                var jobSDPMF = JobEntity.builder().organization(findDSTD.get()).title("Sous-directeur Project Management Finance").parent(jobDA).grade(gradeSD).code("DSTD SDPMF").build();
                jobSDPMF.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDPMF);
            }

            // DCTET
            if (findDCTET.isPresent()) {
                // Add Directeur Central
                JobEntity jobDCTET = JobEntity.builder().organization(findDCTET.get()).title("Directeur Central Transport et Telecommunications").parent(finaljobDGAPTME).grade(gradeDC).code("DI0173").build();
                jobDCTET.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDCTET);

                JobEntity jobSDMGR = JobEntity.builder().organization(findDCTET.get()).title("Sous Directeur Moyens Généraux et Ressources").parent(finaljobDGAPTME).grade(gradeSD).code("SO0055").build();
                jobSDMGR.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDMGR);

                JobEntity jobSDCG = JobEntity.builder().organization(findDCTET.get()).title("Sous Directeur En Charge du Contrôle de Gestion").parent(finaljobDGAPTME).grade(gradeSD).code("SO0187").build();
                jobSDCG.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDCG);

                // Add Two DCTET Director
                JobEntity jobDETE = JobEntity.builder().organization(findDCTET.get()).title("Directeur Exploitation du Transport d'Energie").parent(jobDCTET).grade(gradeD).code("DI0175").build();
                jobDETE.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDETE);

                JobEntity jobDAOT = JobEntity.builder().organization(findDCTET.get()).title("Directeur Appui Opérationnel et Télécommunications").parent(jobDCTET).grade(gradeD).code("DI0174").build();
                jobDAOT.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDAOT);

                var organizationDAOT = organizationJpaRepository.findFirstByCode("DAOT0001");
                if (organizationDAOT.isPresent()) {
                    var jobSDT = JobEntity.builder().organization(organizationDAOT.get()).title("Sous Directeur Télé conduite").parent(jobDAOT).grade(gradeSD).code("SO0168").build();
                    jobSDT.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobSDT);

                    var jobSDM = JobEntity.builder().organization(organizationDAOT.get()).title("Sous Directeur Maintenance").parent(jobDAOT).grade(gradeSD).code("SO0063").build();
                    jobSDM.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobSDM);

                    var jobSDET = JobEntity.builder().organization(organizationDAOT.get()).title("Sous Directeur Etudes et Travaux").parent(jobDAOT).grade(gradeSD).code("SO0169").build();
                    jobSDET.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobSDET);

                    var jobSDTC = JobEntity.builder().organization(organizationDAOT.get()).title("Sous Directeur Télécommunications").parent(jobDAOT).grade(gradeSD).code("SO0182").build();
                    jobSDTC.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobSDTC);
                }

                var organizationDETE = organizationJpaRepository.findFirstByCode("DEXT0001");
                if (organizationDETE.isPresent()) {
                    var jobDRMA = JobEntity.builder().organization(organizationDETE.get()).title("Direction Régionale du Transport d'Énergie et des Télécommunications MAN").parent(jobDETE).grade(gradeDR).code("DI0062").build();
                    jobDRMA.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRMA);

                    var jobDRABJ = JobEntity.builder().organization(organizationDETE.get()).title("Direction Régionale du Transport d'Énergie et des Télécommunications ABIDJAN").parent(jobDETE).grade(gradeDR).code("DI0060").build();
                    jobDRABJ.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRABJ);

                    var jobDRBK = JobEntity.builder().organization(organizationDETE.get()).title("Direction Régionale du Transport d'Énergie et des Télécommunications BOUAKE").parent(jobDETE).grade(gradeDR).code("DI0061").build();
                    jobDRBK.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRBK);

                    var jobDRSB = JobEntity.builder().organization(organizationDETE.get()).title("Direction Régionale du Transport d'Énergie et des Télécommunications SOUBRE").parent(jobDETE).grade(gradeDR).code("DI0160").build();
                    jobDRSB.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRSB);

                    var jobDRKH = JobEntity.builder().organization(organizationDETE.get()).title("Direction Régionale du Transport d'Énergie et des Télécommunications KORHOGO").parent(jobDETE).grade(gradeDR).code("DI0161").build();
                    jobDRKH.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRKH);

                    var jobDRAB = JobEntity.builder().organization(organizationDETE.get()).title("Direction Régionale du Transport d'Énergie et des Télécommunications ABENGOUROU").parent(jobDETE).grade(gradeDR).code("DI0162").build();
                    jobDRAB.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRAB);

                    var jobSDETE = JobEntity.builder().organization(organizationDETE.get()).title("Sous Directeur Exploitation").parent(jobDETE).grade(gradeSD).code("SO0119").build();
                    jobSDETE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobSDETE);
                }
            }

            //DME
            if (findDME.isPresent()) {
                //Add directeur
                var jobDME = JobEntity.builder().organization(findDME.get()).title("Directeur des Mouvement d'Energie").parent(finaljobDGAPTME).grade(gradeD).code("DI0049").build();
                jobDME.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDME);

                var jobDACE = JobEntity.builder().organization(findDME.get()).title("Directeur Adjoint chargé de l’Exploitation").parent(jobDME).grade(gradeD).code("DI0158").build();
                jobDACE.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDACE);
                var organizationDACE = organizationJpaRepository.findFirstByCode("DME0019");

                if (organizationDACE.isPresent()) {
                    var jobSDESE = JobEntity.builder().organization(organizationDACE.get()).title("Sous Directeur Exploitation du Système Electrique").parent(jobDACE).grade(gradeSD).code("SO0058").build();
                    jobSDESE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobSDESE);
                }

                var jobSDMT = JobEntity.builder().organization(findDME.get()).title("Sous Directeur Moyens Techniques").parent(jobDACE).grade(gradeSD).code("SO0057").build();
                jobSDMT.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDMT);

                var jobSDERE = JobEntity.builder().organization(findDME.get()).title("Sous Directeur  Etudes et Retour d’Expérience").parent(jobDME).grade(gradeSD).code("SO0056").build();
                jobSDERE.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDERE);
            }

            //DPE
            if (findDPE.isPresent()) {
                //Add directeur
                var jobDPE = JobEntity.builder().organization(findDPE.get()).title("Directeur de la Production d'Electricité").parent(finaljobDGAPTME).grade(gradeD).code("DI0052").build();
                jobDPE.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDPE);

                var jobDUHK = JobEntity.builder().organization(findDPE.get()).title("Directeur d'Usine  Hydroélectrique Kossou").parent(jobDPE).grade(gradeSD).code("DI0054").build();
                jobDUHK.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDUHK);

                var jobDUTGV = JobEntity.builder().organization(findDPE.get()).title("Directeur d'Usine  Turbine à Gaz Vridi 1").parent(jobDPE).grade(gradeSD).code("DI0056").build();
                jobDUTGV.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDUTGV);

                var jobDUBF = JobEntity.builder().organization(findDPE.get()).title("Directeur d'Usine  Hydroélectrique Buyo et Faye").parent(jobDPE).grade(gradeSD).code("DI0101").build();
                jobDUBF.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDUBF);

                var jobDUHA = JobEntity.builder().organization(findDPE.get()).title("Directeur d'Usine  Hydroelectrique Ayame").parent(jobDPE).grade(gradeSD).code("DI0053").build();
                jobDUHA.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDUHA);

                var jobDUHT = JobEntity.builder().organization(findDPE.get()).title("Directeur d'Usine  Hydroelectrique Taabo").parent(jobDPE).grade(gradeSD).code("DI0055").build();
                jobDUHT.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDUHT);

                var jobPE = JobEntity.builder().organization(findDPE.get()).title("Directeur Adjoint de la Production d'Electricité").parent(jobDPE).grade(gradeDA).code("DI0250").build();
                jobPE.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobPE);

                var jobRSE = JobEntity.builder().organization(findDPE.get()).title("Directeur  Adjoint Chargé de l’Exploitation et de la Maintenance RSQSE-RSE").parent(jobDPE).grade(gradeDA).code("DI0100").build();
                jobRSE.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobRSE);

                var jobSDIRE = JobEntity.builder().organization(findDPE.get()).title("Sous Directeur Ingenierie et Retour d'Expérience").parent(jobDPE).grade(gradeSD).code("SO0060").build();
                jobSDIRE.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDIRE);

                var jobSDAO = JobEntity.builder().organization(findDPE.get()).title("Sous Directeur Appui Opérationnel").parent(jobDPE).grade(gradeSD).code("SO0061").build();
                jobSDAO.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDAO);
            }

            //DEMP
            if (findDEMP.isPresent()) {
                //Add directeur
                var jobDEMP = JobEntity.builder().organization(findDEMP.get()).title("Directeur des Etudes, Méthodes et Projets").parent(finaljobDGAPTME).grade(gradeD).code("DI0251").build();
                jobDEMP.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDEMP);

                var jobDACMP = JobEntity.builder().organization(findDEMP.get()).title("Directeur Adjoint Management des Projets").parent(jobDEMP).grade(gradeD).code("DI0246").build();
                jobDACMP.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDACMP);

                var jobSDESE = JobEntity.builder().organization(findDEMP.get()).title("Sous Directeur Etudes et Stratégies d’Exploitation").parent(jobDEMP).grade(gradeSD).code("SO0233").build();
                jobSDESE.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDESE);

                var jobSDPRE = JobEntity.builder().organization(findDEMP.get()).title("Sous Directeur Performances et Retour d’Expérience").parent(jobDEMP).grade(gradeSD).code("SO0234").build();
                jobSDPRE.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobSDPRE);
            }

            // Fin PTME

            if (findDCCMO.isPresent()) {
                var jobDCCMO = JobEntity.builder().organization(findDCCMO.get()).title("Directeur Central Commercial Marketing et Opérations").parent(finalJobDGA).grade(gradeDC).code("DCCMO").build();
                jobDCCMO.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDCCMO);

                var jobDCOA = JobEntity.builder().organization(findDCCMO.get()).title("Directeur Commercial et Operations Abidjan").parent(jobDCCMO).grade(gradeD).code("DCOA").build();
                jobDCOA.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDCOA);

                var organizationDCAO = organizationJpaRepository.findFirstByCode("DCOA");
                if (organizationDCAO.isPresent()) {
                    var jobDRABO = JobEntity.builder().organization(organizationDCAO.get()).title("Directeur Régional Abobo").parent(jobDCOA).grade(gradeDR).code("DRABO").build();
                    jobDRABO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRABO);

                    var jobDRAN = JobEntity.builder().organization(organizationDCAO.get()).title("Directeur Régional ABIDJAN NORD").parent(jobDCOA).grade(gradeDR).code("DRAN").build();
                    jobDRAN.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRAN);

                    var jobDRAS = JobEntity.builder().organization(organizationDCAO.get()).title("Directeur Régional ABIDJAN SUD").parent(jobDCOA).grade(gradeDR).code("DRAS").build();
                    jobDRAS.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRAS);

                    var jobDRYOP = JobEntity.builder().organization(organizationDCAO.get()).title("Directeur Régional YOPOUGON").parent(jobDCOA).grade(gradeDR).code("DRYOP").build();
                    jobDRYOP.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRYOP);
                }

                var jobDCOI = JobEntity.builder().organization(findDCCMO.get()).title("Directeur Commercial et Operations Intérieurs").parent(jobDCCMO).grade(gradeD).code("DCOI").build();
                jobDCOI.setId(Generators.timeBasedEpochGenerator().generate());
                add(jobDCOI);

                var organizationDCOI = organizationJpaRepository.findFirstByCode("DCOI");
                if (organizationDCOI.isPresent()) {
                    var jobDRCE = JobEntity.builder().organization(organizationDCOI.get()).title("Directeur Régional CENTRE").parent(jobDCOI).grade(gradeDR).code("DRCE").build();
                    jobDRCE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRCE);

                    var jobDRCO = JobEntity.builder().organization(organizationDCOI.get()).title("Directeur Régional CENTRE OUEST").parent(jobDCOI).grade(gradeDR).code("DRCO").build();
                    jobDRCO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRCO);

                    var jobDRCS = JobEntity.builder().organization(organizationDCOI.get()).title("Directeur Régional CENTRE SUD").parent(jobDCOI).grade(gradeDR).code("DRCS").build();
                    jobDRCS.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRCS);

                    var jobDRBC = JobEntity.builder().organization(organizationDCOI.get()).title("Directeur Régional CENTRE BASSE COTE").parent(jobDCOI).grade(gradeDR).code("DRBC").build();
                    jobDRBC.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRBC);

                    var jobDRES = JobEntity.builder().organization(organizationDCOI.get()).title("Directeur Régional EST").parent(jobDCOI).grade(gradeDR).code("DRES").build();
                    jobDRES.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRES);

                    var jobDRSE = JobEntity.builder().organization(organizationDCOI.get()).title("Directeur Régional SUD EST").parent(jobDCOI).grade(gradeDR).code("DRSE").build();
                    jobDRSE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRSE);

                    var jobDRSO = JobEntity.builder().organization(organizationDCOI.get()).title("Directeur Régional SUD OUEST").parent(jobDCOI).grade(gradeDR).code("DRSO").build();
                    jobDRSO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRSO);

                    var jobDRLO = JobEntity.builder().organization(organizationDCOI.get()).title("Directeur Régional LITTORAL OUEST").parent(jobDCOI).grade(gradeDR).code("DRLO").build();
                    jobDRLO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRLO);

                    var jobDRNO = JobEntity.builder().organization(organizationDCOI.get()).title("Directeur Régional NORD").parent(jobDCOI).grade(gradeDR).code("DRNO").build();
                    jobDRNO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRNO);

                    var jobDRO = JobEntity.builder().organization(organizationDCOI.get()).title("Directeur Régional OUEST").parent(jobDCOI).grade(gradeDR).code("DRO").build();
                    jobDRO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(jobDRO);
                }
            }
        }};

        var allJobs = jobJpaRepository.findAll();
        if (allJobs.isEmpty()) {
            jobJpaRepository.save(newJob);
            jobJpaRepository.save(jobDGA);
            jobJpaRepository.save(jobDGAPTME);
            jobJpaRepository.saveAll(jobEntities);
            System.out.println("=============================Job created");
        } else {
            System.out.println("=============================Job already exist");
        }
    }
}
