package se.aigr20.tidrapport.reporting;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.aigr20.tidrapport.model.Activity;
import se.aigr20.tidrapport.model.ActivityReport;
import se.aigr20.tidrapport.model.Day;
import se.aigr20.tidrapport.model.DayReport;
import se.aigr20.tidrapport.model.Tidrapport;
import se.aigr20.tidrapport.model.Week;
import se.aigr20.tidrapport.model.WeekReport;
import se.aigr20.tidrapport.model.Year;
import se.aigr20.tidrapport.model.YearReport;

public class ReportBuilder {

  private Duration requiredDurationPerDay;
  private int daysPerWeek;
  private Set<String> activitiesExcludedFromDurationTotals;

  public ReportBuilder(final Duration requiredDurationPerDay,
                       final int daysPerWeek,
                       final Set<String> activitiesExcludedFromDurationTotals) {
    this.requiredDurationPerDay = requiredDurationPerDay;
    this.daysPerWeek = daysPerWeek;
    this.activitiesExcludedFromDurationTotals = activitiesExcludedFromDurationTotals;
  }

  public YearReport createReportForYear(final Tidrapport tidrapport, final int year) {
    final Year yearModel = tidrapport.years().get(year);
    final List<WeekReport> reports = yearModel
            .weeks()
            .values()
            .stream()
            .sorted(Comparator.comparingInt(Week::number))
            .map(this::toWeekReport)
            .toList();

    return new YearReport(yearModel.year(), reports);
  }

  public WeekReport createReportForWeek(final Tidrapport tidrapport,
                                        final int year,
                                        final int week) {
    final Year yearModel = tidrapport.years().get(year);

    return toWeekReport(yearModel.weeks().get(week));
  }

  public Duration getRequiredDurationPerDay() {
    return requiredDurationPerDay;
  }

  public void setRequiredDurationPerDay(final Duration requiredDurationPerDay) {
    this.requiredDurationPerDay = requiredDurationPerDay;
  }

  public int getDaysPerWeek() {
    return daysPerWeek;
  }

  public void setDaysPerWeek(final int daysPerWeek) {
    this.daysPerWeek = daysPerWeek;
  }

  public Set<String> getActivitiesExcludedFromDurationTotals() {
    return activitiesExcludedFromDurationTotals;
  }

  public void setActivitiesExcludedFromDurationTotals(final Set<String> activitiesExcludedFromDurationTotals) {
    this.activitiesExcludedFromDurationTotals = activitiesExcludedFromDurationTotals;
  }

  private WeekReport toWeekReport(final Week week) {
    final List<DayReport> days = new ArrayList<>();
    Duration weekTotal = Duration.ZERO;
    for (Day day : week.days().values()) {
      DayReport dayReport = toDayReport(day);
      days.add(dayReport);
      weekTotal = weekTotal.plus(dayReport.total());
    }

    final Duration required = requiredDurationPerDay.multipliedBy(daysPerWeek);

    return new WeekReport(week.number(), days, weekTotal, required, required.minus(weekTotal));
  }

  private DayReport toDayReport(final Day day) {
    final Map<String, ActivityReport> activityReports = new HashMap<>();
    Duration dayTotal = Duration.ZERO;
    for (final Activity activity : day.activities()) {
      final ActivityReport report = toActivityReport(activity);
      activityReports.merge(activity.label(), report, this::mergeReports);
      if (!activitiesExcludedFromDurationTotals.contains(report.name())) {
        dayTotal = dayTotal.plus(report.duration());
      }
    }

    return new DayReport(day.day(),
                         new ArrayList<>(activityReports.values()),
                         dayTotal,
                         requiredDurationPerDay,
                         requiredDurationPerDay.minus(dayTotal));
  }

  private ActivityReport toActivityReport(final Activity activity) {
    return new ActivityReport(activity.label(), Duration.between(activity.start(), activity.end()));
  }

  private ActivityReport mergeReports(final ActivityReport a, final ActivityReport b) {
    return new ActivityReport(a.name(), a.duration().plus(b.duration()));
  }
}
