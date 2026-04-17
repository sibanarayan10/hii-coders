package com.sibanarayan.code.exceptions;

public class EntityAlreadyExistException extends RuntimeException{
    public EntityAlreadyExistException(String message){
        super(message);
    }
    public EntityAlreadyExistException(String message,Throwable cause){
        super(message,cause);
    }
}
