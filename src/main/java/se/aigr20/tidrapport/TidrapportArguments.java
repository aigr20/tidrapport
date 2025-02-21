package se.aigr20.tidrapport;

import java.util.Arrays;
import java.util.List;

import se.aigr20.tidrapport.arguments.ArgumentConverter;
import se.aigr20.tidrapport.arguments.NamedArgument;
import se.aigr20.tidrapport.arguments.PositionalArgument;

public class TidrapportArguments {
  @PositionalArgument(1)
  private final String file;

  @NamedArgument(longName = "debug", shortName = "d")
  private final boolean debug;

  @NamedArgument(longName = "current-only")
  private final boolean onlyCurrentWeek;

  @NamedArgument(longName = "week-offset", shortName = "o")
  private final Integer weekOffset;

  @NamedArgument(longName = "exclude-from-sum", converter = CommaSeparatedListConverter.class)
  private final List<String> excludedFromSum;

  public TidrapportArguments() {
    file = null;
    debug = false;
    onlyCurrentWeek = false;
    weekOffset = null;
    excludedFromSum = List.of();
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

  public static class CommaSeparatedListConverter implements ArgumentConverter<List<String>> {
    @Override
    public List<String> convert(final String s) {
      return Arrays.asList(s.split(","));
    }
  }
}
