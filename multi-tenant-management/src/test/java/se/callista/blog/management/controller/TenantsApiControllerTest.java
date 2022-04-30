package se.callista.blog.management.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import se.callista.blog.management.service.TenantManagementService;

@WebMvcTest(TenantsApiController.class)
class TenantsApiControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TenantManagementService tenantManagementService;

    @Test
    void createTenant() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/tenants?tenantId=tenant1&db=tenant1_db&password=secret"))
                .andExpect(status().isOk());

        verify(tenantManagementService).createTenant("tenant1", "tenant1_db", "secret");
    }
}