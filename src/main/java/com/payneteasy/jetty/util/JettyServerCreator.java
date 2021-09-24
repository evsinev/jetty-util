package com.payneteasy.jetty.util;

import lombok.Builder;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

@Builder
public class JettyServerCreator {

    private final int     port;
    private final int     threadsMax;
    private final int     threadMin;
    private final int     threadsIdleTimeoutMs;
    private final String  contextPath;

    public static Server createJettyServer(IJettyStartupParameters aConfig) {
        return JettyServerCreator.builder()
                .port                 ( aConfig.getJettyPort())
                .threadsMax           ( aConfig.getJettyMaxThreads())
                .threadMin            ( aConfig.getJettyMinThreads())
                .threadsIdleTimeoutMs ( aConfig.getJettyIdleTimeoutMs())
                .contextPath          ( aConfig.getJettyContext() )
                .build()
                .createServer();
    }

    public Server createServer() {
        QueuedThreadPool threadPool = new QueuedThreadPool(
                threadsMax
                , threadMin
                , threadsIdleTimeoutMs
        );
        threadPool.setName("jetty");

        Server jetty = new Server(threadPool);

        HttpConfiguration config = new HttpConfiguration();
        config.addCustomizer(new ForwardedRequestCustomizer());
        config.setSendServerVersion(false);
        config.setSendXPoweredBy(false);
        config.setSendDateHeader(true);

        ServerConnector connector = new ServerConnector(jetty, new HttpConnectionFactory(config)); //NOSONAR jetty will close this connection
        connector.setPort(port);

        jetty.setConnectors(new Connector[]{connector});

        return jetty;
    }
}
