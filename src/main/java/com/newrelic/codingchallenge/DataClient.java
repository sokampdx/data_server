package com.newrelic.codingchallenge;

import java.io.*;
import java.net.*;
import java.util.Random;

public class DataClient implements Runnable {
  private static final int INITIAL_VALUE = 100000000;
  private static final int BOUND = 900000000;

  private static final int MAX_CLIENT_ATTEMPT = 40;
  private static final long SLEEP_TIME_BETWEEN_THREAD = 500;
  private static final int TERMINATION_VALUE = 999990000;

  private Socket socket;
  private PrintWriter dout;
  private BufferedReader din;
  private Thread thread;

  DataClient() throws IOException {
    socket = new Socket("127.0.0.1", 4000);
    dout = new PrintWriter(socket.getOutputStream(), true);
    din = new BufferedReader(new InputStreamReader(System.in));

    thread = new Thread(this);
    thread.start();
  }

  @Override
  public void run() {
    Random rand = new Random();
    int n = INITIAL_VALUE;
    System.out.println("START : " + thread.getId());

    while(n < TERMINATION_VALUE) {
        n = rand.nextInt(BOUND) + INITIAL_VALUE;
        //System.out.println(n);
        dout.println(n++);
    }

    if (n % 10 == 0) {
      dout.println(DataValidator.TERMINATE_ALL_CONNECTION);
      System.out.println(">>> " + DataValidator.TERMINATE_ALL_CONNECTION.toUpperCase() + " : " + thread.getId());
    } else {
      dout.println(0);
      dout.println(999999999);
    }

    System.out.println("EXIT : " + thread.getId());
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
      System.out.println("Dispatched : " + i);
      DataClient dataClient = new DataClient();
      try {
        Thread.sleep(SLEEP_TIME_BETWEEN_THREAD);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
