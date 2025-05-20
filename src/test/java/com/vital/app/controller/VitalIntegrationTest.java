
package com.vital.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vital.app.TestSecurityConfig;
import com.vital.app.VitalApp;
import com.vital.app.adoptor.webclient.PatientClient;
import com.vital.app.adoptor.webclient.UserClient;
import com.vital.app.config.TestContainersConfig;
import com.vital.app.domain.dto.PatientInfo;
import com.vital.app.domain.dto.VitalRequest;
import com.vital.app.domain.dto.VitalSignResponse;
import com.vital.app.domain.model.VitalSign;
import com.vital.app.adoptor.out.repository.VitalSignRepository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest(classes = {VitalApp.class},
//     properties = {
//       "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
//       "spring.datasource.driver-class-name=org.h2.Driver",
//       "spring.datasource.username=sa",
//       "spring.datasource.password=",
//       "spring.jpa.hibernate.ddl-auto=create-drop",
//       "spring.cloud.config.enabled=false",
//       "spring.jpa.properties.hibernate.cache.use_second_level_cache=false",
//       "spring.jpa.properties.hibernate.cache.use_query_cache=false"
//     },
//     webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @ActiveProfiles("test")
@SpringBootTest( properties ={
    "spring.jpa.properties.hibernate.cache.use_second_level_cache=false",
    "spring.jpa.properties.hibernate.cache.use_query_cache=false",
    "spring.cloud.config.enabled=false"

})
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VitalIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VitalSignRepository vitalSignRepository;

    @MockBean
    private UserClient userClient;

    @MockBean
    private PatientClient patientClient;

    private MockMvc mockMvc;

    private VitalRequest vitalRequest;


 @Test
void verifyTestProfileAndH2() {
    String url = context.getEnvironment().getProperty("spring.datasource.url");
    String profile = String.join(",", context.getEnvironment().getActiveProfiles());

    System.out.println("Active profile: " + profile);
    System.out.println("Datasource URL: " + url);

    Assertions.assertThat(profile).contains("test");
    Assertions.assertThat(url).contains("jdbc:h2:mem");
}

    @BeforeEach
    public void setup() {
        // Setup MockMvc with real context (no mocks on controller)
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        vitalSignRepository.deleteAll(); // Clean DB before each test

        vitalRequest = VitalRequest.builder()
                .pulse(72)
                .bloodPressure("120/80")
                .temperature(98.6)
                .respirations(16)
                .documentedBy(100L)
                .patientId(200L)
                .build();

        // Mock external client responses
        when(userClient.getUserById(100L))
            .thenReturn(new com.vital.app.domain.dto.UserInfo(100L, "John", "Doe"));

    PatientInfo samplePatient = new PatientInfo();
     samplePatient.setId(200L);
     samplePatient.setMedicalRecordNumber("MRN123456");
     samplePatient.setStartOfCareDate(LocalDate.of(2024, 1, 15));
     samplePatient.setStatus("Active");
     samplePatient.setFirstName("Alice");
     samplePatient.setLastName("Smith");
     samplePatient.setSex("F");
     samplePatient.setBirthDate(LocalDate.of(1980, 5, 20));

        when(patientClient.getPatientById(200L))
            .thenReturn(samplePatient);
    }

    @Test
    public void testCreateVitalSignFullFlow() throws Exception {

        String requestJson = objectMapper.writeValueAsString(vitalRequest);

        String responseJson = mockMvc.perform(post("/api/v1/vital-signs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.pulse").value(72))
            .andExpect(jsonPath("$.bloodPressure").value("120/80"))
            .andExpect(jsonPath("$.temperature").value(98.6))
            .andExpect(jsonPath("$.respirations").value(16))
            .andExpect(jsonPath("$.documentedBy.firstName").value("John"))
            .andExpect(jsonPath("$.patient.firstName").value("Alice"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        VitalSignResponse response = objectMapper.readValue(responseJson, VitalSignResponse.class);

        // Verify record saved in DB
        Optional<VitalSign> savedVitalOpt = vitalSignRepository.findById(response.getId());
        Assertions.assertThat(savedVitalOpt).isPresent();

        VitalSign savedVital = savedVitalOpt.get();
        Assertions.assertThat(savedVital.getPulse()).isEqualTo(72);
        Assertions.assertThat(savedVital.getBloodPressure()).isEqualTo("120/80");
        Assertions.assertThat(savedVital.getTemperature()).isEqualTo(98.6);
        Assertions.assertThat(savedVital.getRespirations()).isEqualTo(16);
    }
}
