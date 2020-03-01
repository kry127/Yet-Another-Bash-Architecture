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

  private final String commandName;
  private LiteralConcat[] args;
  private Executable action;
  private boolean internal;

  /**
   * У любой команды должно быть имя. Поэтому конструирование производится по имени/
   * Конструктор с одним параметром строит встроенную команду интерпретатора.
   *
   * @param name Имя конструируемой комманды
   */
  protected Command(@NotNull String name) {
    this(name, true);
  }

  /**
   * У любой команды должно быть имя. Поэтому конструирование производится по имени.
   *
   * @param name       имя конструируемой комманды
   * @param isInternal является ли команда встроенной в интерпретатор
   */
  protected Command(@NotNull String name, boolean isInternal) {
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
    return commandName;
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
    return (String[]) argv.toArray();
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
    sb.append(getCommandName());
    for (LiteralConcat arg : args) {
      sb.append(' ');
      sb.append(arg.interpolate(environment));
    }
    return sb.toString();
  }
}
