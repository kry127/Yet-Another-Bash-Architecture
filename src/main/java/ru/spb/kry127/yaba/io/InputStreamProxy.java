package ru.spb.kry127.yaba.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс, необходимый для реализации прокси-потока, который позволяет
 * безопасно его закрывать, и при этом работает в неблокирующим режиме.
 * <p>
 * Релизует шаблон проектирования Proxy.
 * <p>
 * Плюсы: консоль может работать в более стабильном режиме
 * Минусы: усиленное холостое потребление процессора потоком исполнения,
 * который перекидывает ввод вывод с одного потока на другой.
 */
public class InputStreamProxy extends FilterInputStream {

    private final static Logger log = Logger.getLogger(InputStreamProxy.class.getName());

    /**
     * семафор, который идентифицирует "закрытость" ресурса
     */
    private volatile boolean closed = false;


    /**
     * Оборачивает InputStream в потокобезопасную версию в рамках консольного приложения.
     * @param in
     */
    public InputStreamProxy(InputStream in) {
        super(in);
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (closed)
            return -1; // если ресурс закрыт, возвращаем -1, как идентификатор
//        if (in instanceof PipedInputStream) {
//            PipedInputStream pip = (PipedInputStream) in;
//            return pip.read();
//        }
        if (super.available() > 0) {
            return super.read(b);
        }
        return 0;
    }


    @Override
    public void close() throws IOException {
        log.log(Level.INFO, "Proxy close called");
        closed = true;
        synchronized (this) {
            this.notifyAll();
        }
    }
}
