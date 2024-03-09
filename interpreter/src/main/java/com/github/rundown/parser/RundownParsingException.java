package com.github.rundown.parser;

import static java.lang.String.format;

import com.github.rundown.lexer.Token;

public class RundownParsingException extends RuntimeException {

    public RundownParsingException(Token token) {
      super(format("parsing failed: Invalid token %s at position %s", token.type(), token.startPosition()));
    }
}
