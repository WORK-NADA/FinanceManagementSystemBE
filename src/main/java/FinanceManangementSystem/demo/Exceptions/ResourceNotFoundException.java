package FinanceManangementSystem.demo.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested resource is not found.
 * HTTP Status: 404 Not Found
 */
public class ResourceNotFoundException extends ApplicationException {
    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    /**
     * Constructs a ResourceNotFoundException with the specified message.
     *
     * @param message A description of the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(HTTP_STATUS, ERROR_CODE, message);
    }
}
