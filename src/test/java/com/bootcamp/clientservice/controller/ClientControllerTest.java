package com.bootcamp.clientservice.controller;

import com.bootcamp.clientservice.application.service.ClientService;
import com.bootcamp.clientservice.domain.model.Client;
import com.bootcamp.clientservice.dto.request.CreateClientRequest;
import com.bootcamp.clientservice.dto.request.PatchClientRequest;
import com.bootcamp.clientservice.dto.request.UpdateClientRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ClientController.
 * Using MockMvc + Mockito to simulate HTTP layer.
 */
@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ClientService clientService;

    // ---------- POST /clientes ----------
    @Test
    void create_returns200_and_body() throws Exception {
        CreateClientRequest req = new CreateClientRequest();
        req.setFirstName("Ana");
        req.setLastName("Rodriguez");
        req.setDni("123444555");
        req.setEmail("ana@mail.com");

        Client saved = new Client(1L, "Ana", "Rodriguez", "ana@mail.com", "123444555");

        when(clientService.register(any(CreateClientRequest.class))).thenReturn(saved);

        mvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.lastName").value("Rodriguez"))
                .andExpect(jsonPath("$.email").value("ana@mail.com"))
                .andExpect(jsonPath("$.dni").value("123444555"));
    }

    // ---------- GET /clientes/{id} ----------
    @Test
    void get_returns200() throws Exception {
        Client c = new Client(7L, "Ana", "Rodriguez", "ana@mail.com", "123444555");
        when(clientService.get(7L)).thenReturn(c);

        mvc.perform(get("/clientes/{id}", 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.lastName").value("Rodriguez"))
                .andExpect(jsonPath("$.email").value("ana@mail.com"))
                .andExpect(jsonPath("$.dni").value("123444555"));
    }

    // ---------- GET /clientes ----------
    @Test
    void list_returns200_and_array() throws Exception {
        List<Client> clients = List.of(
                new Client(1L, "A", "X", "a@mail.com", "11111111"),
                new Client(2L, "B", "Y", "b@mail.com", "22222222")
        );

        when(clientService.list()).thenReturn(clients);

        mvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    // ---------- PUT /clientes/{id} ----------
    @Test
    void update_returns200() throws Exception {
        UpdateClientRequest req = new UpdateClientRequest();
        req.setFirstName("Ana M");
        req.setLastName("Perez");
        req.setEmail("new@mail.com");
        req.setDni("22222222");

        Client updated = new Client(1L, "Ana M", "Perez", "new@mail.com", "22222222");

        when(clientService.updateClient(eq(1L), anyString(), anyString(), anyString(), anyString())).thenReturn(updated);

        mvc.perform(put("/clientes/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ana M"))
                .andExpect(jsonPath("$.email").value("new@mail.com"));
    }

    // ---------- PATCH /clientes/{id} ----------
    @Test
    void patch_returns200() throws Exception {
        PatchClientRequest req = new PatchClientRequest();
        req.setFirstName("SoloNombre");

        Client updated = new Client(1L, "SoloNombre", "Perez", "x@y.com", "11114444");

        when(clientService.updateClientPartial(eq(1L), any(PatchClientRequest.class)))
                .thenReturn(updated);

        mvc.perform(patch("/clientes/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("SoloNombre"));
    }

    // ---------- DELETE /clientes/{id} ----------
    @Test
    void delete_returns204() throws Exception {
        doNothing().when(clientService).deleteClient(9L);

        mvc.perform(delete("/clientes/{id}", 9))
                .andExpect(status().isNoContent());
    }
}
