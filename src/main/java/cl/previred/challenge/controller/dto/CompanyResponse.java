package cl.previred.challenge.controller.dto;

import cl.previred.challenge.entity.Company;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyResponse(
        @Schema(description = "rut", example = "1-9")
        String rut,

        @Schema(description = "Company Name", example = "previred")
        String companyName,

        @Schema(description = "Unique Identifier", example = "pre-2024011022334422")
        String uniqueIdentifier
) {

        public static CompanyResponse convertToDTO(Company company) {
                return new CompanyResponse(company.getRut(), company.getCompanyName(), company.getId());
        }
}
