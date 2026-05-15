package com.payneteasy.jetty.util;

import com.payneteasy.jetty.util.error.ServerStartupException;
import org.eclipse.jetty.ee8.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class JettyServerBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(JettyServerBuilder.class);

    private int      port                 = 8080;
    private int      threadsMax           = 500;
    private int      threadsMin           = 16;
    private int      threadsIdleTimeoutMs = 60_000;
    private Duration stopTimeout          = Duration.ofMinutes(3);
    private String   contextPath          = "/app";
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
        stopTimeout          = aStartup.getJettyStopTimeout();
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
                .stopTimeout          ( stopTimeout          )
                .contextPath          ( contextPath          )
                .build()
                .createServer();

        ServletContextHandler contextHandler = new ServletContextHandler(jetty, contextPath, contextOption.code());

        if(contextListener != null) {
            contextListener.contextDidCreate(contextHandler);
        }

        JettyContextBuilder contextBuilder = new JettyContextBuilder(contextHandler);
        servlets.forEach(servlet -> contextBuilder.servlet(servlet.path, servlet.servlet));
        filters.forEach(filter   -> contextBuilder.filter (filter.path , filter.filter  ));

        StatisticsHandler statisticsHandler = new StatisticsHandler();
        statisticsHandler.setHandler(jetty.getHandler());
        jetty.setHandler(statisticsHandler);

        registerShutdownProgressListener(jetty, statisticsHandler);

        if(metricsEnabled) {
            try {
                new JettyMetricsServer(metricsPort, metricsBuckets)
                        .registerJettyMetrics(jetty, statisticsHandler)
                        .startMetricsServer();
            } catch (Exception e) {
                throw new ServerStartupException("Cannot start metrics server", e);
            }
        }

        jetty.setStopAtShutdown(true);

        return new JettyServer(jetty);
    }


    private void registerShutdownProgressListener(Server aJetty, StatisticsHandler aStats) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(aRunnable -> {
            Thread t = new Thread(aRunnable, "jetty-shutdown-progress");
            t.setDaemon(true);
            return t;
        });
        AtomicReference<ScheduledFuture<?>> taskRef = new AtomicReference<>();

        aJetty.addEventListener(new LifeCycle.Listener() {
            @Override
            public void lifeCycleStopping(LifeCycle event) {
                int active = aStats.getRequestsActive();
                LOG.info("Graceful shutdown started, waiting up to {} for {} in-flight HTTP request(s)",
                        stopTimeout, active);
                ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
                    int n = aStats.getRequestsActive();
                    if (n > 0) {
                        LOG.info("Still waiting for {} in-flight HTTP request(s) to complete", n);
                    }
                }, 10, 10, TimeUnit.SECONDS);
                taskRef.set(task);
            }

            @Override
            public void lifeCycleStopped(LifeCycle event) {
                ScheduledFuture<?> task = taskRef.getAndSet(null);
                if (task != null) {
                    task.cancel(false);
                }
                scheduler.shutdownNow();
                LOG.info("Shutting down ...");
                if (shutdownListener != null) {
                    shutdownListener.run();
                }
            }
        });
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
