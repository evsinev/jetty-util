package com.payneteasy.jetty.util;

import com.payneteasy.jetty.util.error.ServerStartupException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.List;

public class JettyServerBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(JettyServerBuilder.class);

    private int    port                 = 8080;
    private int    threadsMax           = 500;
    private int    threadsMin           = 16;
    private int    threadsIdleTimeoutMs = 60000;
    private String contextPath          = "/app";
    private int    metricsPort          = 9073;
    private String metricsBuckets       = "0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 0.75, 1, 2.5, 5, 7.5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 140, 180, 240, 300, 400, 600, 700, 800";
    private boolean metricsEnabled      = false;

    private final List<ServletContainer> servlets      = new ArrayList<>();
    private final List<FilterContainer>  filters       = new ArrayList<>();
    private Runnable               shutdownListener;
    private JettyContextOption     contextOption = JettyContextOption.NO_SESSIONS;
    private IJettyContextListener  contextListener;

    public JettyServerBuilder startupParameters(IJettyStartupParameters aStartup) {
        port                 = aStartup.getJettyPort();
        threadsMax           = aStartup.getJettyMaxThreads();
        threadsMin           = aStartup.getJettyMinThreads();
        threadsIdleTimeoutMs = aStartup.getJettyIdleTimeoutMs();
        contextPath          = aStartup.getJettyContext();
        metricsPort          = aStartup.getJettyMetricsPort();
        metricsBuckets       = aStartup.metricsRequestsBuckets();
        metricsEnabled       = aStartup.isJettyMetricsEnabled();

        return this;
    }

    public JettyServerBuilder contextOption(JettyContextOption aOption) {
        contextOption = aOption;
        return this;
    }

    public JettyServerBuilder contextListener(IJettyContextListener aListener) {
        contextListener = aListener;
        return this;
    }

    public JettyServerBuilder servlet(String aPath, HttpServlet aServlet) {
        servlets.add(new ServletContainer(aPath, aServlet));
        return this;
    }

    public JettyServerBuilder filter(String aPath, Filter aFilter) {
        filters.add(new FilterContainer(aPath, aFilter));
        return this;
    }

    public JettyServerBuilder shutdownListener(Runnable aShutdownListener) {
        shutdownListener = aShutdownListener;
        return this;
    }

    public JettyServer build() {

        Server jetty = JettyServerCreator.builder()
                .port                 ( port                 )
                .threadsMax           ( threadsMax           )
                .threadMin            ( threadsMin           )
                .threadsIdleTimeoutMs ( threadsIdleTimeoutMs )
                .contextPath          ( contextPath          )
                .build()
                .createServer();

        jetty.addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStopped(LifeCycle event) {
                LOG.info("Shutting down ...");
                if(shutdownListener != null) {
                    shutdownListener.run();
                }
            }
        });

        ServletContextHandler contextHandler = new ServletContextHandler(jetty, contextPath, contextOption.code());

        if(contextListener != null) {
            contextListener.contextDidCreate(contextHandler);
        }

        JettyContextBuilder contextBuilder = new JettyContextBuilder(contextHandler);
        servlets.forEach(servlet -> contextBuilder.servlet(servlet.path, servlet.servlet));
        filters.forEach(filter   -> contextBuilder.filter (filter.path , filter.filter  ));

        if(metricsEnabled) {
            try {
                new JettyMetricsServer(metricsPort, metricsBuckets)
                        .registerJettyMetrics(jetty)
                        .startMetricsServer();
            } catch (Exception e) {
                throw new ServerStartupException("Cannot start metrics server", e);
            }
        }

        return new JettyServer(jetty);
    }


    private static class ServletContainer {
        private final String      path;
        private final HttpServlet servlet;

        private ServletContainer(String path, HttpServlet servlet) {
            this.path = path;
            this.servlet = servlet;
        }
    }

    private static class FilterContainer {
        private final String path;
        private final Filter filter;

        private FilterContainer(String path, Filter filter) {
            this.path = path;
            this.filter = filter;
        }
    }
}
