package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.io.InputStreamProxy;

import java.io.PrintStream;

/**
 * Команда 'echo' выводит в стандартный вывод аргументы, введённые пользователем
 * через пробел в командной строке
 */
public class CommandEcho extends Command {
    protected CommandEcho(@NotNull LiteralConcat name) {
        super(name);
    }

    @Override
    public void execute(InputStreamProxy in, PrintStream out, PrintStream err) {
        String[] argv = getArgv();
        if (argv.length == 0) {
            return;
        }
        out.print(argv[0]);
        for (int i = 1; i < argv.length; i++) {
            out.print(' ');
            out.print(argv[i]);
        }
        out.println();
    }
}
