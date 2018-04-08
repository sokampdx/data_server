package com.newrelic.codingchallenge;

public class StatusUpdater implements Runnable{
  private static final long UPDATE_INTERVAL = 5000;
  private boolean terminate = false;
  private int total_unique = 0;
  private int total_new_duplicate = 0;
  private int total_new_unique =0;
  private Thread thread;

  StatusUpdater() {
    thread = new Thread(this);
    thread.start();
  }

  public void update_new(int new_duplicate, int new_unique) {
    total_new_duplicate += new_duplicate;
    total_new_unique += new_unique;
  }

  public void shutdown() {
    terminate = true;
  }

  private void update_total() {
    total_unique += total_new_unique;
  }

  private void printStatus() {
    update_total();
    System.out.println("Received " + total_new_unique + " unique numbers, " + total_new_duplicate + " duplicates. Unique total: " + total_unique);
    reset_new();
  }

  private void reset_new() {
    total_new_duplicate = 0;
    total_new_unique = 0;
  }

  @Override
  public void run() {
    while(!terminate) {
      try {
        synchronized (this) {
          this.wait( UPDATE_INTERVAL);
          printStatus();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
