package com.newrelic.codingchallenge;

import java.util.concurrent.atomic.AtomicInteger;

public class StatusUpdater implements Runnable{
  private static final long UPDATE_INTERVAL = 5000;
  private boolean terminate = false;

  private AtomicInteger new_duplicate_counter = new AtomicInteger(0);
  private AtomicInteger new_unique_counter = new AtomicInteger(0);
  private AtomicInteger total_unique = new AtomicInteger(0);

  private Thread thread;

  StatusUpdater() {
    thread = new Thread(this);
    thread.start();
  }

  public void update_new(int new_duplicate, int new_unique) {
    new_duplicate_counter.addAndGet(new_duplicate);
    new_unique_counter.addAndGet(new_unique);
  }

  public void shutdown() {
    terminate = true;
  }

  private void printStatus() {
    total_unique.addAndGet(new_unique_counter.get());
    System.out.println("Received " + new_unique_counter.get() + " unique numbers, " + new_duplicate_counter.get() + " duplicates. Unique total: " + total_unique.get());
    reset_new_counters();
  }

  private void reset_new_counters() {
    new_duplicate_counter.set(0);
    new_unique_counter.set(0);
  }

  @Override
  public void run() {
    while(!terminate) {
      try {
        synchronized (this) {
          this.wait(UPDATE_INTERVAL);
          printStatus();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
