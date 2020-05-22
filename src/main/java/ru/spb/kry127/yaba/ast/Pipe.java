package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.io.Environment;
import ru.spb.kry127.yaba.io.InputStreamProxy;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс, который представляет из себя синтаксис неименованного канала
 * <p>
 * Данный класс работает в многопоточном режиме, исполняя команды, указанные
 * в именованных каналах одновременно
 */
public class Pipe implements ExecutableExpr {

    private ExecutableExpr cmdLeft;
    private ExecutableExpr cmdRight;

    /**
     * Конструируется pipe, который вывод команды left направляет в команду right.
     * @param left левая команда
     * @param right правая команда
     */
    public Pipe(ExecutableExpr left, ExecutableExpr right) {
        cmdLeft = left;
        cmdRight = right;
    }

    /**
     * Исполнение данной команды выполняется в многопоточном режиме. Именно поэтому
     * ошибки не могут быть проброшены в основной поток. Входные потоки автоматически
     * перебрасываются с помощью неименованных каналов в оперативной памяти.
     *
     * Семантика исполнения -- ленивая, когда left генерирует выходные данные, они
     * попадают в right.
     *
     * @param in  -- поток ввода
     * @param out -- поток вывода
     * @param err -- поток ошибки
     * @throws CommandNotFoundException исключение выбрасывается, если произошла попытка исполнить внешнюю команду,
     *                                  которая не найдена в операционной системе.
     * @throws IOException              исключение выбрасывается при ошибках ввода-вывода
     */
    @Override
    public void execute(InputStreamProxy in, PrintStream out, PrintStream err)
            throws CommandNotFoundException, IOException {
        // создаём неименованный канал в оперативной памяти
        PipedOutputStream pos = new PipedOutputStream();
        InputStreamProxy pis = new InputStreamProxy(new PipedInputStream(pos, 8192));
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

        // флашим output левой команды:
        posPrinter.flush();
        posPrinter.close();
        pis.close();
        // эта операция позволит myThread завершить свою работу!

        try {
            myThread.join(); // ожидаем окончания работы потока
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "Piped thread has been interrupted!");
        } finally {
            // закрываем оставшиеся ресурсы
            pos.close();
            pis.close();
        }

    }

    @Override
    public String interpolate(@NotNull Environment environment) {
        return MessageFormat.format(
                "{0}|{1}",
                cmdLeft.interpolate(environment), cmdRight.interpolate(environment));
    }
}
