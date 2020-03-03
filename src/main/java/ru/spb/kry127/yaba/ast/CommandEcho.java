package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class CommandEcho extends Command {
  protected CommandEcho(@NotNull LiteralConcat name) {
    super(name);
  }

  @Override
  public void execute(InputStream in, PrintStream out, PrintStream err)
              throws CommandNotFoundException, IOException {
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
