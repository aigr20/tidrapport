package se.aigr20.tidrapport.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

public record DayReport(DayOfWeek day,
                        List<ActivityReport> activities,
                        Duration total,
                        Duration required,
                        Duration difference) {
  @Override
  public List<ActivityReport> activities() {
    return Collections.unmodifiableList(activities);
  }
}
