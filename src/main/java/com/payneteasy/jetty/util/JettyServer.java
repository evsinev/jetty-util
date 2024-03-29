package com.payneteasy.jetty.util;

import com.payneteasy.jetty.util.error.ServerStartupException;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyServer {

    private static final Logger LOG = LoggerFactory.getLogger(JettyServer.class);

    private final Server jetty;

    public JettyServer(Server jetty) {
        this.jetty = jetty;
    }

    public void startJetty() {
        try {
            jetty.start();
            jetty.setStopAtShutdown(true);
        } catch (Exception e) {
            throw new ServerStartupException("Cannot start jetty", e);
        }
    }

    public void stopJetty() {
        try {
            jetty.stop();
        } catch (Exception e) {
            throw new ServerStartupException("Cannot stop jetty", e);
        }
    }

}
