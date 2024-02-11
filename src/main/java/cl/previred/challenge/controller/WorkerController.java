package cl.previred.challenge.controller;

import cl.previred.challenge.controller.dto.*;
import cl.previred.challenge.entity.Worker;
import cl.previred.challenge.service.WorkerService;
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
@RequestMapping(path = "/api/worker", produces = MediaType.APPLICATION_JSON_VALUE)
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @Operation(summary = "Create a new worker")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WorkerResponse.class)))
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping(value = "")
    public ResponseEntity<WorkerResponse> create(@Valid @RequestBody WorkerRequest request) {
        Worker response = workerService.create(request);
        return ResponseEntity.ok(WorkerResponse.convertToDTO(response));
    }

    @Operation(summary = "Update worker")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WorkerResponse.class)))
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PutMapping(value = "/{id}")
    public ResponseEntity<WorkerResponse> update(@PathVariable Long id, @Valid @RequestBody WorkerRequest request) {
        Worker response = workerService.update(id, request);
        return ResponseEntity.ok(WorkerResponse.convertToDTO(response));
    }

    @Operation(summary = "Delete worker")
    @ApiResponse(responseCode = "204", description = "Worker deleted successfully", content = @Content)
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get worker")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WorkerResponse.class)))
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping(value = "/{id}")
    public ResponseEntity<WorkerResponse> get(@PathVariable Long id) {
        Worker response = workerService.get(id);
        return ResponseEntity.ok(WorkerResponse.convertToDTO(response));
    }

    @Operation(summary = "List workers")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CompanyPageResponse.class)))
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping(value = "")
    public ResponseEntity<WorkerPageResponse> list(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) String companyId) {
        Sort.Direction sortDirection = Sort.Direction.fromString("ASC");
        Pageable paging = PageRequest.of(page - 1, size, Sort.by(sortDirection, "rut"));
        Page<Worker> pageWorkers;
        if (companyId != null && !companyId.isEmpty()) {
            pageWorkers = workerService.findAllByCompanyId(paging,companyId);
        } else {
            pageWorkers = workerService.findAll(paging);
        }

        WorkerPageResponse response = new WorkerPageResponse(
                convertToDTOs(pageWorkers.getContent()),
                pageWorkers.getNumber() + 1,
                (int) pageWorkers.getTotalElements(),
                pageWorkers.getTotalPages(),
                pageWorkers.getSize()
        );

        return ResponseEntity.ok(response);
    }

    private List<WorkerResponse> convertToDTOs(List<Worker> workers) {
        return workers.stream()
                .map(WorkerResponse::convertToDTO)
                .collect(Collectors.toList());
    }
}
