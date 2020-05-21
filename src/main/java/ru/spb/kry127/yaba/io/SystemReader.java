package ru.spb.kry127.yaba.io;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Класс-обёртка для абстракции системного потока ввода вывода.
 * Благо, Java сама поддерживает данную абстракцию, поэтому её
 * реализация для консольного приложения очевидна, однако,
 * могут понадобится случаи перекофигурации потока вывода и
 * потока ошибок.
 */
public interface SystemReader {
  /**
   * Получает очередную строку из потока ввода.
   *
   * @return строка, введённая пользователем
   */
  String getLine() throws IOException;

  /**
   * @return стандартный поток ввода.
   */
  InputStreamProxy getInStream();

  /**
   * @return стандартный поток вывода.
   */
  PrintStream getOutStream();

  /**
   * @return стандартный поток ошибок.
   */
  PrintStream getErrStream();
}
