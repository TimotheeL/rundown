package com.github.rundown.parser;

import static com.github.rundown.lexer.TokenType.EOF;

import com.github.rundown.lexer.Token;
import com.github.rundown.lexer.TokenType;
import java.util.List;
import java.util.Set;

public class Parser {

  private int current;
  protected List<Token> tokens;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
    this.current = 0;
  }

  public boolean match(TokenType... types) {
    return match(Set.of(types));
  }

  public boolean match(Set<TokenType> types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  public Token matchOrThrow(TokenType tokenType) {
    if (!match(tokenType)) {
      throw new RundownParsingException(peek());
    }
    return previous();
  }

  public Token matchOrThrow(Set<TokenType> tokenTypes) {
    if (!match(tokenTypes)) {
      throw new RundownParsingException(peek());
    }
    return previous();
  }

  public boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return peek().type() == type;
  }

  public boolean isAtEnd() {
    return peek().type() == EOF;
  }

  public Token peek() {
    return tokens.get(current);
  }

  public void advance() {
    if (!isAtEnd()) {
      current++;
    }
  }

  public Token previous() {
    return tokens.get(current - 1);
  }

  public int getCurrent() {
    return current;
  }

  public void setCurrent(int current) {
    this.current = current;
  }
}
