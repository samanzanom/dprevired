package cl.previred.challenge.controller;

import cl.previred.challenge.controller.dto.*;
import cl.previred.challenge.entity.Company;
import cl.previred.challenge.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    @PostMapping(value = "")
    public ResponseEntity<CompanyResponse> create(@Valid @RequestBody CompanyRequest request) {
        Company response = companyService.create(request);
        return ResponseEntity.ok(new CompanyResponse(response.getId(), response.getRut(), response.getCompanyName()));
    }

    @Operation(summary = "Update company")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CompanyResponse.class)))
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PutMapping(value = "/{id}")
    public ResponseEntity<CompanyResponse> update(@PathVariable String id, @Valid @RequestBody CompanyRequest request) {
        Company response = companyService.update(id,request);
        return ResponseEntity.ok(new CompanyResponse(response.getId(), response.getRut(), response.getCompanyName()));
    }

    @Operation(summary = "Delete company")
    @ApiResponse(responseCode = "204", description = "Company deleted successfully", content = @Content)
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get company")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CompanyResponse.class)))
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping(value = "/{id}")
    public ResponseEntity<CompanyResponse> get(@PathVariable String id) {
        Company company = companyService.get(id);
        return ResponseEntity.ok(new CompanyResponse(company.getId(), company.getRut(), company.getCompanyName()));
    }

    @Operation(summary = "List company")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CompanyPageResponse.class)))
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping(value = "")
    public  ResponseEntity<CompanyPageResponse> list(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Sort.Direction sortDirection = Sort.Direction.fromString("ASC");
        Pageable paging = PageRequest.of(page - 1, size, Sort.by(sortDirection, "rut"));
        Page<Company> pageCompanies = companyService.findAll(paging);

        CompanyPageResponse response = new CompanyPageResponse(
                convertToDTOs(pageCompanies.getContent()),
                pageCompanies.getNumber() + 1,
                (int) pageCompanies.getTotalElements(),
                pageCompanies.getTotalPages(),
                pageCompanies.getSize()
        );

        return ResponseEntity.ok(response);
    }

    private List<CompanyResponse> convertToDTOs(List<Company> companies) {
        return companies.stream()
                .map(CompanyResponse::convertToDTO)
                .collect(Collectors.toList());
    }
}
