package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.io.Environment;

import java.util.LinkedList;
import java.util.List;

/**
 * Синтаксический элемент, представляющий из себя
 * конкатенацию строковых литералов разного сорта
 * <p>
 * Этот класс необходим, так как для интерполяции
 * надо различать два сорта строк -- с одинарными
 * и с двойными кавычками.
 */
public class LiteralConcat implements Expression {

    private List<Literal> list;

    /**
     * Подобный класс должен иметь хотя бы один литерал
     *
     * @param literal Литерал, из которого будет состоять начальный литеральный список
     */
    LiteralConcat(@NotNull Literal literal) {
        list = new LinkedList<>();
        addLiteral(literal);
    }

    /**
     * Добавляет очередной литерал к литеральной конкатенации
     *
     * @param literal добавляемый литерал
     */
    public void addLiteral(@NotNull Literal literal) {
        list.add(literal);
    }

    @Override
    public String interpolate(@NotNull Environment environment) {
        StringBuilder sb = new StringBuilder();
        for (Literal literal : list) {
            sb.append(literal.interpolate(environment));
        }
        return sb.toString();
    }

    /**
     * Метод выполняет извлечение всех сырых строковых значений литералов без
     * использования интерполяций, и возвращает их конкатенацию.
     *
     * @return сырое содержимое строки без интерполяций.
     */
    protected String getRawContents() {
        StringBuilder sb = new StringBuilder();
        for (Literal literal : list) {
            sb.append(literal.getRawContents());
        }
        return sb.toString();
    }

    /**
     * Статический метод предназначен облегчить приведение
     * стоки <tt>java.lang.String</tt> к типу LiteralConcat
     *
     * @return Обёртку строки в виде <tt>LiteralConcat</tt>
     */
    public static LiteralConcat fromString(String s) {
        return new LiteralConcat(new LiteralRaw(s));
    }
}
