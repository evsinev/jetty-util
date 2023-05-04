package com.payneteasy.jetty.util.error;

public class BadRequestException extends HttpUserFriendlyException {

    public BadRequestException(HttpErrorContext context) {
        super(400, "Bad Request", context);
    }

}
