package cl.previred.challenge.controller.dto;

import cl.previred.challenge.entity.Company;
import io.swagger.v3.oas.annotations.media.Schema;

public record CompanyResponse(
        @Schema(description = "id", example = "pre-2024011022334422")
        String id,
        @Schema(description = "rut", example = "1-9")
        String rut,

        @Schema(description = "Company Name", example = "previred")
        String companyName
) {

        public static CompanyResponse convertToDTO(Company company) {
                return new CompanyResponse(company.getId(),company.getRut(), company.getCompanyName());
        }
}
