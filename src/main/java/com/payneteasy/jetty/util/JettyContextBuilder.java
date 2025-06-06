package com.payneteasy.jetty.util;

import org.eclipse.jetty.ee8.servlet.FilterHolder;
import org.eclipse.jetty.ee8.servlet.ServletContextHandler;
import org.eclipse.jetty.ee8.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.util.EnumSet;

public class JettyContextBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(JettyContextBuilder.class);

    private final ServletContextHandler contextHandler;

    public JettyContextBuilder(ServletContextHandler aContext) {
        contextHandler = aContext;
    }


    public JettyContextBuilder servlet(String aPath, HttpServlet aServlet) {
        contextHandler.addServlet( new ServletHolder(aServlet), aPath);
        LOG.info("Adding servlet [{}, {}]", aPath, aServlet.getClass().getSimpleName());
        return this;
    }

    public JettyContextBuilder filter(String aPath, Filter aFilter) {
        LOG.info("Adding filter [{}, {}]", aPath, aFilter.getClass().getSimpleName());
        contextHandler.addFilter(new FilterHolder(aFilter), aPath, EnumSet.of(DispatcherType.REQUEST));
        return this;
    }

}
