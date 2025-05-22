package com.vital.app.adoptor.in;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vital.app.domain.dto.VitalRequest;
import com.vital.app.domain.dto.VitalSignResponse;
import com.vital.app.domain.port.VitalUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/vital-signs")
public class VitalController {


    private final VitalUseCase service;

    public VitalController(VitalUseCase service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a new vital sign record")
    public CompletableFuture<ResponseEntity<VitalSignResponse>> create(@RequestBody VitalRequest request) {
        return service.create(request)
            .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing vital sign record")
    public CompletableFuture<ResponseEntity<VitalSignResponse>> update(@PathVariable Long id,
                                                    @RequestBody VitalRequest request
    ) {
        return service.update(id, request).thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vital sign by ID")
    public CompletableFuture<ResponseEntity<VitalSignResponse>> getById(@PathVariable Long id) {
        return service.getById(id).thenApply(opt -> opt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()));
    }

    @GetMapping
    @Operation(summary = "Get all vital signs")
    public CompletableFuture<ResponseEntity<List<VitalSignResponse>>> getAll() {
        return service.getAll().thenApply(ResponseEntity::ok);
    }

    @GetMapping("/paginated")
    @Operation(summary = "Get all vital signs with pagination")
    public CompletableFuture<ResponseEntity<?>> getAllPaginated(int offset, int limit) {
        return service.getAllPaginated(offset, limit).thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vital sign by ID")
    @ApiResponse(responseCode = "204", description = "Deleted successfully")
    public CompletableFuture<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return service.deleteById(id)
                .thenApply(v -> ResponseEntity.noContent().build());
    }
    
}
