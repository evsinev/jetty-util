package com.payneteasy.jetty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class PreventStackTraceFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(PreventStackTraceFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            long id = System.currentTimeMillis();
            LOG.error("Error while processing trace {}", id, e);
            try {
                response.getOutputStream().println("Error " + id);
            } catch (IOException e1) {
                LOG.error("Cannot write error", e1);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
