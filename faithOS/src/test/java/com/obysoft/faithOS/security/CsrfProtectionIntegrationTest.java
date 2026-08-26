package com.obysoft.faithOS.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
class CsrfProtectionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void csrfEndpointIssuesToken() throws Exception {
        mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.headerName").value("X-XSRF-TOKEN"));
    }

    @Test
    void unsafeRequestWithoutCsrfIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unsafeRequestWithCookieTokenIsAllowed() throws Exception {
        var csrfResponse = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andReturn();
        Cookie cookie = csrfResponse.getResponse().getCookie("XSRF-TOKEN");

        mockMvc.perform(post("/api/auth/logout")
                        .cookie(cookie)
                        .header("X-XSRF-TOKEN", cookie.getValue()))
                .andExpect(status().isNoContent());
    }

    @Test
    void unsafeRequestWithResponseBodyTokenIsAllowed() throws Exception {
        var csrfResponse = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andReturn();
        Cookie cookie = csrfResponse.getResponse().getCookie("XSRF-TOKEN");
        String token = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(csrfResponse.getResponse().getContentAsString())
                .get("token")
                .asText();

        mockMvc.perform(post("/api/auth/logout")
                        .cookie(cookie)
                        .header("X-XSRF-TOKEN", token))
                .andExpect(status().isNoContent());
    }
}
