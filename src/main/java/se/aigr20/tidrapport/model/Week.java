package se.aigr20.tidrapport.model;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public record Week(int number, Map<DayOfWeek, Day> days) {
  public Week(final int number) {
    this(number, new EnumMap<>(DayOfWeek.class));
  }

  @Override
  public Map<DayOfWeek, Day> days() {
    return Collections.unmodifiableMap(days);
  }

  public Day addDay(final DayOfWeek day) {
    return days.computeIfAbsent(day, key -> new Day(key, new ArrayList<>()));
  }
}
