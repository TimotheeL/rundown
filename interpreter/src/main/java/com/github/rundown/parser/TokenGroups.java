package com.github.rundown.parser;

import static com.github.rundown.lexer.TokenType.BPM;
import static com.github.rundown.lexer.TokenType.COOLDOWN;
import static com.github.rundown.lexer.TokenType.DOWNHILL;
import static com.github.rundown.lexer.TokenType.EASY;
import static com.github.rundown.lexer.TokenType.GAP;
import static com.github.rundown.lexer.TokenType.HARD;
import static com.github.rundown.lexer.TokenType.HILLY;
import static com.github.rundown.lexer.TokenType.HM;
import static com.github.rundown.lexer.TokenType.HOUR;
import static com.github.rundown.lexer.TokenType.KILOMETER;
import static com.github.rundown.lexer.TokenType.KILOMETER_QUALIFIED;
import static com.github.rundown.lexer.TokenType.LT1;
import static com.github.rundown.lexer.TokenType.LT2;
import static com.github.rundown.lexer.TokenType.METER;
import static com.github.rundown.lexer.TokenType.METER_QUALIFIED;
import static com.github.rundown.lexer.TokenType.M;
import static com.github.rundown.lexer.TokenType.MILE_QUALIFIED;
import static com.github.rundown.lexer.TokenType.MINUS;
import static com.github.rundown.lexer.TokenType.MINUTE;
import static com.github.rundown.lexer.TokenType.PROGRESSION_REP;
import static com.github.rundown.lexer.TokenType.PROGRESSION_SET;
import static com.github.rundown.lexer.TokenType.RECOVERY_JOG;
import static com.github.rundown.lexer.TokenType.RECOVERY_STATIC;
import static com.github.rundown.lexer.TokenType.RECOVERY_WALK;
import static com.github.rundown.lexer.TokenType.RPE;
import static com.github.rundown.lexer.TokenType.SECOND;
import static com.github.rundown.lexer.TokenType.SPM;
import static com.github.rundown.lexer.TokenType.STEADY;
import static com.github.rundown.lexer.TokenType.STRIDES;
import static com.github.rundown.lexer.TokenType.TEMPO;
import static com.github.rundown.lexer.TokenType.THRESHOLD;
import static com.github.rundown.lexer.TokenType.TRACK;
import static com.github.rundown.lexer.TokenType.TREADMILL;
import static com.github.rundown.lexer.TokenType.UPHILL;
import static com.github.rundown.lexer.TokenType.VO2_MAX;
import static com.github.rundown.lexer.TokenType.WARM_UP;
import static com.github.rundown.lexer.TokenType.WATTS;
import static com.github.rundown.lexer.TokenType.YARD;
import static com.github.rundown.lexer.TokenType.YARD_QUALIFIED;
import static com.github.rundown.lexer.TokenType.ZONE;

import com.github.rundown.lexer.TokenType;
import java.util.Set;

public interface TokenGroups {
  Set<TokenType> DISTANCE_UNITS = Set.of(METER, M, KILOMETER, YARD);
  Set<TokenType> DISTANCE_UNITS_QUALIFIED = Set.of(METER_QUALIFIED,
      MILE_QUALIFIED, KILOMETER_QUALIFIED, YARD_QUALIFIED);
  Set<TokenType> KEYWORDS = Set.of(COOLDOWN, DOWNHILL, EASY,
      HARD, HILLY, STEADY, STRIDES, TEMPO, THRESHOLD, TRACK, TREADMILL, UPHILL, WARM_UP);
  Set<TokenType> PREFIXED_TARGETS = Set.of(GAP, RPE, ZONE);
  Set<TokenType> RECOVERIES = Set.of(RECOVERY_JOG, RECOVERY_STATIC, RECOVERY_WALK);
  Set<TokenType> SUFFIXED_TARGETS = Set.of(BPM, SPM, WATTS);
  Set<TokenType> TARGETS_SINGLE_TOKEN = Set.of(LT1, LT2, TEMPO, VO2_MAX);
  Set<TokenType> TARGETS_SPECIFIC_DISTANCES = Set.of(HM, M);
  Set<TokenType> TIME_UNITS = Set.of(HOUR, MINUTE, SECOND);
  Set<TokenType> TARGET_RANGE_SEPARATORS = Set.of(MINUS, PROGRESSION_REP, PROGRESSION_SET);
}
