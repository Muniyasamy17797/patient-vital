package com.vital.app.domain.port;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.vital.app.domain.dto.PagedResponse;
import com.vital.app.domain.dto.VitalRequest;
import com.vital.app.domain.dto.VitalSignResponse;

public interface VitalUseCase {


    CompletableFuture<VitalSignResponse> create(VitalRequest request);

    CompletableFuture<VitalSignResponse> update(Long id, VitalRequest request);

    CompletableFuture<Optional<VitalSignResponse>> getById(Long id);

    CompletableFuture<List<VitalSignResponse>> getAll();

    CompletableFuture<PagedResponse<VitalSignResponse>> getAllPaginated(int offset, int limit);

    CompletableFuture<Void> deleteById(Long id);
    
}
