package com.cie.hr.application.controller;

import com.cie.hr.application.command.CreateUnitCommand;
import com.cie.hr.application.command.UpdateUnitCommand;
import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.domain.port.UnitsRepositoryPort;
import com.cie.hr.domain.usecase.UnitsUseCases;
import com.cie.hr.infrastructure.service.query.UnitsQuery;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 14/06/2023
 * @project hr-cie
 */
@RestController
@RequestMapping("/api/units")
@SecurityRequirement(name = "cie-hr-api")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Units APIs")
public class UnitsController {

    private final UnitsQuery unitsQuery;
    private final UnitsRepositoryPort unitsRepositoryPort;
    private final HandleRequestResponse handleRequestResponse;
    private final UnitsUseCases unitsUseCases;

    public UnitsController(UnitsQuery unitsQuery, UnitsRepositoryPort unitsRepositoryPort, HandleRequestResponse handleRequestResponse, UnitsUseCases unitsUseCases) {
        this.unitsQuery = unitsQuery;
        this.unitsRepositoryPort = unitsRepositoryPort;
        this.handleRequestResponse = handleRequestResponse;
        this.unitsUseCases = unitsUseCases;
    }

    @PostMapping
    public ResponseEntity<BaseResponseEntity<Object>> createUnit(@RequestBody CreateUnitCommand command) {
        return handleRequestResponse.handleRequest(() -> unitsUseCases.createUnit(command));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseEntity<Object>> updateUnit(@PathVariable("id") UUID id, @RequestBody CreateUnitCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            UpdateUnitCommand unitCommand = new UpdateUnitCommand(id, command);
            return unitsUseCases.updateUnit(unitCommand);
        });
    }

    @GetMapping
    public ResponseEntity<BaseResponseEntity<Object>> retrieveUnits() {
        return handleRequestResponse.handleRequest(unitsQuery::getAllUnits);
    }

    @GetMapping("/{statusId}")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveById(@PathVariable("statusId") UUID statusId) {
        return handleRequestResponse.handleRequest(() -> unitsRepositoryPort.findById(statusId));
    }
}
