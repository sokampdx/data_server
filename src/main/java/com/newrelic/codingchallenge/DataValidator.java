package com.newrelic.codingchallenge;

public class DataValidator {
  private static final String TERMINATE_ALL_CONNECTION = "terminate";

  public static boolean is_terminate(String line) {
    return line.equals(TERMINATE_ALL_CONNECTION);
  }

  public static boolean is_valid(String line) {
    return line != null && line.matches("\\d{9}");
  }
}
