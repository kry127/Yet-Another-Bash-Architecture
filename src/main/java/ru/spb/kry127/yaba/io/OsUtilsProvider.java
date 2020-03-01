package ru.spb.kry127.yaba.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public interface OsUtilsProvider {
  /**
   * Перенаправляет поток ввода в поток вывода.
   *
   * @param in  поток ввода
   * @param out поток вывода
   */
  void redirectIOStreams(@NotNull InputStream in, @NotNull OutputStream out);

  /**
   * Проверяет наличие программы в переменной окружения и
   * возвращает путь, если он существует.
   *
   * @param name программа, которую необходимо найти
   * @return Путь к программе, если он есть, иначе возвращается <tt>null</tt>
   */
  @Nullable
  Path checkProgramExists(@NotNull String name) throws IOException;
}
