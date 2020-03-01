package ru.spb.kry127.yaba.ast;

import ru.spb.kry127.yaba.io.Environment;

import java.text.MessageFormat;

/**
 * <p>
 * Класс-выражение, являющееся строковым литералом и не заключённое в кавычки.
 * По сути, это чистая обёртка над классом String
 * <p>
 * P.S.:На языке C++ это было бы using Literal = String, на Haskell: type Literal = String
 * <p>
 *     Класс необходим, так как используется метод Interpolate для подстановки выражений вида доллар
 * </p>
 */
public class LiteralRaw extends Literal {
    public LiteralRaw(String contents) {
      super(contents);
    }
}
