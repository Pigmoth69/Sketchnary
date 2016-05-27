package gameEngine;

import java.util.ArrayList;
import java.util.HashMap;

import data.Player;

public class GameRoom implements Runnable {

	private Category category;
	private Thread t;

	private String id;
	private String threadName;
	private String catg;
	private Player drawer;
	private String winner;
	private int tries = 0;
	private Boolean on = false;

	private HashMap<Integer, Integer> leaderboard;
	private ArrayList<Player> players;
	private ArrayList<String> wordsList;
	private ArrayList<TcpGuesser> guessers;

	public GameRoom(String id, String name) {
		category = new Category();
		threadName = name;

		this.id = id;
		this.drawer = null;
		this.winner = null;

		this.leaderboard = new HashMap<Integer, Integer>();
		this.players = new ArrayList<Player>();
		this.wordsList = new ArrayList<String>();
		this.guessers = new ArrayList<TcpGuesser>();
	}

	public void run() {
		
		while (winner == null) {
			if (players.size() < 2){
				System.out.println("merda");
				continue;
			}
			else if (players.size() >= 2 && tries >= 1){
				TcpDrawer drawer = new TcpDrawer(this, players.get(0));
				connectClients();
				drawer.start();
				System.out.println("end");
			}
			else {

				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				tries++;
			}

		}

	}

	private void connectClients() {

		for (int i = 0; i < players.size(); i++) {
			if(!(players.get(i).getEmail().equals(drawer.getEmail()))){
				TcpGuesser guesser = new TcpGuesser(players.get(i));
				guessers.add(guesser);
				guesser.start();
				System.out.println("[GAME ROOM] Starting guesser thread");
			}
		}

	}

	public void start() {

		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
		on = true;
	}

	public void setupTcp() {

	}

	public boolean connectPlayer(Player player) {
		if (this.leaderboard.size() >= 10)
			return false;
		else {
			this.leaderboard.put(player.getId(), 0);
			return true;
		}
	}

	public String getID() {
		return id;
	}

	public void setDrawer(Player player) {
		drawer = player;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void addPlayer(Player player) {
		players.add(player);
	}

	public String generateWord() {
		catg = category.getRandomCategory();
		return catg;
	}

	public ArrayList<String> generateWordList() {

		ArrayList<String> words = new ArrayList<String>();

		String cat1;
		String cat2;
		String cat3;

		cat1 = catg;
		cat2 = category.getRandomCategory();
		cat3 = category.getRandomCategory();

		while (cat2.equals(catg))
			cat2 = category.getRandomCategory();
		while (cat3.equals(catg))
			cat3 = category.getRandomCategory();

		words.add(cat1);
		words.add(cat2);
		words.add(cat3);

		wordsList.add(cat1);
		wordsList.add(cat2);
		wordsList.add(cat3);

		return words;

	}

	public Player getDrawer() {
		return drawer;
	}

	public ArrayList<TcpGuesser> getGuessers() {
		return guessers;
	}
	
	public Boolean isOff(){
		return !on;
	}

}
