package com.newrelic.codingchallenge;

import java.io.*;
import java.net.*;
import java.util.Random;

public class DataClient implements Runnable {
  Socket socket;
  PrintWriter dout;
  BufferedReader din;

  Thread thread;

  DataClient() throws IOException {
    try {
      socket = new Socket("127.0.0.1", 4000);
      dout = new PrintWriter(socket.getOutputStream(), true);
      din = new BufferedReader(new InputStreamReader(System.in));
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    thread = new Thread(this);
    thread.start();
  }

  @Override
  public void run() {
    Random rand = new Random();
    int n = 100000000;

    while(n < 1000000000) {
        n = rand.nextInt(900000000) + 100000000;
        dout.println(n);
        System.out.println(n++);
    }

    shutdown_client();
  }

  private void shutdown_client() {
    try {
      dout.close();
      din.close();
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.println("Connecting to server ....");
    DataClient dataClient = new DataClient();
  }
}
