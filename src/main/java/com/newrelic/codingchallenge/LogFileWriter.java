package com.newrelic.codingchallenge;

import java.io.*;
import java.util.Set;

public class LogFileWriter {
  private static final String FILENAME = "number.log";

  public static void reset() throws IOException {
    FileWriter fileWriter = new FileWriter(FILENAME);
    fileWriter.close();
  }

  public static void write(Set content) throws IOException {
    FileWriter fileWriter = new FileWriter(FILENAME, true);
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

    for (Object value : content) {
      bufferedWriter.write(value.toString());
      bufferedWriter.newLine();
    }

    bufferedWriter.close();
    fileWriter.close();
  }
}