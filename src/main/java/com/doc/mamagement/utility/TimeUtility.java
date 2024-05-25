package com.doc.mamagement.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtility {
    static final public String DATE_AND_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss";

    public static String toStringDateTime(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
}
