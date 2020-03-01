package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.io.OsUtilsProvider;
import ru.spb.kry127.yaba.io.OsUtilsProviderImpl;

import java.io.*;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.concurrent.*;

/**
 * Класс-фабрика для построения команд. Необходим для того, чтобы
 * явно сопостовлять имени консольной команды её реализацию в
 * виде Java-класса. Таким образом, мы организовываем расширяемость
 * возможности CLI с помощью ввода новых команд
 */
public class CommandFactory {

  private static final String COMMAND_CAT = "cat";
  private static final String COMMAND_ECHO = "echo";
  private static final String COMMAND_WC = "wc";
  private static final String COMMAND_PWD = "pwd";
  private static final String COMMAND_EXIT = "exit";

  private static final OsUtilsProvider osUtils;

  static {
    osUtils = OsUtilsProviderImpl.getUtilsProvider();
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
  public static Command getCommand(@NotNull String name) {
    switch (name) {
      case COMMAND_CAT:
        return new Command(name) {
          @Override
          public void execute(InputStream in, PrintStream out, PrintStream err)
              throws CommandNotFoundException, IOException {
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
        return new Command(name) {
          @Override
          public void execute(InputStream in, PrintStream out, PrintStream err)
              throws CommandNotFoundException, IOException {
            String[] argv = getArgv();
            if (argv.length == 0) {
              out.println();
              return;
            }
            out.print(argv[0]);
            for (int i = 1; i < argv.length; i++) {
              out.print(' ');
              out.print(argv[i]);
            }
          }
        };
      // End case of COMMAND_ECHO
      case COMMAND_WC:
        return new CommandWc(name);
      // End case of COMMAND_WC
      case COMMAND_PWD:
        return new Command(name) {
          @Override
          public void execute(InputStream in, PrintStream out, PrintStream err)
              throws CommandNotFoundException, IOException {
            final String cwd = System.getProperty("user.dir");
            out.println(cwd);
          }
        };
      // End case of COMMAND_PWD
      case COMMAND_EXIT:
        return new Command(name) {
          @Override
          public void execute(InputStream in, PrintStream out, PrintStream err)
              throws CommandNotFoundException, IOException {
            System.exit(0);
          }
        };
      // End case of COMMAND_EXIT
      default:
        // здесь необходимо сделать комманду, которая будет вызывать внешнюю программу
        return new Command(name) {
          @Override
          public void execute(InputStream in, PrintStream out, PrintStream err)
              throws CommandNotFoundException, IOException {

            final String execName = getCommandName();

            // проверим, что команда существует в системе
            Path execPath = osUtils.checkProgramExists(execName);
            if (execPath == null) {
              throw new CommandNotFoundException(execName);
            }

            // добавляем имя команды как параметр argv[0]
            final String[] oldArgv = getArgv();
            String[] newArgv = new String[oldArgv.length + 1];
            newArgv[0] = execPath.toString();
            System.arraycopy(oldArgv, 0, newArgv, 1, oldArgv.length);

            // пробуем вызвать внешнюю программу
            // TODO прокинуть Environment в создаваемый процесс
            ProcessBuilder builder = new ProcessBuilder(newArgv);

            Process p = builder.start();

            InputStream pinput = p.getInputStream();
            OutputStream poutput = p.getOutputStream();
            InputStream perror = p.getErrorStream();

            // данные команды должны запускаться асинхронно
            // https://stackoverflow.com/a/51006865
            ExecutorService threadPool = Executors.newFixedThreadPool(10);
            CompletableFuture<?> futureIn = (CompletableFuture<?>)
                threadPool.submit(() -> {
              osUtils.redirectIOStreams(in, poutput);
            });
            CompletableFuture<?> futureOut = (CompletableFuture<?>)
                threadPool.submit(() -> {
              osUtils.redirectIOStreams(pinput, out);
            });
            CompletableFuture<?> futureErr = (CompletableFuture<?>)
                threadPool.submit(() -> {
              osUtils.redirectIOStreams(perror, err);
            });

            // барьер синхронизации
            CompletableFuture.allOf(futureIn,futureOut,futureErr).join();

            pinput.close();
            poutput.close();
            perror.close();
          }
        };
    }
  }
}
