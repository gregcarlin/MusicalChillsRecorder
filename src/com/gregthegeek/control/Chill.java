package com.gregthegeek.control;

public class Chill {
  private final long start;
  private final long stop;
  
  public Chill(long start, long stop) {
    this.start = start;
    this.stop = stop;
  }
  
  public long getStart() {
    return start;
  }
  
  public long getStop() {
    return stop;
  }
}
