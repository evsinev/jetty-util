package com.payneteasy.jetty.util.error;

public class ServerStartupException extends IllegalStateException {

    public ServerStartupException(String s) {
        super(s);
    }

    public ServerStartupException(String message, Throwable cause) {
        super(message, cause);
    }
}
