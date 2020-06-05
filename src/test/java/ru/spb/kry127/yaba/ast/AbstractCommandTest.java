package ru.spb.kry127.yaba.ast;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.exceptions.SyntaxException;
import ru.spb.kry127.yaba.io.Environment;
import ru.spb.kry127.yaba.io.EnvironmentProvider;
import ru.spb.kry127.yaba.io.InputStreamProxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

public abstract class AbstractCommandTest {

    private static Parser parser;
    private static Environment env;

    @BeforeClass
    public static void setUp() {
        env = EnvironmentProvider.getEnvironment();
        parser = new ParserLL(env);
    }

    protected static InputStreamProxy nullStream() {
        return new InputStreamProxy(new ByteArrayInputStream(new byte[0]));
    }

    protected static Expression getExpression(String cliLine) throws SyntaxException {
        return parser.parseExpression(cliLine);
    }

    protected static String executeWithoutInputAndGetOutputs(Executable exe)
            throws IOException, CommandNotFoundException {
        try (InputStreamProxy isp = nullStream()) {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                try (PrintStream ps = new PrintStream(os)) {
                    exe.execute(isp, ps, ps);
                    return os.toString();
                }
            }
        }
    }

}
