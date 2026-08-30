package FinanceManangementSystem.demo.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an operation requires stock that is not available.
 * HTTP Status: 409 Conflict
 */
public class InsufficientStockException extends ApplicationException {
    private static final String ERROR_CODE = "INSUFFICIENT_STOCK";
    private static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    /**
     * Constructs an InsufficientStockException with the specified message.
     *
     * @param message A description of the insufficient stock
     */
    public InsufficientStockException(String message) {
        super(HTTP_STATUS, ERROR_CODE, message);
    }
}
