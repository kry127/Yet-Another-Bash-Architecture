package ru.spb.kry127.yaba.io;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.Assert.*;

public class OsUtilsTest {

  static OsUtils osUtils;

  @BeforeClass
  public static void setUp() {
    osUtils = OsUtilsProvider.getUtilsProvider();
  }

  @Test
  public void redirectIOStreams() throws IOException {

    PipedOutputStream pipout1 = new PipedOutputStream();
    PipedInputStream pipin1 = new PipedInputStream(pipout1, 8192);
    BufferedInputStream br = new BufferedInputStream(pipin1);

    PipedOutputStream pipout2 = new PipedOutputStream();
    PipedInputStream pipin2 = new PipedInputStream(pipout2, 8192);
    PrintStream pipout2w = new PrintStream(pipout2);

    // launch in parallel
    new Thread(() -> {
      osUtils.redirectIOStreams(pipin2, pipout1);
    }) .start();


    Scanner sc = new Scanner(br);
    final String sendingStr = "hello, world!";
    pipout2w.println(sendingStr);
    final String recvString = sc.nextLine();

    assertEquals(sendingStr, recvString);
  }

  @Test
  public void ioStreamsRedirector() throws IOException {

    PipedOutputStream pipout1 = new PipedOutputStream();
    PipedInputStream pipin1 = new PipedInputStream(pipout1, 8192);
    BufferedInputStream br = new BufferedInputStream(pipin1);

    PipedOutputStream pipout2 = new PipedOutputStream();
    PipedInputStream pipin2 = new PipedInputStream(pipout2, 8192);
    PrintStream pipout2w = new PrintStream(pipout2);

    // launch in parallel
    new Thread( osUtils.ioStreamsRedirector(pipin2, pipout1, "fine redirector")).start();


    Scanner sc = new Scanner(br);
    final String sendingStr = "I a the broken pipe :) Say me goodnight";
    pipout2w.println(sendingStr);
    final String recvString = sc.nextLine();

    assertEquals(sendingStr, recvString);
  }

  @Test
  public void checkProgramExists_LinuxWhich() throws IOException {
    assertNotNull(osUtils.checkProgramExists("which"));
  }

//  @Test
//  public void checkProgramExists_WindowsWhere() throws IOException {
//    assertNotNull(osUtils.checkProgramExists("where"));
//  }


  @Test
  public void checkProgramExists_LinuxPwd() throws IOException {
    assertNotNull(osUtils.checkProgramExists("pwd"));
  }

  @Test
  public void checkProgramExists_git() throws IOException {
    assertNotNull(osUtils.checkProgramExists("git"));
  }

  @Test
  public void checkProgramExists_WindowsNotepad() throws IOException {
    assertNotNull(osUtils.checkProgramExists("notepad"));
  }

  @Test
  public void checkProgramExists_LinuxBash() throws IOException {
    assertNotNull(osUtils.checkProgramExists("bash"));
  }

//  @Test
//  public void checkProgramExists_WindowsCmd() throws IOException {
//    assertNotNull(osUtils.checkProgramExists("cmd"));
//  }

  @Test
  public void checkProgramExists_Docker() throws IOException {
    assertNotNull(osUtils.checkProgramExists("docker"));
  }
}