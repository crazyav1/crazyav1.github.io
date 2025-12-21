package com.electricity.util;

import java.time.OffsetDateTime;

import static com.electricity.config.Constants.DATE_TIME_FORMATTER;

public final class TimeParsers {

  public static OffsetDateTime parseOffsetDateTime(String raw) {
    if (raw == null) throw new IllegalArgumentException("timestamp is null");
    String s = raw.trim();

    if (s.length() > 10 && s.charAt(10) == 'T') {
      return OffsetDateTime.parse(s);
    }

    if (s.length() > 10 && s.charAt(10) == ' ') {
      return OffsetDateTime.parse(s, DATE_TIME_FORMATTER);
    }

    return OffsetDateTime.parse(s.replace(' ', 'T'));
  }

  private TimeParsers() {}
}
