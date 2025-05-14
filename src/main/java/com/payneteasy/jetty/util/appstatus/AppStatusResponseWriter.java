package com.payneteasy.jetty.util.appstatus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payneteasy.jetty.util.appstatus.messages.AppStatusResponse;
import com.payneteasy.jetty.util.appstatus.messages.AppStatusResponseStub;
import com.payneteasy.jetty.util.appstatus.messages.AppStatusResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class AppStatusResponseWriter {

    private static final Logger LOG = LoggerFactory.getLogger( AppStatusResponseWriter.class );

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final HttpServletResponse   httpResponse;
    private final AppStatusResponseStub stub;

    public AppStatusResponseWriter(HttpServletResponse httpResponse, AppStatusResponseStub stub) {
        this.httpResponse = httpResponse;
        this.stub         = stub;
    }

    public void writeResponse(AppStatusResponse aResponse) {
        httpResponse.setContentType("application/json");
        httpResponse.setStatus(aResponse.getType().httpStatus());

        PrintWriter writer;
        try {
            writer = httpResponse.getWriter();
        } catch (Exception e) {
            LOG.error("Cannot write response", e);
            return;
        }

        writer.write(GSON.toJson(aResponse));
    }

    public void writeError(String aMessage, Exception e) {
        LOG.error(aMessage, e);

        writeResponse(
                stub.responseBuilder()
                    .type(AppStatusResponseType.ERROR)
                    .errorMessage (aMessage)
                    .build()
        );
    }

}
