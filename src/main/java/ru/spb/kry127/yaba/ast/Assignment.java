package ru.spb.kry127.yaba.ast;

import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.io.Environment;

import java.io.*;
import java.text.MessageFormat;

/**
 * Класс-выражение, который определяет, что результат справа от знака "="
 * должен быть записан в переменную среды
 */
public class Assignment implements Executable {
    private final Environment environment;
    private final String environmentVariable;
    private Expression expression;

    /**
     * Конструируется узел дерева разбора, запоминающий, что
     * в переменную окружения необходимо присвоить результат
     * @param environment среда, в которой необходимо сохранить значение переменной
     * @param environmentVariable переменная окружения
     */
    public Assignment(Environment environment, String environmentVariable) {
        this.environment = environment;
        this.environmentVariable = environmentVariable;
    }

    /**
     * Определяем, результат какого выражения мы ожидаем
     * при присвоении выражения. Так как система простая,
     * мы ожидаем только строковый литерал без возможности
     * вложенных конструкций вычисления, однако в дальнейшем
     * это можно расширить.
     * @param rhs результат присвоения
     */
    public void setExpression(Expression rhs) {
        expression = rhs;
    }

    @Override
    public String interpolate(Environment exec_environment) {
        return MessageFormat.format(
                "{0,string}={1}",
                environmentVariable, expression.interpolate(exec_environment));
    }

    @Override
    public void execute(InputStream in, OutputStream out, OutputStream err)
                               throws CommandNotFoundException, IOException {
        if (expression instanceof Executable) {
            OutputStream os = new ByteArrayOutputStream();
            ((Executable) expression).execute(in, os, err);
            final String exprResult = os.toString();
            this.environment.setEnvVariable(environmentVariable, exprResult);
        } else if (expression instanceof Literal) {
            this.environment.setEnvVariable(environmentVariable, expression.toString());
        } else {
            throw new CommandNotFoundException(expression);
        }
    }
}
