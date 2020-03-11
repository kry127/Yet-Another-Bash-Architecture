package ru.spb.kry127.yaba.ast;

import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.exceptions.SyntaxException;
import ru.spb.kry127.yaba.io.InputStreamProxy;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Данная команда запускает 'grep' -- поиск подстрок с помощью регулярного выражения.
 * <p>
 * оддерживаемые ключи:
 * <ol>
 *   <li><b>-i</b> -- нечувствительность к регистру</li>
 *   <li><b>-w</b> -- поиск только слов целиком</li>
 *   <li><b>-A n</b> -- распечатать n строк после строки с совпадением</li>
 * </ol>
 */
public class CommandGrep extends Command {
    private final static Options options = new Options();

    static {
        options.addOption(new Option("i", "insensitive", false, "Case insensitive pattern matching."));
        options.addOption(new Option("w", "word", false, "Search for the whole words only."));
        options.addOption(new Option("A", "Ahead", true, "Print n lines ahead the matched lines."));
    }

    protected CommandGrep(@NotNull LiteralConcat name) {
        super(name);
    }

    @Override
    public void execute(InputStreamProxy in, PrintStream out, PrintStream err)
            throws CommandNotFoundException, IOException {

        // получаем вектор аргументов из родительского класса
        String[] args = getArgv();
        // парсим аргументы с помощью сторонней библиотеки
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            throw new CommandNotFoundException("Apache CLI parser error!", e);
        }

        // получаем необходимые параметры
        boolean caseInsensitive = cmd.hasOption("i");
        boolean onlyWords = cmd.hasOption("w");
        int lookAhead = -1;
        try {
            lookAhead = Integer.parseInt(cmd.getOptionValue("A", "0"));
        } catch (NumberFormatException nfe) {
            throw new CommandNotFoundException("Grep expects int at -A key");
        }


        // смотрим на остаток, которы остался неиспользованным
        List<String> argsRest = cmd.getArgList();
        String regExpString = argsRest.get(0);
        argsRest.remove(0);

        int regexCompilerOptions = 0;
        if (caseInsensitive) {
            regexCompilerOptions |= Pattern.CASE_INSENSITIVE;
        }
        if (onlyWords) {
            regExpString = "\\b" + regExpString + "\\b";
        }

        // исходный код взят отсюда:
        // https://www2.dmst.aueb.gr/dds/ismr/tools/jgrep.htm
        Pattern cre = null;        // Compiled RE
        try {
            cre = Pattern.compile(regExpString, regexCompilerOptions);
        } catch (PatternSyntaxException e) {
            err.println("Invalid RE syntax: " + e.getDescription());
            return;
        }

        // смотрим, если остались ещё аргументы -- интерпретируем их как файлы,
        // иначе, читаем из командной строки
        if (argsRest.isEmpty()) {
            BufferedReader buf_in = new BufferedReader(new InputStreamReader(in));
            processFile(buf_in, out, cre, caseInsensitive, onlyWords, lookAhead);
        } else {
            for (String file : argsRest) {
                BufferedReader buf_in = null;
                try {
                    buf_in = new BufferedReader(new InputStreamReader(
                            new FileInputStream(file)));
                    processFile(buf_in, out, cre, caseInsensitive, onlyWords, lookAhead);
                } catch (FileNotFoundException e) {
                    throw new IOException("Unable to open file " +
                            args[1] + ":( ", e);
                }
            }
        }
    }

    private static void processFile(BufferedReader buf_in, PrintStream out, Pattern cre,
                                    boolean caseInsensitive, boolean onlyWords, int lookAhead) throws IOException {
        String s;
        int cooldown = 0;
        while ((s = buf_in.readLine()) != null) {
            Matcher m = cre.matcher(s);
            if (m.find()) {
                out.println(s);
                cooldown = lookAhead;
            } else if (cooldown > 0) {
                out.println(s);
            }
            cooldown = (cooldown > 0) ? cooldown - 1 : 0;
        }
    }
}
