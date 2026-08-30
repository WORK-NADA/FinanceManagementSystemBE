package FinanceManangementSystem.demo.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a request contains invalid or malformed data.
 * HTTP Status: 400 Bad Request
 */
public class InvalidRequestException extends ApplicationException {
    private static final String ERROR_CODE = "INVALID_REQUEST";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    /**
     * Constructs an InvalidRequestException with the specified message.
     *
     * @param message A description of the invalid request
     */
    public InvalidRequestException(String message) {
        super(HTTP_STATUS, ERROR_CODE, message);
    }
}
