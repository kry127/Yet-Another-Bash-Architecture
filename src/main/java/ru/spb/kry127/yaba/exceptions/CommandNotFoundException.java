package ru.spb.kry127.yaba.exceptions;

import java.text.MessageFormat;

/**
 * Класс ошибки, которая возникает при обнаружении несуществующей команды.
 */
public class CommandNotFoundException extends Exception {

  private final static String OUTPUT_PATTERN = "Cannot find command {0} :(";

  /**
   * У этой ошибки, скорее всего, не может быть причины.
   * Можно добавить второй конструктор, принимающий Throwable
   * по мере надобности
   *
   * @param expr строка, которую следует подставить в шаблон
   */
  public CommandNotFoundException(String expr) {
    super(MessageFormat.format(OUTPUT_PATTERN, expr));
  }

  public CommandNotFoundException(String expr, Throwable cause) {
    super(MessageFormat.format(OUTPUT_PATTERN, expr), cause);
  }
}
