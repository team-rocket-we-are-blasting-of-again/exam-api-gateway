package com.teamrocket.core.service.impl;

import com.teamrocket.core.service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaServiceImpl implements KafkaService {

    private final KafkaTemplate<String, String> stringKafkaTemplate;

    @Override
    public void send(String topic, String message) {
        log.info("sending event '{}'", topic);
        stringKafkaTemplate.send(topic, message);
    }
}
