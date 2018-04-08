package com.newrelic.codingchallenge;

import java.util.concurrent.atomic.AtomicInteger;

public class StatusUpdater implements Runnable{
  private static final long UPDATE_INTERVAL = 5000;
  private boolean terminate = false;

  private AtomicInteger total_new_duplicate = new AtomicInteger(0);
  private AtomicInteger total_new_unique = new AtomicInteger(0);
  private AtomicInteger total_unique = new AtomicInteger(0);

  private Thread thread;

  StatusUpdater() {
    thread = new Thread(this);
    thread.start();
  }

  public void update_new(int new_duplicate, int new_unique) {
    total_new_duplicate.addAndGet(new_duplicate);
    total_new_unique.addAndGet(new_unique);
  }

  public void shutdown() {
    terminate = true;
  }

  private void printStatus() {
    synchronized (this) {
      total_unique.addAndGet(total_new_unique.get());
      System.out.println("Received " + total_new_unique.get() + " unique numbers, " + total_new_duplicate.get() + " duplicates. Unique total: " + total_unique.get());
    }
    reset_new();
  }

  private void reset_new() {
    total_new_duplicate.set(0);
    total_new_unique.set(0);
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
