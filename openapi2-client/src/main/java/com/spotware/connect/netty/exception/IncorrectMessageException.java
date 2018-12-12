package com.spotware.connect.netty.exception;

public class IncorrectMessageException extends RuntimeException{

    public IncorrectMessageException(String str){
        super(str);
    }

    public IncorrectMessageException(Throwable th){
        super(th);
    }
}
