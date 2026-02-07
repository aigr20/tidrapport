package se.aigr20.tidrapport.lex;

public record Token(Type type, String lexeme, int line, int column) {
  public enum Type {
    YEAR,
    WEEK,
    DAY,
    NUMBER,
    IDENTIFIER,
    COLON,
    DASH,
    NEWLINE,
    EOF
  }
}
