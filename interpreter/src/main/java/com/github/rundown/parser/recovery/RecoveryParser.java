package com.github.rundown.parser.recovery;

import static com.github.rundown.lexer.TokenType.COMMA;
import static com.github.rundown.lexer.TokenType.CYCLE;
import static com.github.rundown.lexer.TokenType.EQUAL;
import static com.github.rundown.lexer.TokenType.PARENTHESIS_LEFT;
import static com.github.rundown.lexer.TokenType.PARENTHESIS_RIGHT;
import static com.github.rundown.lexer.TokenType.WHITE_SPACE;
import static com.github.rundown.parser.TokenGroups.RECOVERIES;
import static com.github.rundown.parser.recovery.RecoveryType.STATIC;
import static com.github.rundown.parser.recovery.RecoveryType.WALK;

import com.github.rundown.parser.Expression.Distance;
import com.github.rundown.parser.Expression.Metadata;
import com.github.rundown.parser.Expression.Recovery;
import com.github.rundown.parser.Expression.RecoveryCycle;
import com.github.rundown.parser.Expression.RecoverySection;
import com.github.rundown.parser.Expression.Rep;
import com.github.rundown.parser.Expression.Target;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.RundownSemanticException;
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
    boolean isCycle = parser.match(CYCLE);
    Recovery recovery = recoverySimple();
    if (recovery == null) {
      return null;
    }

    validateRecovery(recovery);

    if (isCycle) {
      return new RecoveryCycle(recovery.type, recovery.section);
    }
    return recovery;
  }

  public Recovery recoverySimple() {
    if (!parser.match(RECOVERIES)) {
      return null;
    }
    RecoveryType type = RecoveryType.fromTokenType(parser.previous().type());
    parser.matchOrThrow(EQUAL);
    Rep timeOrDistance = actionParser.timeOrDistance();
    if (timeOrDistance != null) {
      return new Recovery(type, new RecoverySection(timeOrDistance, null, null));
    }
    parser.matchOrThrow(PARENTHESIS_LEFT);
    Recovery recovery = new Recovery(type, recoverySection());
    parser.matchOrThrow(PARENTHESIS_RIGHT);
    return recovery;
  }

  private RecoverySection recoverySection() {
    Rep rep = actionParser.timeOrDistance();
    parser.match(WHITE_SPACE);
    parser.match(COMMA);
    parser.match(WHITE_SPACE);
    Metadata metadata = metadataParser.metadata();
    parser.match(WHITE_SPACE);
    parser.match(COMMA);
    parser.match(WHITE_SPACE);
    Target target = targetParser.target();
    return new RecoverySection(rep, metadata, target);
  }

  private void validateRecovery(Recovery recovery) {
    if ((recovery.type == WALK || recovery.type == STATIC) && recovery.section.target != null) {
      throw new RundownSemanticException("A target can not be specified for walk recoveries");
    }

    if (recovery.type == STATIC && recovery.section.rep instanceof Distance) {
      throw new RundownSemanticException("A distance can not be specified as the recovery action for static recoveries");
    }
  }
}
