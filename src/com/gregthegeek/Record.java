package com.gregthegeek;

import java.util.ArrayList;

public class Record {
  private final String song;
  private final ArrayList<Chill> chills = new ArrayList<Chill>();
  
  public Record(String song) {
    this.song = song;
  }
  
  public void addChill(Chill chill) {
    chills.add(chill);
  }
  
  public String getSong() {
    return song;
  }
  
  public Chill[] getChills() {
    return chills.toArray(new Chill[0]);
  }
}
