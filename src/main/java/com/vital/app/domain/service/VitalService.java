package com.vital.app.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
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
    public VitalSignResponse create(VitalRequest request) {
       
            VitalSign vital = mapper.toEntity(request);
            VitalSign saved = vitalSignRepository.save(vital);
            VitalSignResponse response = getExternalData(saved);
            return response;
       
    }

    @Override
    public VitalSignResponse update(Long id, VitalRequest request) {
        VitalSign vital = vitalSignRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Vital sign not found: " + id));
        mapper.updateEntityFromDto(request, vital);
        VitalSign updated = vitalSignRepository.save(vital);
        return getExternalData(updated);    
    }



    @Override
    public Optional<VitalSignResponse> getById(Long id) {
        return vitalSignRepository.findById(id)
            .map(this::getExternalData);    
        }



    @Override
    public List<VitalSignResponse> getAll() {

         return vitalSignRepository.findAll().stream()
            .map(this::getExternalData)
            .collect(Collectors.toList());    
        }



    @Override
    public PagedResponse<VitalSignResponse> getAllPaginated(int offset, int limit) {
        
        var pageRequest = PageRequest.of(offset, limit);
        var page = vitalSignRepository.findAll(pageRequest);
        List<VitalSignResponse> responses = page.getContent().stream()
            .map(this::getExternalData)
            .collect(Collectors.toList());
        return new PagedResponse<>(
            responses,
            page.getTotalElements(),
            page.getNumber(),
            page.getSize()
        );
    }



    @Override
    public void deleteById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
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
