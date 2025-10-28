package se.aigr20.tidrapport.parsers;

import java.io.IOException;

import se.aigr20.tidrapport.CharacterByCharacterReader;
import se.aigr20.tidrapport.ParseException;
import se.aigr20.tidrapport.tokens.Token;

public final class YearParser implements Parser<Token.Year> {

  private final CharacterByCharacterReader reader;
  private final StringBuilder buffer;

  public YearParser(final CharacterByCharacterReader reader) {
    this.reader = reader;
    this.buffer = new StringBuilder();
  }

  @Override
  public Token.Year parse() throws IOException, ParseException {
    var read = reader.nextCharacter();
    if (Character.toLowerCase(read) != 'y') {
      throw new ParseException("Oväntat tecken: %s. Väntade ett Y eller ett y.".formatted(read));
    }

    read = reader.nextCharacter();
    while (Character.isDigit(read)) {
      buffer.append(read);
      read = reader.nextCharacter();
    }

    if (buffer.length() != 4) {
      throw new ParseException("Väntade ett 4-siffrigt år. Fick: %s".formatted(buffer.toString()));
    }

    final int year;
    try {
      year = Integer.parseInt(buffer.toString());
      buffer.setLength(0);
    } catch (final NumberFormatException e) {
      throw new ParseException("Väntade ett heltal.", e);
    }

    return new Token.Year(year);
  }
}
