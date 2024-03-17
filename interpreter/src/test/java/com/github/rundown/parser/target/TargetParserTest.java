package com.github.rundown.parser.target;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.rundown.lexer.Lexer;
import com.github.rundown.lexer.Token;
import com.github.rundown.parser.Expression.Distance;
import com.github.rundown.parser.Expression.IntegerValue;
import com.github.rundown.parser.Expression.Pace;
import com.github.rundown.parser.Expression.Speed;
import com.github.rundown.parser.Expression.Target;
import com.github.rundown.parser.Expression.TargetFixed;
import com.github.rundown.parser.Expression.TargetRange;
import com.github.rundown.parser.Expression.TargetValue;
import com.github.rundown.parser.Expression.Time;
import com.github.rundown.parser.Parser;
import com.github.rundown.parser.RundownParsingException;
import com.github.rundown.parser.distance.DistanceParser;
import com.github.rundown.parser.distance.DistanceUnit;
import com.github.rundown.parser.time.TimeParser;
import com.github.rundown.parser.time.TimeUnit;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class TargetParserTest {

  private final Lexer lexer = new Lexer();

  private TargetParser underTest;

  private void setUpParser(String input) {
    List<Token> tokens = lexer.lex(input);
    Parser parser = new Parser(tokens);
    TimeParser timeParser = new TimeParser(parser);
    DistanceParser distanceParser = new DistanceParser(parser);
    underTest = new TargetParser(parser, timeParser, distanceParser);
  }

  @ParameterizedTest
  @MethodSource("targetValues")
  void canParseSingleTargetValues(String input, TargetValue expected) {
    // given
    setUpParser(input);

    // when
    Target target = underTest.target();

    // then
    assertThat(target).usingRecursiveComparison().isEqualTo(expected);
  }

  private static Set<Arguments> targetValues() {
    return Set.of(
        Arguments.of("@4:40/km", new TargetValue(TargetValueType.PACE, new Pace(new Time(0, 4, 40), DistanceUnit.KILOMETER))),
        Arguments.of("@6mn/M", new TargetValue(TargetValueType.PACE, new Pace(new Time(0, 6, 0), DistanceUnit.MILE))),
        Arguments.of("@gap5mn10s/km", new TargetValue(TargetValueType.GAP, new Pace(new Time(0, 5, 10), DistanceUnit.KILOMETER))),
        Arguments.of("@gap10mn/M", new TargetValue(TargetValueType.GAP, new Pace(new Time(0, 10, 0), DistanceUnit.MILE))),
        Arguments.of("@18km/h", new TargetValue(TargetValueType.SPEED, new Speed(new Distance(18.0, DistanceUnit.KILOMETER), TimeUnit.HOUR))),
        Arguments.of("@400m/mn", new TargetValue(TargetValueType.SPEED, new Speed(new Distance(400.0, DistanceUnit.METER), TimeUnit.MINUTE))),
        Arguments.of("@10kP", new TargetValue(TargetValueType.RACE_PACE, new Distance(10.0, DistanceUnit.KILOMETER))),
        Arguments.of("@13.1MP", new TargetValue(TargetValueType.RACE_PACE, new Distance(13.1, DistanceUnit.MILE))),
        Arguments.of("@rpe10", new TargetValue(TargetValueType.RPE, new IntegerValue(10))),
        Arguments.of("@180spm", new TargetValue(TargetValueType.STEPS, new IntegerValue(180))),
        Arguments.of("@160bpm", new TargetValue(TargetValueType.HEARTRATE, new IntegerValue(160))),
        Arguments.of("@Z5", new TargetValue(TargetValueType.ZONE, new IntegerValue(5))),
        Arguments.of("@VO2max", new TargetValue(TargetValueType.FIXED, new TargetFixed(TargetFixedType.VO2_MAX))),
        Arguments.of("@tempo", new TargetValue(TargetValueType.FIXED, new TargetFixed(TargetFixedType.TEMPO))),
        Arguments.of("@LT1", new TargetValue(TargetValueType.FIXED, new TargetFixed(TargetFixedType.LT1))),
        Arguments.of("@LT2", new TargetValue(TargetValueType.FIXED, new TargetFixed(TargetFixedType.LT2))),
        Arguments.of("@MP", new TargetValue(TargetValueType.RACE_PACE, new TargetFixed(TargetFixedType.M))),
        Arguments.of("@HMP", new TargetValue(TargetValueType.RACE_PACE, new TargetFixed(TargetFixedType.HM)))
    );
  }

  @ParameterizedTest
  @MethodSource("targetTimes")
  void canParseTimeTarget(String input, Time expectedTime) {
    // given
    setUpParser(input);

    // when
    Target target = underTest.target();

    // then
    assertThat(target).usingRecursiveComparison().isEqualTo(new TargetValue(TargetValueType.TIME, expectedTime));
  }

  private static Set<Arguments> targetTimes() {
    return Set.of(
        Arguments.of("@55s", new Time(0, 0, 55)),
        Arguments.of("@1mn10", new Time(0, 1, 10)),
        Arguments.of("@4:00", new Time(0, 4, 0)),
        Arguments.of("@2h", new Time(2, 0, 0))
    );
  }

  @ParameterizedTest
  @MethodSource("targetRanges")
  void canParseTargetRanges(String input, TargetRange expected) {
    // given
    setUpParser(input);

    // when
    Target target = underTest.target();

    // then
    assertThat(target).usingRecursiveComparison().isEqualTo(expected);
  }

  private static Set<Arguments> targetRanges() {
    return Set.of(
        Arguments.of(
            "@4:40/km-4:50/km", new TargetRange(
                new TargetValue(TargetValueType.PACE, new Pace(new Time(0, 4, 40), DistanceUnit.KILOMETER)),
                new TargetValue(TargetValueType.PACE, new Pace(new Time(0, 4, 50), DistanceUnit.KILOMETER)),
                TargetRangeType.RANGE
            )
        ),
        Arguments.of(
            "@Z5>VO2max", new TargetRange(
                new TargetValue(TargetValueType.ZONE, new IntegerValue(5)),
                new TargetValue(TargetValueType.FIXED, new TargetFixed(TargetFixedType.VO2_MAX)),
                TargetRangeType.PROGRESSION_REP
            )
        ),
        Arguments.of(
            "@130bpm>>150bpm", new TargetRange(
                new TargetValue(TargetValueType.HEARTRATE, new IntegerValue(130)),
                new TargetValue(TargetValueType.HEARTRATE, new IntegerValue(150)),
                TargetRangeType.PROGRESSION_SET
            )
        ),
        Arguments.of(
            "@Z(3-4)", new TargetRange(
                new TargetValue(TargetValueType.ZONE, new IntegerValue(3)),
                new TargetValue(TargetValueType.ZONE, new IntegerValue(4)),
                TargetRangeType.RANGE
            )
        ),
        Arguments.of(
            "@rpe(7>9)", new TargetRange(
                new TargetValue(TargetValueType.RPE, new IntegerValue(7)),
                new TargetValue(TargetValueType.RPE, new IntegerValue(9)),
                TargetRangeType.PROGRESSION_REP
            )
        ),
        Arguments.of(
            "@gap(4:40-4:20)/km", new TargetRange(
                new TargetValue(TargetValueType.GAP, new Pace(new Time(0, 4, 40), DistanceUnit.KILOMETER)),
                new TargetValue(TargetValueType.GAP, new Pace(new Time(0, 4, 20), DistanceUnit.KILOMETER)),
                TargetRangeType.RANGE
            )
        ),
        Arguments.of(
            "@(4mn40>4mn20)/km", new TargetRange(
                new TargetValue(TargetValueType.PACE, new Pace(new Time(0, 4, 40), DistanceUnit.KILOMETER)),
                new TargetValue(TargetValueType.PACE, new Pace(new Time(0, 4, 20), DistanceUnit.KILOMETER)),
                TargetRangeType.PROGRESSION_REP
            )
        ),
        Arguments.of(
            "@(150-155)bpm", new TargetRange(
                new TargetValue(TargetValueType.HEARTRATE, new IntegerValue(150)),
                new TargetValue(TargetValueType.HEARTRATE, new IntegerValue(155)),
                TargetRangeType.RANGE
            )
        ),
        Arguments.of(
            "@(55-60)s", new TargetRange(
                new TargetValue(TargetValueType.TIME, new Time(0, 0, 55)),
                new TargetValue(TargetValueType.TIME, new Time(0, 0, 60)),
                TargetRangeType.RANGE
            )
        ),
        Arguments.of(
            "@(12.5>>14)km/h", new TargetRange(
                new TargetValue(TargetValueType.SPEED, new Speed(new Distance(12.5, DistanceUnit.KILOMETER), TimeUnit.HOUR)),
                new TargetValue(TargetValueType.SPEED, new Speed(new Distance(14.0, DistanceUnit.KILOMETER), TimeUnit.HOUR)),
                TargetRangeType.PROGRESSION_SET
            )
        )
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"@5km", "@10M", "@M", "@200m", "@ 4:40/km", "@bpm150", "@40s/200m"})
  void throwsOnInvalidTargets(String input) {
    // given
    setUpParser(input);

    // when
    // then
    assertThatThrownBy(() -> underTest.target())
        .isInstanceOf(RundownParsingException.class);
  }
}