package com.cie.hr.domain.usecase;

import com.cie.hr.application.command.CloseScoreCardCommand;
import com.cie.hr.application.command.CreateScorecardCommand;
import com.cie.hr.application.command.ReCreatedScorecardCommand;
import com.cie.hr.application.command.UpdateScoreCardCommand;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
public interface ScorecardUseCases {
    UUID createScorecard(CreateScorecardCommand command);

    UUID updateScorecard(UpdateScoreCardCommand command) throws MessagingException, UnsupportedEncodingException;

    UUID closeScorecard(CloseScoreCardCommand command);

    UUID reCreatedScorecard(ReCreatedScorecardCommand command);
}
