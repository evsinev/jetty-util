package com.payneteasy.jetty.util.appstatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.payneteasy.jetty.util.Strings.isEmpty;


public class AppStatusVersion {

    private static final Logger LOG = LoggerFactory.getLogger( AppStatusVersion.class );

    public static String fetchAppVersion(long aStartedTime, Class<?> aAppClass) {
        String version = aAppClass.getPackage().getImplementationVersion();
        if (isEmpty(version)) {
            return "error-no-impl-version-st-" + aStartedTime;
        }
        return version;
    }

    public static String fetchAppVersion(long aStartedTime, String pomLocation) {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(pomLocation);
        if (in == null) {
            return "error-no-pom-st-" + aStartedTime;
        }

        try {
            Properties props = new Properties();
            try {
                props.load(in);
            } catch (IOException e) {
                LOG.error("Cannot load properties from {}", pomLocation, e);
                return "error-pom-st-" + aStartedTime;
            }

            String version = props.getProperty("version");
            if (isEmpty(version)) {
                return "error-no-version-st-" + aStartedTime;
            }

            return version;
        } finally {
            closeInputStream(in);
        }
    }

    private static void closeInputStream(InputStream in) {
        try {
            in.close();
        } catch (IOException e) {
            LOG.error("Cannot close input stream", e);
        }
    }

}
