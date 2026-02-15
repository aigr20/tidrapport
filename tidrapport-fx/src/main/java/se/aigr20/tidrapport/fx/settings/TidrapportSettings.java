package se.aigr20.tidrapport.fx.settings;

import java.time.Duration;
import java.util.Set;

public record TidrapportSettings(String tidrapportFilePath,
                                 double hoursPerDay,
                                 int daysPerWeek,
                                 Set<String> excludedActivities) {
  public Duration hoursPerDayDuration() {
    final long minutes = (long) (hoursPerDay * 60);
    return Duration.ofMinutes(minutes);
  }

  public TidrapportSettings withTidrapportFilePath(final String tidrapportFilePath) {
    return new TidrapportSettings(tidrapportFilePath, hoursPerDay, daysPerWeek, excludedActivities);
  }

  public TidrapportSettings withHoursPerDay(final double hoursPerDay) {
    return new TidrapportSettings(tidrapportFilePath, hoursPerDay, daysPerWeek, excludedActivities);
  }

  public TidrapportSettings withDaysPerWeek(final int daysPerWeek) {
    return new TidrapportSettings(tidrapportFilePath, hoursPerDay, daysPerWeek, excludedActivities);
  }

  public TidrapportSettings withExcludedActivities(final Set<String> excludedActivities) {
    return new TidrapportSettings(tidrapportFilePath, hoursPerDay, daysPerWeek, excludedActivities);
  }
}
