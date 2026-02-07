package se.aigr20.tidrapport.model;

import java.util.Collections;
import java.util.List;

public record YearReport(int year, List<WeekReport> weeks) {
  @Override
  public List<WeekReport> weeks() {
    return Collections.unmodifiableList(weeks);
  }
}
