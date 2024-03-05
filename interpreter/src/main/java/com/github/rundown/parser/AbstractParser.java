package com.github.rundown.parser;

import static com.github.rundown.lexer.token.TokenType.EOF;

import com.github.rundown.lexer.token.Token;
import com.github.rundown.lexer.token.TokenType;
import java.util.List;
import java.util.Set;

public abstract class AbstractParser<T> {

  private int current;
  protected List<Token> tokens;

  public AbstractParser(List<Token> tokens) {
    this.tokens = tokens;
    this.current = 0;
  }

  public abstract T parse();

  protected boolean match(TokenType... types) {
    return match(Set.of(types));
  }

  protected boolean match(Set<TokenType> types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  protected boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return peek().type() == type;
  }

  protected boolean isAtEnd() {
    return peek().type() == EOF;
  }

  protected Token peek() {
    return tokens.get(current);
  }

  protected Token advance() {
    if (!isAtEnd()) {
      current++;
    }

    return previous();
  }

  protected Token backtrack() {
    Token currentToken = peek();
    if (current > 0) {
      current--;
    }

    return currentToken;
  }

  protected Token previous() {
    return tokens.get(current - 1);
  }
}
