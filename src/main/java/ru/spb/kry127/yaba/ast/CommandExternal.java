package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.io.Environment;
import ru.spb.kry127.yaba.io.EnvironmentProvider;
import ru.spb.kry127.yaba.io.OsUtils;
import ru.spb.kry127.yaba.io.OsUtilsProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

public class CommandExternal extends Command {

  private final OsUtils osUtils;

  public CommandExternal(@NotNull LiteralConcat name) {
    super(name);
    osUtils = OsUtilsProvider.getUtilsProvider();
  }

  @Override
  public void execute(InputStream in, PrintStream out, PrintStream err)
      throws CommandNotFoundException, IOException {
    {
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
      Environment env = EnvironmentProvider.getEnvironment();
      ProcessBuilder builder = new ProcessBuilder(newArgv);
      builder.environment().putAll(env.getFullEnvironment());

      Process p = builder.start();

      InputStream pinput = p.getInputStream();
      OutputStream poutput = p.getOutputStream();
      InputStream perror = p.getErrorStream();

      OsUtils osUtils = OsUtilsProvider.getUtilsProvider();
      Runnable runnable1 = osUtils.ioStreamsRedirector(in, poutput, "in");
      Runnable runnable2 = osUtils.ioStreamsRedirector(pinput, out, "stdout");
      Runnable runnable3 = osUtils.ioStreamsRedirector(perror, err, "stderr");

      Thread thread1 = new Thread(runnable1);
      Thread thread2 = new Thread(runnable2);
      Thread thread3 = new Thread(runnable3);

      thread1.start();
      thread2.start();
      thread3.start();

      // blocking call
      try {
        p.waitFor();
        // manually terminate threads
        thread1.interrupt();
        thread2.interrupt();
        thread3.interrupt();
      } catch (InterruptedException e) {
        err.println("Command '" + execName + "' was interrupted");
      } finally {
        pinput.close();
        poutput.close();
        perror.close();
      }

      p.destroy();

    }
  }
}
