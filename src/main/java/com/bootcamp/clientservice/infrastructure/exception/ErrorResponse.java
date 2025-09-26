package com.bootcamp.clientservice.exception;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(name = "ErrorResponse", description = "Standard error response structure")
public class ErrorResponse {

    @Schema(description = "Timestamp of the error", example = "2025-09-19T00:13:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "HTTP status description", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed error message", example = "email: Email must be valid")
    private String message;

    @Schema(description = "Request path that caused the error", example = "/clientes")
    private String path;
}
