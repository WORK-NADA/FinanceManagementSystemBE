package FinanceManangementSystem.demo.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Abstract base class for all application-specific exceptions.
 * Each exception includes an errorCode for programmatic classification,
 * a user-friendly message, and an HTTP status code for REST response mapping.
 */
public abstract class ApplicationException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    /**
     * Constructs an ApplicationException with the specified HTTP status, error code, and message.
     * This protected constructor is used by subclasses to bind their exceptions to specific HTTP statuses.
     *
     * @param httpStatus The HTTP status code to return for this exception
     * @param errorCode  A machine-readable error classification (e.g., "RESOURCE_NOT_FOUND")
     * @param message    A human-readable error description
     */
    protected ApplicationException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code for this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the HTTP status code for this exception.
     *
     * @return the HTTP status
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
