package com.payneteasy.jetty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

public class SafeServletResponse {

    private static final Logger LOG = LoggerFactory.getLogger(SafeServletResponse.class);

    private final HttpServletResponse delegate;

    public SafeServletResponse(HttpServletResponse delegate) {
        this.delegate = delegate;
    }

    public void showErrorPage(int aStatusCode, String aMessage) {
        delegate.setStatus(aStatusCode);
        try {
            delegate.getWriter().println(aMessage);
        } catch (IOException e) {
            LOG.error("Cannot write", e);
            throw new IllegalStateException("Cannot write to output", e);
        }
    }

    public void copyFrom(InputStream aInput) {
        byte[] buf = new byte[4096];
        int count;
        try {
            ServletOutputStream out = delegate.getOutputStream();
            while ( (count = aInput.read(buf)) >=0 ) {
                out.write(buf, 0,  count);
            }
        } catch (IOException e) {
            LOG.error("Cannot copy input stream", e);
        }
    }

    public void writeBytes(byte[] aBytes) {
        try {
            delegate.getOutputStream().write(aBytes);
        } catch (IOException e) {
            LOG.error("Cannot write bytes", e);
        }
    }

    public void writeChunk(String aText) {
        try {
            ServletOutputStream out = delegate.getOutputStream();
            out.println(aText);
            out.flush();
            delegate.flushBuffer();
        } catch (IOException e) {
            LOG.error("Cannot write", e);
        }
    }

    public void write(String aText) {
        try {
            delegate.getOutputStream().print(aText);
        } catch (IOException e) {
            LOG.error("Cannot write", e);
        }
    }

    public void setContentType(String aContentType) {
        delegate.setContentType(aContentType);
    }

    public void setHeader(String aName, String aValue) {
        delegate.setHeader(aName, aValue);
    }

    public void setStatus(int sc) {
        delegate.setStatus(sc);
    }

    public Writer getWriter() {
        try {
            return delegate.getWriter();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot get writer", e);
        }
    }

    public void sendRedirect(String aUrl) {
        if(Strings.isEmpty(aUrl)) {
            throw new IllegalStateException("Url for redirect is empty [" + aUrl + "]");
        }
        try {
            delegate.sendRedirect(aUrl);
        } catch (IOException e) {
            LOG.error("Cannot send redirect to {}", aUrl, e);
            throw new IllegalStateException("Cannot send redirect", e);
        }
    }
}
