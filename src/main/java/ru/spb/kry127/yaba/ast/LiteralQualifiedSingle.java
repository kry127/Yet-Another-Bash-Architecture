package ru.spb.kry127.yaba.ast;

import ru.spb.kry127.yaba.io.Environment;

import java.text.MessageFormat;

/**
 * Класс-выражение, инкапсулирующий одинарные кавычки.
 */
public class LiteralQualifiedSingle extends Literal {

  protected LiteralQualifiedSingle(String contents) {
    super(contents);
  }

  @Override
  public String interpolate(Environment environment) {
    final String interpolated = super.interpolate(environment);
    // not working
    // return MessageFormat.format("'{0}'", interpolated);
    return "'" + interpolated + "'";
  }
}
