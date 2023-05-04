package com.payneteasy.jetty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeParameters {

    private static final Logger LOG = LoggerFactory.getLogger( SafeParameters.class );

    private final SafeServletRequest delegate;

    public SafeParameters(SafeServletRequest delegate) {
        this.delegate = delegate;
    }

    public String getRequired(String aName) {
        return delegate.getRequiredStringParameter(aName);
    }

    public String get(String aName, String aDefaultValue) {
        return delegate.getStringParameter(aName, aDefaultValue);
    }

    public int getInt(String aName, int aDefault) {
        return delegate.getIntParameter(aName, aDefault);
    }

    public long getRequiredLong(String aName) {
        return delegate.getRequiredLongParameter(aName);
    }

}
