package se.aigr20.tidrapport.lex;

import java.util.Iterator;

import se.aigr20.tidrapport.parse.ParseException;

public class TokenStream {
  private final Iterator<Token> iterator;
  private Token lookahead;

  public TokenStream(final Lexer lexer) {
    this.iterator = lexer.iterator();
    this.lookahead = null;
  }

  public Token peek() {
    if (lookahead == null && iterator.hasNext()) {
      lookahead = iterator.next();
    }
    return lookahead;
  }

  public Token consume() {
    final Token result = peek();
    lookahead = null;
    return result;
  }

  public boolean match(final Token.Type type) {
    return peek().type() == type;
  }

  public Token expect(final Token.Type type) throws ParseException {
    final Token token = consume();
    if (token.type() != type) {
      throw new ParseException("Expected token of type " +
                               type +
                               " but found " +
                               token.type() +
                               " at " +
                               token.line() +
                               ":" +
                               token.column());
    }

    return token;
  }
}
