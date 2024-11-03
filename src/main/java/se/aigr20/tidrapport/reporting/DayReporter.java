package se.aigr20.tidrapport.reporting;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DayReporter implements Reporter {
  private final DayOfWeek day;
  private final Map<String, Long> activities;

  public DayReporter(final DayOfWeek day) {
    this.day = day;
    activities = new HashMap<>();
  }

  public void addActivity(final String activity, final Duration duration) {
    activities.compute(activity, (key, oldDuration) -> {
      if (oldDuration == null) {
        return duration.toMinutes();
      }
      return oldDuration + duration.toMinutes();
    });
  }

  @Override
  public void report() {
    System.out.println(getDaySe());
    activities.forEach((activity, timeSpent) -> System.out.printf("%s: %.2f%n", activity, (double)timeSpent / 60));
    System.out.println();
  }

  public DayOfWeek getDay() {
    return day;
  }
  
  private String getDaySe() {
    return switch (day) {
      case MONDAY -> "Måndag";
      case TUESDAY -> "Tisdag";
      case WEDNESDAY -> "Onsdag";
      case THURSDAY -> "Torsdag";
      case FRIDAY -> "Fredag";
      case SATURDAY -> "Lördag";
      case SUNDAY -> "Söndag";
    };
  }
}
