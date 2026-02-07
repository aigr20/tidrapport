package se.aigr20.tidrapport.model;

import java.util.Collections;
import java.util.SortedMap;

public record Year(int year, SortedMap<Integer, Week> weeks) {
  @Override
  public SortedMap<Integer, Week> weeks() {
    return Collections.unmodifiableSortedMap(weeks);
  }

  public Week addWeek(final int week) {
    return weeks.computeIfAbsent(week, Week::new);
  }
}
