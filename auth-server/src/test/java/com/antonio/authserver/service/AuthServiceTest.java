package com.antonio.authserver.service;

import com.antonio.authserver.controller.AuthController;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.LoginCredential;
import com.antonio.authserver.request.ClientLoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthServiceTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthController authController;

    @Test
    public void shouldReturn405() throws Exception {
        this.mockMvc.perform(get("/oauth/client-login")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public Code successfulLogin() throws Exception {
        ClientLoginRequest loginRequest = new ClientLoginRequest();
        loginRequest.setIdentifier("test");
        loginRequest.setPassword("test");
        loginRequest.setClientId("myclient");
        loginRequest.setClientSecret("clientPass");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(loginRequest);

        MvcResult result = this.mockMvc.perform(post("/oauth/client-login")
            .content(json)
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andReturn();

        Code code = mapper.readValue(result.getResponse().getContentAsString(), Code.class);
        assertNotNull(code);
        return code;
    }

    @Test
    public void successfulAccessTokenCall() throws Exception {
        Code code = successfulLogin();
        LoginCredential loginCredential = new LoginCredential();
        loginCredential.setClientCode(code.getCode());

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(loginCredential);

        MvcResult result = this.mockMvc.perform(post("/oauth/token")
                .content(json)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();

        JwtObject jwt = mapper.readValue(result.getResponse().getContentAsString(), JwtObject.class);
        assertNotNull(jwt);
    }
}
