package com.bootcamp.clientservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.bootcamp.clientservice.domain.Client;
import com.bootcamp.clientservice.dto.request.CreateClientRequest;
import com.bootcamp.clientservice.dto.request.UpdateClientRequest;
import com.bootcamp.clientservice.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    ClientService service;

    @Test
    void create_returns200_and_body() throws Exception {
        CreateClientRequest req = new CreateClientRequest();
        req.setFirstName("Ana"); req.setLastName("Perez");
        req.setDni("123"); req.setEmail("a@a.com");

        Client saved = Client.builder().id(1L).firstName("Ana").lastName("Perez")
                .dni("123").email("a@a.com").build();
        when(service.register(any(CreateClientRequest.class))).thenReturn(saved);

        mvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    void get_returns200() throws Exception {
        Client c = Client.builder().id(7L).firstName("Ana").lastName("P").build();
        when(service.get(7L)).thenReturn(c);

        mvc.perform(get("/clientes/{id}", 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L));
    }

    @Test
    void list_returns200_and_array() throws Exception {
        when(service.list()).thenReturn(List.of(
                Client.builder().id(1L).build(),
                Client.builder().id(2L).build()
        ));

        mvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void update_returns200() throws Exception {
        UpdateClientRequest req = new UpdateClientRequest();
        req.setFirstName("Ana M"); req.setLastName("Perez"); req.setEmail("new@mail.com");

        Client updated = Client.builder()
                .id(1L).firstName("Ana M").lastName("Perez").email("new@mail.com").build();
        when(service.updateClient(eq(1L), anyString(), anyString(), anyString())).thenReturn(updated);

        mvc.perform(put("/clientes/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ana M"))
                .andExpect(jsonPath("$.email").value("new@mail.com"));
    }

    @Test
    void patch_returns200() throws Exception {
        String body = "{ \"firstName\": \"SoloNombre\" }";

        Client updated = Client.builder()
                .id(1L).firstName("SoloNombre").lastName("Perez").email("x@y.com").build();
        when(service.updateClient(eq(1L), anyString(), any(), any())).thenReturn(updated);

        mvc.perform(patch("/clientes/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("SoloNombre"));
    }

    @Test
    void delete_returns204() throws Exception {
        mvc.perform(delete("/clientes/{id}", 9))
                .andExpect(status().isNoContent());
    }
}
