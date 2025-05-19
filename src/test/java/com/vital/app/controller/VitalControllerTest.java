package com.vital.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.vital.app.TestSecurityConfig;
import com.vital.app.VitalApp;
import com.vital.app.domain.dto.VitalRequest;
import com.vital.app.domain.dto.VitalSignResponse;
import com.vital.app.domain.port.VitalUseCase;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = {VitalApp.class, TestSecurityConfig.class}, properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.jpa.properties.hibernate.cache.use_second_level_cache=false",
    "spring.jpa.properties.hibernate.cache.use_query_cache=false"
})
@AutoConfigureMockMvc
public class VitalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VitalUseCase vitalUseCase;

    private VitalRequest vitalRequest;
    private VitalSignResponse vitalResponse;

    @BeforeEach
    void setup() {
        vitalRequest = VitalRequest.builder()
                       .pulse(72)
                       .bloodPressure("120/80")
                       .temperature(98.6)
                       .build();

        vitalResponse = VitalSignResponse.builder()
                       .id(1L)
                       .pulse(72)
                       .bloodPressure("120/80")
                       .temperature(98.6)
                       .respirations(16)
                       .build();
                 
    }

    @Test
    public void testCreateVital() throws Exception {
        when(vitalUseCase.create(any(VitalRequest.class))).thenReturn(vitalResponse);

        String jsonResponse = mockMvc.perform(post("/api/v1/vital-signs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vitalRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

        // Assert using JSONassert
        String expectedJson = "{\"id\":1,\"pulse\":72,\"bloodPressure\":\"120/80\",\"temperature\":98.6,\"respirations\":16}";
        JSONAssert.assertEquals(expectedJson, jsonResponse, false);

        // Assert using JsonPath
        String name = JsonPath.read(jsonResponse, "$.bloodPressure");
        Assertions.assertThat(name).isEqualTo("120/80");
    }

    @Test
    public void testGetVitalById() throws Exception {
        when(vitalUseCase.getById(1L)).thenReturn(Optional.of(vitalResponse));

        mockMvc.perform(get("/api/v1/vital-signs/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.pulse").value(72))
        .andExpect(jsonPath("$.bloodPressure").value("120/80"))
        .andExpect(jsonPath("$.temperature").value(98.6))
        .andExpect(jsonPath("$.respirations").value(16));  }

    @Test
    public void testGetAllVitals() throws Exception {
        List<VitalSignResponse> vitalList = Arrays.asList(vitalResponse);
        when(vitalUseCase.getAll()).thenReturn(vitalList);

        String responseJson = mockMvc.perform(get("/api/v1/vital-signs"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].pulse").value(72))
            .andExpect(jsonPath("$[0].bloodPressure").value("120/80"))
            .andExpect(jsonPath("$[0].temperature").value(98.6))
            .andExpect(jsonPath("$[0].respirations").value(16))
            .andReturn()
            .getResponse()
            .getContentAsString();

        // Verify using AssertJ
        List<String> names = JsonPath.read(responseJson, "$[*].bloodPressure");
        Assertions.assertThat(names).contains("120/80");
    }

    @Test
    public void testUpdateVital() throws Exception {
        when(vitalUseCase.update(eq(1L), any(VitalRequest.class))).thenReturn(vitalResponse);

        mockMvc.perform(put("/api/v1/vital-signs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vitalRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.pulse").value(72))
            .andExpect(jsonPath("$.bloodPressure").value("120/80"));
    }

    @Test
    public void testDeleteVital() throws Exception {
        doNothing().when(vitalUseCase).deleteById(1L);

        mockMvc.perform(delete("/api/v1/vital-signs/1"))
            .andExpect(status().isNoContent());

        verify(vitalUseCase, times(1)).deleteById(1L);
    }
}
