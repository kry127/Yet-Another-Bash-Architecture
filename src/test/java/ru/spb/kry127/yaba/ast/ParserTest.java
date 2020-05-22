package ru.spb.kry127.yaba.ast;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.spb.kry127.yaba.exceptions.SyntaxException;
import ru.spb.kry127.yaba.io.Environment;
import ru.spb.kry127.yaba.io.EnvironmentProvider;

import static org.junit.Assert.*;

public class ParserTest {

  static Parser parser;
  static Environment env;

  @BeforeClass
  public static void setUp() {
    env = EnvironmentProvider.getEnvironment();
    parser = new ParserLL(env);
  }

  @Test
  public void parseExpression_literal() throws SyntaxException {
    Expression result = parser.parseExpression("abc");
    Command lit = (Command) result; // cast should be OK
  }


  @Test
  public void parseExpression_assignmentSimple() throws SyntaxException {
    String s = "wxy=918847";
    Expression result = parser.parseExpression(s);
    Assignment ass = (Assignment) result; // cast should be OK
    assertEquals(s, ass.interpolate(env));
  }

  @Test
  public void parseExpression_assignmentInterpolation() throws SyntaxException {
    String s = "wxy=$wow";
    Expression result = parser.parseExpression(s);
    Assignment ass = (Assignment) result; // cast should be OK
    env.setEnvVariable("wow", "LUL");
    assertEquals("wxy=LUL", ass.interpolate(env));
  }

  @Test
  public void parseExpression_assignmentComplicatedInterpolation() throws SyntaxException {
    String s = "zz=hy\"$wow\"'$ob'one\"$wow\"nobi";
    Expression result = parser.parseExpression(s);
    Assignment ass = (Assignment) result; // cast should be OK
    env.setEnvVariable("wow", "KEK");
    assertEquals("zz=hy\"KEK\"'$ob'one\"KEK\"nobi", ass.interpolate(env));
  }
}