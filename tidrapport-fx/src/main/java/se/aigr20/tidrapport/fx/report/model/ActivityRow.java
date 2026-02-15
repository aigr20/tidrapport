package se.aigr20.tidrapport.fx.report.model;

import java.time.Duration;
import java.util.Objects;

import se.aigr20.tidrapport.model.ActivityReport;

public final class ActivityRow implements ReportRow {
  private final ActivityReport report;

  public ActivityRow(final ActivityReport report) {
    this.report = report;
  }

  @Override
  public String getLabel() {
    return report.name();
  }

  @Override
  public Duration getTotal() {
    return report.duration();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }

    final ActivityRow that = (ActivityRow) obj;
    return Objects.equals(report.name(), that.report.name()) &&
           Objects.equals(report.duration(), that.report.duration());
  }

  @Override
  public int hashCode() {
    return Objects.hash(report.name(), report.duration());
  }
}
