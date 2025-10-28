package se.aigr20.tidrapport.reporting;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import se.aigr20.tidrapport.tokens.Token;

public class SummaryReporter implements Reporter {
  private final List<Token<?>> tokens;
  private final Map<Integer, List<WeekReporter>> weeklyReports;
  private int currentYear;
  private WeekReporter currentWeekReporter;
  private DayReporter currentDayReporter;

  public SummaryReporter(final List<Token<?>> tokens) {
    this.tokens = tokens;
    weeklyReports = new HashMap<>();
  }

  @Override
  public void report(final ReporterOptions options) {
    try {
      calculateWeeklyReports();
    } catch (final Exception e) {
      System.out.println("Stötte på ett fel: " + e.getMessage());
      System.out.println("Inläst rapport hittills: ");
    }

    final var weekToReport = options.getWeekOnlyReport();
    if (weekToReport.isPresent()) {
      final var report = findSingleWeekReport(weekToReport.get());
      if (report.isEmpty()) {
        throw new NullPointerException("Ingen rapport för vecka %d finns för år %d.".formatted(
                weekToReport.get().week(),
                weekToReport.get().year()));
      }
      report.get().report(options);
      return;
    }

    for (final Map.Entry<Integer, List<WeekReporter>> entry : weeklyReports.entrySet()) {
      final var year = entry.getKey();
      final var reports = entry.getValue();

      System.out.printf("År: %d%n", year);
      for (final var weeklyReport : reports) {
        weeklyReport.report(options);
      }
    }
  }

  private Optional<WeekReporter> findSingleWeekReport(final ReporterOptions.YearWeekPair yearAndWeek) {
    return weeklyReports
            .getOrDefault(yearAndWeek.year(), List.of())
            .stream()
            .filter(report -> report.getWeek() == yearAndWeek.week())
            .findFirst();
  }

  private void calculateWeeklyReports() {
    final var iterator = tokens.iterator();
    while (iterator.hasNext()) {
      switch (iterator.next()) {
        case Token.Year year -> currentYear = year.getValue();
        case Token.Week week -> {
          currentWeekReporter = new WeekReporter(week.getValue());
          weeklyReports
                  .computeIfAbsent(currentYear, ignored -> new ArrayList<>(7))
                  .add(currentWeekReporter);
        }
        case Token.Day day -> currentDayReporter = currentWeekReporter.addBlankDay(day.getValue());
        case Token.Activity activity -> {
          try {
            final var startTime = (Token.Time) iterator.next();
            final var endTime = (Token.Time) iterator.next();
            final var duration = Duration.between(startTime.getValue(), endTime.getValue());
            currentDayReporter.addActivity(activity.getValue(), duration);
          } catch (final NoSuchElementException | ClassCastException e) {
            throw new IllegalStateException("Kunde inte hämta tider för aktiviteten " +
                                            activity.getValue(), e);
          }
        }
        case Token.Time ignored ->
                throw new IllegalStateException("Tid skall endast läsas när aktiviteter hanteras.");
      }
    }
  }
}
