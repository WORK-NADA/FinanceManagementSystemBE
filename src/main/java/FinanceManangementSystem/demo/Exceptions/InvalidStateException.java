package FinanceManangementSystem.demo.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an operation violates the current state of a resource.
 * HTTP Status: 409 Conflict
 */
public class InvalidStateException extends ApplicationException {
    private static final String ERROR_CODE = "INVALID_STATE";
    private static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    /**
     * Constructs an InvalidStateException with the specified message.
     *
     * @param message A description of the state violation
     */
    public InvalidStateException(String message) {
        super(HTTP_STATUS, ERROR_CODE, message);
    }
}
