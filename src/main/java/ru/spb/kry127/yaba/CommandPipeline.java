package ru.spb.kry127.yaba;

/**
 * Данный интерфейс специфицирует основной цикл
 * чтение-исполнение-печать (REPL).
 */
public interface CommandPipeline extends Runnable {

    /**
     * Блокирующий вызов -- запуск программы в режиме REPL
     */
    void run();
}
