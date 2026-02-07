package se.aigr20.tidrapport.model;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public record Tidrapport(SortedMap<Integer, Year> years) {

  public Tidrapport() {
    this(new TreeMap<>());
  }

  @Override
  public SortedMap<Integer, Year> years() {
    return Collections.unmodifiableSortedMap(years);
  }

  public Year addYear(final int year) {
    return years.computeIfAbsent(year, key -> new Year(key, new TreeMap<>()));
  }
}
