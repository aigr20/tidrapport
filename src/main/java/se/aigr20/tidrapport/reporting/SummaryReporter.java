package se.aigr20.tidrapport.reporting;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import se.aigr20.tidrapport.tokens.Token;

public class SummaryReporter implements Reporter {
  private final List<Token<?>> tokens;
  private final List<WeekReporter> weeklyReports;
  private WeekReporter currentWeekReporter;
  private DayReporter currentDayReporter;

  public SummaryReporter(final List<Token<?>> tokens) {
    this.tokens = tokens;
    weeklyReports = new ArrayList<>();
  }

  @Override
  public void report() {
    calculateWeeklyReports();
    weeklyReports.forEach(Reporter::report);
  }

  private void calculateWeeklyReports() {
    final var iterator = tokens.iterator();
    while (iterator.hasNext()) {
      switch (iterator.next()) {
        case Token.Week week -> {
          currentWeekReporter = new WeekReporter(week.getValue());
          weeklyReports.add(currentWeekReporter);
        }
        case Token.Day day -> currentDayReporter = currentWeekReporter.addBlankDay(day.getValue());
        case Token.Activity activity -> {
          try {
            final var startTime = (Token.Time) iterator.next();
            final var endTime = (Token.Time) iterator.next();
            final var duration = Duration.between(startTime.getValue(), endTime.getValue());
            currentDayReporter.addActivity(activity.getValue(), duration);
          } catch (final NoSuchElementException | ClassCastException e) {
            System.err.println("Kunde inte hämta tider för aktiviteten " + activity.getValue());
            throw e;
          }
        }
        case Token.Time ignored -> throw new IllegalStateException("Tid skall endast läsas när aktiviteter hanteras.");
      }
    }
  }
}
