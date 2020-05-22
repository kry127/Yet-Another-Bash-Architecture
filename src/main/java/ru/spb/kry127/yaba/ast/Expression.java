package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.io.Environment;

/**
 * Обобщённый интерфейс для элементов, представляющих из себя
 * разбор выражения. Потомки, реализующие интерфейс Expression
 * являются следующие классы:
 * <ul>
 *  <li> Command[Name] -- встроенные комманды в интерпретатор</li>
 *  <li> CommandExternal -- внешняя системная команда</li>
 *  <li> Pipe -- аналог неименованного канала</li>
 *  <li> Quoting -- выражения в кавычках</li>
 *  <li> Interpolation -- подстановка выражений (доллары)</li>
 *  <li> Assignment -- присвоение переменной окружения значения</li>
 * </ul>
 * <p>
 *  Объекты данного интерфейса несут конкретный семантический
 *  смысл, а также операционную семантику. Также, разбираемое
 *  выражение подчиняется грамматике:
 *  <ul>
 *   <li>S ::= Pipe | LiteralQualified=Pipe</li>
 *   <li>Pipe ::= Command | Command[|Pipe]</li>
 *   <li>Command ::= Name | Name LiteralList</li>
 *   <li>LiteralList ::= LiteralConcat | LiteralConcat LiteralList</li>
 *   <li>LiteralConcat ::= LiteralQualified | LiteralQualified.LiteralConcat</li>
 *   <li>LiteralQualified ::= Literal | "Literal" | 'Literal'</li>
 *   <li>Literal ::= /([^"'] | \' | \")+/</li>
 *  </ul>
 * <p>
 *  Грамматический разбор происходит после всех интерполяций
 */
public interface Expression {

    /**
     * Метод производит интерполяцию оператора подстановки '$', т.е.
     * просматривает инстансы Interpolation
     *
     * @param environment Окружение, в котором происходит подстановка
     * @return Строка, полученная после интерполяции выражения
     */
    String interpolate(@NotNull Environment environment);
}
