package com.newrelic.codingchallenge;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataServer {
  static final int MAX_CONNECTION = 5;
  static int connections = 0;
  static Set<String> totalSet = new HashSet<>();

  DataServer() throws IOException {
    ServerSocket server = new ServerSocket(4000);
    LogFileWriter.reset();

    while(true) {
      if (connections < MAX_CONNECTION) {
        create_client(server);
      }
    }
  }

  private void create_client(ServerSocket server) throws IOException {
    ++connections;
    Socket client = server.accept();
    AcceptClient acceptClient = new AcceptClient(client);
  }

  private class AcceptClient extends Thread {
    Socket ClientSocket;
    BufferedReader din;
    LogFileWriter logFileWriter;
    List<String> currentList = new ArrayList<>();
    Set<String> currentSet;
    int num_duplicate = 0;
    int num_unique = 0;

    private BufferedReader create_client_buffer_reader(Socket client) throws IOException {
      ClientSocket = client;
      return new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
    }

    AcceptClient(Socket client) throws IOException {
      din = create_client_buffer_reader(client);
      start();
    }

    public void run() {
      try {
        String line = din.readLine();
        while (is_valid(line)) {
          currentList.add(line);
          //System.out.println(Thread.currentThread().getId() + ":" + line);
          line = din.readLine();
        }

        currentSet = new HashSet<>(currentList);
        currentSet.removeAll(totalSet);
        totalSet.addAll(currentSet);

        logFileWriter.write(currentSet);
        num_duplicate = currentList.size() - currentSet.size();

        System.out.println("Received " + currentSet.size() + " unique numbers, " + num_duplicate + " duplicates. Unique total: " + totalSet.size());
        shutdown_client();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void shutdown_client() throws IOException {
      din.close();
      ClientSocket.close();
      --connections;
    }
  }

  static boolean is_valid(String line) {
    return line != null && line.matches("\\d{9}");
  }

  public static void main(String[] args) throws IOException {
    System.out.println("Starting up server ....");
    DataServer dataServer = new DataServer();
  }

  private static class LogFileWriter {
    private static final String FILENAME = "number.log";

    public static void reset() throws IOException {
      FileWriter fileWriter = new FileWriter(FILENAME);
      fileWriter.close();
    }

    public static void write(Set content) throws IOException {
      FileWriter fileWriter = new FileWriter(FILENAME, true);
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

      for (Object value : content) {
        bufferedWriter.write((String) value);
        bufferedWriter.newLine();
      }

      bufferedWriter.close();
      fileWriter.close();
    }
  }
}
