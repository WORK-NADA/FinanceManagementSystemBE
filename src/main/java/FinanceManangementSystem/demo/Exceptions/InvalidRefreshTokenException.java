package FinanceManangementSystem.demo.Exceptions;

public class InvalidRefreshTokenException extends RuntimeException{

    public InvalidRefreshTokenException(String msg){
        super(msg);
    }
}
