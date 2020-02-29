package ru.spb.kry127.yaba.exceptions;

import ru.spb.kry127.yaba.ast.Expression;

import java.text.MessageFormat;

/**
 * Класс ошибки, которая возникает при обнаружении несуществующей команды.
 */
public class CommandNotFoundException extends Exception {
    /**
     * У этой ошибки, скорее всего, не может быть причины.
     * Можно добавить второй конструктор, принимающий Throwable
     * по мере надобности
     *
     * @param expr Выражение, на котором произошла ошибка
     */
    public CommandNotFoundException(Expression expr) {
        super(MessageFormat.format(
                "Cannot find command {0,string} :(",
                expr.toString()));
    }
}
