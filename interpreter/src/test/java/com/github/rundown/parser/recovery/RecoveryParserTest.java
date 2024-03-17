package com.github.rundown.parser.recovery;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.rundown.lexer.Lexer;
import com.github.rundown.lexer.Token;
import com.github.rundown.parser.Expression.Pace;
import com.github.rundown.parser.Expression.Recovery;
import com.github.rundown.parser.Expression.RecoveryCycle;
import com.github.rundown.parser.Expression.RecoverySection;
import com.github.rundown.parser.Expression.TargetValue;
import com.github.rundown.parser.Expression.Time;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.action.ActionParser;
import com.github.rundown.parser.distance.DistanceParser;
import com.github.rundown.parser.distance.DistanceUnit;
import com.github.rundown.parser.metadata.MetadataParser;
import com.github.rundown.parser.target.TargetParser;
import com.github.rundown.parser.target.TargetValueType;
import com.github.rundown.parser.time.TimeParser;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class RecoveryParserTest {

  private final Lexer lexer = new Lexer();

  private RecoveryParser underTest;

  private void setUpParser(String input) {
    List<Token> tokens = lexer.lex(input);
    Parser parser = new Parser(tokens);
    TimeParser timeParser = new TimeParser(parser);
    DistanceParser distanceParser = new DistanceParser(parser);
    ActionParser actionParser = new ActionParser(parser, timeParser, distanceParser);
    MetadataParser metadataParser = new MetadataParser(parser);
    TargetParser targetParser = new TargetParser(parser, timeParser, distanceParser);
    underTest = new RecoveryParser(parser, actionParser, metadataParser, targetParser);
  }

  @ParameterizedTest
  @MethodSource("recoveries")
  void canParseRecovery(String input, Recovery expected) {
    // given
    setUpParser(input);

    // when
    Recovery recovery = underTest.recovery();

    // then
    assertThat(recovery).usingRecursiveComparison().isEqualTo(expected);
  }

  private static Set<Arguments> recoveries() {
    return Set.of(
        Arguments.of(
            "R=3:00", new Recovery(RecoveryType.JOG, new RecoverySection(new Time(0, 3, 0), null, null)),
            "S=1mn", new Recovery(RecoveryType.STATIC, new RecoverySection(new Time(0, 1, 0), null, null)),
            "CR=1mn @5:30/km", new RecoveryCycle(RecoveryType.STATIC, new RecoverySection(new Time(0, 1, 0), null,
                new TargetValue(TargetValueType.PACE, new Pace(new Time(0, 5, 30), DistanceUnit.KILOMETER))
            ))
        )
    );
  }
}
