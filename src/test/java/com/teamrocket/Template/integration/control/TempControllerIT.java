package com.teamrocket.Template.integration.control;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamrocket.Template.dto.TemplateDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TempControllerIT {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getHelloStatus() throws Exception {

        mvc.perform(get("/tmpl")).andExpect(status().is2xxSuccessful());

    }

    @Test
    void getHelloContentType() throws Exception {
        mvc.perform(get("/tmpl")).andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getHelloResponseBody() throws Exception {
        mvc.perform(get("/tmpl")).andExpect(jsonPath("$.msg").value("Hello, You!"));
    }

    @Test
    void getHelloResponse() throws Exception {
        String responseBody = this.mvc.perform(get("/tmpl"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TemplateDTO resultDTO = mapper.readValue(responseBody, TemplateDTO.class);
        assertEquals(resultDTO.getId(), 99);
        assertEquals(resultDTO.getMsg(), "Hello, You!");
    }

}