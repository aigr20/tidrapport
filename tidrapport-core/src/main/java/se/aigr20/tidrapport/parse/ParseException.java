package se.aigr20.tidrapport.parse;

public class ParseException extends Exception {
  public ParseException(String message) {
    this(message, null);
  }

  public ParseException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
