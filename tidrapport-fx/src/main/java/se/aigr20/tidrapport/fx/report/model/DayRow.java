package se.aigr20.tidrapport.fx.report.model;

import java.time.Duration;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import se.aigr20.tidrapport.model.DayReport;

public final class DayRow implements ReportRow {

  private final DayReport report;
  private String label;

  public DayRow(final DayReport report) {
    this.report = report;
  }

  public List<ActivityRow> getActivities() {
    return report.activities().stream().map(ActivityRow::new).toList();
  }

  @Override
  public String getLabel() {
    if (label == null) {
      final String dayLocalized = report
              .day()
              .getDisplayName(TextStyle.FULL, Locale.of("sv", "SE"));
      label = Character.toUpperCase(dayLocalized.charAt(0)) + dayLocalized.substring(1);
    }
    return label;
  }

  @Override
  public Duration getTotal() {
    return report.total();
  }

  @Override
  public Duration getRequired() {
    return report.required();
  }

  @Override
  public Duration getDifference() {
    return report.difference();
  }
}
