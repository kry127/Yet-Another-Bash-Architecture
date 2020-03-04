package ru.spb.kry127.yaba.exceptions;

/**
 * Класс ошибки, который призван обозначить ошибку парсинга входной строки.
 * Может быть полезен для вывода места ошибки парсинга.
 */
public class SyntaxException extends Exception {
  /**
   * Для целей проекта достаточно пока что одного конструктора от строки.
   *
   * @param errorMsg Сообщение об ошибке
   */
  public SyntaxException(String errorMsg) {
    super(errorMsg);
  }
}
