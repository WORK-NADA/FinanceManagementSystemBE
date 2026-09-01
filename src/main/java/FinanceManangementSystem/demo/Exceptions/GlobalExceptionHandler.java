package FinanceManangementSystem.demo.Exceptions;

import FinanceManangementSystem.demo.Payloads.ResponseDTO.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Validation Exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        for(FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponse resp = new ErrorResponse(
                "VALIDATION_ERROR",
                "Validation failed",
                errors
        );

        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(org.springframework.web.bind.MissingServletRequestParameterException ex) {
        ErrorResponse resp = new ErrorResponse(
                "MISSING_PARAMETER",
                "Required parameter '" + ex.getParameterName() + "' is missing."
        );
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all typed application exceptions (ResourceNotFoundException, DuplicateResourceException, etc.)
     * Each exception carries its own HTTP status code, error code, and message.
     * This handler is more specific than the catch-all Exception.class handler below.
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex) {
        ErrorResponse resp = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(resp);
    }

    /**
     * Handles InvalidRefreshToken exceptions.
     * Kept separate from ApplicationException handler for backward compatibility.
     */
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex){
        ErrorResponse resp = new ErrorResponse(
                "INVALID_REFRESH_TOKEN",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(resp);
    }

    /**
     * Handles RefreshTokenExpired exceptions.
     * Kept separate from ApplicationException handler for backward compatibility.
     */
    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenExpired(RefreshTokenExpiredException ex) {

        ErrorResponse resp = new ErrorResponse(
                "REFRESH_TOKEN_EXPIRED",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(resp);
    }

    /**
     * Handles all Spring Security authentication failures (bad credentials, disabled/locked
     * accounts, etc.) that occur during login. This is an expected, routine failure case —
     * not a bug — so it must NOT be logged as ERROR or leak to the generic exception handler.
     *
     * Covers: BadCredentialsException, DisabledException, LockedException,
     * AccountExpiredException, and any other AuthenticationException subclass.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        // WARN — not ERROR: wrong password is routine user error, not an application defect
        log.warn("Authentication failed: {}", ex.getMessage());

        ErrorResponse resp = new ErrorResponse(
                "INVALID_CREDENTIALS",
                "Invalid email or password"
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(resp);
    }

    /**
     * Handles all unexpected/unhandled exceptions that are not caught by more specific handlers.
     * Spring exception handler matching is by most-specific-first, so this handler will only
     * catch truly unexpected errors (NPEs, bugs, etc.) that are NOT instances of ApplicationException,
     * MethodArgumentNotValidException, InvalidRefreshTokenException, RefreshTokenExpiredException,
     * or AuthenticationException.
     *
     * NOTE: In production, unexpected errors should be logged with full stack trace at ERROR level
     * but the client response should NOT leak internal implementation details.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        // Log the unexpected exception with full stack trace for debugging
        log.error("Unhandled exception encountered", ex);

        ErrorResponse resp = new ErrorResponse(
                "INTERNAL_ERROR",
                "An unexpected error occurred"
        );

        return new ResponseEntity<>(
                resp,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
