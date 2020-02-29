package ru.spb.kry127.yaba.ast;

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
 *   <li>S ::= Assignment=Pipe | Pipe</li>
 *   <li>Pipe ::= Command | Command[|Pipe]</li>
 *   <li>Command ::= Name ParameterList</li>
 *   <li>ParameterList ::= ParameterQualified | Parameter ParameterList</li>
 *   <li>ParameterQualified ::= Parameter | "Parameter" | 'Parameter'</li>
 *   <li>Paramter ::= /([^"'] | \' | \")+/</li>
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
    String interpolate(Environment environment);
}
