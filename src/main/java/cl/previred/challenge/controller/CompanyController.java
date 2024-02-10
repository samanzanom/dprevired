package cl.previred.challenge.controller;

import cl.previred.challenge.controller.dto.*;
import cl.previred.challenge.entity.Company;
import cl.previred.challenge.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/company", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }


    @Operation(summary = "Create a new company")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CompanyResponse.class)))
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping(value = "/")
    public ResponseEntity<CompanyResponse> login(@Valid @RequestBody CompanyRequest request) {
        Company response = companyService.create(request);
        return ResponseEntity.ok(new CompanyResponse(response.getRut(), response.getCompanyName(),response.getId()));
    }
}
