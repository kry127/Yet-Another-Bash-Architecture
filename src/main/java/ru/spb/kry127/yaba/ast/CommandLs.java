package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Optional;

public class CommandLs extends Command {
    protected CommandLs(@NotNull LiteralConcat name) {
        super(name);
    }

    @Override
    public void execute(InputStream in, PrintStream out, PrintStream err) {
        String[] argv = getArgv();
        String path;
        if (argv.length == 0) {
            path = System.getProperty("user.dir");
        } else if (argv.length == 1) {
            path = argv[0];
        } else {
            err.printf("Expected path, but got %s", Arrays.toString(argv));
            err.println();
            return;
        }
        File file = new File(path);
        String[] files = file.list();
        if (files != null) {
            for (String fileName : files) {
                out.println(fileName);
            }
        } else {
            out.println(path);
        }
    }
}
