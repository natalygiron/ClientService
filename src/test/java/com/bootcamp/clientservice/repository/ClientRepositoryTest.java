package com.bootcamp.clientservice.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.bootcamp.clientservice.domain.port.IClientRepository;

@SpringBootTest
class ClientRepositoryTest {

    @Autowired
    private IClientRepository clientRepository;

    @Test
    void contextLoads() {
        assertNotNull(clientRepository);
    }
}

