package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.io.OsUtils;
import ru.spb.kry127.yaba.io.OsUtilsProvider;

import java.io.*;
import java.text.MessageFormat;

/**
 * Класс-фабрика для построения команд. Необходим для того, чтобы
 * явно сопостовлять имени консольной команды её реализацию в
 * виде Java-класса. Таким образом, мы организовываем расширяемость
 * возможности CLI с помощью ввода новых команд
 */
public class CommandFactory {

  public static final String COMMAND_CAT = "cat";
  public static final String COMMAND_ECHO = "echo";
  public static final String COMMAND_WC = "wc";
  public static final String COMMAND_GREP = "grep";
  public static final String COMMAND_PWD = "pwd";
  public static final String COMMAND_LS = "ls";
  public static final String COMMAND_CD = "cd";
  public static final String COMMAND_EXIT = "exit";

  private static final OsUtils osUtils;

  static {
    osUtils = OsUtilsProvider.getUtilsProvider();
  }

  /**
   * Основной метод фабрики -- фабрика команд. При получении
   * имени команды определяет, является она встроенной или нет,
   * и соответственно конструирует необходимый класс на лету
   * <p>
   * Перечисление имён поддерживаемых встроенных команд в CLI.
   * Плюсы: безопасное переименовывание команд
   * Минусы: могут быть проблемы с рефакторингом, так как
   * многие классы создаются прямо на месте
   *
   * @param name название команды
   * @return Исполняемая команда в разобранной строке
   */
  public static Command getCommand(@NotNull LiteralConcat name) {
    switch (name.getRawContents()) {
      case COMMAND_CAT:
        return new Command(name) {
          @Override
          public void execute(InputStream in, PrintStream out, PrintStream err)
              throws IOException {
            String[] argv = getArgv();
            if (argv.length == 0) {
              osUtils.redirectIOStreams(in, out);
            } else {
              // выполняем чтение из файла
              for (String path : argv) {
                try (InputStream inputStream = new FileInputStream(path)) {
                  osUtils.redirectIOStreams(inputStream, out);
                } catch (FileNotFoundException e) {
                  String errMessage = MessageFormat.format(
                      "{0}: {1}: No such file or directory",
                      getCommandName(), path);
                  err.print(errMessage);
                }
              }
            }
          }
        };
      // End case of COMMAND_CAT
      case COMMAND_ECHO:
        return new CommandEcho(name);
      // End case of COMMAND_ECHO
      case COMMAND_WC:
        return new CommandWc(name);
      // End case of COMMAND_WC
      case COMMAND_GREP:
        return new CommandGrep(name);
      // End case of COMMAND_WC
      case COMMAND_PWD:
        return new Command(name) {
          @Override
          public void execute(InputStream in, PrintStream out, PrintStream err) {
            final String cwd = System.getProperty("user.dir");
            out.println(cwd);
          }
        };
      // End case of COMMAND_PWD
      case COMMAND_LS:
        return new CommandLs(name);
      case COMMAND_CD:
        return new CommandCd(name);
      case COMMAND_EXIT:
        return new Command(name) {
          @Override
          public void execute(InputStream in, PrintStream out, PrintStream err) {
            System.exit(0);
          }
        };
      // End case of COMMAND_EXIT
      default:
        // здесь необходимо сделать комманду, которая будет вызывать внешнюю программу
        return new CommandExternal(name);
    }
  }
}
