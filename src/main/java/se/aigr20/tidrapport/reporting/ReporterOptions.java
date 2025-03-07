package se.aigr20.tidrapport.reporting;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.OptionalInt;

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

  public OptionalInt getWeekOnlyReport() {
    final var currentWeek = LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

    if (currentWeekOnly()) {
      return OptionalInt.of(currentWeek);
    }
    if (weekOffset != null) {
      return OptionalInt.of(currentWeek + weekOffset());
    }
    return OptionalInt.empty();
  }
}
