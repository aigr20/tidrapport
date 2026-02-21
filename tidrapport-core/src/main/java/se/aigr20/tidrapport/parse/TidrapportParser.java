package se.aigr20.tidrapport.parse;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.StringJoiner;

import se.aigr20.tidrapport.lex.Lexer;
import se.aigr20.tidrapport.lex.Token;
import se.aigr20.tidrapport.lex.TokenStream;
import se.aigr20.tidrapport.model.Day;
import se.aigr20.tidrapport.model.Tidrapport;
import se.aigr20.tidrapport.model.Week;
import se.aigr20.tidrapport.model.Year;

public class TidrapportParser {
  private final TokenStream tokens;
  private Tidrapport tidrapport;

  public TidrapportParser(final Lexer lexer) {
    this.tokens = lexer.stream();
  }

  public Tidrapport parse() throws ParseException {
    tidrapport = new Tidrapport();

    while (!tokens.match(Token.Type.EOF)) {
      parseYear(tidrapport);
      skipNewLines();
    }

    return tidrapport;
  }

  /**
   * Ger tidrapporten som parsats. Uppstod ett parsefel kan man få ut den rapport som var parsar
   * fram till att felet uppstod.
   * 
   * @return Tidrapport. Antingen helt parsad eller delvis.
   */
  public Tidrapport getParsedTidrapport() {
    return tidrapport;
  }

  private void parseYear(final Tidrapport tidrapport) throws ParseException {
    skipNewLines();
    final Token yearToken = tokens.expect(Token.Type.YEAR);
    final int yearNum = Integer.parseInt(yearToken.lexeme().substring(1));
    skipNewLines();

    final Year year = tidrapport.addYear(yearNum);
    while (tokens.match(Token.Type.WEEK)) {
      parseWeek(year);
    }
  }

  private void parseWeek(final Year year) throws ParseException {
    final Token weekToken = tokens.expect(Token.Type.WEEK);
    final int weekNum = Integer.parseInt(weekToken.lexeme().substring(1));
    skipNewLines();

    final Week week = year.addWeek(weekNum);
    while (tokens.match(Token.Type.DAY)) {
      parseDay(week);
      skipNewLines();
    }
  }

  private void parseDay(final Week week) throws ParseException {
    final Token dayToken = tokens.expect(Token.Type.DAY);
    final DayOfWeek dayEnum = convertDay(dayToken.lexeme());
    final Day day = week.addDay(dayEnum);

    skipNewLines();
    while (tokens.match(Token.Type.IDENTIFIER)) {
      parseActivity(day);
      skipNewLines();
    }
  }

  private void parseActivity(final Day day) throws ParseException {
    final StringJoiner nameJoiner = new StringJoiner(" ");
    while (tokens.match(Token.Type.IDENTIFIER)) {
      nameJoiner.add(tokens.consume().lexeme());
    }
    tokens.expect(Token.Type.COLON);

    final LocalTime start = parseTime();
    tokens.expect(Token.Type.DASH);
    final LocalTime end = parseTime();

    day.addActivity(nameJoiner.toString(), start, end);
    skipNewLines();
  }

  private LocalTime parseTime() throws ParseException {
    final Token hourToken = tokens.expect(Token.Type.NUMBER);
    tokens.expect(Token.Type.COLON);
    final Token minuteToken = tokens.expect(Token.Type.NUMBER);

    final int hour = Integer.parseInt(hourToken.lexeme(), 10);
    final int minute = Integer.parseInt(minuteToken.lexeme(), 10);

    return LocalTime.of(hour, minute);
  }

  private void skipNewLines() {
    while (tokens.match(Token.Type.NEWLINE)) {
      tokens.consume();
    }
  }

  private DayOfWeek convertDay(final String day) {
    return switch (day.toLowerCase()) {
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
