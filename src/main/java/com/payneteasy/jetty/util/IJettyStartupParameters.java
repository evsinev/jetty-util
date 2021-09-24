package com.payneteasy.jetty.util;

import com.payneteasy.startup.parameters.AStartupParameter;

public interface IJettyStartupParameters {

    /**
     * Jetty max threads. The value could be from 50 to 500 as of https://www.eclipse.org/jetty/documentation/current/high-load.html
     *
     * @return max threads
     */
    @AStartupParameter(name = "JETTY_MAX_THREADS", value = "500")
    int getJettyMaxThreads();

    @AStartupParameter(name = "JETTY_MIN_THREADS", value = "16")
    int getJettyMinThreads();

    @AStartupParameter(name = "JETTY_POOL_IDLE_TIMEOUT_MS", value = "60000")
    int getJettyIdleTimeoutMs();

    @AStartupParameter(name = "JETTY_PORT", value = "8080")
    int getJettyPort();

    @AStartupParameter(name = "JETTY_METRICS_ENABLED", value = "false")
    boolean isJettyMetricsEnabled();

    @AStartupParameter(name = "JETTY_METRICS_PORT", value = "9073")
    int getJettyMetricsPort();

    @AStartupParameter(name = "JETTY_METRICS_REQUEST_BUCKETS", value = "0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 0.75, 1, 2.5, 5, 7.5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 140, 180, 240, 300, 400, 600, 700, 800")
    String metricsRequestsBuckets();

    @AStartupParameter(name = "JETTY_CONTEXT", value = "/app")
    String getJettyContext();


}
