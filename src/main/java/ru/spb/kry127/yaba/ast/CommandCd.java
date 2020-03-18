package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;

public class CommandCd extends Command {
    protected CommandCd(@NotNull LiteralConcat name) {
        super(name);
    }

    @Override
    public void execute(InputStream in, PrintStream out, PrintStream err) {
        String[] argv = getArgv();
        String homeDir = System.getProperty("user.home");
        String userDir = System.getProperty("user.dir");
        File file = null;
        if (argv.length == 0) {
            file = new File(homeDir);
        } else if (argv.length == 1) {
            File userDirFile = new File(userDir);
            Path p = userDirFile.toPath().resolve(argv[0]);
            file = p.toFile();
        } else {
            err.printf("Expected path, but got %s", Arrays.toString(argv));
            err.println();
            return;
        }
        System.setProperty("user.dir", file.getAbsolutePath());

    }
}
