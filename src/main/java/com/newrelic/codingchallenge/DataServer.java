package com.newrelic.codingchallenge;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataServer {
  private static final int PORT = 4000;
  private static final int MAX_CONNECTION = 5;
  private static final ExecutorService executor = Executors.newFixedThreadPool(MAX_CONNECTION);

  private static final StatusUpdater status_updater = new StatusUpdater();
  private static LogFileWriter log_writer;

  private static final Set<String> totalSet = new HashSet<>();
  private static Boolean shutdown = false;

  DataServer() throws IOException {
    ServerSocket server = new ServerSocket(PORT);
    log_writer = new LogFileWriter();

    while(!shutdown) {
      executor.execute(new AcceptClient(server.accept()));
    }

    shutdown_all();
    server.close();
    System.exit(0);
  }

  private class AcceptClient implements Runnable {
    private Socket ClientSocket;
    private BufferedReader din;
    private List<String> currentList;

    AcceptClient(Socket client) throws IOException {
      ClientSocket = client;
      din = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
    }

    @Override
    public void run() {
      currentList = new ArrayList<>();

      try {
        String line;
        for (line = din.readLine(); DataValidator.is_valid(line); line = din.readLine()) {
          currentList.add(line);
        }

        if (DataValidator.is_terminate(line)) {
          shutdown_server();
        } else {
          process_client_data(currentList);
          shutdown_client();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void process_client_data(List<String> currentList) throws IOException {
      Set<String> currentSet = new HashSet<>(currentList);

      synchronized (totalSet) {
        currentSet.removeAll(totalSet);
        totalSet.addAll(currentSet);
      }

      log_writer.write(currentSet);
      status_updater.update_new(currentList.size() - currentSet.size(), currentSet.size());
    }

    private void shutdown_client() throws IOException {
      din.close();
      ClientSocket.close();
    }
  }

  private void shutdown_server() {
    shutdown = true;
  }

  private void shutdown_all() {
    status_updater.shutdown();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> executor.shutdown()));
  }

  public static void main(String[] args) throws IOException {
    System.out.println("Starting up server ....");
    DataServer dataServer = new DataServer();
  }
}