package FinanceManangementSystem.demo.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when attempting to create a resource that already exists (duplicate).
 * HTTP Status: 409 Conflict
 */
public class DuplicateResourceException extends ApplicationException {
    private static final String ERROR_CODE = "DUPLICATE_RESOURCE";
    private static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    /**
     * Constructs a DuplicateResourceException with the specified message.
     *
     * @param message A description of the duplicate resource
     */
    public DuplicateResourceException(String message) {
        super(HTTP_STATUS, ERROR_CODE, message);
    }
}
