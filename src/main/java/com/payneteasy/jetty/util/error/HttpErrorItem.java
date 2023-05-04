package com.payneteasy.jetty.util.error;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class HttpErrorItem {
    String key;
    String value;
}
