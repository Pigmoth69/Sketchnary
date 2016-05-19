package gameEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import server.Player;

public class GameRoom implements Runnable {
  private Thread t;
  private String threadName;
  private HashMap<Integer, Integer> leaderboard;
  private ArrayList<String> words;
  private int rounds;

  public GameRoom( String name, int rounds){
    threadName = name;
    this.words = new ArrayList<String>();
    this.leaderboard = new HashMap<Integer, Integer>();
    this.rounds = rounds;
  }

  public void run() {
	  
	for(int i = 0; i < this.rounds; i++){
		
		String currentDraw = randomWord();
		
		/*
		if (alguém_acertou){
			atualizaPontuação();
			continue;
		}
		*/
		
	}
    
    //System.out.println("Thread " +  threadName + " exiting.");
  }

  public void start (){
    
    if (t == null){
      t = new Thread (this, threadName);
      t.start ();
    }
  }

  public boolean connectPlayer(Player player){
	  if (this.leaderboard.size() >= 10)
		  return false;
	  else{
		  this.leaderboard.put(player.getId(), 0);
		  return true;
	  }
  }
  
  
  public void generateWordList(String filename){
	  this.words.clear();
	  String  thisLine;
	  try{
		  // open input stream test.txt for reading purpose.
		  BufferedReader br = new BufferedReader(new FileReader(filename));
		  
		  while ((thisLine = br.readLine()) != null) {
			  this.words.add(thisLine);
		  }       
		  
	  }catch(Exception e){
		  e.printStackTrace();
	  }
  }
  
  public String randomWord(){
	  Random r = new Random();
	  int val = r.nextInt(this.words.size());
	  String s = this.words.get(val);
	  return s;
  }
  
  
  
}
