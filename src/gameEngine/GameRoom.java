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
	private int tries = 2;

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
		
		TcpDrawer drawer = new TcpDrawer(this, players.get(0));
		connectClients();
		
		while (winner == null) {
			if (players.size() < 0)
				continue;
			else if (players.size() > 0 && tries > 1){
				drawer.start();
			}
			else {

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				tries++;
			}

		}

	}

	private void connectClients() {

		for (int i = 0; i < players.size(); i++) {
			if(players.get(i) != drawer){
				TcpGuesser guesser = new TcpGuesser(players.get(i));
				guesser.start();
				guessers.add(guesser);
			}
		}

	}

	public void start() {

		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
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

}
