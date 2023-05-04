package com.payneteasy.jetty.util.error;

import com.payneteasy.jetty.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.payneteasy.jetty.util.Strings.hasText;

public class HttpErrorContext {

    public static final HttpErrorContext EMPTY_CONTEXT = new HttpErrorContext("empty");

    private final List<HttpErrorItem> userItems = new ArrayList<>();
    private final List<HttpErrorItem> sysItems  = new ArrayList<>();
    private final String              internalMessage;
    private       String              friendlyMessage;
    private       Throwable           cause;
    private final String              traceId = UUID.randomUUID().toString();

    private HttpErrorContext(String aInternal) {
        internalMessage = aInternal;
    }

    public HttpErrorContext friendly(String aFriendly) {
        friendlyMessage = aFriendly;
        return this;
    }

    public HttpErrorContext cause(Throwable aCause) {
        cause = aCause;
        return this;
    }

    public HttpErrorContext user(String aKey, String aValue) {
        userItems.add(new HttpErrorItem(aKey, aValue));
        return this;
    }

    public static HttpErrorContext errorCtx(String aId) {
        return new HttpErrorContext(aId);
    }

    public static HttpErrorContext errorCtx(String aId, Throwable cause) {
        return new HttpErrorContext(aId).cause(cause);
    }

    public HttpErrorContext sys(String aKey, String aValue) {
        sysItems.add(new HttpErrorItem(aKey, aValue));
        return this;
    }

    public List<HttpErrorItem> getUserItems() {
        return userItems;
    }

    public List<HttpErrorItem> getSysItems() {
        return sysItems;
    }

    public String getInternalMessage() {
        return internalMessage;
    }

    public String getFriendlyMessage() {
        return friendlyMessage;
    }

    public Throwable getCause() {
        return cause;
    }

    public String getTraceId() {
        return traceId;
    }

    public HttpErrorContext friendlySame() {
        return friendly(internalMessage);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpErrorContext {");
        if(!userItems.isEmpty()) {
            sb.append("\n");
            sb.append("userItems=").append(userItems);
        }
        if(!sysItems.isEmpty()) {
            sb.append("\n");
            sb.append(", sysItems=").append(sysItems);
        }
        if(hasText(internalMessage)) {
            sb.append("\n");
            sb.append(", internalMessage='").append(internalMessage).append('\'');
        }
        if(hasText(friendlyMessage)) {
            sb.append("\n");
            sb.append(", friendlyMessage='").append(friendlyMessage).append('\'');
        }
        sb.append("\n");
        sb.append(", traceId='").append(traceId).append('\'');
        sb.append("\n");
        sb.append('}');
        return sb.toString();
    }
}
