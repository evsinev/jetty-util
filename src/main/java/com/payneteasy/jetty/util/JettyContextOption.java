package com.payneteasy.jetty.util;

public enum  JettyContextOption {

      NO_SESSIONS (0)
    , SESSIONS    (1);


    public int code() {
        return code;
    }

    private final int code;

    JettyContextOption(int code) {
        this.code = code;
    }
}
