package com.teamrocket.Template.unit.service;

import com.teamrocket.Template.service.TemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TemplateServiceTest {

    @Autowired
    TemplateService templateService;
    private String who = "Me";

    @Test
    void helloTest() {
        assertEquals(templateService.hello("Me").getMsg(), "Hello, " + who + "!");
        assertEquals(templateService.hello("Me").getId(), 99);
    }
}