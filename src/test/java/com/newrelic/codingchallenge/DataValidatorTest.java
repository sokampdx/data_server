package com.newrelic.codingchallenge;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataValidatorTest {

  @Test
  public void is_terminate() {
    assertTrue(DataValidator.is_terminate("terminate"));
    assertFalse(DataValidator.is_terminate( "" ));
  }

  @Test
  public void is_valid() {
    assertFalse(DataValidator.is_valid(null));
    assertFalse(DataValidator.is_valid(""));
    assertFalse(DataValidator.is_valid("00000000."));
    assertFalse(DataValidator.is_valid("1234567890"));
    assertTrue(DataValidator.is_valid("000000000"));
    assertTrue(DataValidator.is_valid("123456789"));
  }
}