package se.aigr20.tidrapport.reporting;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Optional;

import se.aigr20.tidrapport.TidrapportArguments;

public record ReporterOptions(List<String> excludeFromSum,
                              boolean currentWeekOnly,
                              Integer weekOffset,
                              int daysPerWeek,
                              double hoursPerDay) {
  public ReporterOptions(final TidrapportArguments arguments) {
    this(arguments.getExcludedFromSum(),
         arguments.isOnlyCurrentWeek(),
         arguments.getWeekOffset(),
         arguments.getDaysPerWeek(),
         arguments.getHoursPerDay());
  }

  public Optional<YearWeekPair> getWeekOnlyReport() {
    final var now = LocalDate.now();

    if (currentWeekOnly()) {
      return Optional.of(YearWeekPair.ofLocalDate(now));
    }
    if (weekOffset != null) {
      final var dayOffset = weekOffset() * 7;
      return Optional.of(YearWeekPair.ofLocalDate(now.plusDays(dayOffset)));
    }
    return Optional.empty();
  }

  public record YearWeekPair(int year, int week) {
    public static YearWeekPair ofLocalDate(final LocalDate date) {
      return new YearWeekPair(date.getYear(), date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
    }
  }
}
