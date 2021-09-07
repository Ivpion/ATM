package com.atm.exception;


public class AuthenticationException extends Exception {

    public AuthenticationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AuthenticationException(String s) {
        super(s);
    }
}
