package cl.previred.challenge.exceptions;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.ArrayList;
import java.util.List;

import cl.previred.challenge.controller.dto.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * The {@code RestExceptionHandler} class acts as a global exception handler for the application.
 * <p>
 * This class captures exceptions thrown by any controller and returns consistent, standardized error responses.
 * It uses the {@link org.springframework.web.bind.annotation.ControllerAdvice} annotation to ensure it applies to exceptions
 * thrown by all controllers across the application.
 * <p>
 * Implementing specific handlers within this class allows for customization of responses for different types of exceptions,
 * thereby enhancing the end-user experience and facilitating error debugging and monitoring.
 * <p>
 * Examples of handled exceptions include {@link org.springframework.web.HttpRequestMethodNotSupportedException},
 * {@link org.springframework.web.bind.MethodArgumentNotValidException}, among others, each potentially with its own handler method.
 * <p>
 * It's important to ensure that all exception handlers return an {@link org.springframework.http.ResponseEntity} object
 * containing a standardized response body, which might include an error message, an HTTP status code, and optionally,
 * additional details relevant to the error.
 * <p>
 * This class enhances the consistency of error responses throughout the application and centralizes error handling,
 * reducing the need for repetitive error handling in individual controllers.
 */
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND.value(), "Not found"));
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateException(DuplicateException e) {
        return ResponseEntity.status(BAD_REQUEST).body(new ApiErrorResponse(BAD_REQUEST.value(), "Duplicated Element"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleRequestNotValidException(MethodArgumentNotValidException e) {

        List<String> errors = new ArrayList<>();
        e.getBindingResult()
                .getFieldErrors().forEach(error -> errors.add(error.getField() + ": " + error.getDefaultMessage()));
        e.getBindingResult()
                .getGlobalErrors() //Global errors are not associated with a specific field but are related to the entire object being validated.
                .forEach(error -> errors.add(error.getObjectName() + ": " + error.getDefaultMessage()));

        String message = "Validation of request failed: %s".formatted(String.join(", ", errors));
        return ResponseEntity.status(BAD_REQUEST).body(new ApiErrorResponse(BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException() {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(new ApiErrorResponse(UNAUTHORIZED.value(), "Invalid username or password"));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        // Log the exception details internally for debugging purposes
        logger.error("Internal server error: ", e);
        return ResponseEntity.status(UNAUTHORIZED).body(new ApiErrorResponse(UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknownException(Exception e) {
        // Log the exception details internally for debugging purposes
        logger.error("Internal server error: ", e);

        // Return a generic error response
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiErrorResponse(INTERNAL_SERVER_ERROR.value(), "Internal Server Error"));
    }

}