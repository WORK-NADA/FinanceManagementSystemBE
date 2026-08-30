package FinanceManangementSystem.demo.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a business rule is violated by the request.
 * HTTP Status: 422 Unprocessable Entity
 */
public class BusinessRuleViolationException extends ApplicationException {
    private static final String ERROR_CODE = "BUSINESS_RULE_VIOLATION";
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    /**
     * Constructs a BusinessRuleViolationException with the specified message.
     *
     * @param message A description of the business rule violation
     */
    public BusinessRuleViolationException(String message) {
        super(HTTP_STATUS, ERROR_CODE, message);
    }
}
