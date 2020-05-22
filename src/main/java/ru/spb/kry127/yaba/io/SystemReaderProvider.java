package ru.spb.kry127.yaba.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Реализация протокола доступа к системному потоку ввода вывода.
 * Является синглтоном
 */
public class SystemReaderProvider implements SystemReader {

    private static SystemReader singleton;
    private final BufferedReader bufferedReader;

    private SystemReaderProvider() {
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Поставляет экземпляр класса, который реализует интерфейс
     * <tt>SystemReader</tt> стандартным образом --
     * предоставляет stdout и stderr, предоставляемые
     * виртуальной машиной Java. Однако, stdin оборачивается
     * в потокобезопасную версию в рамках консольного приложения.
     *
     * @return объект-синглтон класса <tt>SystemReaderProvider</tt>
     */
    public static SystemReader getSystemReader() {
        if (singleton == null) {
            singleton = new SystemReaderProvider();
        }
        return singleton;
    }

    @Override
    public String getLine() throws IOException {
        return bufferedReader.readLine();
    }


    /**
     * Возвращаем проксированный поток ввода-вывода с целью того,
     * чтобы избегать непосредственного вызова close() на стандартный поток вывода
     *
     * @return
     */
    @Override
    public InputStreamProxy getInStream() {
        return new InputStreamProxy(System.in);
    }

    @Override
    public PrintStream getOutStream() {
        return System.out;
    }

    @Override
    public PrintStream getErrStream() {
        return System.err;
    }
}
