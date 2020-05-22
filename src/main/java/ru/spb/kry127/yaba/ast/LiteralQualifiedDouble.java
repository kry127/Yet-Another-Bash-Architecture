package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.io.Environment;

import java.text.MessageFormat;

/**
 * Класс-выражение, инкапсулирующий двойные кавычки.
 */
public class LiteralQualifiedDouble extends Literal {

    protected LiteralQualifiedDouble(String contents) {
        super(contents);
    }

    @Override
    public String interpolate(@NotNull Environment environment) {
        final String interpolated = super.interpolate(environment);
        // TODO fix escaped symbols
        return MessageFormat.format("\"{0}\"", interpolated);
    }
}
