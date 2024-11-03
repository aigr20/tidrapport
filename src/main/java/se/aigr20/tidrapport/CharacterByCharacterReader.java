package se.aigr20.tidrapport;

import java.io.IOException;

public interface CharacterByCharacterReader {
  char nextCharacter() throws IOException;
}
