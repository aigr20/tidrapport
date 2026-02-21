package se.aigr20.tidrapport.cli;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.HashSet;

import se.aigr20.tidrapport.arguments.ArgumentParser;
import se.aigr20.tidrapport.lex.Lexer;
import se.aigr20.tidrapport.model.Tidrapport;
import se.aigr20.tidrapport.model.WeekReport;
import se.aigr20.tidrapport.model.YearReport;
import se.aigr20.tidrapport.parse.TidrapportParser;
import se.aigr20.tidrapport.parse.ParseException;
import se.aigr20.tidrapport.reporting.ReportBuilder;

public class TidrapportRunner {
  public static void main(String[] args) throws Exception {
    final var argParser = new ArgumentParser<>(args, TidrapportArguments.class);
    final var arguments = argParser.parse();

    if (arguments.isShowHelp()) {
      System.out.println(argParser.createHelpMessage("tidrapport.jar", null));
      return;
    }

    ParseException parseException = null;
    Tidrapport tidrapport;
    try (Reader reader = Files.newBufferedReader(Path.of(arguments.getFile()));
         Lexer lexer = new Lexer(reader)) {
      final TidrapportParser parser = new TidrapportParser(lexer);
      try {
        tidrapport = parser.parse();
      } catch (final ParseException e) {
        tidrapport = parser.getParsedTidrapport();
        parseException = e;
      }
    }

    final long minutesPerDay = (long) (arguments.getHoursPerDay() * 60);
    final ReportBuilder reportBuilder = new ReportBuilder(Duration.ofMinutes(minutesPerDay),
                                                          arguments.getDaysPerWeek(),
                                                          new HashSet<>(arguments
                                                                  .getExcludedFromSum()));

    if (arguments.isOnlyCurrentWeek()) {
      final YearWeekPair yearWeekPair = YearWeekPair.ofLocalDate(LocalDate.now());
      final WeekReport report = reportBuilder
              .createReportForWeek(tidrapport, yearWeekPair.year(), yearWeekPair.week());
      new ConsoleReportPrinter().printWeek(report);
    } else if (arguments.getWeekOffset() != null) {
      final YearWeekPair yearWeekPair = YearWeekPair.ofLocalDate(LocalDate.now());
      final WeekReport report = reportBuilder
              .createReportForWeek(tidrapport,
                                   yearWeekPair.year(),
                                   yearWeekPair.week() + arguments.getWeekOffset());
      new ConsoleReportPrinter().printWeek(report);
    } else {
      for (final int year : tidrapport.years().keySet()) {
        final YearReport report = reportBuilder.createReportForYear(tidrapport, year);
        new ConsoleReportPrinter().printYear(report);
      }
    }

    if (parseException != null) {
      System.out.println("""
                         Ett parsingfel uppstod. \
                         Rapporten ovan är den som kunde \
                         parsas till dess att felet uppstod.
                         """);
      System.out.println(parseException);
    }
  }

  private record YearWeekPair(int year, int week) {
    public static YearWeekPair ofLocalDate(final LocalDate date) {
      return new YearWeekPair(date.getYear(), date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
    }
  }
}
