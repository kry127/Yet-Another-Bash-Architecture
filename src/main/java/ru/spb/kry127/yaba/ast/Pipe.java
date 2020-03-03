package ru.spb.kry127.yaba.ast;

import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.io.Environment;

import java.io.*;
import java.text.MessageFormat;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс, который представляет из себя синтаксис неименованного канала
 * <p>
 * Данный класс работает в многопоточном режиме, исполняя команды, указанные
 * в именованных каналах одновременно
 */
public class Pipe implements ExecutableExpr {

  ExecutableExpr cmdLeft;
  ExecutableExpr cmdRight;

  public Pipe(ExecutableExpr left, ExecutableExpr right) {
    cmdLeft = left;
    cmdRight = right;
  }

  /**
   * Исполнение данной команды выполняется в многопоточном режиме. Именно поэтому
   * ошибки не могут быть проброшены в основной поток. Входные потоки автоматически
   * перебрасываются с помощью неименованных каналов в оперативной памяти.
   *
   * @param in  -- поток ввода
   * @param out -- поток вывода
   * @param err -- поток ошибки
   * @throws CommandNotFoundException
   * @throws IOException
   */
  @Override
  public void execute(InputStream in, PrintStream out, PrintStream err)
      throws CommandNotFoundException, IOException {
    // создаём неименованный канал в оперативной памяти
    PipedOutputStream pos = new PipedOutputStream();
    PipedInputStream pis = new PipedInputStream(pos, 8192);
    PrintStream posPrinter = new PrintStream(pos);
    final Logger log = Logger.getLogger(Pipe.class.getName());

    // делаем цепочку из пайпов

    Runnable runnable = () -> {
      final PrintStream eps = new PrintStream(err);
      try {
        cmdRight.execute(pis, out, err);
        // TODO take a look at custom ThreadExecutors
      } catch (CommandNotFoundException e) {
        log.log(Level.SEVERE, "Exception happened in pipe thread!!");
        eps.println(e.toString());
        e.printStackTrace(eps);
        //throw new RuntimeException(e);
      } catch (IOException e) {
        log.log(Level.SEVERE, "Exception happened in pipe thread!!");
        e.printStackTrace(eps);
      }
    };

    Thread myThread = new Thread(runnable);
    myThread.start();

    // только после старта второй команды запускаем первую!
    cmdLeft.execute(in, posPrinter, err);

    synchronized (posPrinter) {
      posPrinter.flush();
    }
    synchronized (pos) {
      pos.flush();
    }
    pis.close();


    // закрываем пайпы, чтобы показать, что мы сделали работу
    pos.close();
    pis.close();
    posPrinter.close();

    try {
      myThread.join();
    } catch (InterruptedException e) {
      log.log(Level.WARNING, "Piped thread has been interrupted!");
    }

  }

  @Override
  public String interpolate(Environment environment) {
    return MessageFormat.format(
        "{0}|{1}",
        cmdLeft.interpolate(environment), cmdRight.interpolate(environment));
  }
}
