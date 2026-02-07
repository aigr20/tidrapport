package se.aigr20.tidrapport.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public record Day(DayOfWeek day, List<Activity> activities) {
  @Override
  public List<Activity> activities() {
    return Collections.unmodifiableList(activities);
  }

  public void addActivity(final String label, final LocalTime start, final LocalTime end) {
    activities.add(new Activity(label, start, end));
  }
}
