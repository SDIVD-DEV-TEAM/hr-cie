package com.cie.hr.domain.usecase;

import com.cie.hr.application.command.*;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 08/05/2023
 * @project hr
 */
public interface JobUseCases {
    UUID createJob(CreateJobCommand command);

    UUID updateJob(UpdateJobCommand command);

    Boolean deleteJob(DeleteJobCommand command);

    Boolean assignJobToEmployee(AssignJobToEmployeeCommand command);

    UUID updateJobScorecard(UpdateJobScorecard command);
}
