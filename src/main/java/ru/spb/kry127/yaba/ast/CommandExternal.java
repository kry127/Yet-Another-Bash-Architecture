package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Класс, который запускает внешние команды в операционной системе.
 */
public class CommandExternal extends Command {

    private final OsUtils osUtils;
    private static final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(12);

    /**
     * Инициализация внешней команды операционной системы с именем name.
     *
     * @param name Имя внешней команды операционной системы
     */
    public CommandExternal(@NotNull LiteralConcat name) {
        super(name);
        osUtils = OsUtilsProvider.getUtilsProvider();
    }

    @Override
    public void execute(InputStreamProxy in, PrintStream out, PrintStream err)
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

            threadPool.execute(runnable1);
            threadPool.execute(runnable2);
            threadPool.execute(runnable3);

            // blocking call
            try {
                poutput.flush();
                p.waitFor();
                // manually terminate threads
                threadPool.remove(runnable1);
                threadPool.remove(runnable2);
                threadPool.remove(runnable3);
            } catch (InterruptedException e) {
                err.println("Command '" + execName + "' was interrupted");
            } finally {
                in.close(); // использовать ТОЛЬКО с прокси-обёрткой! см. класс InputStreamProxy
                pinput.close();
                poutput.close();
                perror.close();
            }

            p.destroy();

        }
    }
}
