package com.github.rundown.parser;

import com.github.rundown.lexer.Token;
import com.github.rundown.parser.distance.DistanceParser;
import com.github.rundown.parser.metadata.MetadataParser;
import com.github.rundown.parser.target.TargetParser;
import com.github.rundown.parser.time.TimeParser;
import java.util.List;

public class ParserFactory {
  public static WorkoutParser createParser(List<Token> tokens) {
    Parser parser = new Parser(tokens);
    TimeParser timeParser = new TimeParser(parser);
    DistanceParser distanceParser = new DistanceParser(parser);
    MetadataParser metadataParser = new MetadataParser(parser);
    TargetParser targetParser = new TargetParser(parser, timeParser, distanceParser);

    return new WorkoutParser(
        parser,
        timeParser,
        distanceParser,
        metadataParser,
        targetParser
    );
  }
}
