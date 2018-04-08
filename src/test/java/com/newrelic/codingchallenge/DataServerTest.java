package com.newrelic.codingchallenge;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataServerTest {
  @Test
  public void test_is_valid() {
    assertFalse(DataServer.is_valid(null));
    assertFalse(DataServer.is_valid(""));
    assertFalse(DataServer.is_valid("00000000."));
    assertFalse(DataServer.is_valid("1234567890"));
    assertTrue(DataServer.is_valid("000000000"));
    assertTrue(DataServer.is_valid("123456789"));
  }
}