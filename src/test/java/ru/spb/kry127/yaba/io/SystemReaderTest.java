package ru.spb.kry127.yaba.io;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class SystemReaderTest {

  static SystemReader systemReader;

  @BeforeClass
  public static void setUp() {
    systemReader = SystemReaderProvider.getSystemReader();
  }

  @Test
  public void getInStream() {
    assertNotEquals(System.in, systemReader.getInStream());
  }

  @Test
  public void getOutStream() {
    assertEquals(System.out, systemReader.getOutStream());
  }

  @Test
  public void getErrStream() {
    assertEquals(System.err, systemReader.getErrStream());
  }
}