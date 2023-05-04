package com.payneteasy.jetty.util.error;

public class InternalErrorException extends HttpUserFriendlyException {

    public InternalErrorException(HttpErrorContext context) {
        super(500, "Internal Error", context);
    }

}
