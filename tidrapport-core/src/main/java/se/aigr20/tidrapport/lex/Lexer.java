package se.aigr20.tidrapport.lex;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class Lexer implements AutoCloseable, Iterable<Token> {
  private static final Set<String> LEGAL_DAYS = Set.of("måndag",
                                                       "tisdag",
                                                       "onsdag",
                                                       "torsdag",
                                                       "fredag",
                                                       "lördag",
                                                       "söndag");

  private final Reader reader;
  private int column;
  private int line;
  private int lookahead;

  public Lexer(final Reader reader) {
    Objects.requireNonNull(reader);
    this.reader = reader instanceof BufferedReader ? reader : new BufferedReader(reader);
    this.column = 0;
    this.line = 1;
    this.lookahead = -1;

  }

  public TokenStream stream() {
    return new TokenStream(this);
  }

  @Override
  public Iterator<Token> iterator() {
    return new Iterator<>() {
      private Token next;
      private boolean hasEof;

      @Override
      public boolean hasNext() {
        if (next == null && !hasEof) {
          try {
            next = nextToken();
            hasEof = next.type() == Token.Type.EOF;
          } catch (final IOException e) {
            throw new RuntimeException(e);
          }
        }

        return next != null;
      }

      @Override
      public Token next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        final Token result = next;
        next = null;
        return result;
      }
    };
  }

  private Token nextToken() throws IOException {
    final int startLine = line;
    final int startColumn = column + 1;
    final char c;
    try {
      c = advance();
    } catch (final EOFException e) {
      return new Token(Token.Type.EOF, null, startLine, startColumn);
    }

    return switch (c) {
      case ':' -> new Token(Token.Type.COLON, ":", startLine, startColumn);
      case '-' -> new Token(Token.Type.DASH, "-", startLine, startColumn);
      case '\n' -> new Token(Token.Type.NEWLINE, null, startLine, startColumn);
      case ' ', '\t', '\r' -> nextToken();
      default -> {
        if (Character.isDigit(c)) {
          yield lexNumber(c, startLine, startColumn);
        }
        yield lexWord(c, startLine, startColumn);
      }
    };
  }

  private Token lexWord(final char initial,
                        final int startLine,
                        final int startColumn) throws IOException {
    final StringBuilder buffer = new StringBuilder();
    buffer.append(initial);

    while (isWordChar(peek())) {
      final char next = peek();
      if (!isWordChar(next)) {
        break;
      }
      buffer.append(advance());
    }

    final String word = buffer.toString();
    if (isYear(word)) {
      return new Token(Token.Type.YEAR, word, startLine, startColumn);
    }
    if (isWeek(word)) {
      return new Token(Token.Type.WEEK, word, startLine, startColumn);
    }
    if (isDay(word)) {
      return new Token(Token.Type.DAY, word, startLine, startColumn);
    }

    return new Token(Token.Type.IDENTIFIER, word, startLine, startColumn);
  }

  private Token lexNumber(final char initial,
                          final int startLine,
                          final int startColumn) throws IOException {
    final StringBuilder buffer = new StringBuilder();
    buffer.append(initial);

    while (Character.isDigit(peek())) {
      buffer.append(advance());
    }

    return new Token(Token.Type.NUMBER, buffer.toString(), startLine, startColumn);
  }

  private boolean isYear(final String word) {
    final int length = word.length();
    if (length != 5) {
      return false;
    }
    if (Character.toLowerCase(word.charAt(0)) != 'y') {
      return false;
    }

    for (int i = 1; i < length; i++) {
      if (!Character.isDigit(word.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  private boolean isWeek(final String word) {
    final int length = word.length();
    if (length < 2 || length > 3) {
      return false;
    }
    if (Character.toLowerCase(word.charAt(0)) != 'v') {
      return false;
    }

    for (int i = 1; i < length; i++) {
      if (!Character.isDigit(word.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  private boolean isDay(final String word) {
    return LEGAL_DAYS.contains(word.toLowerCase());
  }

  private boolean isWordChar(final char c) {
    return Character.isLetterOrDigit(c) || c == '-';
  }

  private char advance() throws IOException {
    final int read;

    if (lookahead != -1) {
      read = lookahead;
      lookahead = -1;
    } else {
      read = reader.read();
    }

    if (read == -1) {
      throw new EOFException();
    }

    if (((char) read) == '\n') {
      line++;
      column = 0;
      return (char) read;
    }
    column++;
    return (char) read;
  }

  private char peek() throws IOException {
    if (lookahead == -1) {
      lookahead = reader.read();
      if (lookahead == -1) {
        throw new EOFException();
      }
    }

    return (char) lookahead;
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }
}
