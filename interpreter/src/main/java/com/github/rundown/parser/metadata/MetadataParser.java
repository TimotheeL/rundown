package com.github.rundown.parser.metadata;

import static com.github.rundown.lexer.TokenType.MULTIPLIER;
import static com.github.rundown.lexer.TokenType.NUMBER;
import static com.github.rundown.lexer.TokenType.WHITE_SPACE;
import static com.github.rundown.parser.TokenGroups.KEYWORDS;

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
    if (!parser.match(KEYWORDS)) {
      return null;
    }

    List<Token> keywords = new ArrayList<>();
    keywords.add(parser.previous());

    while (parser.match(WHITE_SPACE) && parser.match(KEYWORDS)) {
      keywords.add(parser.previous());
    }

    if (parser.previous().type() == WHITE_SPACE) {
      parser.setCurrent(parser.getCurrent() - 1);
    }

    return new Metadata(keywords);
  }

  private MetadataMultiple metadataMultiple() {
    int current = parser.getCurrent();

    if (!parser.match(NUMBER)) {
      return null;
    }

    int multiplier = Integer.parseInt(parser.previous().value());
    parser.match(WHITE_SPACE);
    if (!parser.match(MULTIPLIER)) {
      parser.setCurrent(current);
      return null;
    }
    parser.match(WHITE_SPACE);
    Metadata metadata = metadata();
    if (metadata == null) {
      parser.setCurrent(current);
      return null;
    }
    return new MetadataMultiple(multiplier, metadata.keywords);
  }
}
