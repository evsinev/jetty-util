package com.payneteasy.jetty.util.appstatus.messages;

import com.payneteasy.jetty.util.appstatus.AppStatusInfo;
import com.payneteasy.jetty.util.appstatus.AppStatusVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.UUID.randomUUID;

public class AppStatusResponseStub {

    private static final Logger LOG = LoggerFactory.getLogger(AppStatusResponseStub.class);

    private final int    jettyPort;
    private final String hostname;
    private final String appInstanceName;
    private final long   startedTime;
    private final String appVersion;

    public AppStatusResponseStub(AppStatusInfo aInfo, String aHostname) {
        jettyPort       = aInfo.getJettyConfig().getJettyPort();
        hostname        = aHostname;
        appInstanceName = aInfo.getInstanceName();
        startedTime     = System.currentTimeMillis();
        appVersion      = AppStatusVersion.fetchAppVersion(startedTime, aInfo.getApplicationClass());

        LOG.atInfo()
                .addKeyValue("jettyPort", jettyPort)
                .addKeyValue("hostname", hostname)
                .addKeyValue("appInstanceName", appInstanceName)
                .addKeyValue("appVersion", appVersion)
                .log("Application version");
    }


    public AppStatusResponse.AppStatusResponseBuilder responseBuilder() {
        long currentTime = System.currentTimeMillis();
        return new AppStatusResponse.AppStatusResponseBuilder()
                .appInstanceName    ( appInstanceName   )
                .port               ( jettyPort         )
                .hostname           ( hostname          )
                .responseEpoch      ( currentTime       )
                .appVersion         ( appVersion        )
                .uptimeMs           ( currentTime - startedTime)
                .responseId         ( randomUUID().toString())
                ;
    }

}
