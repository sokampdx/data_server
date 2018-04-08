package com.newrelic.codingchallenge;

import java.io.*;
import java.net.*;
import java.util.Random;

public class DataClient implements Runnable {
  private static final int MAX_CLIENT_ATTEMPT = 10;
  private Socket socket;
  private PrintWriter dout;
  private BufferedReader din;
  private Thread thread;

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

    while(n < 999999000) {
        n = rand.nextInt(900000000) + 100000000;
        //System.out.println(n);
        dout.println(n++);
    }
    dout.println(999999999);
    dout.println(0);
    //dout.println(DataValidator.TERMINATE_ALL_CONNECTION);
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
    for (int i = 0; i < MAX_CLIENT_ATTEMPT; ++i) {
      DataClient dataClient = new DataClient();
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
