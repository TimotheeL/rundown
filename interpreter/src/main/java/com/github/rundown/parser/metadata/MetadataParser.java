package com.github.rundown.parser.metadata;

import static com.github.rundown.lexer.TokenType.KEYWORD;
import static com.github.rundown.lexer.TokenType.MULTIPLIER;
import static com.github.rundown.lexer.TokenType.NUMBER;
import static com.github.rundown.lexer.TokenType.WHITE_SPACE;

import com.github.rundown.lexer.Token;
import com.github.rundown.parser.Expression.Metadata;
import com.github.rundown.parser.Expression.MetadataMultiple;
import com.github.rundown.parser.Parser;
import java.util.ArrayList;
import java.util.List;

public class MetadataParser {

  private final Parser parser;

  public MetadataParser(Parser parser) {
    this.parser = parser;
  }

  public Metadata mainMetadata() {
    MetadataMultiple metadataMultiple = metadataMultiple();
    if (metadataMultiple != null) {
      return metadataMultiple;
    }
    return metadata();
  }

  public Metadata metadata() {
    List<Token> keywords = new ArrayList<>();
    while (parser.match(KEYWORD)) {
      keywords.add(parser.previous());
      parser.match(WHITE_SPACE);
    }
    if (keywords.isEmpty()) {
      return null;
    }
    return new Metadata(keywords);
  }

  private MetadataMultiple metadataMultiple() {
    int current = parser.getCurrent();
    if (parser.match(NUMBER)) {
      int multiplier = Integer.parseInt(parser.previous().value());
      parser.match(WHITE_SPACE);
      if (parser.match(MULTIPLIER)) {
        parser.match(WHITE_SPACE);
        Metadata metadata = metadata();
        if (metadata == null) {
          parser.setCurrent(current);
          return null;
        }
        return new MetadataMultiple(multiplier, metadata.keywords);
      }
    }
    parser.setCurrent(current);
    return null;
  }
}
