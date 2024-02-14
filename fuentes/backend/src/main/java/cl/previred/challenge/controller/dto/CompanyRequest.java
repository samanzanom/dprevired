package cl.previred.challenge.controller.dto;

import cl.previred.challenge.validation.ValidRut;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequest(
        @Schema(description = "rut", example = "1-9")
        @NotBlank(message = "Rut cannot be blank")
        @ValidRut(message = "Invalid Rut")
        String rut,

        @Schema(description = "companyName", example = "previred")
        @NotBlank(message = "Company name cannot be blank")
        @Size(min = 6, max = 50, message = "Company name must be between 6 and 50 characters")
        String companyName) {

}
