package se.aigr20.tidrapport.parsers;

import java.io.IOException;

import se.aigr20.tidrapport.ParseException;
import se.aigr20.tidrapport.tokens.Token;

public sealed interface Parser<T extends Token<?>> permits ActivityParser, DayParser, TimeParser, WeekParser {
  T parse() throws IOException, ParseException;
}
