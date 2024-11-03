package se.aigr20.tidrapport.tokens;

import java.time.DayOfWeek;
import java.time.LocalTime;

public sealed abstract class Token<T> permits Token.Activity, Token.Day, Token.Time, Token.Week {
  private final T value;

  public Token(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "%s{value=%s}".formatted(getClass().getSimpleName(), value);
  }

  public static final class Week extends Token<Integer> {
    public Week(final int value) {
      super(value);
    }
  }

  public static final class Day extends Token<DayOfWeek> {
    public Day(final DayOfWeek value) {
      super(value);
    }
  }

  public static final class Activity extends Token<String> {
    public Activity(final String value) {
      super(value);
    }
  }

  public static final class Time extends Token<LocalTime> {
    public Time(final LocalTime value) {
      super(value);
    }
  }
}
