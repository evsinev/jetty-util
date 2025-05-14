package com.payneteasy.jetty.util.appstatus.messages;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class AppStatusMatchRequest {
    String  host;
    String  instance;
    Integer port;
}
