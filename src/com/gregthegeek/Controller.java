package com.gregthegeek;

import java.util.HashMap;
import java.util.List;

public class Controller {
  private final HashMap<String, List<Record>> users = new HashMap<String, List<Record>>();
  
  public void onButtonPress(String user) {
    // TODO
  }
  
  public void onButtonRelease(String user) {
    // TODO
  }
  
  private void addRecord(String user, String song, long start, long stop) {
    
  }
  
  private List<Record> getRecords(String user) {
    if(users.containsKey(user)) {
      return users.get(user);
    }
    List<Record> newList = new ArrayList<Record>();
    users.put(user, newList);
    return newList;
  }
}
