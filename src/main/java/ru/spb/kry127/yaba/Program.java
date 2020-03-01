package ru.spb.kry127.yaba;

import ru.spb.kry127.yaba.ast.Executable;
import ru.spb.kry127.yaba.ast.ExecutableExpr;
import ru.spb.kry127.yaba.ast.Parser;
import ru.spb.kry127.yaba.ast.ParserLL;
import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.exceptions.SyntaxException;
import ru.spb.kry127.yaba.io.Environment;
import ru.spb.kry127.yaba.io.EnvironmentProvider;
import ru.spb.kry127.yaba.io.SystemReader;
import ru.spb.kry127.yaba.io.SystemReaderProvider;

import java.io.IOException;
import java.io.PrintStream;

public class Program implements CommandPipeline {

  private SystemReader systemReader;
  private Environment environment;
  private Parser parser;

  Program() {
    // определяем, какие реализации интерфейсов будем использовать в программе
    systemReader = SystemReaderProvider.getSystemReader();
    environment = EnvironmentProvider.getEnvironment();
    parser = new ParserLL(environment);
  }

  /**
   * Метод исполняется по одному разу для каждой строки
   *
   * @throws SyntaxException
   * @throws IOException
   * @throws CommandNotFoundException
   */
  private void readEvalPrint() throws SyntaxException, IOException, CommandNotFoundException {
    // читаем входную строку
    String line = systemReader.getLine();
    // разбираем её парсером
    ExecutableExpr predCmd = parser.parseExpression(line);
    // предкоманду один раз интерполируем переменными среды
    String interpolated = predCmd.interpolate(environment);
    // повторный разбор команды
    Executable cmd = parser.parseExpression(interpolated);
    // исполнение команды
    cmd.execute(
        systemReader.getInStream(),
        systemReader.getOutStream(),
        systemReader.getErrStream()
    );
  }

  @Override
  public void run() {
    PrintStream errOutput = systemReader.getErrStream();
    while (true) {
      try {
        readEvalPrint();
      } catch (SyntaxException | IOException | CommandNotFoundException e) {
        errOutput.println(e.getMessage());
      }
    }
  }

  /**
   * Точка входа в программу.
   *
   * @param args Аргументы командной строки. Игнорируются.
   */
  public static void main(String[] args) {
    Program mainProgram = new Program();
    mainProgram.run(); // run, forest, run
  }

}
