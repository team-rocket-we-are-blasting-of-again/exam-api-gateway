package com.teamrocket.core.integration;

import static org.junit.jupiter.api.Assertions.fail;

import com.teamrocket.core.util.annotaion.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@IntegrationTest
public class SomethingTest {

    @Test
    @DisplayName("something")
    void something() throws Exception {
        System.out.println("hello world");
    }

}
