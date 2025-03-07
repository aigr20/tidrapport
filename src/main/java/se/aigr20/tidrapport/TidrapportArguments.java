package se.aigr20.tidrapport;

import java.util.Arrays;
import java.util.List;

import se.aigr20.tidrapport.arguments.ArgumentConverter;
import se.aigr20.tidrapport.arguments.NamedArgument;
import se.aigr20.tidrapport.arguments.PositionalArgument;

public class TidrapportArguments {
  @PositionalArgument(1)
  private final String file;

  @NamedArgument(longName = "debug", shortName = "d", description = "Visa de tokens som skapats från filen.")
  private final boolean debug;

  @NamedArgument(longName = "current-only", shortName = "c", description = "Visa bara denna veckan")
  private final boolean onlyCurrentWeek;

  @NamedArgument(longName = "week-offset",
                 shortName = "o",
                 description = "Justering från denna veckan, t.ex -1 visar förra veckans rapport")
  private final Integer weekOffset;

  @NamedArgument(longName = "help", shortName = "h", stopsParsing = true)
  private final boolean showHelp;

  @NamedArgument(longName = "exclude-from-sum",
                 converter = CommaSeparatedListConverter.class,
                 description = "Kommaseparerad lista, t.ex lunch,rast,etc. De aktiviteter som är med i listan tas ej med när tiden summeras.")
  private final List<String> excludedFromSum;

  public TidrapportArguments() {
    file = null;
    debug = false;
    onlyCurrentWeek = false;
    weekOffset = null;
    excludedFromSum = List.of();
    showHelp = false;
  }

  public String getFile() {
    return file;
  }

  public boolean isDebug() {
    return debug;
  }

  public List<String> getExcludedFromSum() {
    return excludedFromSum;
  }

  public boolean isOnlyCurrentWeek() {
    return onlyCurrentWeek;
  }

  public Integer getWeekOffset() {
    return weekOffset;
  }

  public boolean isShowHelp() {
    return showHelp;
  }

  public static class CommaSeparatedListConverter implements ArgumentConverter<List<String>> {
    @Override
    public List<String> convert(final String s) {
      return Arrays.asList(s.split(","));
    }
  }
}
