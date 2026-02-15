package se.aigr20.tidrapport.fx.report;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.List;

import se.aigr20.tidrapport.fx.settings.SettingsService;
import se.aigr20.tidrapport.fx.settings.TidrapportSettings;
import se.aigr20.tidrapport.lex.Lexer;
import se.aigr20.tidrapport.model.Tidrapport;
import se.aigr20.tidrapport.model.YearReport;
import se.aigr20.tidrapport.parse.TidrapportParser;
import se.aigr20.tidrapport.reporting.ReportBuilder;

import javafx.concurrent.Task;

public class ReportService {

  private final SettingsService configuration;
  private Tidrapport cached;

  public ReportService(final SettingsService configuration) {
    this.configuration = configuration;
  }

  public Task<Boolean> loadFile(final Path file) {
    return new Task<>() {
      @Override
      protected Boolean call() throws Exception {
        try (final Reader reader = Files.newBufferedReader(file);
             final Lexer lexer = new Lexer(reader)) {
          final TidrapportParser parser = new TidrapportParser(lexer);
          cached = parser.parse();
        }
        return true;
      }
    };
  }

  public List<Year> getAvailableYears() {
    if (cached == null) {
      return List.of();
    }

    return cached.years().keySet().stream().sorted().map(Year::of).toList();
  }

  public Task<YearReport> openYearReport(final int year) {
    return new Task<>() {
      @Override
      protected YearReport call() throws Exception {
        if (cached == null) {
          throw new IllegalStateException("no file loaded");
        }

        final TidrapportSettings settings = configuration.getCurrent();
        final ReportBuilder builder = new ReportBuilder(settings.hoursPerDayDuration(),
                                                        settings.daysPerWeek(),
                                                        settings.excludedActivities());
        return builder.createReportForYear(cached, year);
      }
    };
  }
}
