package ru.spb.kry127.yaba.ast;

import ru.spb.kry127.yaba.exceptions.CommandNotFoundException;
import ru.spb.kry127.yaba.io.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CommandCat implements Command {
    Literal[] args;

    final static String NAME = "cat";

    @Override
    public void setArgs(Literal[] args) {
        this.args = args;
    }
}
