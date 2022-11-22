package com.teamrocket.gateway.service;

public interface KafkaService {

    void send(String topic, String message);

}
