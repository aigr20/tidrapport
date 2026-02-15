package se.aigr20.tidrapport.fx.report.model;

import java.time.Duration;

public sealed interface ReportRow permits ActivityRow, DayRow {
  String getLabel();

  Duration getTotal();

  default Duration getRequired() {
    return null;
  }

  default Duration getDifference() {
    return null;
  }
}
