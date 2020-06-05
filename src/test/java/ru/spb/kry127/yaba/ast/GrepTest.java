package ru.spb.kry127.yaba.ast;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.exceptions.SyntaxException;
import ru.spb.kry127.yaba.io.Environment;
import ru.spb.kry127.yaba.io.EnvironmentProvider;
import ru.spb.kry127.yaba.io.InputStreamProxy;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GrepTest extends AbstractCommandTest {

    @Test
    public void testWordAbsentInBuildGradle() throws SyntaxException, IOException, CommandNotFoundException {
        String s = "grep -w plugin build.gradle";
        CommandGrep grepCmd = (CommandGrep) getExpression(s); // cast should be OK

        String output = executeWithoutInputAndGetOutputs(grepCmd);
        assertThat(output, isEmptyOrNullString());
    }

    @Test
    public void testWordPresentInBuildGradle() throws SyntaxException, IOException, CommandNotFoundException {
        String s = "grep -w plugins build.gradle";
        CommandGrep grepCmd = (CommandGrep) getExpression(s); // cast should be OK

        String output = executeWithoutInputAndGetOutputs(grepCmd);
        assertThat(output, is("plugins {\n"));
    }


    @Test
    public void testHeadOfBuildGradle() throws SyntaxException, IOException, CommandNotFoundException {
        String s = "grep -A 2 plugin build.gradle";
        CommandGrep grepCmd = (CommandGrep) getExpression(s); // cast should be OK

        String output = executeWithoutInputAndGetOutputs(grepCmd);
        assertThat(output, is(
                "plugins {\n" +
                "    id 'java'\n" +
                "}\n"));
    }


    @Test
    public void testZeroHeadOfBuildGradle() throws SyntaxException, IOException, CommandNotFoundException {
        String s = "grep -A 0 plugin build.gradle";
        CommandGrep grepCmd = (CommandGrep) getExpression(s); // cast should be OK

        String output = executeWithoutInputAndGetOutputs(grepCmd);
        assertThat(output, is("plugins {\n"));
    }

    @Test(expected = CommandNotFoundException.class)
    public void testNegativeHeadOfBuildGradle() throws SyntaxException, IOException, CommandNotFoundException {
        String s = "grep -A -1 plugin build.gradle";
        CommandGrep grepCmd = (CommandGrep) getExpression(s); // cast should be OK

        String output = executeWithoutInputAndGetOutputs(grepCmd);
        assertThat(output, is("grep: -1: invalid context length argument\n"));
    }


    @Test(expected = CommandNotFoundException.class)
    public void testCorruptedCommandOnBuildGradle() throws SyntaxException, IOException, CommandNotFoundException {
        String s = "grep -A -2 build.gradle";
        CommandGrep grepCmd = (CommandGrep) getExpression(s); // cast should be OK

        String output = executeWithoutInputAndGetOutputs(grepCmd);
        assertThat(output, is("grep: -2: invalid context length argument\n"));
    }


    @Test(expected = CommandNotFoundException.class)
    public void testMissingArgumentOfBuildGradle() throws SyntaxException, IOException, CommandNotFoundException {
        String s = "grep -A plugin build.gradle";
        CommandGrep grepCmd = (CommandGrep) getExpression(s); // cast should be OK

        String output = executeWithoutInputAndGetOutputs(grepCmd);
        assertThat(output, is("grep: plugin: invalid context length argument\n"));
    }


    @Test(expected = IOException.class)
    public void testMissingFile() throws SyntaxException, IOException, CommandNotFoundException {
        String s = "grep -i ololo asdfg";
        CommandGrep grepCmd = (CommandGrep) getExpression(s); // cast should be OK

        String output = executeWithoutInputAndGetOutputs(grepCmd);
        assertThat(output, is("grep: asdfg: No such file or directory\n"));
    }

}
