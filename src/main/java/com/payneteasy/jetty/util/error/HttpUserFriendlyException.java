package com.payneteasy.jetty.util.error;

public class HttpUserFriendlyException extends IllegalStateException {

    private final int              statusCode;
    private final String           reasonPhrase;
    private final HttpErrorContext context;

    public HttpUserFriendlyException(int statusCode, String reasonPhrase, HttpErrorContext context) {
        super(context.toString(), context.getCause());
        this.statusCode   = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.context      = context;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public HttpErrorContext getContext() {
        return context;
    }
}
