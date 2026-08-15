package FinanceManangementSystem.demo.Exceptions;

public class RefreshTokenExpiredException extends RuntimeException{

    public RefreshTokenExpiredException(String msg){
        super(msg);
    }
}