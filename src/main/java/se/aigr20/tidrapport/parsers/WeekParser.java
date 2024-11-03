package se.aigr20.tidrapport.parsers;

import java.io.IOException;

import se.aigr20.tidrapport.CharacterByCharacterReader;
import se.aigr20.tidrapport.ParseException;
import se.aigr20.tidrapport.tokens.Token;

public final class WeekParser implements Parser<Token.Week> {

  private final CharacterByCharacterReader reader;
  private final StringBuilder buffer;

  public WeekParser(final CharacterByCharacterReader reader) {
    this.reader = reader;
    buffer = new StringBuilder();
  }

  @Override
  public Token.Week parse() throws IOException, ParseException {
    var read = reader.nextCharacter();
    if (Character.toLowerCase(read) != 'v') {
      throw new ParseException("Oväntat tecken: %s. Väntade ett V eller ett v.".formatted(read));
    }
    read = reader.nextCharacter();
    while (Character.isDigit(read)) {
      buffer.append(read);
      read = reader.nextCharacter();
    }

    final int week;
    try {
      week = Integer.parseInt(buffer.toString());
      buffer.setLength(0);
    } catch (final NumberFormatException e) {
      throw new ParseException("Väntade ett veckonummer.", e);
    }

    return new Token.Week(week);
  }
}
