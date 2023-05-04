package com.payneteasy.jetty.util;

import com.payneteasy.jetty.util.error.HttpErrorContext;
import com.payneteasy.jetty.util.error.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import static com.payneteasy.jetty.util.Strings.isEmpty;
import static com.payneteasy.jetty.util.error.HttpErrorContext.errorCtx;

public class SafeServletResponse {

    private static final Logger LOG = LoggerFactory.getLogger(SafeServletResponse.class);

    private final String url;
    private final HttpServletResponse delegate;

    public SafeServletResponse(String aUrl, HttpServletResponse delegate) {
        url = aUrl;
        this.delegate = delegate;
    }

    public void showErrorPage(int aStatusCode, String aMessage) {
        delegate.setStatus(aStatusCode);
        try {
            delegate.getWriter().println(aMessage);
        } catch (IOException e) {
            LOG.error("Cannot write", e);
            throw new InternalErrorException(
                errorCtx("Cannot write to output stream", e)
                    .sys("statusCode", aStatusCode + "")
                    .sys("message"   , aMessage)
                    .sys("url"       , url)
            );
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
            throw new InternalErrorException(errorCtx("Cannot get writer", e)
                    .friendly("Cannot write to output")
                    .sys("url"       , url)
            );
        }
    }

    public void sendRedirect(String aUrl) {
        if(isEmpty(aUrl)) {
            throw new InternalErrorException(errorCtx("URL for redirect is empty")
                    .friendlySame()
                    .sys("url"       , url)
            );
        }
        try {
            delegate.sendRedirect(aUrl);
        } catch (IOException e) {
            LOG.error("Cannot send redirect to {}", aUrl, e);
            throw new InternalErrorException(errorCtx("Cannot send redirect")
                    .friendly("Cannot send redirect to " + aUrl)
                    .sys("redirectUrl", aUrl)
                    .sys("url"        , url)
            );
        }
    }
}
