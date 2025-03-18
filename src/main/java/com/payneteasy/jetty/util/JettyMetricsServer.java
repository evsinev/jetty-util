package com.payneteasy.jetty.util;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.filter.MetricsFilter;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.jetty.JettyStatisticsCollector;
import io.prometheus.client.jetty.QueuedThreadPoolStatisticsCollector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyMetricsServer {

    private static final Logger LOG = LoggerFactory.getLogger(JettyMetricsServer.class);

    private final int    port;
    private final String buckets;

    public JettyMetricsServer(int aMetricsPort, String aBuckets) {
        port = aMetricsPort;
        buckets = aBuckets;
    }


    public void startMetricsServer() throws Exception {
        Server managementServer = createManagementServer();
        managementServer.start();
        managementServer.setStopAtShutdown(true);
        LOG.info("Started metrics server on port {}", port);
    }

    public MetricsFilter createRequestMetricsFilter() {
        return new MetricsFilter("requests"
                , "The time taken fulfilling servlet requests"
                , -1
                , getRequestsBuckets(buckets)
        );

    }

    private Server createManagementServer() {
        Server                jetty   = new Server(port);
        ServletContextHandler context = new ServletContextHandler(jetty, "/", ServletContextHandler.NO_SESSIONS);

        DefaultExports.initialize();

        context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");

        return jetty;
    }

    public JettyMetricsServer registerJettyMetrics(Server aTargetServer) {
        StatisticsHandler stats = new StatisticsHandler();
        stats.setHandler(aTargetServer.getHandler());
        aTargetServer.setHandler(stats);

        new JettyStatisticsCollector(stats).register();

        new QueuedThreadPoolStatisticsCollector((QueuedThreadPool) aTargetServer.getThreadPool(), "jetty_server_thread_pool").register();

        return this;
    }

    public static double[] getRequestsBuckets(String aText) {
        String[] bucketParams = aText.split(",");
        double[] buckets      = new double[bucketParams.length];

        for (int i = 0; i < bucketParams.length; i++) {
            buckets[i] = Double.parseDouble(bucketParams[i]);
        }

        return buckets;
    }


}
