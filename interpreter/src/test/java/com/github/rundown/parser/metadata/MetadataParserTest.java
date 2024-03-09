package com.github.rundown.parser.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.rundown.lexer.Lexer;
import com.github.rundown.lexer.Token;
import com.github.rundown.parser.Expression.Metadata;
import com.github.rundown.parser.Expression.MetadataMultiple;
import com.github.rundown.parser.Parser;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MetadataParserTest {

  private final Lexer lexer = new Lexer();
  private MetadataParser underTest;

  private void setUpParser(String input) {
    List<Token> tokens = lexer.lex(input);
    underTest = new MetadataParser(new Parser(tokens));
  }

  @Test
  void canParseMetadata() {
    // given
    setUpParser("downhill");

    // when
    Metadata metadata = underTest.metadata();

    // then
    assertThat(metadata.keywords).extracting("value").containsExactly("downhill");
  }

  @Test
  void canParseMetadata_multipleKeywords() {
    // given
    setUpParser("easy downhill strides");

    // when
    Metadata metadata = underTest.metadata();

    // then
    assertThat(metadata.keywords).extracting("value").containsExactly("easy", "downhill", "strides");
  }

  @Test
  void canParseMainMetadata() {
    // given
    setUpParser("strides");

    // when
    Metadata metadata = underTest.mainMetadata();

    // then
    assertThat(metadata.keywords).extracting("value").containsExactly("strides");
  }

  @Test
  void canParseMainMetadata_multiple() {
    // given
    setUpParser("6 x strides");

    // when
    Metadata metadata = underTest.mainMetadata();

    // then
    assertThat(metadata).isInstanceOf(MetadataMultiple.class);
    assertThat(((MetadataMultiple) metadata).multiplier).isEqualTo(6);
    assertThat(metadata.keywords).extracting("value").containsExactly("strides");
  }

  @Test
  void canParseMainMetadata_multipleKeywords() {
    // given
    setUpParser("6 x easy downhill strides");

    // when
    Metadata metadata = underTest.mainMetadata();

    // then
    assertThat(metadata).isInstanceOf(MetadataMultiple.class);
    assertThat(((MetadataMultiple) metadata).multiplier).isEqualTo(6);
    assertThat(metadata.keywords).extracting("value").containsExactly("easy", "downhill", "strides");
  }
}
