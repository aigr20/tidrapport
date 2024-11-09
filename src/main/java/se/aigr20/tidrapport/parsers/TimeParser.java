package se.aigr20.tidrapport.parsers;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import se.aigr20.tidrapport.CharacterByCharacterReader;
import se.aigr20.tidrapport.ParseException;
import se.aigr20.tidrapport.tokens.Token;

public final class TimeParser implements Parser<Token.Time> {

  private static final DateTimeFormatter FALLBACK_PATTERN = DateTimeFormatter.ofPattern("H:mm");
  private final CharacterByCharacterReader reader;
  private final StringBuilder buffer;

  public TimeParser(final CharacterByCharacterReader reader) {
    this.reader = reader;
    buffer = new StringBuilder();
  }

  @Override
  public Token.Time parse() throws IOException, ParseException {
    var read = reader.nextCharacter();
    while (Character.isDigit(read) || read == ':') {
      buffer.append(read);
      read = reader.nextCharacter();
    }

    final LocalTime time;
    try {
      if (buffer.length() > 4) {
        time = LocalTime.parse(buffer, DateTimeFormatter.ISO_LOCAL_TIME);
      } else {
        time = LocalTime.parse(buffer, FALLBACK_PATTERN);
      }
      buffer.setLength(0);
    } catch (final DateTimeParseException e) {
      throw new ParseException("%s kan inte tolkas som en tid".formatted(buffer), e);
    }
    return new Token.Time(time);
  }
}
