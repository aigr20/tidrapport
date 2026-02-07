package se.aigr20.tidrapport.model;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public record WeekReport(int week,
                         List<DayReport> days,
                         Duration total,
                         Duration required,
                         Duration difference) {
  @Override
  public List<DayReport> days() {
    return Collections.unmodifiableList(days);
  }
}
