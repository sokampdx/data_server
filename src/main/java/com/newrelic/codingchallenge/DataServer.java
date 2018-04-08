package com.newrelic.codingchallenge;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataServer {
  private static final int MAX_CONNECTION = 5;
  private static final String TERMINATE_ALL_CONNECTION = "terminate";
  private static int connections = 0;
  private static Set<String> totalSet = new HashSet<>();

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
    List<String> currentList = new ArrayList<>();
    Set<String> currentSet;
    int current_duplicate = 0;

    private BufferedReader create_client_buffer_reader(Socket client) throws IOException {
      ClientSocket = client;
      return new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
    }

    AcceptClient(Socket client) throws IOException {
      din = create_client_buffer_reader(client);
      start();
    }

    @Override
    public void run() {
      try {
        String line = din.readLine();
        while (is_valid(line)) {
          currentList.add(line);
          //System.out.println(Thread.currentThread().getId() + ":" + line);
          line = din.readLine();
        }

        if (is_terminate(line)) {
          // TODO: terminate all connections
        } else {

          currentSet = new HashSet<>(currentList);
          currentSet.removeAll(totalSet);
          totalSet.addAll(currentSet);

          LogFileWriter.write(currentSet);
          current_duplicate = currentList.size() - currentSet.size();

          System.out.println("Received " + currentSet.size() + " unique numbers, " + current_duplicate + " duplicates. Unique total: " + totalSet.size());
        }

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

  private static boolean is_terminate(String line) {
    return line.equals(TERMINATE_ALL_CONNECTION);
  }

  private static boolean is_valid(String line) {
    return line != null && line.matches("\\d{9}");
  }

  public static void main(String[] args) throws IOException {
    System.out.println("Starting up server ....");
    DataServer dataServer = new DataServer();
  }
}
