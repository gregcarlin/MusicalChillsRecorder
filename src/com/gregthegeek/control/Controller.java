package com.gregthegeek.control;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.gregthegeek.gui.View;

public class Controller {
  private final HashMap<String, List<Record>> users = new HashMap<String, List<Record>>();
  private final View view;
  private long lastPress;
  
  public Controller(View view) {
      this.view = view;
  }
  
  public void onButtonPress(String user) {
    lastPress = System.currentTimeMillis();
  }
  
  public void onButtonRelease(String user) {
    addChill(user, view.getUserName(), lastPress, System.currentTimeMillis());
  }
  
  private void addChill(String user, String song, long start, long stop) {
      getRecord(user, song).addChill(new Chill(start, stop));
  }
  
  private Record getRecord(String user, String song) {
	  List<Record> records = getRecords(user);
	  for(Record r : records) {
		  if(r.getSong().equalsIgnoreCase(song)) {
			  return r;			  
		  }
	  }
	  
	  Record r = new Record(song);
	  records.add(r);
	  return r;
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
