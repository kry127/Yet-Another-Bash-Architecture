package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.io.Environment;

/**
 * Класс-выражение, инкапсулирующий одинарные кавычки.
 */
public class LiteralQualifiedSingle extends Literal {

  protected LiteralQualifiedSingle(String contents) {
    super(contents);
  }

  @Override
  public String interpolate(@NotNull Environment environment) {
    // not working
    // return MessageFormat.format("'{0}'", interpolated);
    return "'" + super.getRawContents() + "'";
  }
}
