package ru.spb.kry127.yacli;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TestRunnerTest {

    private Logger log;

    @Before
    public void setUp() {
        log = Logger.getLogger(TestRunnerTest.class.getName());
        log.log(Level.WARNING, "Set up complete!");
    }

    @After
    public void tearDown() {
        log.log(Level.WARNING, "Tearing down...");
    }

    @Test
    public void main() {
        log.log(Level.FINE, "Main test working...");
        assertTrue("WOW, STOP", false);
        log.log(Level.FINE, "All ok :)");
    }
}