package com.vital.app.adoptor.webclient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vital.app.domain.dto.LoginRequest;
import com.vital.app.domain.dto.PatientInfo;
import com.vital.app.domain.dto.TokenResponse;
import com.vital.app.infrastructure.config.FeignConfig;

/**
 * Feign client to interact with the Patient service.
 */
@FeignClient(name = "patientModule", configuration = FeignConfig.class)
public interface PatientClient {

    @GetMapping("/api/v1/patients/{id}")
    PatientInfo getPatientById(@PathVariable("id") Long id);

    @PostMapping("/api/authenticate")
    TokenResponse authenticate(@RequestBody LoginRequest request);
}
