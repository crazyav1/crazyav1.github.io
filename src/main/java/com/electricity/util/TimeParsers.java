package com.electricity.util;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class TimeParsers {

  // 2014-07-01 00:00:00+00:00
  private static final DateTimeFormatter OFFSET_SPACE =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxxx");

  public static OffsetDateTime parseOffsetDateTime(String raw) {
    if (raw == null) throw new IllegalArgumentException("timestamp is null");
    String s = raw.trim();

    if (s.length() > 10 && s.charAt(10) == 'T') {
      return OffsetDateTime.parse(s);
    }

    if (s.length() > 10 && s.charAt(10) == ' ') {
      return OffsetDateTime.parse(s, OFFSET_SPACE);
    }

    return OffsetDateTime.parse(s.replace(' ', 'T'));
  }

  private TimeParsers() {}
}
