package com.newrelic.codingchallenge;

import java.io.*;
import java.util.Set;

public class LogFileWriter {
  private static final String FILENAME = "number.log";
  private static final String NEW_LINE = System.getProperty("line.separator");

  LogFileWriter() throws IOException {
    reset();
  }

  private void reset() throws IOException {
    FileWriter fileWriter = new FileWriter(FILENAME);
    fileWriter.close();
  }

  public void write(Set<String> content) throws IOException {
    FileWriter fileWriter = new FileWriter(FILENAME, true);
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

    String value = String.join(NEW_LINE, content) + NEW_LINE;
    bufferedWriter.write(value);

    bufferedWriter.close();
    fileWriter.close();
  }
}