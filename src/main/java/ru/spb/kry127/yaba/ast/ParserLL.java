package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.exceptions.SyntaxException;
import ru.spb.kry127.yaba.io.Environment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Имплементация парсера, реализующая LL-спуск по грамматике
 * <p>
 * Существенный недостаток парсера -- управление на исключениях.
 *
 * @see ru.spb.kry127.yaba.ast.Parser
 * @see ru.spb.kry127.yaba.ast.Expression
 */
public class ParserLL implements Parser {

  /*
   * Эти регулярные выражения являются префиксными, то есть, они проверяют,
   * корректно ли выражение спереди, соответствует ли оно литералу в одинарных,
   * двойных кавычках, или вовсе без кавычек. Остаток анализируется отдельно дальше.
   */
  final static Pattern DOUBLE_QUALIFIED_REGEX = Pattern.compile("^\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"");
  final static Pattern SINGLE_QUALIFIED_REGEX = Pattern.compile("^'([^'\\\\]*(?:\\\\.[^'\\\\]*)*)'");
  final static Pattern RAW_QUALIFIED_REGEX = Pattern.compile("^([^\\s\"'|=]+)");
  final static Pattern PIPE_REGEX = Pattern.compile("^\\s*\\|\\s*");
  final static Pattern EMPTY_REGEX = Pattern.compile("^\\s*");
  final static Pattern NONEMPTY_REGEX = Pattern.compile("\\S");

  private Environment environment;

  public ParserLL(Environment environment) {
    this.environment = environment;
  }

  /**
   * Обёртка, которая позволяет возвращать и разобранное выражение
   * в виде объекта класса T, и его остаток в виде строки
   * <p>
   * Это внутренний инвариант класса, не следует его использовать вне ParserLL
   *
   * @param <T> наследник класса Expression -- результат парсинга
   */
  private static class InformationBundle<T extends Expression> {
    private T expression; // результат парсинга
    private String rest;  // остаток разбираемой строки

    // даже создавать его запрещаем (по крайней мере легальными методами)
    private InformationBundle(T expression, String rest) {
      this.expression = expression;
      this.rest = rest;
    }
  }

  @Override
  @NotNull
  public ExecutableExpr parseExpression(@NotNull String input) throws SyntaxException {
    // единственным публичным методом интерфейса Parser
    // наконец возвращаем результат парсинга
    if (isEmpty(input)) {
      Command echo = new CommandEcho(LiteralConcat.fromString(""));
      echo.setArgs(new LiteralConcat[0]);
      return echo;
    }
    return buildAst(input);
  }

  /**
   * Строим абстрактное синтаксическое дерево по входной строке.
   * Разбор производится раскрытием начального нетерминала с
   * помощью метода <tt>parseAssignment</tt>
   *
   * @param input строка для разбора
   * @return разобранное выражение
   * @throws SyntaxException Сообщение об ошибке парсинга
   */
  @NotNull
  private ExecutableExpr buildAst(@NotNull String input) throws SyntaxException {
    InformationBundle<ExecutableExpr> execExprBundle = parseAssignment(input);
    ExecutableExpr ret = execExprBundle.expression;
    String tail = execExprBundle.rest;
    if (!isEmpty(tail)) {
      throw new SyntaxException("Unexpected nonempty tail after parsed command line");
    }
    // наконец-то возвращаем разобранное выражение!
    return ret;
  }

  /**
   * Метод парсит присвоение переменной окружения.
   * Используется методы <tt>parseOneLiteral</tt> и ручная проверка первого символа
   * на совпадение со знаком '='.
   * <p>
   * Это самый главный метод синтаксического разбора (начальный нетерминал S)
   * <p>
   * Обратим внимание, что именно <tt>parseOneLiteral</tt>, так как Bash не воспринимает
   * конкатенацию разнородных строк перед знаком "=" как переменную среды.
   */
  @NotNull
  private InformationBundle<ExecutableExpr> parseAssignment(@NotNull String input) throws SyntaxException {
    // if first throws, than there is no literal list here
    InformationBundle<Literal> ib = parseOneLiteral(input);
    String rest = ib.rest;
    Literal env_variable = ib.expression;

    if (rest.length() > 0 && rest.charAt(0) == '=') {
      // да, действительно, это присвоение переменной среды
      // тут может быть либо пайп (команда), либо литерал
      rest = rest.substring(1);
      try {
        // здесь может упасть, тогда это не просто литерал
        InformationBundle<LiteralConcat> ibconcat = parseLiteral(rest);
        String subRest = ibconcat.rest;
        LiteralConcat concat = ibconcat.expression;
        if (isEmpty(subRest)) {
          // окей, если строка пустая после парса
          Assignment assignment = new Assignment(environment, env_variable.getRawContents());
          // определяем, что присваиваем литералу
          assignment.setExpression(concat);
          return new InformationBundle<>(assignment, subRest);
        }
      } catch (SyntaxException exc) {
        // OK, не получилось распарсить просто литерал
      }
      // теперь попытаемся распарсить его как пайп
      // но уже без отлова исключения, так как это второй и последний кейс
      InformationBundle<ExecutableExpr> ibpipe = parsePipe(rest);
      rest = ibpipe.rest;
      ExecutableExpr pipe = ibpipe.expression;
      Assignment pipeAssignment = new Assignment(environment, env_variable.getRawContents());
      pipeAssignment.setExpression(pipe);
      return new InformationBundle<>(pipeAssignment, rest);
    }

    // если дошли до сюда, то распарсить как присвоение переменной окружения
    // не получилось -- пробуем распарсить как пайп целиком
    // теперь попытаемся распарсить его как пайп
    // но уже без отлова исключения, так как это второй и последний кейс
    InformationBundle<ExecutableExpr> pip = parsePipe(input);
    rest = pip.rest;
    ExecutableExpr pipe = pip.expression;
    return new InformationBundle<>(pipe, rest);
  }


  /**
   * Метод парсит неименованные каналы на основе метода <tt>parseCommand</tt>
   */
  @NotNull
  private InformationBundle<ExecutableExpr> parsePipe(@NotNull String input) throws SyntaxException {
    // if first throws, than there is no literal list here
    InformationBundle<Command> ib = parseCommand(input);
    String rest = ib.rest;
    // actually, this is a Command, but we need to rise the abstraction
    // LCA(Command, Pipe) = ExecutableExpr
    ExecutableExpr command = ib.expression;

    // add it to list of piped commands
    LinkedList<ExecutableExpr> pipedCommands = new LinkedList<>();
    pipedCommands.addLast(command);

    // in loop we concatenate Commands via Pipes
    while (!isEmpty(rest)) {
      // purge space before each Pipe, just in case...
      rest = purgeSpaces(rest);

      // then we search for pipe
      Matcher matcherPipe = PIPE_REGEX.matcher(rest);

      // purge space after each Pipe, just in case...
      rest = purgeSpaces(rest);

      if (matcherPipe.find()) {
        int end = matcherPipe.end();
        rest = rest.substring(end);
      } else {
        // this should be error, because grammar not supposed to reside any
        // nonterminals behind Pipe ...
        String message = "Invalid syntax at position: '" + rest + "'. (Expected pipe or nothing)";
        throw new SyntaxException(message);
      }

      // pipe found, then we should find command and parse it
      // get the second command (SHOULD exist)
      ib = parseCommand(rest);
      rest = ib.rest;
      pipedCommands.addLast(ib.expression);
    }

    // собираем pipedCommands с правого конца для правильного применения эффектов
    while (pipedCommands.size() > 1) {
      ExecutableExpr eeRhs = pipedCommands.removeLast();
      ExecutableExpr eeLhs = pipedCommands.removeLast();
      Pipe pipe = new Pipe(eeLhs, eeRhs);
      pipedCommands.addLast(pipe);
    }

    // Наконец возвращаем собранный пайплайн
    return new InformationBundle<>(pipedCommands.getFirst(), rest);

  }


  /**
   * Метод парсит команду на основе методов <tt>parseLiteral</tt>
   */
  @NotNull
  private InformationBundle<Command> parseCommand(@NotNull String input) throws SyntaxException {
    // if first throws, than there is no literal list here
    InformationBundle<LiteralConcat> ib = parseLiteral(input);
    String rest = ib.rest;
    LiteralConcat program_name = ib.expression;


    // getting arguments of a program
    List<LiteralConcat> argv = new ArrayList<>();
    try {
      while (!isEmpty(rest)) {
        // purge space after each LiteralConcat
        rest = purgeSpaces(rest);
        ib = parseLiteral(rest);
        rest = ib.rest;
        argv.add(ib.expression);
      }
    } catch (SyntaxException ex) {
      // OK, literal parsing is over
    }

    // composing Command with factory class
    Command cmd = CommandFactory.getCommand(program_name);
    cmd.setArgs(argv.toArray(new LiteralConcat[0]));
    return new InformationBundle<>(cmd, rest);
  }

  /**
   * Метод обрабатывает серию литералов и конкатенирует их в один литерал,
   * используя метод <tt>parseLiteral</tt>
   */
  @NotNull
  private InformationBundle<LiteralConcat> parseLiteral(@NotNull String input)
      throws SyntaxException {
    // if first throws, than there is no literal here
    InformationBundle<Literal> ib = parseOneLiteral(input);
    String rest = ib.rest;
    LiteralConcat lc = new LiteralConcat(ib.expression);

    try {
      while (!isEmpty(rest)) {
        ib = parseOneLiteral(rest);
        rest = ib.rest;
        lc.addLiteral(ib.expression);
      }
    } catch (SyntaxException ex) {
      // OK, literal parsing is over
    }
    return new InformationBundle<>(lc, rest);
  }

  /**
   * Метод обрабатывает с текущей позиции только один литерал в одинарных, двойных кавычках,
   * или без кавычек
   */
  @NotNull
  private InformationBundle<Literal> parseOneLiteral(@NotNull String input)
      throws SyntaxException {

    Matcher matcherDouble = DOUBLE_QUALIFIED_REGEX.matcher(input);
    Matcher matcherSingle = SINGLE_QUALIFIED_REGEX.matcher(input);
    Matcher matcherRaw = RAW_QUALIFIED_REGEX.matcher(input);

    if (matcherDouble.find()) {
      String rawValue = matcherDouble.group(1);
      Literal lqd = new LiteralQualifiedDouble(rawValue);
      int end = matcherDouble.end();
      return new InformationBundle<>(lqd, input.substring(end));
    }

    if (matcherSingle.find()) {
      String rawValue = matcherSingle.group(1);
      Literal lqs = new LiteralQualifiedSingle(rawValue);
      int end = matcherSingle.end();
      return new InformationBundle<>(lqs, input.substring(end));
    }

    if (matcherRaw.find()) {
      String rawValue = matcherRaw.group(0);
      Literal lr = new LiteralRaw(rawValue);
      int end = matcherRaw.end();
      return new InformationBundle<>(lr, input.substring(end));
    }

    throw new SyntaxException("Error during string literal parsing");
  }

  /**
   * Метод пропускает пробелы в литерале
   */
  @NotNull
  private String purgeSpaces(@NotNull String input) {
    Matcher matcherEmpty = EMPTY_REGEX.matcher(input);

    if (matcherEmpty.find()) {
      int end = matcherEmpty.end();
      return input.substring(end);
    } else {
      return input;
    }
  }

  /**
   * Специальная проверка строки на пустоту. Метод isEmpty класса String не подходит
   * в данной ситуации, так как нам нужно убедиться, что строка пустая в
   * семантическом смысле, т.е. она пробельная, то есть удовлетворяет
   * регулярному выражению "<tt>\s*</tt>"
   *
   * @param s тестируемая строка
   * @return true, если строка является пустой (населённой пробелами)
   */
  private boolean isEmpty(String s) {
    Matcher matcherEmpty = NONEMPTY_REGEX.matcher(s);
    return !matcherEmpty.find();
  }
}
