package com.github.rundown.lexer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

  public List<Token> lex(String input) {
    ArrayList<Token> tokens = new ArrayList<>();
    int position = 0;
    while (position < input.length()) {
      Token token = findToken(input, position);
      tokens.add(token);
      position += token.value().length();
    }

    tokens.add(new Token(TokenType.EOF, "", position));

    return tokens;
  }

  private Token findToken(String input, int position) {
    List<Token> matches = new ArrayList<>();
    for (TokenType tokenType : TokenType.values()) {
      Pattern pattern = Pattern.compile("(" + tokenType.regexPattern() + ").*");
      Matcher matcher = pattern.matcher(input.substring(position));

      if (matcher.matches()) {
        matches.add(new Token(tokenType, matcher.group(1), position));
      }
    }

    return matches.stream().max(Comparator.comparingInt(t -> t.value().length()))
        .orElseThrow(() -> new RuntimeException("Invalid token at position " + position + " in input: " + input));
  }
}
