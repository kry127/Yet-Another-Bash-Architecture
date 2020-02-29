package ru.spb.kry127.yaba.io;

import java.io.OutputStream;

/**
 * Класс-обёртка для абстракции системного потока ввода вывода.
 * Благо, Java сама поддерживает данную абстракцию, поэтому её
 * реализация для консольного приложения очевидна, однако,
 * могут понадобится случаи перекофигурации потока вывода и
 * потока ошибок.
 */
public interface SystemReader {
    /**
     * Получает очередную строку из потока ввода.
     *
     * @return строка, введённая пользователем
     */
    String getLine();

    /**
     * @return стандартный поток вывода.
     */
    OutputStream getOutStream();

    /**
     * @return стандартный поток ошибок.
     */
    OutputStream getErrStream();
}
