package ru.spb.kry127.yaba.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Класс, методы которого предназначенный для работы с файловой системой.
 * Является синглтоном
 */
public class OsUtilsProvider implements OsUtils {

  private final static Logger logger;
  private final static OsUtils singleton;
  private static final int BUFFER_SIZE = 8192;

  static {
    logger = Logger.getLogger(OsUtilsProvider.class.getName());
    singleton = new OsUtilsProvider();
  }

  /**
   * Закрываем конструктор для того, чтобы всегда имелся только один инстанс данного класса
   */
  private OsUtilsProvider() {
  }

  public static OsUtils getUtilsProvider() {
    return singleton;
  }

  /**
   * Производит перенаправление потока ввода в поток вывода
   * в активном режиме.
   *
   * @param in  поток ввода
   * @param out поток вывода
   */
  @Override
  public void redirectIOStreams(@NotNull InputStream in, @NotNull OutputStream out) {
    byte[] buffer = new byte[BUFFER_SIZE];
    int len;
    try {
      while ((len = in.read(buffer)) != -1) {
        out.write(buffer, 0, len);
      }
    } catch (IOException ex) {

    }
  }

  @Override
  public void redirectIOStdStreams(@NotNull OutputStream outStream1,
                                   @NotNull OutputStream outStream2,
                                   @NotNull OutputStream outStream3,
                                   @NotNull InputStream inStream1,
                                   @NotNull InputStream inStream2,
                                   @NotNull InputStream inStream3) {

    byte[] buffer = new byte[BUFFER_SIZE];
    int len;

    try {
      if ((len = inStream2.read(buffer)) != -1) {
        outStream2.write(buffer, 0, len);
      } else {
        return;
      }
    } catch (IOException ex) {
    }
    try {
      if ((len = inStream3.read(buffer)) != -1) {
        outStream3.write(buffer, 0, len);
      } else {
        return;
      }
    } catch (IOException ex) {
    }
    try {
      if ((len = inStream1.read(buffer)) != -1) {
        outStream1.write(buffer, 0, len);
      }
    } catch (IOException ex) {
    }
  }

  /**
   * Проверяет наличие программы в переменной окружения и
   * возвращает путь, если он существует.
   *
   * @param name программа, которую необходимо найти
   * @return Путь к программе, если он есть, иначе возвращается <tt>null</tt>
   * @link https://stackoverflow.com/questions/934191/how-to-check-existence-of-a-program-in-the-path/38073998#38073998
   */
  @Override
  @Nullable
  public Path checkProgramExists(@NotNull String name) throws IOException {

    ProcessBuilder pb = new ProcessBuilder(isWindows() ? "where" : "which", name);
    Path foundProgram = null;
    try {
      Process proc = pb.start();
      int errCode = proc.waitFor();
      if (errCode == 0) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
          foundProgram = Paths.get(reader.readLine());
        }
        logger.info(name + " has been found at : " + foundProgram);
      } else {
        logger.info(name + " not in PATH");
      }
    } catch (InterruptedException ex) {
      logger.warning("Interruption happened while searching for " + name);
    } catch (IOException ex) {
      logger.warning("IO exception happened while searching for " + name);
      // should we rethrow ex? I think not
    }
    return foundProgram;
  }

  ;


  /**
   * Проверяет, что программа запущена в операционной системе Windows
   *
   * @return true если работает на Windows, false в противном случае
   * @link https://stackoverflow.com/questions/934191/how-to-check-existence-of-a-program-in-the-path/38073998#38073998
   */
  private static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }
}
