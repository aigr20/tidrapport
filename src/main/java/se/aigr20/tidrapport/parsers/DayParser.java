package se.aigr20.tidrapport.parsers;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.Set;

import se.aigr20.tidrapport.CharacterByCharacterReader;
import se.aigr20.tidrapport.ParseException;
import se.aigr20.tidrapport.tokens.Token;

public final class DayParser implements Parser<Token.Day> {
  private static final Set<String> LEGAL_DAYS = Set.of("måndag",
                                                       "tisdag",
                                                       "onsdag",
                                                       "torsdag",
                                                       "fredag",
                                                       "lördag",
                                                       "söndag");

  private final CharacterByCharacterReader reader;
  private final StringBuilder buffer;

  public DayParser(final CharacterByCharacterReader reader) {
    this.reader = reader;
    buffer = new StringBuilder();
  }

  @Override
  public Token.Day parse() throws IOException, ParseException {
    var read = reader.nextCharacter();
    while (!Character.isWhitespace(read)) {
      buffer.append(read);
      read = reader.nextCharacter();
    }

    final var readCharacters = buffer.toString().toLowerCase();
    buffer.setLength(0);
    if (!LEGAL_DAYS.contains(readCharacters)) {
      throw new ParseException("Läste %s när en veckodag väntades.".formatted(readCharacters));
    }

    return new Token.Day(convertDay(readCharacters));
  }

  private DayOfWeek convertDay(final String day) {
    return switch (day) {
      case "måndag" -> DayOfWeek.MONDAY;
      case "tisdag" -> DayOfWeek.TUESDAY;
      case "onsdag" -> DayOfWeek.WEDNESDAY;
      case "torsdag" -> DayOfWeek.THURSDAY;
      case "fredag" -> DayOfWeek.FRIDAY;
      case "lördag" -> DayOfWeek.SATURDAY;
      case "söndag" -> DayOfWeek.SUNDAY;
      default -> throw new IllegalArgumentException("%s är inte en dag".formatted(day));
    };
  }
}

