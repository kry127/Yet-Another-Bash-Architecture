package ru.spb.kry127.yaba.ast;

import ru.spb.kry127.yaba.exceptions.SyntaxException;

/**
 * Интерфейс парсера необходим для того, чтобы убрать зависимость от
 * конкретной реализации парсера. Сегодня мы парсим с помощью LL
 * анализатора и конечного автомата с МП, а завтра захотим LR(0)
 * парсер, или вообще SLR парсер, так и ANTLR вообще прикрутим :)
 */
public interface Parser {
  /**
   * Разбирает входную строку на выражения
   *
   * @return экземпляр класса разобранного выражения
   */
  Executable parseExpression(String input) throws SyntaxException;

}
