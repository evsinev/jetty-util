package com.payneteasy.jetty.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.payneteasy.jetty.util.Strings.isEmpty;

public class UrlParametersBuilder {

    private enum State {
        URL_ADDED, FIRST_ADDED
    }

    private final StringBuilder sb = new StringBuilder();
    private       State         state;


    public UrlParametersBuilder(String aUrl) {
        sb.append(aUrl);
        state = State.URL_ADDED;
    }

    public UrlParametersBuilder add(String aName, long aValue) {
        return add(aName, String.valueOf(aValue));
    }

    public UrlParametersBuilder addOnlyWithValue(String aName, String aValue) {
        if (isEmpty(aValue)) {
            return this;
        }
        return add(aName, aValue);
    }

    public UrlParametersBuilder add(String aName, String aValue) {
        switch (state) {
            case URL_ADDED:
                sb.append('?');
                state = State.FIRST_ADDED;
                break;
            case FIRST_ADDED:
                sb.append('&');
                break;
        }

        sb.append(urlEncode(aName));
        sb.append('=');
        sb.append(urlEncode(aValue));
        return this;
    }

    private String urlEncode(String aValue) {
        try {
            return URLEncoder.encode(aValue, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Cannot encode url", e);
        }
    }

    public String build() {
        return sb.toString();
    }


}