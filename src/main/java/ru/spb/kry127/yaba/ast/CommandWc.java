package ru.spb.kry127.yaba.ast;

import ru.spb.kry127.yaba.io.InputStreamProxy;

import java.io.*;
import java.text.MessageFormat;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Команда wc в UNIX выполняет подсчёт количества байтов,
 * слов и строк.
 *
 * @author Ernest Davis
 * @link https://cs.nyu.edu/faculty/davise/DataStructures/SampleCode/wc.java
 */
public class CommandWc extends Command {

    private final static String OUTPUT_PATTERN = "\t\t{0}\t\t{1}\t\t{2}\t\t{3}";
    private final static String ERROR_PATTERN = "{0}: {1}: No such file or directory";

    protected CommandWc(LiteralConcat name) {
        super(name);
    }

    /**
     * Callback-механизм для вызова wc на нескольких файлах
     * для вывода секции Total
     */
    private interface InfoCallback {
        void transfer(int lineCount, int wordCount, int numBytes);
    }

    @Override
    public void execute(InputStreamProxy in, PrintStream out, PrintStream err) throws IOException {
        final String[] argv = getArgv();
        final PrintStream pws = new PrintStream(out);
        final PrintStream eps = new PrintStream(err);
        if (argv.length == 0) {
            wc(in, out, "", null);
            return;
        }
        if (argv.length == 1) {
            try (InputStream fis = new FileInputStream(argv[0])) {
                wc(fis, out, argv[0], null);
            } catch (FileNotFoundException e) {
                String errMessage = MessageFormat.format(
                        ERROR_PATTERN,
                        getCommandName(), argv[0]);
                eps.print(errMessage);

            }
            return;
        }

        AtomicInteger totalLines = new AtomicInteger();
        AtomicInteger totalWords = new AtomicInteger();
        AtomicInteger totalBytes = new AtomicInteger();
        for (String s : argv) {
            try (InputStream fis = new FileInputStream(argv[0])) {
                wc(fis, out, s, (int lc, int wc, int bc) -> {
                    totalLines.addAndGet(lc);
                    totalWords.addAndGet(wc);
                    totalBytes.addAndGet(bc);
                });
            } catch (FileNotFoundException e) {
                String errMessage = MessageFormat.format(
                        ERROR_PATTERN,
                        getCommandName(), s);
                eps.print(errMessage);

            }
        }

        String countMsg = MessageFormat.format(
                OUTPUT_PATTERN, totalLines.get(), totalWords.get(), totalBytes.get(), "total");
        pws.println(countMsg);


    }


    private void wc(InputStream in, PrintStream out,
                    String filename, InfoCallback callback) {

        Scanner sc = new Scanner(in);
        int numWords = 0;
        int numLines = 0;
        int numBytes = 0;
        String line;

        while (sc.hasNextLine()) {
            line = sc.nextLine(); // read a line
            numBytes += line.length();
            if (sc.hasNextLine()) {
                numLines++;
                // TODO do we need this?
                // numBytes += 1;
            }
            Scanner sw = new Scanner(line);  // scan through the line
            while (sw.hasNext()) {
                sw.next(); // take a token off the line
                numWords++;
            }                  //  end loop over words
        }

        String countMsg = MessageFormat.format(
                OUTPUT_PATTERN, numLines, numWords, numBytes, filename);
        out.println(countMsg);
        if (callback != null) {
            callback.transfer(numLines, numWords, numBytes);
        }
    }
}
