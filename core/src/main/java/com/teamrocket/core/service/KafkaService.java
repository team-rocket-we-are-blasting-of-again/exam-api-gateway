package com.teamrocket.core.service;

public interface KafkaService {

    void send(String topic, String message);

}
