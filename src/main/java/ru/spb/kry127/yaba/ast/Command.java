package ru.spb.kry127.yaba.ast;

/**
 * Команда -- это исполняемая команда, как в программе
 * имя программы и передаваемые аргументы
 */
public interface Command extends Executable {
    /**
     * Команда обязана иметь принимаемые аргументы
     * @param args
     */
    void setArgs(Literal[] args);
}
