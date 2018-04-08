package com.newrelic.codingchallenge;

import java.io.*;
import java.net.*;

public class DataServer {
  static final int MAX_CONNECTION = 5;
  static int connections = 0;

  DataServer() throws IOException {
    ServerSocket server = new ServerSocket(4000);

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
          System.out.println(Thread.currentThread().getId() + ":" + line);
          line = din.readLine();
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

  static boolean is_valid(String line) {
    return line != null && line.matches("\\d{9}");
  }

  public static void main(String[] args) throws IOException {
    System.out.println("Starting up server ....");
    DataServer dataServer = new DataServer();
  }
}
