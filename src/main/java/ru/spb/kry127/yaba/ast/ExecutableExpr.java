package ru.spb.kry127.yaba.ast;

/**
 * Интерфейс объединяет объекты с общими свойствами
 * быть выражениями AST дерева, и быть "исполняемыми"
 */
public interface ExecutableExpr extends Executable, Expression {
}
