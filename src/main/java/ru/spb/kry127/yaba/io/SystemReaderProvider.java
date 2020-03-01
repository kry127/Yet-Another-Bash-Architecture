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


  @Override
  public InputStream getInStream() {
    return System.in;
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
