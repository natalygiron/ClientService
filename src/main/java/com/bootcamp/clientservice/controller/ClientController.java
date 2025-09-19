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
import com.bootcamp.clientservice.exception.ErrorResponse;
import com.bootcamp.clientservice.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Cliente", description = "Operaciones para gestión de clientes")
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("clientes")
public class ClientController {

    private final ClientService clientService;

    /**
     * Registrar un nuevo cliente
     */
    @Operation(summary = "Registrar nuevo cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos necesarios para registrar un nuevo cliente",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateClientRequest.class),
                    examples = @ExampleObject(
                            name = "Ejemplo válido",
                            value = "{\n" +
                                    "  \"firstName\": \"Lucía\",\n" +
                                    "  \"lastName\": \"Gómez\",\n" +
                                    "  \"dni\": \"12345678\",\n" +
                                    "  \"email\": \"lucia.gomez@example.com\"\n" +
                                    "}"
                    )
            )
    )
    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody CreateClientRequest request) {
        Client client = clientService.register(request);
        log.info("New customer registration {}", request);
        return ResponseEntity.ok(ClientResponse.from(client));
    }

    /**
     * Obtener cliente por ID
     */
    @Operation(summary = "Obtener cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> get(@PathVariable Long id) {
        log.info("Fetching client with ID: {}", id);
        return ResponseEntity.ok(ClientResponse.from(clientService.get(id)));
    }

    /**
     * Listar todos los clientes
     */
    @Operation(summary = "Listar todos los clientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<ClientResponse>> list() {
        log.info("Listing all clients");
        return ResponseEntity.ok(
                clientService.list().stream()
                        .map(ClientResponse::from)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Actualizar cliente por ID
     */
    @Operation(summary = "Actualizar cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Correo electrónico ya en uso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UpdateClientRequest.class),
                    examples = @ExampleObject(
                            name = "Actualización completa",
                            summary = "Actualizar todos los datos del cliente",
                            value = "{\n" +
                                    "  \"firstName\": \"Carlos\",\n" +
                                    "  \"lastName\": \"Ramírez\",\n" +
                                    "  \"email\": \"carlos.ramirez@example.com\"\n" +
                                    "}"
                    )
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateClientRequest req) {
        log.info("Updating client with ID: {}", id);
        Client updatedClient = clientService.updateClient(id, req.getFirstName(), req.getLastName(), req.getEmail());
        return ResponseEntity.ok(ClientResponse.from(updatedClient));
    }

    /**
     * Actualización parcial del cliente
     */
    @Operation(summary = "Actualizar parcialmente cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado parcialmente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Correo electrónico ya en uso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PatchClientRequest.class),
                    examples = @ExampleObject(
                            name = "Actualización parcial",
                            summary = "Modificar solo el correo electrónico",
                            value = "{\n" +
                                    "  \"email\": \"nuevo.email@example.com\"\n" +
                                    "}"
                    )
            )
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ClientResponse> patch(@PathVariable Long id,
                                                @Valid @RequestBody PatchClientRequest req) {
        log.info("Patching client with ID: {}", id);
        Client updatedClient = clientService.updateClient(id, req.getFirstName(), req.getLastName(), req.getEmail());
        return ResponseEntity.ok(ClientResponse.from(updatedClient));
    }

    /**
     * Eliminar cliente por ID
     */
    @Operation(summary = "Eliminar cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Cliente tiene cuentas activas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting client with ID: {}", id);
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
