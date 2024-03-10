package com.github.rundown.parser;

import static com.github.rundown.lexer.TokenType.BPM;
import static com.github.rundown.lexer.TokenType.GAP;
import static com.github.rundown.lexer.TokenType.HMP;
import static com.github.rundown.lexer.TokenType.HOUR;
import static com.github.rundown.lexer.TokenType.KILOMETER;
import static com.github.rundown.lexer.TokenType.KILOMETER_QUALIFIED;
import static com.github.rundown.lexer.TokenType.LT1;
import static com.github.rundown.lexer.TokenType.LT2;
import static com.github.rundown.lexer.TokenType.METER;
import static com.github.rundown.lexer.TokenType.METER_QUALIFIED;
import static com.github.rundown.lexer.TokenType.MILE;
import static com.github.rundown.lexer.TokenType.MILE_QUALIFIED;
import static com.github.rundown.lexer.TokenType.MINUS;
import static com.github.rundown.lexer.TokenType.MINUTE;
import static com.github.rundown.lexer.TokenType.MP;
import static com.github.rundown.lexer.TokenType.PROGRESSION_REP;
import static com.github.rundown.lexer.TokenType.PROGRESSION_SET;
import static com.github.rundown.lexer.TokenType.RPE;
import static com.github.rundown.lexer.TokenType.SECOND;
import static com.github.rundown.lexer.TokenType.SPM;
import static com.github.rundown.lexer.TokenType.TEMPO;
import static com.github.rundown.lexer.TokenType.VO2_MAX;
import static com.github.rundown.lexer.TokenType.WATTS;
import static com.github.rundown.lexer.TokenType.YARD;
import static com.github.rundown.lexer.TokenType.YARD_QUALIFIED;
import static com.github.rundown.lexer.TokenType.ZONE;

import com.github.rundown.lexer.TokenType;
import java.util.Set;

public interface TokenGroups {
  Set<TokenType> DISTANCE_UNITS = Set.of(METER, MILE, KILOMETER, YARD);
  Set<TokenType> DISTANCE_UNITS_QUALIFIED = Set.of(METER_QUALIFIED,
      MILE_QUALIFIED, KILOMETER_QUALIFIED, YARD_QUALIFIED);
  Set<TokenType> POSTFIXED_TARGET_TOKENS = Set.of(BPM, SPM, WATTS);
  Set<TokenType> PREFIXED_TARGET_TOKENS = Set.of(GAP, RPE, ZONE);
  Set<TokenType> TARGET_TOKENS = Set.of(HMP, LT1, LT2, MP, TEMPO, VO2_MAX);
  Set<TokenType> TIME_UNITS = Set.of(HOUR, MINUTE, SECOND);
  Set<TokenType> TARGET_RANGE_SEPARATORS = Set.of(MINUS, PROGRESSION_REP, PROGRESSION_SET);
}
