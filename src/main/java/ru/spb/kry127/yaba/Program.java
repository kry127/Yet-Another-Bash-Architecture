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
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Класс программы, в котором находится стартовая точка исполнения кода.
 */
public class Program implements CommandPipeline {

    private final static String PS1 = " YABA >> ";

    private final SystemReader systemReader;
    private final Environment environment;
    private final Parser parser;

    private final Logger logger;

    private Program(SystemReader systemReader, Environment environment, Parser parser, boolean logging) {
        // определяем, какие реализации интерфейсов будем использовать в программе
        this.systemReader = systemReader;
        this.environment = environment;
        this.parser = parser;
        // также заводим логгер
        logger = Logger.getLogger(Program.class.getName());
        if (!logging) {
            // если необходимо отключить логгинг глобально, можно использовать:
            LogManager.getLogManager().reset();
        }

    }

    /**
     * Метод исполняется по одному разу для каждой строки
     *
     * @throws SyntaxException          В процессе выполнения произошло синтаксическое исключение
     * @throws IOException              В процессе работы произошло исключение в подсистеме ввода вывода
     *                                  (кидается только в основном потоке)
     * @throws CommandNotFoundException Внешняя команда оказалась не найденной
     *                                  (кидается только в основном потоке)
     */
    private void readEvalPrint() throws SyntaxException, IOException, CommandNotFoundException {
        // Выводим prompt
        PrintStream ps = systemReader.getOutStream();
        ps.println(PS1); // we cannot flush print :((
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

    /**
     * Функция основного цикла нашего REPL-интерпретатора
     */
    @Override
    public void run() {
        PrintStream errOutput = systemReader.getErrStream();
        while (true) {
            try {
                readEvalPrint();
            } catch (SyntaxException | IOException | CommandNotFoundException e) {
                errOutput.println(e.getMessage());
            } catch (NoSuchElementException e) {
                // Возникает, когда консоль закрывается
                logger.log(Level.WARNING, e.toString());
                System.exit(1);
            }
        }
    }

    /**
     * Точка входа в программу.
     *
     * @param args Аргументы командной строки. Игнорируются.
     */
    public static void main(String[] args) {
        final SystemReader systemReader = SystemReaderProvider.getSystemReader();
        final Environment environment = EnvironmentProvider.getEnvironment();
        final Parser parser = new ParserLL(environment);
        Program mainProgram = new Program(systemReader, environment, parser, false);
        mainProgram.run(); // run, forest, run
    }

}
