package ru.spb.kry127.yaba.exceptions;

/**
 * Класс ошибки, который призван обозначить ошибку парсинга входной строки.
 * Может быть полезен для вывода места ошибки парсинга.
 */
public class SyntaxException extends Exception {
  public SyntaxException(String errorMsg) {
    super(errorMsg);
  }
}
