package com.teamrocket.Template.service;

import com.teamrocket.Template.dto.TemplateDTO;
import org.springframework.stereotype.Service;
import com.teamrocket.Template.control.TempController;

@Service
public class TemplateService {
    public TemplateDTO hello(String who) {
        return new TemplateDTO(99, "Hello, " + who + "!");
    }
}
