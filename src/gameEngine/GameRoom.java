package gameEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import data.Player;

public class GameRoom implements Runnable {

	private Category category;
	private Thread t;

	private String id;
	private String threadName;
	private int tcp_port;
	private String catg;
	private Player drawer;

	private HashMap<Integer, Integer> leaderboard;
	private ArrayList<Player> players;

	public GameRoom(String id, String name, int tcp_port) {
		category = new Category();
		threadName = name;

		this.id = id;
		this.tcp_port = tcp_port;
		this.drawer = null;

		this.leaderboard = new HashMap<Integer, Integer>();
		this.players = new ArrayList<Player>();
	}

	public void run() {

	}

	public void start() {

		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	public void setPort(int port) {
		this.tcp_port = port;
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
		
		while(cat2.equals(catg))
			cat2 = category.getRandomCategory();
		while(cat3.equals(catg))
			cat3 = category.getRandomCategory();

		words.add(cat1);
		words.add(cat2);
		words.add(cat3);

		return words;

	}

	public Player getDrawer() {
		return drawer;
	}

}
