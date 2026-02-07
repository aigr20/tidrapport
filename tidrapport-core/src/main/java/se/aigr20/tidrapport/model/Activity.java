package se.aigr20.tidrapport.model;

import java.time.Duration;
import java.time.LocalTime;

public record Activity(String label, LocalTime start, LocalTime end) {

  public Duration getDuration() {
    return Duration.between(start, end);
  }
}
