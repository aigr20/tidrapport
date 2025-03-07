package se.aigr20.tidrapport.reporting;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class WeekReporter implements Reporter {
  private final int week;
  private final List<DayReporter> reports;
  private final EnumSet<DayOfWeek> addedDays;

  public WeekReporter(final int week) {
    this.week = week;
    reports = new ArrayList<>();
    addedDays = EnumSet.noneOf(DayOfWeek.class);
  }

  public DayReporter addBlankDay(final DayOfWeek day) {
    if (addedDays.contains(day)) {
      throw new IllegalArgumentException(day + " är redan inlagd för vecka " + week);
    }
    final var newReport = new DayReporter(day);
    reports.add(newReport);
    addedDays.add(day);
    return newReport;
  }

  @Override
  public void report(final ReporterOptions options) {
    var weekSum = 0d;
    final var requiredWeekSum = options.daysPerWeek() * options.hoursPerDay();

    System.out.println("Vecka " + week);
    reports.sort(Comparator.comparing(DayReporter::getDay));
    for (final var report : reports) {
      report.report(options);
      weekSum += report.totalMinutes(options.excludeFromSum());
    }
    final var diffFromRequired = requiredWeekSum - weekSum;

    System.out.printf("Totalt vecka %d: %.2fh%n", week, weekSum);
    if (diffFromRequired < 0) {
      System.out.printf("%.2fh övertid.%n", Math.abs(diffFromRequired));
    } else if (diffFromRequired > 0) {
      System.out.printf("%.2fh timmar kvar på arbetsveckan.%n", diffFromRequired);
    }
    System.out.println();
  }

  public int getWeek() {
    return week;
  }
}
