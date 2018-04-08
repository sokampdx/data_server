package com.newrelic.codingchallenge;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataServer {
  private static final int MAX_CONNECTION = 5;
  private static int connections = 0;
  private static final Set<String> totalSet = new HashSet<>();
  private static final StatusUpdater status_updater = new StatusUpdater();
  private static LogFileWriter log_writer;

  DataServer() throws IOException {
    ServerSocket server = new ServerSocket(4000);
    log_writer = new LogFileWriter();

    while(true) {
      if (connections < MAX_CONNECTION) {
        create_client(server);
      }
    }
  }

  private void create_client(ServerSocket server) throws IOException {
    ++connections; //use threadpool
    Socket client = server.accept();
    AcceptClient acceptClient = new AcceptClient(client);
  }

  private class AcceptClient extends Thread {
    Socket ClientSocket;
    BufferedReader din;
    List<String> currentList;

    AcceptClient(Socket client) throws IOException {
      din = create_client_buffer_reader(client);
      start();
    }

    @Override
    public void run() {
      currentList = new ArrayList<>();
      try {
        String line = din.readLine();

        while (DataValidator.is_valid(line)) {
          currentList.add(line);
          //System.out.println(Thread.currentThread().getId() + ":" + line);
          line = din.readLine();
        }

        if (DataValidator.is_terminate(line)) {
          shutdown_all();
        } else {
          process_client_data(currentList);
        }

        shutdown_client();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private BufferedReader create_client_buffer_reader(Socket client) throws IOException {
      ClientSocket = client;
      return new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
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
      --connections;
    }
  }

  private void shutdown_all() {
    // TODO: terminate all connections
    status_updater.shutdown();
  }

  public static void main(String[] args) throws IOException {
    System.out.println("Starting up server ....");
    DataServer dataServer = new DataServer();
  }
}
