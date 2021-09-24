package com.payneteasy.jetty.util;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.payneteasy.jetty.util.Strings.hasText;
import static com.payneteasy.jetty.util.Strings.isEmpty;
import static java.lang.Integer.parseInt;

public class SafeServletRequest implements IRequestParameters {

    private final HttpServletRequest delegate;

    public SafeServletRequest(HttpServletRequest delegate) {
        this.delegate = delegate;
    }

    public long getRequiredLongParameter(String aName) {
        return Long.parseLong(getRequiredStringParameter(aName));
    }

    public int getIntParameter(String aName, int aDefaultValue) {
        String value = delegate.getParameter(aName);
        return isEmpty(value) ? aDefaultValue: parseInt(value) ;
    }

    public String getRequiredStringParameter(String aName) {
        String value = delegate.getParameter(aName);
        if(isEmpty(value)) {
            throw new IllegalStateException("No value for parameter " + aName);
        }
        return value;
    }

    public String getRequestBody() {
        return RequestUtils.getRequestBody(delegate);
    }

    public String getStringParameter(String aName, String aDefault) {
        String value = delegate.getParameter(aName);
        return isEmpty(value) ? aDefault : value;
    }

    @Override
    public String getStringParameter(String aName) {
        return delegate.getParameter(aName);
    }

    public String getRemoteIpAddress() {
        return delegate.getRemoteAddr();
    }

    public String getRemoteHostname() {
        return delegate.getRemoteHost();
    }

    public String getRequestUrl() {
        return delegate.getRequestURL().toString();
    }

    public boolean hasHeader(String aName) {
        return hasText(delegate.getHeader(aName));
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = delegate.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, delegate.getHeader(header));
        }
        return headers;
    }

    public List<String> getParameterNames() {
        List<String> parameters = new ArrayList<>();
        Enumeration<String> en = delegate.getParameterNames();
        while (en.hasMoreElements()) {
            String name = en.nextElement();
            parameters.add(name);
        }
        return parameters;
    }
}
