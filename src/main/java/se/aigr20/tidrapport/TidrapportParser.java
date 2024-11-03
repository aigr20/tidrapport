package se.aigr20.tidrapport;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import se.aigr20.tidrapport.parsers.ActivityParser;
import se.aigr20.tidrapport.parsers.DayParser;
import se.aigr20.tidrapport.parsers.TimeParser;
import se.aigr20.tidrapport.parsers.WeekParser;
import se.aigr20.tidrapport.tokens.Token;

public class TidrapportParser implements AutoCloseable, CharacterByCharacterReader {

  private static final char ACTIVITY_DELIMITER = ':';

  private final Reader reader;
  private final WeekParser weekParser;
  private final DayParser dayParser;
  private final ActivityParser activityParser;
  private final TimeParser timeParser;
  private final List<Token<?>> tokens;
  private int line;
  private int pos;

  public TidrapportParser(final String file) throws IOException {
    reader = Files.newBufferedReader(Path.of(file));
    tokens = new ArrayList<>();
    line = 1;
    pos = 0;
    skipWhitespace();
    weekParser = new WeekParser(this);
    dayParser = new DayParser(this);
    activityParser = new ActivityParser(this, ACTIVITY_DELIMITER);
    timeParser = new TimeParser(this);
  }

  public List<Token<?>> parse() throws IOException {
    try {
      int preSize;
      do {
        preSize = tokens.size();
        tokens.add(weekParser.parse());
        skipWhitespace();
        parseDaysInWeek();
      } while (preSize < tokens.size());
    } catch (ParseException e) {
      System.err.printf("Fel på rad %d position %d%n%s%n", line, pos, e.getMessage());
      return tokens;
    } catch (final EOFException e) {
      return tokens;
    }

    return tokens;
  }

  @Override
  public char nextCharacter() throws IOException {
    var read = reader.read();
    if (read == -1) {
      throw new EOFException();
    }

    if ((char) read == '\n') {
      line++;
      pos = 0;
    } else {
      pos++;
    }
    return (char) read;
  }

  @Override
  public void close() throws Exception {
    reader.close();
  }

  private void parseDaysInWeek() throws IOException, ParseException {
    do {
      tokens.add(dayParser.parse());
      skipWhitespace();
      parseActivities();
      skipWhitespace();
    } while (Character.toLowerCase(peek()) != 'v');
  }

  private void parseActivities() throws IOException, ParseException {
    while (peek() != '\n') {
      skipWhitespace();
      tokens.add(activityParser.parse());
      skipDelimiter(ACTIVITY_DELIMITER);
      skipWhitespace();
      tokens.add(timeParser.parse());
      skipDelimiter('-');
      tokens.add(timeParser.parse());
    }
  }

  private void skipWhitespace() throws IOException {
    var foundNonWhitespace = false;
    while (!foundNonWhitespace) {
      final int oldLine = line;
      final int oldPos = pos;
      reader.mark(1);
      var read = nextCharacter();
      if (!Character.isWhitespace(read)) {
        reader.reset();
        line = oldLine;
        pos = oldPos;
        foundNonWhitespace = true;
      }
    }
  }

  private char peek() throws IOException {
    reader.mark(1);
    var read = nextCharacter();
    reader.reset();
    return read;
  }

  private void skipDelimiter(final char delimiter) throws IOException {
    reader.mark(1);
    var read = nextCharacter();
    if (read == delimiter) {
      return;
    }
    reader.reset();
  }
}
