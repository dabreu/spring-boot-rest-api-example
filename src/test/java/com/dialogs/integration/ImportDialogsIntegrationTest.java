package com.dialogs.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.dialogs.TestUtils;
import com.dialogs.model.Company;
import com.dialogs.model.Dialog;
import com.dialogs.model.DialogThread;
import com.dialogs.model.DialogThreadConflict;
import com.dialogs.repository.DialogThreadConflictRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
public class ImportDialogsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepositoryForTest repository;

    @Autowired
    private DialogThreadConflictRepository conflictThreadsRepository;

    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public static void setupOnce() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @BeforeEach
    public void setup() {
        repository.deleteAll();
        conflictThreadsRepository.deleteAll();
    }

    @Test
    public void testImportWithEmptyDataReturnsError() throws Exception {
        Company company = new Company();
        MvcResult result = performImport(Arrays.asList(company)).andExpect(status().isBadRequest()).andReturn();
        verifyError(result, HttpStatus.BAD_REQUEST, "Companies could not be imported");
    }

    @Test
    public void testImportWithMissingRequiredDataReturnsError() throws Exception {
        Company company = new Company();
        company.setId(1L);
        company.setName(null);
        MvcResult result = performImport(Arrays.asList(company)).andExpect(status().isBadRequest()).andReturn();
        verifyError(result, HttpStatus.BAD_REQUEST, "Companies could not be imported");
    }

    @Test
    public void testCompaniesAreImportedIfValidData() throws Exception {
        DialogThread[] threads = new DialogThread[3];
        threads[0] = TestUtils.createThread(1L, "thread dialog1 #1");
        threads[1] = TestUtils.createThread(2L, "thread dialog1 #2");
        threads[2] = TestUtils.createThread(3L, "thread dialog1 #3");
        Dialog dialog1 = TestUtils.createDialog(1L, 1L, 1200L, "user@mail.com", LocalDateTime.of(2020, 01, 01, 10, 00, 00), threads);
        Company company1 = TestUtils.createCompany(1L, "c1", LocalDate.of(2020, 01, 10), dialog1);

        threads = new DialogThread[1];
        threads[0] = TestUtils.createThread(4L, "another thread #1");
        Dialog dialog2 = TestUtils.createDialog(2L, 2L, 2500L, "userx@mail.com", LocalDateTime.of(2019, 02, 21, 11, 30, 00), threads);
        Company company2 = TestUtils.createCompany(2L, "c2", LocalDate.of(2019, 11, 20), dialog2);

        performImport(Arrays.asList(company1, company2)).andExpect(status().isOk()).andReturn();

        Company company;
        Dialog dialog;
        List<DialogThread> cThreads;
        // verify company #1
        company = repository.findAllDialogsAndThreads(1L);
        assertEquals("c1", company.getName());
        assertEquals(LocalDate.of(2020, 01, 10), company.getSignedUp());
        // verify dialog of company #1
        dialog = company.getDialogs().get(0);
        assertEquals(1, (long) dialog.getId());
        assertEquals(1, (long) dialog.getNumber());
        assertEquals(1200, (long) dialog.getUserId());
        assertEquals("user@mail.com", dialog.getFrom());
        assertEquals(LocalDateTime.of(2020, 01, 01, 10, 00, 00), dialog.getReceived());
        cThreads = dialog.getThreads();
        assertEquals(3, cThreads.size());
        // verify threads of dialog of company #1
        assertEquals(1, (long) cThreads.get(0).getId());
        assertEquals("thread dialog1 #1", cThreads.get(0).getPayload());
        assertEquals(0, (long) cThreads.get(0).getOrder());
        assertEquals(2, (long) cThreads.get(1).getId());
        assertEquals("thread dialog1 #2", cThreads.get(1).getPayload());
        assertEquals(1, (long) cThreads.get(1).getOrder());
        assertEquals(3, (long) cThreads.get(2).getId());
        assertEquals("thread dialog1 #3", cThreads.get(2).getPayload());
        assertEquals(2, (long) cThreads.get(2).getOrder());

        // verify company #2
        company = repository.findAllDialogsAndThreads(2L);
        assertEquals("c2", company.getName());
        assertEquals(LocalDate.of(2019, 11, 20), company.getSignedUp());
        // verify dialog of company #2
        dialog = company.getDialogs().get(0);
        assertEquals(2, (long) dialog.getId());
        assertEquals(2, (long) dialog.getNumber());
        assertEquals(2500, (long) dialog.getUserId());
        assertEquals("userx@mail.com", dialog.getFrom());
        assertEquals(LocalDateTime.of(2019, 02, 21, 11, 30, 00), dialog.getReceived());
        cThreads = dialog.getThreads();
        assertEquals(1, cThreads.size());
        // verify threads of dialog of company #2
        assertEquals(4, (long) cThreads.get(0).getId());
        assertEquals("another thread #1", cThreads.get(0).getPayload());
        assertEquals(0, (long) cThreads.get(0).getOrder());
    }

    @Test
    public void testImportEndpointIsAtomic() throws Exception {
        DialogThread[] threads = new DialogThread[3];
        threads[0] = TestUtils.createThread(1L, "thread dialog1 #1");
        threads[1] = TestUtils.createThread(2L, "thread dialog1 #2");
        threads[2] = TestUtils.createThread(3L, "thread dialog1 #3");
        Dialog dialog1 = TestUtils.createDialog(1L, 1L, 1200L, "user@mail.com", LocalDateTime.of(2020, 01, 01, 10, 00, 00), threads);
        Company company1 = TestUtils.createCompany(1L, "c1", LocalDate.of(2020, 01, 10), dialog1);

        threads = new DialogThread[1];
        threads[0] = TestUtils.createThread(null, "another thread #1");
        Dialog dialog2 = TestUtils.createDialog(2L, 2L, 2500L, "userx@mail.com", LocalDateTime.of(2019, 02, 21, 11, 30, 00), threads);
        Company company2 = TestUtils.createCompany(null, "c2", LocalDate.of(2019, 11, 20), dialog2);

        MvcResult result = performImport(Arrays.asList(company1, company2)).andExpect(status().isBadRequest()).andReturn();
        verifyError(result, HttpStatus.BAD_REQUEST, "Companies could not be imported");

        assertNull(repository.findAllDialogsAndThreads(1L));
        assertNull(repository.findAllDialogsAndThreads(2L));

    }

    @Test
    public void testImportCompanyWithSameIdTwiceReturnsError() throws Exception {
        DialogThread[] threads = new DialogThread[3];
        threads[0] = TestUtils.createThread(1L, "thread dialog1 #1");
        threads[1] = TestUtils.createThread(2L, "thread dialog1 #2");
        threads[2] = TestUtils.createThread(3L, "thread dialog1 #3");
        Dialog dialog1 = TestUtils.createDialog(1L, 1L, 1200L, "user@mail.com", LocalDateTime.of(2020, 01, 01, 10, 00, 00), threads);
        Company company = TestUtils.createCompany(1L, "c1", LocalDate.of(2020, 01, 10), dialog1);

        // import the company the first time
        performImport(Arrays.asList(company)).andExpect(status().isOk()).andReturn();

        // company was persisted
        assertNotNull(repository.findAllDialogsAndThreads(1L));

        threads = new DialogThread[1];
        threads[0] = TestUtils.createThread(null, "another thread #1");
        Dialog dialog2 = TestUtils.createDialog(2L, 2L, 2500L, "userx@mail.com", LocalDateTime.of(2019, 02, 21, 11, 30, 00), threads);
        company = TestUtils.createCompany(1L, "c2", LocalDate.of(2019, 11, 20), dialog2);

        MvcResult result = performImport(Arrays.asList(company)).andExpect(status().isBadRequest()).andReturn();
        verifyError(result, HttpStatus.BAD_REQUEST, "Companies could not be imported");

        assertNotNull(repository.findAllDialogsAndThreads(1L));
    }

    @Test
    public void testImportCompanyWithNoDialogs() throws Exception {
        Company company = TestUtils.createCompany(1L, "c1", LocalDate.of(2020, 01, 10), new Dialog[] {});

        performImport(Arrays.asList(company)).andExpect(status().isOk()).andReturn();

        Company companyExample = new Company();
        companyExample.setId(1L);
        Example<Company> example = Example.of(company);
        Optional<Company> opt = repository.findOne(example);
        assertTrue(opt.isPresent());
        company = opt.get();
        assertEquals("c1", company.getName());
        assertEquals(LocalDate.of(2020, 01, 10), company.getSignedUp());
    }

    @Test
    public void testImportCompanyWithIgnoredSignUpDateIsNotPersisted() throws Exception {
        DialogThread[] threads = new DialogThread[3];
        threads[0] = TestUtils.createThread(1L, "thread dialog1 #1");
        threads[1] = TestUtils.createThread(2L, "thread dialog1 #2");
        threads[2] = TestUtils.createThread(3L, "thread dialog1 #3");
        Dialog dialog1 = TestUtils.createDialog(1L, 1L, 1200L, "user@mail.com", LocalDateTime.of(2020, 01, 01, 10, 00, 00), threads);
        Company company = TestUtils.createCompany(1L, "c1", LocalDate.of(2017, 01, 10), dialog1);

        performImport(Arrays.asList(company)).andExpect(status().isOk()).andReturn();

        // company was not persisted
        assertNull(repository.findAllDialogsAndThreads(1L));
    }

    @Test
    public void testImportCompanyWithDuplicatedThreadIdsPersistThreadsInConflictStorage() throws Exception {
        DialogThread[] threads = new DialogThread[3];
        threads[0] = TestUtils.createThread(1L, "thread dialog1 #1");
        threads[1] = TestUtils.createThread(3L, "thread dialog1 #2");
        threads[2] = TestUtils.createThread(3L, "thread dialog1 #3");
        Dialog dialog = TestUtils.createDialog(1L, 1L, 1200L, "user@mail.com", LocalDateTime.of(2020, 01, 01, 10, 00, 00), threads);
        Company company = TestUtils.createCompany(1L, "c1", LocalDate.of(2020, 01, 10), dialog);

        performImport(Arrays.asList(company)).andExpect(status().isOk()).andReturn();

        company = repository.findAllDialogsAndThreads(1L);
        assertEquals("c1", company.getName());
        assertEquals(LocalDate.of(2020, 01, 10), company.getSignedUp());
        // verify dialog of company #1
        dialog = company.getDialogs().get(0);
        assertEquals(1, (long) dialog.getId());
        assertEquals(1, (long) dialog.getNumber());
        assertEquals(1200, (long) dialog.getUserId());
        assertEquals("user@mail.com", dialog.getFrom());
        assertEquals(LocalDateTime.of(2020, 01, 01, 10, 00, 00), dialog.getReceived());
        List<DialogThread> cThreads = dialog.getThreads();
        assertEquals(1, cThreads.size());
        // verify threads of dialog of company #1
        assertEquals(1, (long) cThreads.get(0).getId());
        assertEquals("thread dialog1 #1", cThreads.get(0).getPayload());
        assertEquals(0, (long) cThreads.get(0).getOrder());

        // verify that the other threads were stored on conflict table
        DialogThreadConflict thread = new DialogThreadConflict();
        thread.setId(3L);
        Example<DialogThreadConflict> example = Example.of(thread);
        assertEquals(2, conflictThreadsRepository.findAll(example).size());
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

    @SuppressWarnings("unchecked")
    private void verifyError(MvcResult result, HttpStatus status, String expectedError) throws Exception {
        Map<String, Object> mapResult = fromJson(result, Map.class);
        assertEquals(status.value(), mapResult.get("status"));
        assertTrue(mapResult.containsKey("error"));
        String error = (String) mapResult.get("error");
        assertTrue(error.contains(expectedError));
    }
}
