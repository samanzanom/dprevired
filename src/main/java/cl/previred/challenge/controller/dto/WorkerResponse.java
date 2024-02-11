package cl.previred.challenge.controller.dto;

import cl.previred.challenge.entity.Worker;
import io.swagger.v3.oas.annotations.media.Schema;

public record WorkerResponse(
        @Schema(description = "id", example = "1")
        Long id,
        @Schema(description = "rut", example = "1-9")
        String rut,

        @Schema(description = "Worker names", example = "Sebastian Amadiel")
        String names,

        @Schema(description = "Worker first surname", example = "Manzano")
        String firstSurname,

        @Schema(description = "Worker Second surname", example = "Manzano")
        String secondSurname,

        @Schema(description = "Company identifier", example = "pre20240211014921711")
        String companyId,

        @Schema(description = "Company Name", example = "Previred")
        String companyName
) {

        public static WorkerResponse convertToDTO(Worker worker) {
                return new WorkerResponse(
                        worker.getId(),
                        worker.getRut(),
                        worker.getNames(),
                        worker.getFirstSurname(),
                        worker.getSecondSurname(),
                        worker.getCompany().getId(),
                        worker.getCompany().getCompanyName()
                );
        }
}
