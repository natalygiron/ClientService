package com.bootcamp.clientservice.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bootcamp.clientservice.domain.Client;
import com.bootcamp.clientservice.dto.request.CreateClientRequest;
import com.bootcamp.clientservice.dto.request.PatchClientRequest;
import com.bootcamp.clientservice.dto.request.UpdateClientRequest;
import com.bootcamp.clientservice.dto.response.ClientResponse;
import com.bootcamp.clientservice.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Client", description = "Customer-related operations")
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("clientes")
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Registrar nuevo cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateClientRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody CreateClientRequest request) {
        Client client = clientService.register(request);
        log.info("New customer registration {}", request);
        return ResponseEntity.ok(ClientResponse.from(client));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(ClientResponse.from(clientService.get(id)));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> list() {
        return ResponseEntity.ok(
                clientService.list().stream()
                        .map(ClientResponse::from)
                        .collect(Collectors.toList())
        );
    }

    @Operation(summary = "Update customer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "409", description = "Email already in use"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateClientRequest req) {
        Client updatedClient = clientService.updateClient(id, req.getFirstName(), req.getLastName(), req.getEmail());
        log.info("Updated client information {}", updatedClient);
        return ResponseEntity.ok(ClientResponse.from(updatedClient));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClientResponse> patch(@PathVariable Long id,
                                                @RequestBody PatchClientRequest req) {
        Client updatedClient = clientService.updateClient(id, req.getFirstName(), req.getLastName(), req.getEmail());
        log.info("Patched client information {}", updatedClient);
        return ResponseEntity.ok(ClientResponse.from(updatedClient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
