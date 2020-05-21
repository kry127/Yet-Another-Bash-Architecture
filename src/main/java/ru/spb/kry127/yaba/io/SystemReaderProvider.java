package ru.spb.kry127.yaba.io;

import java.io.*;

/**
 * Реализация протокола доступа к системному потоку ввода вывода.
 * Является синглтоном
 */
public class SystemReaderProvider implements SystemReader {

  private static SystemReader singleton;
  private final BufferedReader bufferedReader;

  private SystemReaderProvider() {
    bufferedReader = new BufferedReader(new InputStreamReader(System.in));
  }

  /**
   * Поставляет экземпляр класса, который реализует интерфейс
   * <tt>SystemReader</tt> наиболее стандартным образом --
   * предоставляет stdin, stdout и stderr, предоставляемые
   * виртуальной машиной Java
   *
   * @return объект-синглтон класса <tt>SystemReaderProvider</tt>
   */
  public static SystemReader getSystemReader() {
    if (singleton == null) {
      singleton = new SystemReaderProvider();
    }
    return singleton;
  }

  @Override
  public String getLine() throws IOException {
    return bufferedReader.readLine();
  }


  /**
   * Возвращаем проксированный поток ввода-вывода с целью того,
   * чтобы избегать непосредственного вызова close() на стандартный поток вывода
   *
   * @return
   */
  @Override
  public InputStreamProxy getInStream() {
    return new InputStreamProxy(System.in);
  }

  @Override
  public PrintStream getOutStream() {
    return System.out;
  }

  @Override
  public PrintStream getErrStream() {
    return System.err;
  }
}
