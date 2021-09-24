package com.payneteasy.jetty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RequestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(RequestUtils.class);

    public static String getRequestBody(HttpServletRequest request) {

        String result = null;
        try {
            result = getString(request.getInputStream());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        return result;
    }

    private static String getString(InputStream inputStream) {
        if (inputStream == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            char[] charBuffer = new char[128];
            int    bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        return stringBuilder.toString();
    }
}
