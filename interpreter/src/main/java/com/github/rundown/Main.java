package com.github.rundown;

import com.github.rundown.lexer.Lexer;
import com.github.rundown.lexer.token.Token;
import com.github.rundown.parser.Expression.Workout;
import com.github.rundown.parser.WorkoutParser;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    String input = "3 x 9km, R=5mn";
    Lexer lexer = new Lexer();
    List<Token> tokens = lexer.lex(input);
    WorkoutParser parser = new WorkoutParser(tokens);
    Workout workout = parser.parse();
    System.out.println(workout);
  }
}