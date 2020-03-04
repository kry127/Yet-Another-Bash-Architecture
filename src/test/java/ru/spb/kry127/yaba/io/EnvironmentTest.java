package ru.spb.kry127.yaba.io;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class EnvironmentTest {

  static Environment empty_environment;
  static Environment default_environment;

  @BeforeClass
  public static void setUp() {
    default_environment = EnvironmentProvider.getEnvironment();
    empty_environment = EnvironmentProvider.getEnvironment();
  }

  @Test
  public void getEnvVariable_check_somethingIsAbsent() {
    assertEquals("", empty_environment.getEnvVariable("something"));
  }


  @Test
  public void getEnvVariable_checkNonEmpty_PATH() {
    assertNotEquals("", empty_environment.getEnvVariable("PATH"));
  }

  @Test
  public void getEnvVariable_checkDefault_something() {
    assertEquals("", default_environment.getEnvVariable("something"));
  }

  @Test
  public void getEnvVariable_checkSingleton() {
    assertEquals(default_environment, empty_environment);
    assertEquals(default_environment, EnvironmentProvider.getEnvironment());
  }


  @Test
  public void getEnvVariable_checkSingleton2() {
    assertEquals(default_environment.getEnvVariable("OS"), empty_environment.getEnvVariable("OS"));
  }

  @Test
  public void setEnvVariable_singletonCheck() {
    default_environment.setEnvVariable("PS2", " >>>");
    assertEquals(" >>>", empty_environment.getEnvVariable("PS2"));
  }

  @Test
  public void getFullEnvironment() {

    Map<String, String> fullEnv = empty_environment.getFullEnvironment();
    for( String key : empty_environment.getFullEnvironment().keySet()) {
      assertEquals(fullEnv.get(key), empty_environment.getEnvVariable(key));
    }
  }
}