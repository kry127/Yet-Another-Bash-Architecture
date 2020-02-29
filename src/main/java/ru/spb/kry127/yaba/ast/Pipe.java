package ru.spb.kry127.yaba.ast;

import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.io.Environment;

import java.io.*;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс, который представляет из себя синтаксис неименованного канала
 * <p>
 * Данный класс работает в многопоточном режиме, исполняя команды, указанные
 * в именованных каналах одновременно
 */
public class Pipe implements Executable {

    Executable cmdLeft;
    Executable cmdRight;

    public Pipe(Executable left, Executable right) {
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
    public void execute(InputStream in, OutputStream out, OutputStream err)
            throws CommandNotFoundException, IOException {
        // создаём неименованный канал в оперативной памяти
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos, 1024);
        cmdLeft.execute(in, pos, err);
        // делаем цепочку из пайпов
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        threadPool.execute(() -> {
            final Logger log = Logger.getLogger(Pipe.class.getName());
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
        });
    }

    @Override
    public String interpolate(Environment environment) {
        return MessageFormat.format(
                "{0,string}|{1,string}",
                cmdLeft.interpolate(environment), cmdRight.interpolate(environment));
    }
}
