package ru.spb.kry127.yaba.io;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Реализация протокола доступа к системному потоку ввода вывода.
 * Является синглтоном
 */
public class SystemReaderProvider implements SystemReader {

  private static SystemReader singleton;
  private final Scanner sc;

  private SystemReaderProvider() {
    sc = new Scanner(System.in);
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
  public String getLine() {
    return sc.nextLine();
  }


  /**
   * Возвращаем проксированный поток ввода-вывода с целью того,
   * чтобы избегать непосредственного вызова close() на стандартный поток вывода
   *
   * @return
   */
  @Override
  public InputStream getInStream() {
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
