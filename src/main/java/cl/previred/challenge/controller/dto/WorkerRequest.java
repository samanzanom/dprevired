package cl.previred.challenge.controller.dto;

import cl.previred.challenge.validation.ValidRut;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WorkerRequest(

        @Schema(description = "rut", example = "1-9")
        @NotBlank(message = "Rut cannot be blank")
        @ValidRut(message = "Invalid Rut")
        String rut,

        @Schema(description = "Worker Names", example = "Sebastian Amadiel")
        @NotBlank(message = "Names cannot be blank")
        @Size(min = 6, max = 200, message = "Names must be between 6 and 200 characters")
        String names,

        @Schema(description = "First surname", example = "Manzano")
        @NotBlank(message = "First surname cannot be blank")
        @Size(min = 6, max = 100, message = "First surname must be between 6 and 100 characters")
        String firstSurname,

        @Schema(description = "Second surname", example = "Manzano")
        @NotBlank(message = "Second surname cannot be blank")
        @Size(min = 6, max = 100, message = "First surname must be between 6 and 100 characters")
        String secondSurname,

        @Schema(description = "Company identifier", example = "pre20240211014921711")
        @NotBlank(message = "Company id cannot be blank")
        String companyId
) {}
