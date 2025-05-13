package com.vital.app.domain.port;

import java.util.List;
import java.util.Optional;

import com.vital.app.domain.dto.PagedResponse;
import com.vital.app.domain.dto.VitalRequest;
import com.vital.app.domain.dto.VitalSignResponse;

public interface VitalUseCase {


    VitalSignResponse create(VitalRequest request);

    VitalSignResponse update(Long id, VitalRequest request);

    Optional<VitalSignResponse> getById(Long id);

    List<VitalSignResponse> getAll();

    PagedResponse<VitalSignResponse> getAllPaginated(int offset, int limit);

    void deleteById(Long id);
    
}
