package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.io.Environment;

import java.util.LinkedList;
import java.util.List;

/**
 * Команда -- это исполняемая команда, как в программе
 * имя программы и передаваемые аргументы
 */
public abstract class Command implements ExecutableExpr {

  // Тут стоял String, и это была архитектурная ошибка, она стоила дорого
  private LiteralConcat commandName;
  private LiteralConcat[] args;
  private Executable action;
  private boolean internal;

  /**
   * У любой команды должно быть имя. Поэтому конструирование производится по имени/
   * Конструктор с одним параметром строит встроенную команду интерпретатора.
   *
   * @param name Имя конструируемой комманды
   */
  protected Command(@NotNull LiteralConcat name) {
    this(name, true);
  }

  /**
   * У любой команды должно быть имя. Поэтому конструирование производится по имени.
   *
   * @param name       имя конструируемой комманды
   * @param isInternal является ли команда встроенной в интерпретатор
   */
  protected Command(@NotNull LiteralConcat name, boolean isInternal) {
    commandName = name;
    internal = isInternal;
  }

  public boolean isInternal() {
    return internal;
  }

  /**
   * Финальный метод -- имя команды генерируется при конструировании
   * и не может быть переопределено
   *
   * @return возвращает имя команды
   */
  @NotNull
  public final String getCommandName() {
    // хорошо, что мы предусмотрели геттер для поля,
    // которое поменяло тип, и нам придётся поменять только маленький
    // участок кода :)
    return commandName.getRawContents();
  }

  /**
   * Получаем аргументы команды, или иначе, argv, которые
   * передаются в команду/программу при исполнении
   *
   * @return argv = Argument vector
   */
  @NotNull
  public final String[] getArgv() {
    List<String> argv = new LinkedList<String>();
    for (LiteralConcat arg : args) {
      argv.add(arg.getRawContents());
    }
    return argv.toArray(new String[0]);
  }

  /**
   * Команда обязана иметь принимаемые аргументы
   *
   * @param args аргументы команды
   */
  public void setArgs(LiteralConcat[] args) {
    this.args = args;
  }

  @Override
  @NotNull
  public String interpolate(@NotNull Environment environment) {
    StringBuilder sb = new StringBuilder();
    String interpolatedCmdName = commandName.interpolate(environment);
    sb.append(interpolatedCmdName);
    for (LiteralConcat arg : args) {
      sb.append(' ');
      sb.append(arg.interpolate(environment));
    }
    return sb.toString();
  }
}
