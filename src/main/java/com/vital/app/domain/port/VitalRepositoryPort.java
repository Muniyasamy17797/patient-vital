package com.vital.app.domain.port;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.vital.app.domain.model.VitalSign;

public interface VitalRepositoryPort {
   
    VitalSign save(VitalSign user);
    Optional<VitalSign> findById(Long id);
    List<VitalSign> findAll();
    Page<VitalSign> findAll(Pageable pageable);
    void deleteById(Long id);
    
}
