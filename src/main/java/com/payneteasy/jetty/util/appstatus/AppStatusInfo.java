package com.payneteasy.jetty.util.appstatus;

import com.payneteasy.jetty.util.IJettyStartupParameters;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class AppStatusInfo {
    IJettyStartupParameters jettyConfig;
    String                  instanceName;
    Class<?>                applicationClass;
    String                  bearerToken;
}
