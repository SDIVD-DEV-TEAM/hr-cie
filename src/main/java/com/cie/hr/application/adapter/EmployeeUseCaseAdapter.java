package com.cie.hr.application.adapter;

import com.cie.hr.application.command.*;
import com.cie.hr.common.event.CreateEmployeeEvent;
import com.cie.hr.common.event.OnResetPasswordEvent;
import com.cie.hr.common.event.publish.CreateEmployeeRequestMessagePublisher;
import com.cie.hr.common.event.publish.OnResetPasswordRequestMessagePublisher;
import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.security.port.CustomAuthenticationManager;
import com.cie.hr.common.security.service.LoginAttemptService;
import com.cie.hr.common.utils.CheckRHEmployee;
import com.cie.hr.common.utils.PasswordValidator;
import com.cie.hr.common.utils.ScorecardUtils;
import com.cie.hr.domain.entity.*;
import com.cie.hr.domain.port.*;
import com.cie.hr.domain.usecase.EmployeeUseCases;
import com.cie.hr.domain.valueobject.EmployeeId;
import com.fasterxml.uuid.Generators;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.cie.hr.common.constant.Constant.EMAIL_ALREADY_EXISTS;
import static com.cie.hr.common.constant.Constant.NO_USER_FOUND_BY_EMAIL;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
@Component
public class EmployeeUseCaseAdapter implements EmployeeUseCases {

    private final EmployeeRepositoryPort employeeRepository;

    private final DelegationRepositoryPort delegationRepositoryPort;

    private final ProfileRepositoryPort profileRepositoryPort;

    private final JobRepositoryPort jobRepositoryPort;

    private final CustomAuthenticationManager authenticatePort;

    private final DerogationRepositoryPort derogationRepositoryPort;

    private final CampaignRepositoryPort campaignRepositoryPort;

    private final ScoreCardRepositoryPort scoreCardRepositoryPort;

    private final StatusRepositoryPort statusRepositoryPort;

    private final CheckRHEmployee checkRHEmployee;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final OnResetPasswordRequestMessagePublisher onResetPasswordRequestMessagePublisher;

    private final CreateEmployeeRequestMessagePublisher createEmployeeRequestMessagePublisher;

    private final ScorecardUtils scorecardUtils;

    private final LoginAttemptService loginAttemptService;

    public EmployeeUseCaseAdapter(EmployeeRepositoryPort employeeRepository, DelegationRepositoryPort delegationRepositoryPort,
                                  ProfileRepositoryPort profileRepositoryPort, JobRepositoryPort jobRepositoryPort,
                                  CustomAuthenticationManager authenticatePort, DerogationRepositoryPort derogationRepositoryPort,
                                  CampaignRepositoryPort campaignRepositoryPort, ScoreCardRepositoryPort scoreCardRepositoryPort,
                                  StatusRepositoryPort statusRepositoryPort, CheckRHEmployee checkRHEmployee,
                                  OnResetPasswordRequestMessagePublisher onResetPasswordRequestMessagePublisher,
                                  CreateEmployeeRequestMessagePublisher createEmployeeRequestMessagePublisher,
                                  ScorecardUtils scorecardUtils, LoginAttemptService loginAttemptService) {
        this.employeeRepository = employeeRepository;
        this.delegationRepositoryPort = delegationRepositoryPort;
        this.profileRepositoryPort = profileRepositoryPort;
        this.jobRepositoryPort = jobRepositoryPort;
        this.authenticatePort = authenticatePort;
        this.derogationRepositoryPort = derogationRepositoryPort;
        this.campaignRepositoryPort = campaignRepositoryPort;
        this.scoreCardRepositoryPort = scoreCardRepositoryPort;
        this.statusRepositoryPort = statusRepositoryPort;
        this.checkRHEmployee = checkRHEmployee;
        this.onResetPasswordRequestMessagePublisher = onResetPasswordRequestMessagePublisher;
        this.createEmployeeRequestMessagePublisher = createEmployeeRequestMessagePublisher;
        this.scorecardUtils = scorecardUtils;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UUID createEmployee(CreateEmployeeCommand command) throws MessagingException, IOException {
        command.checkValidity();
        var currentUser = authenticatePort.getCurrentUser();

        var createdBy = employeeRepository.findByEmail(currentUser);
        Integer access;
        if (createdBy.isEmpty()) {
            throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
        } else {
            if (checkRHEmployee.employeeIsNotRH()) {
                throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
            }
        }

        // Get User Profile
        var profile = profileRepositoryPort.findById(command.profileId());

        if (profile.isEmpty()) {
            throw new ApplicationException("Ce profil n'existe pas");
        }

        var employeeNumber = command.employeeNumber();

        if (!employeeNumber.isBlank() && employeeNumber.length() > 7) {
            throw new ApplicationException("Le matricule ne doit pas excéder sept(07) caractères.");
        }

        if (!employeeNumber.isBlank()) {
            var checkUniqueNumber = employeeRepository.findByEmployeeNumber(command.employeeNumber());
            if (checkUniqueNumber.isPresent()) {
                throw new ApplicationException("Ce matricule existe déjà");
            }
        }

        var checkEmail = employeeRepository.findByEmail(command.email().strip().toLowerCase());
        if (checkEmail.isPresent()) {
            throw new ApplicationException(EMAIL_ALREADY_EXISTS);
        }

        Job job = null;
        if (command.jobId() != null) {
            var findJob = jobRepositoryPort.findById(command.jobId());
            if (findJob.isEmpty()) {
                throw new ApplicationException("Ce poste n'existe pas");
            }

            job = findJob.get();

            if (job.getEmployeeId() != null) {
                throw new ApplicationException("Un employé est déjà affecté à ce poste");
            }
        }

        var employeeId = new EmployeeId(Generators.timeBasedEpochGenerator().generate());

        try {
            access = command.accessLevel() == null ? null : Integer.valueOf(command.accessLevel());
        } catch (ApplicationException ex) {
            throw new ApplicationException("Le niveau d'accès doit être compris entre 0 et 4");
        }

        // Check if it's active campaign
        var checkCampaign = campaignRepositoryPort.findFirstByStatusCode("1");
        var status = statusRepositoryPort.findByCode("1");
        if (status.isEmpty()) {
            throw new ApplicationException("Ce statut n'existe pas");
        }

        var employee = EmployeeDomain.newBuilder()
                .employeeId(employeeId)
                .profile(profile.get())
                .email(command.email().strip().toLowerCase())
                .firstname(command.firstname())
                .lastname(command.lastname())
                .employeeNumber(employeeNumber.toUpperCase())
                .accessLevel(access)
                .active(true)
                .isNotLocked(true)
                .isFirstConnect(true)
                .isDeleted(false)
                .sendAccountIdEmail(checkCampaign.isPresent())
                .build();

        this.employeeRepository.save(employee);

        if (job != null) {
            job.setEmployeeId(employee);
            jobRepositoryPort.updateAndSave(job);
        }


        if (job != null && checkCampaign.isPresent()) {
            var grade = job.getGrade().getCode();
            var statusNotStarted = statusRepositoryPort.findByCode("0");

            if (statusNotStarted.isEmpty()) {
                throw new ApplicationException("Ce statut n'existe pas");
            }

            // Find if scorecard already exist
            var findScorecard = scoreCardRepositoryPort.findByAssessed_IdAndCampaignId(employee.getId().getValue(), checkCampaign.get().getId());

            findScorecard.ifPresent(scoreCardRepositoryPort::delete);

            boolean isManager = !grade.equals("CE");
            ScorecardDomain scorecard = isManager ? scorecardUtils.createScorecardForManager(job, checkCampaign.get(), employee, statusNotStarted.get()) : scorecardUtils.createScorecardForExpert(job, checkCampaign.get(), employee, statusNotStarted.get());
            this.scoreCardRepositoryPort.save(scorecard);
        }

        if (employee.sendAccountIdEmail()) {
            var event = new CreateEmployeeEvent(employee, ZonedDateTime.now(ZoneId.of("UTC")));
            createEmployeeRequestMessagePublisher.publish(event);
        }
        return employee.getId().getValue();
    }

    @Override
    public EmployeeDomain updateEmployee(UpdateEmployeeCommand command) {
        command.checkValidity();
        var currentUser = authenticatePort.getCurrentUser();

        var modifiedBy = employeeRepository.findByEmail(currentUser);

        if (modifiedBy.isEmpty()) {
            throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
        } else {
            if (checkRHEmployee.employeeIsNotRH()) {
                throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
            }
        }

        // Get User Profile
        var profile = profileRepositoryPort.findById(command.profileId());

        var employeeToUpdate = employeeRepository.findById(command.employeeId());

        if (employeeToUpdate.isEmpty()) {
            throw new ApplicationException("Employée introuvable");
        }

        if (profile.isEmpty()) {
            throw new ApplicationException("Ce profil n'existe pas");
        }

        var employeeNumber = command.employeeNumber();

        if (!employeeNumber.isBlank() && employeeNumber.length() > 7) {
            throw new ApplicationException("Le matricule ne doit pas excéder sept(07) caractères.");
        }

        var checkEmail = employeeRepository.findByEmail(command.email().strip().toLowerCase());
        if (checkEmail.isPresent() && !employeeToUpdate.get().id().equals(checkEmail.get().id())) {
            throw new ApplicationException(EMAIL_ALREADY_EXISTS);
        }

        if (!employeeNumber.isBlank()) {
            var checkUniqueNumber = employeeRepository.findByEmployeeNumber(command.employeeNumber());
            if (checkUniqueNumber.isPresent() && !employeeToUpdate.get().id().equals(checkUniqueNumber.get().id())) {
                throw new ApplicationException("Ce matricule existe déjà");
            }
        }

        var firstName = command.firstname().strip();
        if (Objects.equals(firstName, "")) {
            firstName = employeeToUpdate.get().firstname();
        }

        var lastName = command.lastname().strip();
        if (Objects.equals(lastName, "")) {
            lastName = employeeToUpdate.get().lastname();
        }

        Integer accessLevel = command.accessLevel() == null ? null : Integer.valueOf(command.accessLevel());

        var employee = EmployeeDomain.newBuilder()
                .profile(profile.get())
                .email(command.email().strip().toLowerCase())
                .firstname(firstName)
                .lastname(lastName)
                .employeeNumber(employeeNumber.toUpperCase())
                .accessLevel(accessLevel)
                .build();
        employee.setId(new EmployeeId(employeeToUpdate.get().id()));
        this.employeeRepository.updateAndSave(employee);

        return employee;
    }

    @Override
    public EmployeeDomain authentication(AuthenticationCommand command) {
        command.checkValidity();
        var email = command.email().strip().toLowerCase();
        var password = command.password();

        Boolean authenticate = this.authenticatePort.authenticate(email, password);
        if (authenticate) {
            Optional<EmployeeDomain> employee = this.employeeRepository.findByEmail(email);

            if (employee.isEmpty()) {
                throw new ApplicationException(NO_USER_FOUND_BY_EMAIL + email);
            }

            var dbEmployeeRepository = this.employeeRepository.findByEmail(email);
            if (dbEmployeeRepository.isPresent()) {
                return dbEmployeeRepository.get();
            } else {
                throw new ApplicationException(NO_USER_FOUND_BY_EMAIL + email);
            }
        } else {
            LOGGER.info("User is not authenticate");
            return null;
        }
    }

    @Override
    public UUID initChangePassword(ForgotPasswordCommand command) throws MessagingException, UnsupportedEncodingException {
        command.checkValidity();
        var email = command.email().strip().toLowerCase();
        Optional<EmployeeDomain> employee = this.employeeRepository.findByEmail(email);
        if (employee.isEmpty()) {
            throw new ApplicationException("Cet e-mail n'existe pas");
        }

        var event = new OnResetPasswordEvent(employee.get(), ZonedDateTime.now(ZoneId.of("UTC")));
        onResetPasswordRequestMessagePublisher.publish(event);

        return employee.get().id();
    }

    @Override
    public Boolean delegateNotation(DelegateNotationCommand command) {
        command.checkValidity();

        var currentUser = authenticatePort.getCurrentUser();

        var giver = employeeRepository.findByEmail(currentUser);
        if (giver.isEmpty()) {
            throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
        }
        EmployeeDomain giverEmployee = giver.get();

        ScorecardDomain findScorecard = scoreCardRepositoryPort.findByAssessed_IdAndCampaignId(command.employeeId(), command.campaignId()).orElse(null);
        if (findScorecard == null) {
            throw new ApplicationException("Cette employée n'a pas de fiche d'évaluation");
        }

        EmployeeDomain employee = findScorecard.assessed();
        Campaign campaign = findScorecard.campaign();

        Status status = campaign.getStatus();
        if (!status.getCode().equals("1")) {
            throw new ApplicationException("La délégation n'est possible que lors d'une campagne active");
        }

        EmployeeDomain receiver = employeeRepository.findById(command.receiverId()).orElse(null);

        // Find employee job
        Job employeeJob = jobRepositoryPort.findByEmployeeId(command.employeeId()).orElse(null);

        if (employeeJob == null) {
            throw new ApplicationException("Cet employée n'a pas de poste");
        }

        // Find receive job
        Job receiverJob = jobRepositoryPort.findByEmployeeId(command.receiverId()).orElse(null);

        if (receiverJob == null) {
            throw new ApplicationException("Le destinataire n'a pas de poste");
        }

        if (employeeJob.getGrade().getRank() != 1 && receiverJob.getGrade().getRank() >= employeeJob.getGrade().getRank()) {
            throw new ApplicationException("Un employé ne peut être noter que par un autre d'un grade supérieur au sien");
        }

        // Find if delegation is another delegation
        var checkDelegation = delegationRepositoryPort.findByEmployeeIdAndCampaignId(employee.id(), campaign.getId());

        if (checkDelegation.isPresent() && !checkDelegation.get().getDeleted()) {
            throw new ApplicationException("Il n'est pas possible de déléguer une délégation");
        }

        if (!giverEmployee.profile().getCode().equals("RH")) {
            Job giverJob = jobRepositoryPort.findByEmployeeId(giverEmployee.id()).orElse(null);
            if (giverJob == null) {
                throw new ApplicationException("Vous n'avez aucun poste");
            }
            if (!employeeJob.getParentId().equals(giverJob.getId())) {
                throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
            }
        }

        var delegation = Delegation.builder()
                .giver(giverEmployee)
                .receiver(receiver)
                .employee(employee)
                .campaign(campaign)
                .reason(command.reason())
                .deleted(false).build();

        this.delegationRepositoryPort.save(delegation);
        findScorecard.setManager(receiver);
        scoreCardRepositoryPort.updateAndSave(findScorecard);
        return true;
    }

    @Override
    public UUID giveDerogation(GiveDerogationCommand command) {
        command.checkValidity();

        // Check if employee is RH
        if (checkRHEmployee.employeeIsNotRH()) {
            throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
        }

        // Check if campaign is closed
        Optional<Campaign> campaign = campaignRepositoryPort.findFirstByStatusCodeAndDeletedFalseOrderByEndDateDesc("2");
        if (campaign.isEmpty()) {
            throw new ApplicationException("Aucune campagne n'est fermée");
        }

        Optional<Campaign> checkCampaign = campaignRepositoryPort.findById(command.campaignId());
        if (checkCampaign.isEmpty()) {
            throw new ApplicationException("Cette campagne n'existe pas");
        }

        if (!Objects.equals(campaign.get().getId(), command.campaignId())) {
            throw new ApplicationException("Impossible d'effectué une dérogation sur cette campagne");
        }

        // Check if employee exist
        Optional<EmployeeDomain> employee = employeeRepository.findById(command.employeeId());

        if (employee.isEmpty()) {
            throw new ApplicationException("Cet employée n'existe pas");
        }

        // Check if employee is already in derogation
        List<Derogation> checkDerogation = derogationRepositoryPort.findByEmployeeIdAndCampaignId(command.employeeId(), command.campaignId());
        if (checkDerogation.stream().anyMatch(e -> !e.getDeleted())) {
            throw new ApplicationException("Cette dérogation existe déjà");
        }

        // Check employee manager by job
        Job employeeJob = jobRepositoryPort.findByEmployeeId(employee.get().id()).orElse(null);
        if (employeeJob == null) {
            throw new ApplicationException("Cet employée n'a pas de poste");
        }

        // Check employee manager
        Job employeeManager = jobRepositoryPort.findById(employeeJob.getParentId()).orElse(null);
        if (employeeManager == null) {
            throw new ApplicationException("Cet employée n'a pas de manager");
        }

        try {
            var derogation = Derogation.builder()
                    .id(Generators.timeBasedEpochGenerator().generate())
                    .employee(employee.get())
                    .manager(employeeManager.getEmployeeId())
                    .campaign(checkCampaign.get())
                    .expiredAt(command.expiredAt())
                    .build();
            derogationRepositoryPort.save(derogation);
            return derogation.getId();
        } catch (Exception ex) {
            LOGGER.error("Error while giving derogation", ex);
            throw new ApplicationException("Impossible de donner une dérogation");
        }

    }

    @Override
    public Boolean removeDelegation(RemoveDelegationCommand command) {
        command.checkValidity();

        var currentUser = authenticatePort.getCurrentUser();

        var giver = employeeRepository.findByEmail(currentUser);
        if (giver.isEmpty()) {
            throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
        }
        EmployeeDomain giverEmployee = giver.get();
        Optional<Delegation> delegation = delegationRepositoryPort.findByEmployeeIdAndCampaignId(command.employeeId(), command.campaignId());

        Optional<ScorecardDomain> findScorecard = scoreCardRepositoryPort.findByAssessed_IdAndCampaignId(command.employeeId(), command.campaignId());

        if (findScorecard.isEmpty()) {
            throw new ApplicationException("Aucune fiche de notation pour cet employé");
        }

        ScorecardDomain scorecardDomain = findScorecard.get();
        Status status = scorecardDomain.status();

        if (delegation.isEmpty()) {
            throw new ApplicationException("Cette délégation n'existe pas");
        }

        Delegation currentDelegation = delegation.get();
        if (currentDelegation.getDeleted()) {
            throw new ApplicationException("Cette délégation n'existe pas");
        }

        if (command.remove()) {
            // Le manager annule la délégation
            EmployeeDomain delegationGiver = delegation.get().getGiver();
            EmployeeDomain delegationReceiver = delegation.get().getReceiver();
            if ((delegationGiver.id().equals(giverEmployee.id()) || delegationReceiver.id().equals(giverEmployee.id())) && status.getCode().equals("0")) {
                currentDelegation.setDeleted(true);
                delegationRepositoryPort.updateAndSave(currentDelegation);
                return true;
            } else {
                throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
            }
        } else {
            // L'employée refuse la délégation
            EmployeeDomain delegationReceiver = delegation.get().getReceiver();
            if (delegationReceiver.id().equals(giverEmployee.id()) && status.getCode().equals("0")) {
                scorecardDomain.setManager(null);
                scoreCardRepositoryPort.updateAndSave(scorecardDomain);
                return delegationRepositoryPort.deleteDelegation(delegation.get().getId());
            } else {
                throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
            }
        }
    }

    @Override
    public Boolean removeDerogation(RemoveDerogationCommand command) {
        command.checkValidity();

        if (checkRHEmployee.employeeIsNotRH()) {
            throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
        }

        List<Derogation> derogation = derogationRepositoryPort.findByEmployeeIdAndCampaignId(command.employeeId(), command.campaignId());
        if (derogation.isEmpty()) {
            throw new ApplicationException("Cette dérogation n'existe pas");
        }

        try {
            Derogation currentDerogation = derogation.stream().filter(e -> !e.getDeleted()).findFirst().orElse(null);
            if (currentDerogation == null) {
                throw new ApplicationException("Cette dérogation n'existe pas");
            }
            currentDerogation.setDeleted(true);
            derogationRepositoryPort.updateAndSave(currentDerogation);
            return true;
        } catch (ApplicationException ex) {
            throw new ApplicationException(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Error while removing derogation", ex);
            throw new ApplicationException("Impossible de supprimer la dérogation");
        }
    }

    @Override
    public UUID resetPassword(ResetPasswordCommand command) {
        command.checkValidity();
        var password = command.newPassword();
        var token = command.token().toUpperCase();
        var email = command.email().strip().toLowerCase();

        boolean checkStrongPassword = PasswordValidator.isStrongPassword(password);
        if (!checkStrongPassword) {
            throw new ApplicationException("Le mot de passe n'est pas conforme aux règle de sécurité");
        }
        var checkEmployee = this.employeeRepository.findByEmail(email);
        if (checkEmployee.isEmpty()) {
            throw new ApplicationException("Cet employée n'existe pas");
        }

        if (!checkEmployee.get().token().equals(token)) {
            throw new ApplicationException("Code de confirmation incorrect");
        }

        EmployeeDomain employeeDomain = checkEmployee.get();
        String newPasswordEncode = this.authenticatePort.encodePassword(password);
        employeeDomain.setPassword(newPasswordEncode);

        var optionalPassword = employeeDomain.passwordStores().stream().
                filter(passwordStore -> authenticatePort.matches(password, passwordStore.getLastPassword())).findFirst();

        if (optionalPassword.isPresent()) {
            throw new ApplicationException("Ce mot de passe est déjà utiliser");
        }

        employeeDomain.addNewPasswordToAStore(newPasswordEncode);

        employeeDomain.saveToken(null);
        employeeDomain.destroyExpireDate();
        employeeDomain.setFirstConnect(false);
        employeeRepository.updateAndSave(employeeDomain);

        return employeeDomain.id();
    }

    @Override
    public Boolean deleteEmployee(DeleteEmployeeCommand command) {
        command.checkValidity();
        var currentUser = authenticatePort.getCurrentUser();

        var currentEmployee = employeeRepository.findByEmail(currentUser);
        if (currentEmployee.isEmpty()) {
            throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
        }

        if (checkRHEmployee.employeeIsNotRH()) {
            throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
        }

        var employeeToDelete = employeeRepository.findById(command.id());
        if (employeeToDelete.isPresent()) {
            EmployeeDomain employeeDomain = employeeToDelete.get();
            employeeDomain.setIsDeleted(true);
            employeeRepository.updateAndSave(employeeDomain);

            // Check if it's active campaign
            var checkCampaign = campaignRepositoryPort.findFirstByStatusCode("1");
            var status = statusRepositoryPort.findByCode("1");
            if (status.isEmpty()) {
                throw new ApplicationException("Ce statut n'existe pas");
            }

            if (checkCampaign.isPresent()) {
                var findScorecard = scoreCardRepositoryPort.findByAssessed_IdAndCampaignId(employeeDomain.id(), checkCampaign.get().getId());
                if (findScorecard.isPresent()) {
                    Status scorecardStatus = findScorecard.get().status();
                    if (!scorecardStatus.getCode().equals("2")) scoreCardRepositoryPort.delete(findScorecard.get());
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Boolean reactivateEmployee(ReactivateUserCommand command) {
        try {
            command.checkValidity();
            var currentUser = authenticatePort.getCurrentUser();

            var currentEmployee = employeeRepository.findByEmail(currentUser);
            if (currentEmployee.isEmpty()) {
                throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
            }

            if (checkRHEmployee.employeeIsNotRH()) {
                throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
            }

            var employeeToReactivate = employeeRepository.findById(command.employeeId());
            if (employeeToReactivate.isPresent()) {
                EmployeeDomain employeeDomain = employeeToReactivate.get();

                LOGGER.info("Employee {} is before reactivating", employeeDomain);
                if (employeeDomain.isNotLocked()) {
                    LOGGER.info("Employee {} is already active", employeeDomain.isNotLocked());
                    throw new ApplicationException("Cet employé est déjà actif");
                }

                employeeDomain.setIsDeleted(false);
                employeeDomain.setLocked(true);
                employeeRepository.updateAndSave(employeeDomain);
                LOGGER.info("Employee {} is reactivated", employeeDomain);
                loginAttemptService.evictUserFromLoginAttemptCache(employeeDomain.email());
                return true;
            }
        } catch (Exception ex) {
            LOGGER.error("Error while reactivating employee", ex);
        }
        return false;
    }
}
