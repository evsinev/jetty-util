package com.payneteasy.jetty.util.appstatus;

import com.payneteasy.jetty.util.appstatus.messages.AppStatusResponseStub;
import com.payneteasy.jetty.util.appstatus.messages.AppStatusResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;

public class AppStatusServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger( AppStatusServlet.class );

    private final AppStatusResponseStub stub;
    private final String                bearerTokenHeaderValue;
    private final AppStatusMatch        match;

    public AppStatusServlet(AppStatusInfo info) {
        String hostname = getHostname();
        stub = new AppStatusResponseStub(info, hostname);
        bearerTokenHeaderValue = "Bearer " + info.getBearerToken();
        match = new AppStatusMatch(info, hostname);
    }

    @Override
    protected void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse) {
        if (!bearerTokenHeaderValue.equals(aRequest.getHeader("Authorization"))) {
            aResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        AppStatusResponseWriter writer = new AppStatusResponseWriter(aResponse, stub);

        try {
            AppStatusMatch.MatchResult matchResult = match.isMatch(aRequest.getRequestURI());
            if (!matchResult.isMatched()) {
                writer.writeResponse(
                    stub.responseBuilder()
                        .type(AppStatusResponseType.NOT_MATCHED)
                        .errorMessage(matchResult.errorText())
                        .build()
                );
                return;
            }

            writer.writeResponse(stub.responseBuilder()
                    .type ( AppStatusResponseType.OK )
                    .build());

        } catch (Exception e) {
            writer.writeError("Cannot create response", e);
        }
    }

    private static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            LOG.error("Cannot get hostname", e);
            return "unknown";
        }
    }

}
