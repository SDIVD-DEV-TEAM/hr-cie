package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.OrganizationEntity;
import com.cie.hr.infrastructure.entity.OrganizationTypeEntity;
import com.cie.hr.infrastructure.repository.OrganizationJpaRepository;
import com.cie.hr.infrastructure.repository.OrganizationTypeJpaRepository;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Order(3)
@Component
public class OrganisationBootstrapCommandLineRunner implements CommandLineRunner {

    private final OrganizationJpaRepository organizationJpaRepository;
    private final OrganizationTypeJpaRepository organizationTypeJpaRepository;

    public OrganisationBootstrapCommandLineRunner(OrganizationJpaRepository organizationJpaRepository, OrganizationTypeJpaRepository organizationTypeJpaRepository) {
        this.organizationJpaRepository = organizationJpaRepository;
        this.organizationTypeJpaRepository = organizationTypeJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<OrganizationEntity> organizationEntities = new ArrayList<>() {{
            var optionalPoleOrganizationType = organizationTypeJpaRepository.findByCode("1");
            OrganizationTypeEntity poleOrganizationType;
            if (optionalPoleOrganizationType.isPresent()) {
                poleOrganizationType = optionalPoleOrganizationType.get();
                var poleDG = OrganizationEntity.builder().name("Direction Générale").code("Pôle DG").type(poleOrganizationType).parent(null).build();
                poleDG.setId(Generators.timeBasedEpochGenerator().generate());
                var poleSG = OrganizationEntity.builder().name("Sécrétariat Général").code("Pôle SG").type(poleOrganizationType).parent(poleDG).build();
                poleSG.setId(Generators.timeBasedEpochGenerator().generate());
                var poleAGF = OrganizationEntity.builder().name("Administration Gestion Finances").code("Pôle AGF").type(poleOrganizationType).parent(poleDG).build();
                poleAGF.setId(Generators.timeBasedEpochGenerator().generate());
                var polePTME = OrganizationEntity.builder().name("Production Transport et Mouvement d'Energie").code("Pôle PTME").type(poleOrganizationType).parent(poleDG).build();
                polePTME.setId(Generators.timeBasedEpochGenerator().generate());
                var poleSC = OrganizationEntity.builder().name("Supply Chain").code("Pôle SC").type(poleOrganizationType).parent(poleDG).build();
                poleSC.setId(Generators.timeBasedEpochGenerator().generate());
                var poleDC = OrganizationEntity.builder().name("Distribution et Commercialisation").code("Pôle DC").type(poleOrganizationType).parent(poleDG).build();
                poleDC.setId(Generators.timeBasedEpochGenerator().generate());

                add(poleDG);
                add(poleSG);
                add(polePTME);
                add(poleAGF);
                add(poleDC);
                add(poleSC);

                var organizationTypeDC = organizationTypeJpaRepository.findByCode("2");
                var organizationTypeD = organizationTypeJpaRepository.findByCode("3");
                var organizationTypeDA = organizationTypeJpaRepository.findByCode("4");
                var organizationTypeSD = organizationTypeJpaRepository.findByCode("5");
                var organizationTypeDR = organizationTypeJpaRepository.findByCode("6");

                OrganizationEntity directionDCPIQP = null;
                OrganizationEntity directionDSTD;
                OrganizationEntity directionCME;
                OrganizationEntity directionDCEGPS = null;
                OrganizationEntity directionDCRH = null;
                OrganizationEntity directionDCTET = null;
                OrganizationEntity directionDCRD = null;
                OrganizationEntity directionDCCMO = null;
                OrganizationEntity directionDCOI = null;
                OrganizationEntity directionDCOA = null;
                OrganizationEntity directionDETE = null;
                OrganizationEntity directionDPE = null;
                OrganizationEntity directionDME = null;
                OrganizationEntity directionDAOT = null;
                OrganizationEntity directionDMP = null;
                OrganizationEntity directionDCE = null;

                OrganizationTypeEntity directionOrganizationType;

                if (organizationTypeDC.isPresent()) {
                    directionOrganizationType = organizationTypeDC.get();
                    directionDCPIQP = OrganizationEntity.builder().name("Direction Centrale Planification, Ingénierie et Qualité du Produit").code("DCPIQP").parent(poleDG).type(directionOrganizationType).build();
                    directionDCPIQP.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCPIQP);

                    directionDSTD = OrganizationEntity.builder().name("Direction de la Stratégie et de la Transformation Digitale").code("DSTD").type(directionOrganizationType).parent(poleDG).build();
                    directionDSTD.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDSTD);

                    directionDCEGPS = OrganizationEntity.builder().name("Direction Centrale des Etudes Générales et de la Planification Stratégique").code("DCEGPS").type(directionOrganizationType).parent(poleDG).build();
                    directionDCEGPS.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCEGPS);

                    directionDCRH = OrganizationEntity.builder().name("Direction Centrale des Ressources Humaines").code("DCRH").type(directionOrganizationType).parent(poleSG).build();
                    directionDCRH.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCRH);

                    directionDCTET = OrganizationEntity.builder().name("Direction Centrale des Transports d'Energie et Télécommunications").code("DCTET01").type(directionOrganizationType).parent(polePTME).build();
                    directionDCTET.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCTET);

                    directionDCRD = OrganizationEntity.builder().name("Direction Centrale Réseau de Distribution").code("DCRD").type(directionOrganizationType).parent(poleDC).build();
                    directionDCRD.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCRD);

                    directionDCCMO = OrganizationEntity.builder().name("Direction Centrale Commercial Marketing et Opérations").code("DCCMO").type(directionOrganizationType).parent(poleDC).build();
                    directionDCCMO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCCMO);
                }

                if (organizationTypeD.isPresent()) {
                    directionOrganizationType = organizationTypeD.get();
                    directionCME = OrganizationEntity.builder().name("Centre des Metiers de l'Electricité").code("CME").parent(poleDG).type(directionOrganizationType).build();
                    directionCME.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionCME);

                    var directionDO = OrganizationEntity.builder().name("Direction de l'Organisation").code("DO").parent(directionDCEGPS).type(directionOrganizationType).build();
                    directionDO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDO);

                    var directionDRI = OrganizationEntity.builder().name("Direction des Relations Institutionnelles").code("DRI").parent(poleDG).type(directionOrganizationType).build();
                    directionDRI.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRI);

                    var directionDST = OrganizationEntity.builder().name("Direction  de la Sécurité au Travail").code("DST").parent(poleDG).type(directionOrganizationType).build();
                    directionDST.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDST);

                    var directionDCEM = OrganizationEntity.builder().name("Direction de la Communication Externe et de la Marque").code("DCEM").parent(poleDG).type(directionOrganizationType).build();
                    directionDCEM.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCEM);

                    var directionDPTI = OrganizationEntity.builder().name("Direction de la Planification Technique et de l'Innovation").code("DPTI").parent(directionDCPIQP).type(directionOrganizationType).build();
                    directionDPTI.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDPTI);

                    var directionDSQP = OrganizationEntity.builder().name("Direction de la Stratégie de la Qualité du Produit").code("DSQP").parent(directionDCPIQP).type(directionOrganizationType).build();
                    directionDSQP.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDSQP);

                    var directionDIT = OrganizationEntity.builder().name("Direction de l'Ingénierie et des Travaux").code("DIT").parent(directionDCPIQP).type(directionOrganizationType).build();
                    directionDIT.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDIT);

                    var directionDPS = OrganizationEntity.builder().name("Direction des Prestations Sociales").code("DPS").parent(poleSG).type(directionOrganizationType).build();
                    directionDPS.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDPS);

                    var directionDECCI = OrganizationEntity.builder().name("Directeur en charge du Control Interne").code("DECCI").parent(poleSG).type(directionOrganizationType).build();
                    directionDECCI.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDECCI);

                    var directionDDRC = OrganizationEntity.builder().name("Direction de la Documentation, RSE et Conformité").code("DDRC").parent(poleSG).type(directionOrganizationType).build();
                    directionDDRC.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDDRC);

                    var directionDARH = OrganizationEntity.builder().name("Direction Administration des Ressources Humaines").code("DARH").parent(directionDCRH).type(directionOrganizationType).build();
                    directionDARH.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDARH);

                    var directionDMT = OrganizationEntity.builder().name("Direction de la Médecine du Travail").code("DMT").parent(directionDCRH).type(directionOrganizationType).build();
                    directionDMT.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDMT);

                    var directionDDCI = OrganizationEntity.builder().name("Direction de la Communication Interne").code("DDCCI").parent(directionDCRH).type(directionOrganizationType).build();
                    directionDDCI.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDDCI);

                    var directionDDRH = OrganizationEntity.builder().name("Direction du Développement des Ressources Humaines").code("DDRH").parent(directionDCRH).type(directionOrganizationType).build();
                    directionDDRH.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDDRH);

                    var directionDQPMP = OrganizationEntity.builder().name("Direction des Etudes, Méthodes et Projets").code("DEMP0001").parent(polePTME).type(directionOrganizationType).build();
                    directionDQPMP.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDQPMP);

                    directionDPE = OrganizationEntity.builder().name("Direction de la Production d'Electricité").code("DPE0001").parent(polePTME).type(directionOrganizationType).build();
                    directionDPE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDPE);

                    directionDME = OrganizationEntity.builder().name("Direction des Mouvements d'Energie").code("DME0001").parent(polePTME).type(directionOrganizationType).build();
                    directionDME.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDME);

                    directionDETE = OrganizationEntity.builder().name("Direction Exploitation du Transport d'Energie").code("DEXT0001").parent(directionDCTET).type(directionOrganizationType).build();
                    directionDETE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDETE);

                    directionDAOT = OrganizationEntity.builder().name("Direction Appui Opérationnel et Télécommunications").code("DAOT0001").parent(directionDCTET).type(directionOrganizationType).build();
                    directionDAOT.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDAOT);

                    var directionDAS = OrganizationEntity.builder().name("Direction des Approvisionnements et des Stocks").code("DAS").parent(poleSC).type(directionOrganizationType).build();
                    directionDAS.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDAS);

                    var directionDDA = OrganizationEntity.builder().name("Direction des Achats").code("DDA").parent(poleSC).type(directionOrganizationType).build();
                    directionDDA.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDDA);

                    var directionDPPDC = OrganizationEntity.builder().name("Direction des Projets du Pôle DC").code("DPPDC").parent(poleDC).type(directionOrganizationType).build();
                    directionDPPDC.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDPPDC);

                    var directionDRHO = OrganizationEntity.builder().name("Directeur en charge des Ressources Humaines Opérationnelles").code("DRHO").parent(poleDC).type(directionOrganizationType).build();
                    directionDRHO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRHO);

                    var directionDDE = OrganizationEntity.builder().name("Direction de l'Exploitation").code("DDE").parent(directionDCRD).type(directionOrganizationType).build();
                    directionDDE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDDE);

                    var directionDDLM = OrganizationEntity.builder().name("Direction de la Maintenance").code("DDLM").parent(directionDCRD).type(directionOrganizationType).build();
                    directionDDLM.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDDLM);

                    var directionDCCB = OrganizationEntity.builder().name("Direction Commercial Client Business").code("DCCB").parent(directionDCCMO).type(directionOrganizationType).build();
                    directionDCCB.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCCB);

                    var directionDCCI = OrganizationEntity.builder().name("Direction Commerciale Clients Institutionnels").code("DCCI").parent(directionDCCMO).type(directionOrganizationType).build();
                    directionDCCI.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCCI);

                    var directionDEP = OrganizationEntity.builder().name("Direction Eclairage Public").code("DEP").parent(directionDCCMO).type(directionOrganizationType).build();
                    directionDEP.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDEP);

                    var directionDDLF = OrganizationEntity.builder().name("Direction de la Facturation").code("DDLF").parent(directionDCCMO).type(directionOrganizationType).build();
                    directionDDLF.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDDLF);

                    directionDCOA = OrganizationEntity.builder().name("Direction Commerciale et Operations Abidjan").code("DCOA").parent(directionDCCMO).type(directionOrganizationType).build();
                    directionDCOA.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCOA);

                    directionDCOI = OrganizationEntity.builder().name("Direction Commerciale et Operations Intérieurs").code("DCOI").parent(directionDCCMO).type(directionOrganizationType).build();
                    directionDCOI.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCOI);

                    var directionDMRC = OrganizationEntity.builder().name("Direction Marketing et Relation Client").code("DMRC").parent(directionDCCMO).type(directionOrganizationType).build();
                    directionDMRC.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDMRC);

                    var directionDP = OrganizationEntity.builder().name("Direction du Patrimoine").code("DP").parent(poleAGF).type(directionOrganizationType).build();
                    directionDP.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDP);

                    var directionDII = OrganizationEntity.builder().name("Direction de l'Ingénierie Immobilière").code("DII").parent(poleAGF).type(directionOrganizationType).build();
                    directionDII.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDII);

                    var directionDCGB = OrganizationEntity.builder().name("Direction du Contrôle de Gestion et du Budget").code("DCGB").parent(poleAGF).type(directionOrganizationType).build();
                    directionDCGB.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCGB);

                    var directionDFC = OrganizationEntity.builder().name("Direction Finance Comptabilité").code("DFC").parent(poleAGF).type(directionOrganizationType).build();
                    directionDFC.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDFC);
                }

                if (organizationTypeDA.isPresent()) {
                    OrganizationTypeEntity typeDA = organizationTypeDA.get();

                    var directionDAPE = OrganizationEntity.builder().name("Direction Adjointe de la Production d'Electricité").code("DPE0053").parent(directionDPE).type(typeDA).build();
                    directionDAPE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDAPE);

                    var directionDARSQSE = OrganizationEntity.builder().name("Direction Adjointe Chargée de l’Exploitation et de la Maintenance RSQSE-RSE").code("DPE0048").parent(directionDPE).type(typeDA).build();
                    directionDARSQSE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDARSQSE);

                    directionDCE = OrganizationEntity.builder().name("Direction Adjointe Chargée de l’Exploitation").code("DME0019").parent(directionDME).type(typeDA).build();
                    directionDCE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDCE);

                    directionDMP = OrganizationEntity.builder().name("Direction Adjointe Chargée du Management des Projets").code("DEMP0002").parent(directionDME).type(typeDA).build();
                    directionDMP.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDMP);
                }

                if (organizationTypeSD.isPresent()) {
                    OrganizationTypeEntity typeDR = organizationTypeSD.get();

                    var sousDirectionMGB = OrganizationEntity.builder().name("Sous Direction Moyens Généraux et Ressources").code("DGAP0001").parent(polePTME).type(typeDR).build();
                    sousDirectionMGB.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionMGB);

                    var sousDirectionCG = OrganizationEntity.builder().name("Sous Direction Chargée du Contrôle de Gestion").code("DGAP0002").parent(polePTME).type(typeDR).build();
                    sousDirectionCG.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionCG);

                    var sousDirectionExp = OrganizationEntity.builder().name("Sous Direction Exploitation").code("DEXT0002").parent(directionDETE).type(typeDR).build();
                    sousDirectionExp.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionExp);

                    var sousDirectionET = OrganizationEntity.builder().name("Sous Direction Etudes et Travaux").code("DAOT0002").parent(directionDAOT).type(typeDR).build();
                    sousDirectionET.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionET);

                    var sousDirectionMa = OrganizationEntity.builder().name("Sous Direction Maintenance").code("DAOT0007").parent(directionDAOT).type(typeDR).build();
                    sousDirectionMa.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionMa);

                    var sousDirectionTC = OrganizationEntity.builder().name("Sous Direction Télé conduite").code("DAOT0017").parent(directionDAOT).type(typeDR).build();
                    sousDirectionTC.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionTC);

                    var sousDirectionTel = OrganizationEntity.builder().name("Sous Direction Télécommunications").code("DAOT0025").parent(directionDAOT).type(typeDR).build();
                    sousDirectionTel.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionTel);

                    var sousDirectionESE = OrganizationEntity.builder().name("Sous Direction Etudes et Stratégies d'Exploitation").code("DEMP0003").parent(directionDMP).type(typeDR).build();
                    sousDirectionESE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionESE);

                    var sousDirectionPRE = OrganizationEntity.builder().name("Sous Direction Perfomances et Retour d'Expérience").code("DEMP0004").parent(directionDMP).type(typeDR).build();
                    sousDirectionPRE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionPRE);

                    var sousDirectionDE = OrganizationEntity.builder().name("Sous Direction Etudes et Retour d'Expérience").code("DME0003").parent(directionDME).type(typeDR).build();
                    sousDirectionDE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionDE);

                    var sousDirectionMT = OrganizationEntity.builder().name("Sous Direction Moyens Techniques").code("DME0006").parent(directionDME).type(typeDR).build();
                    sousDirectionMT.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionMT);

                    var sousDirectionSE = OrganizationEntity.builder().name("Sous Direction Exploitation du Système Electrique").code("DME0009").parent(directionDCE).type(typeDR).build();
                    sousDirectionSE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionSE);

                    var sousDirectionAO = OrganizationEntity.builder().name("Sous Direction Appui Opérationnel").code("DPE0026").parent(directionDPE).type(typeDR).build();
                    sousDirectionAO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionAO);

                    var sousDirectionAY = OrganizationEntity.builder().name("Direction d'Usine Hydroélectrique AYAME").code("DPE0005").parent(directionDPE).type(typeDR).build();
                    sousDirectionAY.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionAY);

                    var sousDirectionKS = OrganizationEntity.builder().name("Direction d'Usine Hydroélectrique KOSSOU").code("DPE0041").parent(directionDPE).type(typeDR).build();
                    sousDirectionKS.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionKS);

                    var sousDirectionTA = OrganizationEntity.builder().name("Direction d'Usine Hydroélectrique TAABO").code("DPE0047").parent(directionDPE).type(typeDR).build();
                    sousDirectionTA.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionTA);

                    var sousDirectionBU = OrganizationEntity.builder().name("Direction d'Usine Hydroélectrique BUYO FAYE").code("DPE0033").parent(directionDPE).type(typeDR).build();
                    sousDirectionBU.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionBU);

                    var sousDirectionVR = OrganizationEntity.builder().name("Direction d'Usine Turbine à Gaz VRIDI 1").code("DPE0030").parent(directionDPE).type(typeDR).build();
                    sousDirectionVR.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionVR);

                    var sousDirectionIRE = OrganizationEntity.builder().name("Sous Direction Ingénierie et Retour d’Expérience").code("DPE0023").parent(directionDPE).type(typeDR).build();
                    sousDirectionIRE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(sousDirectionIRE);
                }

                if (organizationTypeDR.isPresent()) {
                    OrganizationTypeEntity organizationTypeEntity = organizationTypeDR.get();

                    var directionDRABO = OrganizationEntity.builder().name("Direction Régionale ABOBO").code("DRABO").parent(directionDCOA).type(organizationTypeEntity).build();
                    directionDRABO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRABO);

                    var directionDRAN = OrganizationEntity.builder().name("Direction Régionale ABIDJAN NORD").code("DRAN").parent(directionDCOA).type(organizationTypeEntity).build();
                    directionDRAN.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRAN);

                    var directionDRAS = OrganizationEntity.builder().name("Direction Régionale ABIDJAN SUD").code("DRAS").parent(directionDCOA).type(organizationTypeEntity).build();
                    directionDRAS.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRAS);

                    var directionDRYOP = OrganizationEntity.builder().name("Direction Régionale YOPOUGON").code("DRYOP").parent(directionDCOA).type(organizationTypeEntity).build();
                    directionDRYOP.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRYOP);

                    var directionDRCE = OrganizationEntity.builder().name("Direction Régionale CENTRE").code("DRCE").parent(directionDCOI).type(organizationTypeEntity).build();
                    directionDRCE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRCE);

                    var directionDRCO = OrganizationEntity.builder().name("Direction Régionale CENTRE OUEST").code("DRCO").parent(directionDCOI).type(organizationTypeEntity).build();
                    directionDRCO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRCO);

                    var directionDRCS = OrganizationEntity.builder().name("Direction Régionale CENTRE SUD").code("DRCS").parent(directionDCOI).type(organizationTypeEntity).build();
                    directionDRCS.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRCS);

                    var directionDRBC = OrganizationEntity.builder().name("Direction Régionale BASSE COTE").code("DRBC").parent(directionDCOI).type(organizationTypeEntity).build();
                    directionDRBC.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRBC);

                    var directionDRES = OrganizationEntity.builder().name("Direction Régionale EST").code("DRES").parent(directionDCOI).type(organizationTypeEntity).build();
                    directionDRES.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRES);

                    var directionDRSE = OrganizationEntity.builder().name("Direction Régionale SUD EST").code("DRSE").parent(directionDCOI).type(organizationTypeEntity).build();
                    directionDRSE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRSE);

                    var directionDRSO = OrganizationEntity.builder().name("Direction Régionale SUD OUEST").code("DRSO").parent(directionDCOI).type(organizationTypeEntity).build();
                    directionDRSO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRSO);

                    var directionDRLO = OrganizationEntity.builder().name("Direction Régionale LITTORAL OUEST").code("DRLO").parent(directionDCOI).type(organizationTypeEntity).build();
                    directionDRLO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRLO);

                    var directionDRNO = OrganizationEntity.builder().name("Direction Régionale NORD").code("DRNO").parent(directionDCOI).type(organizationTypeEntity).build();
                    directionDRNO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRNO);

                    var directionDRO = OrganizationEntity.builder().name("Direction Régionale OUEST").code("DRO").parent(directionDCOI).type(organizationTypeEntity).build();
                    directionDRO.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRO);

                    var directionDRTET_ABJ = OrganizationEntity.builder().name("Direction Régionale du Transport d'Energie et des Télécommunications ABIDJAN").code("DRTA0001").parent(directionDETE).type(organizationTypeEntity).build();
                    directionDRTET_ABJ.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRTET_ABJ);

                    var directionDRTET_BKE = OrganizationEntity.builder().name("Direction Régionale du Transport d'Energie et des Télécommunications BOUAKE").code("DRTB0001").parent(directionDETE).type(organizationTypeEntity).build();
                    directionDRTET_BKE.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRTET_BKE);

                    var directionDRTET_MAN = OrganizationEntity.builder().name("Direction Régionale du Transport d'Energie et des Télécommunications MAN").code("DRTM0001").parent(directionDETE).type(organizationTypeEntity).build();
                    directionDRTET_MAN.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRTET_MAN);

                    var directionDRTET_ABG = OrganizationEntity.builder().name("Direction Régionale du Transport d'Energie et des Télécommunications ABENGOUROU").code("DRTE0001").parent(directionDETE).type(organizationTypeEntity).build();
                    directionDRTET_ABG.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRTET_ABG);

                    var directionDRTET_KOR = OrganizationEntity.builder().name("Direction Régionale du Transport d'Energie et des Télécommunications KORHOGO").code("DRTK0001").parent(directionDETE).type(organizationTypeEntity).build();
                    directionDRTET_KOR.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRTET_KOR);

                    var directionDRTET_SBR = OrganizationEntity.builder().name("Direction Régionale du Transport d'Energie et des Télécommunications SOUBRE").code("DRTS0001").parent(directionDETE).type(organizationTypeEntity).build();
                    directionDRTET_SBR.setId(Generators.timeBasedEpochGenerator().generate());
                    add(directionDRTET_SBR);
                }
            }
        }};

        if (organizationJpaRepository.findAll().isEmpty()) {
            organizationJpaRepository.saveAll(organizationEntities);
        }
    }
}
