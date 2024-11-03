package se.aigr20.tidrapport.parsers;

import java.io.IOException;

import se.aigr20.tidrapport.CharacterByCharacterReader;
import se.aigr20.tidrapport.tokens.Token;

public final class ActivityParser implements Parser<Token.Activity> {

  private final CharacterByCharacterReader reader;
  private final char delimiter;
  private final StringBuilder buffer;

  public ActivityParser(final CharacterByCharacterReader reader, final char delimiter) {
    this.reader = reader;
    this.delimiter = delimiter;
    buffer = new StringBuilder();
  }

  @Override
  public Token.Activity parse() throws IOException {
    var read = reader.nextCharacter();
    while (read != delimiter) {
      buffer.append(read);
      read = reader.nextCharacter();
    }

    final var activity = new Token.Activity(buffer.toString());
    buffer.setLength(0);
    return activity;
  }
}
