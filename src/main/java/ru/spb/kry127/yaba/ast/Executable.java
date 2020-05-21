package ru.spb.kry127.yaba.ast;

import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.io.InputStreamProxy;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Исполняемая команда -- может быть элемент синтаксиса выражения,
 * ExecutableExpr, однако обладает "операционной семантикой",
 * то есть  может производить некоторый эффект на переменные
 * среды, ОС, файловую систему, и т.д.
 */
public interface Executable {
  /**
   * Команда исполняется с помощью метода в контексте
   * трёх стандартных потоков ввода, вывода и ошибки.
   *  @param in  -- поток ввода
   * @param out -- поток вывода
   * @param err -- поток ошибки
   */
  void execute(InputStreamProxy in, PrintStream out, PrintStream err)
      throws CommandNotFoundException, IOException;
}
