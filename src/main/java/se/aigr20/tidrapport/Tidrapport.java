package se.aigr20.tidrapport;

import java.util.List;

import se.aigr20.tidrapport.reporting.SummaryReporter;
import se.aigr20.tidrapport.tokens.Token;

public class Tidrapport {
  private static final String USAGE = "tidrapport <fil> [--debug --help -h]";
  private static boolean help = false;
  private static boolean debug = false;
  private static String file = null;

  public static void handleArgs(final String[] args) {
    for (final var arg : args) {
      switch (arg) {
        case "--help", "-h" -> help = true;
        case "--debug" -> debug = true;
        default -> {
          if (file != null) {
            throw new IllegalArgumentException("Oväntat argument och tidrapportsfil är redan satt: " + arg);
          }
          file = arg;
        }
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.err.println(USAGE);
      System.exit(1);
    }

    handleArgs(args);
    if (help) {
      System.err.println(USAGE);
      System.exit(0);
    }

    final List<Token<?>> tokens;
    try (var parser = new TidrapportParser(file)) {
      tokens = parser.parse();
    }

    if (debug) {
      tokens.forEach(System.out::println);
    }

    final var reporter = new SummaryReporter(tokens);
    reporter.report();
  }
}
