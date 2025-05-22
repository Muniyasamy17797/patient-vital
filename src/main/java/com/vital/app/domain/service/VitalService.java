package com.vital.app.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vital.app.adoptor.out.repository.VitalSignRepository;
import com.vital.app.adoptor.webclient.PatientClient;
import com.vital.app.adoptor.webclient.UserClient;
import com.vital.app.domain.dto.PagedResponse;
import com.vital.app.domain.dto.PatientInfo;
import com.vital.app.domain.dto.UserInfo;
import com.vital.app.domain.dto.VitalRequest;
import com.vital.app.domain.dto.VitalSignResponse;
import com.vital.app.domain.mapper.VitalMapper;
import com.vital.app.domain.model.VitalSign;
import com.vital.app.domain.port.VitalUseCase;

import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;

@Service
@Transactional

public class VitalService  implements VitalUseCase{

    private final VitalSignRepository vitalSignRepository;
    private final VitalMapper mapper;
    private final PatientClient patientClient;
    private final UserClient userClient;

    public VitalService(VitalSignRepository vitalSignRepository, VitalMapper mapper, PatientClient patientClient,
            UserClient userClient, ObjectMapper objectMapper) {
        this.vitalSignRepository = vitalSignRepository;
        this.mapper = mapper;
        this.patientClient = patientClient;
        this.userClient = userClient;
    }

    @Override
    @Async("taskExecutor")
    @Timed(value = "vitalService.create", description = "Time taken to create a vital sign")
    public CompletableFuture<VitalSignResponse> create(VitalRequest request) {
        VitalSign vital = mapper.toEntity(request);
        VitalSign saved = vitalSignRepository.save(vital);
        return CompletableFuture.completedFuture(getExternalData(saved));
    }

    
    @Override
    @Timed(value = "vitalService.update", description = "Time taken to update a vital sign")
    @Async("taskExecutor")
    public CompletableFuture<VitalSignResponse> update(Long id, VitalRequest request) {
        VitalSign vital = vitalSignRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Vital sign not found: " + id));
        mapper.updateEntityFromDto(request, vital);
        VitalSign updated = vitalSignRepository.save(vital);
        return CompletableFuture.completedFuture(getExternalData(updated));
    }


    @Override
    @Timed(value = "vitalService.getById", description = "Time taken to get a vital sign by id")
    @Async("taskExecutor")
    public CompletableFuture<Optional<VitalSignResponse>> getById(Long id) {
            return CompletableFuture.completedFuture(
                vitalSignRepository.findById(id).map(this::getExternalData)
            );
    }



    @Override
    @Timed(value = "vitalService.getAll", description = "Time taken to get all vital signs")
    public CompletableFuture<List<VitalSignResponse>> getAll() {

         return CompletableFuture.completedFuture(vitalSignRepository.findAll().stream()
            .map(this::getExternalData)
            .collect(Collectors.toList()));    
        }


    @Override
    @Timed(value = "vitalService.getAllPaginated", description = "Time taken to get all vital signs paginated")
    @Async("taskExecutor")
    public CompletableFuture<PagedResponse<VitalSignResponse>> getAllPaginated(int offset, int limit) {
        var pageRequest = PageRequest.of(offset, limit);
        var page = vitalSignRepository.findAll(pageRequest);
        List<VitalSignResponse> responses = page.getContent().stream()
            .map(this::getExternalData)
            .collect(Collectors.toList());
        return CompletableFuture.completedFuture(new PagedResponse<>(
            responses, page.getTotalElements(), page.getNumber(), page.getSize()
        ));
    }

    @Override
    @Timed(value = "vitalService.delete", description = "Time taken to delete a vital sign")
    @Async("taskExecutor")
    public CompletableFuture<Void> deleteById(Long id) {
        VitalSign vital = vitalSignRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Vital sign not found: " + id));
        vitalSignRepository.delete(vital);
        return CompletableFuture.completedFuture(null);
    }


    private VitalSignResponse getExternalData(VitalSign vital) {
        VitalSignResponse response = mapper.toResponse(vital);
        UserInfo userInfo = userClient.getUserById(vital.getDocumentedBy());
        response.setDocumentedBy(new UserInfo(userInfo.getId(), userInfo.getFirstName(), userInfo.getLastName()));
        PatientInfo patientInfo = patientClient.getPatientById(vital.getPatientId());
        response.setPatient(patientInfo);
        return response;
    }

    
}
