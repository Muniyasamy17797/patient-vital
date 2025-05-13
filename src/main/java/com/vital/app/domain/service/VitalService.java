package com.vital.app.domain.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vital.app.adoptor.out.repository.VitalSignRepository;
import com.vital.app.domain.dto.VitalRequest;
import com.vital.app.domain.dto.VitalSignResponse;
import com.vital.app.domain.mapper.VitalMapper;
import com.vital.app.domain.model.VitalSign;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class VitalService {

    
}
