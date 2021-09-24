package com.payneteasy.jetty.util;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.System.nanoTime;
import static java.time.Duration.ofNanos;

public class DurationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long startTime = nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            httpResponse.setHeader("X-Duration", ofNanos(nanoTime() - startTime).toString());
        }
    }

    @Override
    public void destroy() {

    }
}
