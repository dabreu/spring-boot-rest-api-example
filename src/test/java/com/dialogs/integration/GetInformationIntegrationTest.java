package com.dialogs.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.dialogs.TestUtils;
import com.dialogs.dto.CompanyInformation;
import com.dialogs.dto.DialogInformation;
import com.dialogs.dto.TranscriptThread;
import com.dialogs.model.Company;
import com.dialogs.model.Dialog;
import com.dialogs.model.DialogThread;
import com.dialogs.repository.CompanyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
public class GetInformationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository repository;

    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public static void setupOnce() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @BeforeEach
    public void setup() {
        repository.deleteAll();
    }

    @Test
    public void testGetCompanyInformation() throws Exception {
        DialogThread[] threads = new DialogThread[3];
        threads[0] = TestUtils.createThread(1L, "thread dialog1 #1");
        threads[1] = TestUtils.createThread(2L, "thread dialog1 #2");
        threads[2] = TestUtils.createThread(3L, "thread dialog1 #3");
        Dialog dialog1 = TestUtils.createDialog(1L, 1L, 1200L, "user@mail.com", LocalDateTime.of(2020, 01, 01, 10, 00, 00), threads);
        threads = null;
        Dialog dialog2 = TestUtils.createDialog(201L, 2L, 1500L, "user1@mail.com", LocalDateTime.of(2020, 02, 02, 10, 00, 00), threads);
        Dialog dialog3 = TestUtils.createDialog(301L, 3L, 1200L, "user@mail.com", LocalDateTime.of(2020, 03, 02, 10, 00, 00), threads);
        Dialog dialog4 = TestUtils.createDialog(401L, 4L, 2000L, "user2@mail.com", LocalDateTime.of(2020, 04, 02, 10, 00, 00), threads);
        Company company = TestUtils.createCompany(1504L, "c1", LocalDate.of(2020, 01, 10), dialog1, dialog2, dialog3, dialog4);

        // import the company
        performImport(Arrays.asList(company)).andExpect(status().isOk()).andReturn();

        // get a dialog transcript
        MvcResult result = performGetCompanyInformation(1504L).andExpect(status().isOk()).andReturn();
        CompanyInformation information = fromJson(result, CompanyInformation.class);

        assertEquals("c1", information.getName());
        assertEquals(4, (long) information.getDialogsCount());
        assertEquals(1200, (long) information.getMostPopularCustomer());
    }

    @Test
    public void testGetCompanyInformationWithNoDialogs() throws Exception {
        Company company = new Company();
        company.setId(1401L);
        company.setName("c1");
        company.setSignedUp(LocalDate.of(2020, 01, 10));

        // import the company
        performImport(Arrays.asList(company)).andExpect(status().isOk()).andReturn();

        // get a dialog transcript
        MvcResult result = performGetCompanyInformation(1401L).andExpect(status().isOk()).andReturn();
        CompanyInformation information = fromJson(result, CompanyInformation.class);

        assertEquals("c1", information.getName());
        assertEquals(0, (long) information.getDialogsCount());
        assertNull(information.getMostPopularCustomer());
    }

    @Test
    public void testGetCompanyInformationWithInvalidIdReturnsException() throws Exception {
        MvcResult result = performGetCompanyInformation(1500L).andExpect(status().isNotFound()).andReturn();
        verifyError(result, HttpStatus.NOT_FOUND, "Company not found. Id: 1500");
    }

    @Test
    public void testGetDialogInformation() throws Exception {
        DialogThread[] threads = new DialogThread[3];
        threads[0] = TestUtils.createThread(1L, "thread dialog1 #1");
        threads[1] = TestUtils.createThread(2L, "thread dialog1 #2");
        threads[2] = TestUtils.createThread(3L, "thread dialog1 #3");
        Dialog dialog = TestUtils.createDialog(1801L, 1L, 1200L, "user@mail.com", LocalDateTime.of(2020, 01, 01, 10, 00, 00), threads);
        Company company = TestUtils.createCompany(1504L, "c1", LocalDate.of(2020, 01, 10), dialog);

        // import the company
        performImport(Arrays.asList(company)).andExpect(status().isOk()).andReturn();

        // get a dialog transcript
        MvcResult result = performGetDialogInformation(1801L).andExpect(status().isOk()).andReturn();
        DialogInformation information = fromJson(result, DialogInformation.class);

        assertEquals(1801L, (long) information.getId());
        assertEquals(3, information.getTranscript().size());
        List<TranscriptThread> transcript = information.getTranscript();
        assertEquals(1, (int) transcript.get(0).getOrder());
        assertEquals("thread dialog1 #1", transcript.get(0).getText());
        assertEquals(2, (int) transcript.get(1).getOrder());
        assertEquals("thread dialog1 #2", transcript.get(1).getText());
        assertEquals(3, (int) transcript.get(2).getOrder());
        assertEquals("thread dialog1 #3", transcript.get(2).getText());
    }

    @Test
    public void testGetDialogInformationWithNoThreads() throws Exception {
        DialogThread[] threads = null;
        Dialog dialog = TestUtils.createDialog(1500L, 1L, 1200L, "user@mail.com", LocalDateTime.of(2020, 01, 01, 10, 00, 00), threads);
        Company company = TestUtils.createCompany(1401L, "c1", LocalDate.of(2020, 01, 10), dialog);

        // import the company
        performImport(Arrays.asList(company)).andExpect(status().isOk()).andReturn();

        // get a dialog transcript
        MvcResult result = performGetDialogInformation(1500L).andExpect(status().isOk()).andReturn();
        DialogInformation information = fromJson(result, DialogInformation.class);

        assertEquals(1500, (long) information.getId());
        assertEquals(0, information.getTranscript().size());
    }

    @Test
    public void testGetDialogInformationWithInvalidIdReturnsException() throws Exception {
        MvcResult result = performGetDialogInformation(1500L).andExpect(status().isNotFound()).andReturn();
        verifyError(result, HttpStatus.NOT_FOUND, "Dialog not found. Id: 1500");
    }

    private ResultActions performImport(List<Company> companies) throws Exception {
        return mockMvc.perform(post("/company/import").content(toJson(companies)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
    }

    private String toJson(List<Company> companies) throws IOException {
        String str = mapper.writeValueAsString(companies);
        return str;
    }

    private <T> T fromJson(MvcResult result, Class<T> tClass) throws Exception {
        return mapper.readValue(result.getResponse().getContentAsString(), tClass);
    }

    private ResultActions performGetCompanyInformation(Long companyId) throws Exception {
        return mockMvc.perform(get("/company/info/" + companyId).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
    }

    private ResultActions performGetDialogInformation(Long dialogId) throws Exception {
        return mockMvc.perform(get("/dialog/info/" + dialogId).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
    }

    @SuppressWarnings("unchecked")
    private void verifyError(MvcResult result, HttpStatus status, String expectedError) throws Exception {
        Map<String, Object> mapResult = fromJson(result, Map.class);
        assertEquals(status.value(), mapResult.get("status"));
        assertTrue(mapResult.containsKey("error"));
        String error = (String) mapResult.get("error");
        assertTrue(error.contains(expectedError));
    }

}
