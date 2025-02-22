package se.aigr20.tidrapport;

import java.util.List;

import se.aigr20.tidrapport.arguments.ArgumentParser;
import se.aigr20.tidrapport.reporting.ReporterOptions;
import se.aigr20.tidrapport.reporting.SummaryReporter;
import se.aigr20.tidrapport.tokens.Token;

public class Tidrapport {
  public static void main(String[] args) throws Exception {
    final var argParser = new ArgumentParser<>(args, TidrapportArguments.class);
    final var arguments = argParser.parse();

    if (arguments.isShowHelp()) {
      System.out.println(argParser.createHelpMessage("tidrapport.jar", null));
      return;
    }

    final List<Token<?>> tokens;
    try (var parser = new TidrapportParser(arguments.getFile())) {
      tokens = parser.parse();
    }

    if (arguments.isDebug()) {
      tokens.forEach(System.out::println);
    }

    final var reporter = new SummaryReporter(tokens);
    reporter.report(new ReporterOptions(arguments));
  }
}
