package se.aigr20.tidrapport;

import java.time.DayOfWeek;
import java.time.Duration;

import se.aigr20.tidrapport.model.ActivityReport;
import se.aigr20.tidrapport.model.DayReport;
import se.aigr20.tidrapport.model.WeekReport;
import se.aigr20.tidrapport.model.YearReport;

public class ConsoleReportPrinter {
  public void printYear(final YearReport report) {
    System.out.printf("År: %d%n", report.year());

    for (final WeekReport weekReport : report.weeks()) {
      printWeek(weekReport);
      System.out.println();
    }
  }

  public void printWeek(final WeekReport report) {
    System.out.printf("Vecka %d%n", report.week());

    for (final DayReport dayReport : report.days()) {
      printDay(dayReport);
      System.out.println();
      System.out.println();
    }

    System.out.printf("Totalt vecka %d: %.2fh av %.2fh%n",
                      report.week(),
                      hours(report.total()),
                      hours(report.required()));
    if (report.difference().isPositive()) {
      System.out.printf("%.2fh kvar på arbetsveckan", hours(report.difference()));
    } else {
      System.out.printf("%.2fh övertid", hours(report.difference().abs()));
    }
  }

  private void printDay(final DayReport report) {
    System.out.println(getDaySe(report.day()));

    for (final ActivityReport activity : report.activities()) {
      System.out.printf("%s: %.2fh%n", activity.name(), hours(activity.duration()));
    }
    System.out.println();

    System.out.printf("Totalt: %.2fh av %.2fh%n", hours(report.total()), hours(report.required()));
    if (report.difference().isPositive()) {
      System.out.printf("%.2fh kvar på dagen", hours(report.difference()));
    } else {
      System.out.printf("%.2fh övertid", hours(report.difference().abs()));
    }
  }

  private double hours(final Duration duration) {
    return duration.toMinutes() / 60d;
  }

  private String getDaySe(final DayOfWeek day) {
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
