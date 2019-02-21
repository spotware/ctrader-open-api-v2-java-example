package com.spotware.connect.netty.exception;

public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String str){
        super(str);
    }

    public AuthorizationException(Throwable th){
        super(th);
    }
}
