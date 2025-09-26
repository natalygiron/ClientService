package com.bootcamp.clientservice.infrastructure.exception;

import com.bootcamp.clientservice.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests para GlobalExceptionHandler.
 * No levantamos Spring MVC, solo instanciamos la clase y mockeamos HttpServletRequest / excepciones.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleValidation -> devuelve 400 con mensaje de la ValidationException")
    void handleValidation_returnsBadRequest() {
        // given
        ValidationException ex = new ValidationException("Campos inválidos");
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/clientes");

        // when
        ResponseEntity<ErrorResponse> resp = handler.handleValidation(ex, req);

        // then
        assertThat(resp.getStatusCodeValue()).isEqualTo(400);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("Campos inválidos");
        assertThat(resp.getBody().getPath()).isEqualTo("/clientes");
    }

    @Test
    @DisplayName("handleIllegalArg -> devuelve 409 CONFLICT con el mensaje de IllegalArgumentException")
    void handleIllegalArg_returnsConflict() {
        // given
        IllegalArgumentException ex = new IllegalArgumentException("Valor inválido");
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test");

        // when
        ResponseEntity<ErrorResponse> resp = handler.handleIllegalArg(ex, req);

        // then
        assertThat(resp.getStatusCodeValue()).isEqualTo(409);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("Valor inválido");
        assertThat(resp.getBody().getPath()).isEqualTo("/test");
    }

    @Test
    @DisplayName("handleValidationErrors -> construye mensaje con todos los field errors y devuelve 400")
    void handleValidationErrors_buildsMessageFromFieldErrors() {
        // given - un BindingResult real con errores
        Object target = new Object(); // cualquier objeto dummy
        BindingResult bindingResult = new BeanPropertyBindingResult(target, "client");
        bindingResult.addError(new FieldError("client", "email", "must be valid"));
        bindingResult.addError(new FieldError("client", "dni", "size must be between 8 and 12"));

        MethodParameter methodParam = null; // no lo necesitamos realmente
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParam, bindingResult);

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/clientes");

        // when
        ResponseEntity<ErrorResponse> resp = handler.handleValidationErrors(ex, req);

        // then
        assertThat(resp.getStatusCodeValue()).isEqualTo(400);
        assertThat(resp.getBody()).isNotNull();
        String message = resp.getBody().getMessage();
        assertThat(message).contains("email: must be valid");
        assertThat(message).contains("dni: size must be between 8 and 12");
    }

    @Test
    @DisplayName("handleGeneral -> captura excepciones inesperadas y devuelve 500 con mensaje genérico")
    void handleGeneral_returnsInternalServerError() {
        // given
        Exception ex = new RuntimeException("boom!");
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/clientes");

        // when
        ResponseEntity<ErrorResponse> resp = handler.handleGeneral(ex, req);

        // then
        assertThat(resp.getStatusCodeValue()).isEqualTo(500);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("Error inesperado");
        assertThat(resp.getBody().getPath()).isEqualTo("/clientes");
    }
}
