package com.payneteasy.jetty.util;

import com.payneteasy.jetty.util.error.BadRequestException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.payneteasy.jetty.util.error.HttpErrorContext.errorCtx;

public abstract class SafeHttpServlet extends HttpServlet {

    @Override
    protected final  void doPost(HttpServletRequest aRequest, HttpServletResponse aResponse) {
        doSafePost(new SafeServletRequest(aRequest), new SafeServletResponse(aRequest.getRequestURL().toString(), aResponse));
    }

    @Override
    protected final  void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse) {
        doSafeGet(new SafeServletRequest(aRequest), new SafeServletResponse(aRequest.getRequestURL().toString(), aResponse));
    }

    protected void doSafePost(SafeServletRequest aRequest, SafeServletResponse aResponse) {
        throw new BadRequestException(errorCtx("doSafePost(...) method not implemented")
                .sys("class", getClass().getName())
                .friendly("POST not supported")
        );
    }

    protected void doSafeGet(SafeServletRequest aRequest, SafeServletResponse aResponse) {
        throw new BadRequestException(errorCtx("doSafeGet(...) method not implemented")
                .sys("class", getClass().getName())
                .friendly("GET not supported")
        );
    }


}
