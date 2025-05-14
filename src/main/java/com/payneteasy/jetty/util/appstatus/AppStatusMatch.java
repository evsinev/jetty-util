package com.payneteasy.jetty.util.appstatus;

import com.payneteasy.jetty.util.appstatus.messages.AppStatusMatchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static com.payneteasy.jetty.util.Strings.hasText;
import static com.payneteasy.jetty.util.Strings.isEmpty;

public class AppStatusMatch {

    private static final Logger LOG = LoggerFactory.getLogger( AppStatusMatch.class );

    private static final String      MATCH_SEGMENT   = "/match-all/";
    private static final MatchResult OK_MATCH_RESULT = new MatchResult(true, null);

    private final AppStatusInfo info;
    private final String        hostname;
    private final int           port;

    public record MatchResult(boolean isMatched, String errorText) {}

    public AppStatusMatch(AppStatusInfo info, String hostname) {
        this.info     = info;
        this.hostname = hostname;
        port = info.getJettyConfig().getJettyPort();
    }

    public MatchResult isMatch(String aPath) {
        if (!aPath.contains(MATCH_SEGMENT)) {
            return OK_MATCH_RESULT;
        }

        Map<String, String>   params       = parsePath(aPath);
        AppStatusMatchRequest matchRequest = toMatchRequest(params);

        if (hasText(matchRequest.getHost()) && !hostname.equals(matchRequest.getHost())) {
            return new MatchResult(false, "Host mismatched: " + hostname + " != " + matchRequest.getHost());
        }

        if (hasText(matchRequest.getInstance()) && !info.getInstanceName().equals(matchRequest.getInstance())) {
            return new MatchResult(false, "Instance mismatched: " + info.getInstanceName() + " != " + matchRequest.getInstance());
        }

        if (matchRequest.getPort() != null && port != matchRequest.getPort()) {
            return new MatchResult(false, "Port mismatched: " + port + " != " + matchRequest.getPort());
        }

        return OK_MATCH_RESULT;
    }

    private AppStatusMatchRequest toMatchRequest(Map<String, String> params) {
        return AppStatusMatchRequest.builder()
                .host       ( params.get("host"))
                .instance   ( params.get("instance"))
                .port       ( parseInteger(params.get("port")))
                .build();
    }

    private Integer parseInteger(String port) {
        if (isEmpty(port)) {
            return null;
        }

        try {
            return Integer.valueOf(port);
        } catch (NumberFormatException e) {
            LOG.error("Cannot parse port as int {}", port, e);
            return null;
        }
    }

    private Map<String, String> parsePath(String aPath) {
        int position = aPath.indexOf(MATCH_SEGMENT);
        if (position < 0) {
            return Map.of();
        }

        String parametersText = aPath.substring(position + MATCH_SEGMENT.length());
        StringTokenizer st = new StringTokenizer(parametersText, "/");
        Map<String, String> map = new HashMap<>();
        while (st.hasMoreTokens()) {
            String key = st.nextToken();
            if (st.hasMoreTokens()) {
                String value = st.nextToken();
                map.put(key, value);
            }
        }
        return map;
    }
}
