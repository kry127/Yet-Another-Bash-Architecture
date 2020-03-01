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
   * @param literal
   */
  LiteralConcat(@NotNull Literal literal) {
    list = new LinkedList<>();
    addLiteral(literal);
  }

  /**
   * Добавляет очередной литерал к литеральной конкатенации
   *
   * @param literal
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

  protected String getRawContents() {
    StringBuilder sb = new StringBuilder();
    for (Literal literal : list) {
      sb.append(literal.getRawContents());
    }
    return sb.toString();
  }
}
