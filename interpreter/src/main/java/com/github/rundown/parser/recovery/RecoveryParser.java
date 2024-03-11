package com.github.rundown.parser.recovery;

import static com.github.rundown.lexer.TokenType.COMMA;
import static com.github.rundown.lexer.TokenType.EQUAL;
import static com.github.rundown.lexer.TokenType.PARENTHESIS_LEFT;
import static com.github.rundown.lexer.TokenType.PARENTHESIS_RIGHT;
import static com.github.rundown.lexer.TokenType.WHITE_SPACE;
import static com.github.rundown.parser.TokenGroups.RECOVERIES;

import com.github.rundown.parser.Expression.Action;
import com.github.rundown.parser.Expression.Metadata;
import com.github.rundown.parser.Expression.Recovery;
import com.github.rundown.parser.Expression.Section;
import com.github.rundown.parser.Expression.Target;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.RundownParsingException;
import com.github.rundown.parser.action.ActionParser;
import com.github.rundown.parser.metadata.MetadataParser;
import com.github.rundown.parser.target.TargetParser;

public class RecoveryParser {

  private final Parser parser;
  private final ActionParser actionParser;
  private final MetadataParser metadataParser;
  private final TargetParser targetParser;

  public RecoveryParser(
      Parser parser,
      ActionParser actionParser,
      MetadataParser metadataParser,
      TargetParser targetParser
  ) {
    this.parser = parser;
    this.actionParser = actionParser;
    this.metadataParser = metadataParser;
    this.targetParser = targetParser;
  }

  public Recovery recovery() {
    if (parser.match(RECOVERIES)) {
      RecoveryType type = RecoveryType.fromTokenType(parser.previous().type());
      if (parser.match(EQUAL)) {
        Action timeOrDistance = actionParser.timeOrDistance();
        if (timeOrDistance != null) {
          return new Recovery(type, new Section(timeOrDistance, null, null, null));
        }
        if (parser.match(PARENTHESIS_LEFT)) {
          Recovery recovery = new Recovery(type, recoverySection());
          if (!parser.match(PARENTHESIS_RIGHT)) {
            throw new RundownParsingException(parser.peek());
          }
          return recovery;
        }
      }
      throw new RundownParsingException(parser.peek());
    }

    return null;
  }

  private Section recoverySection() {
    Action action = actionParser.action();
    parser.match(WHITE_SPACE);
    parser.match(COMMA);
    parser.match(WHITE_SPACE);
    Metadata metadata = metadataParser.metadata();
    parser.match(WHITE_SPACE);
    parser.match(COMMA);
    parser.match(WHITE_SPACE);
    Target target = targetParser.target();
    return new Section(action, metadata, target, null);
  }
}
