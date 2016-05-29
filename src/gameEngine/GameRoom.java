package gameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import data.Player;

public class GameRoom implements Runnable {

	private Category category;
	private Thread t;

	private String id;
	private String threadName;
	private String catg;
	private Player drawer;
	private Boolean on = false;

	private Map<String, String> playersGuess;
	private ArrayList<Player> players;
	private ArrayList<String> wordsList;
	private ArrayList<TcpGuesser> guessers;
	private ArrayList<Player> present;
	private ArrayList<Integer> forbidden;

	public GameRoom(String id, String name) {
		category = new Category();
		threadName = name;

		this.id = id;
		this.drawer = null;

		this.playersGuess = new HashMap<String, String>();
		this.players = new ArrayList<Player>();
		this.wordsList = new ArrayList<String>();
		this.guessers = new ArrayList<TcpGuesser>();
		this.present = new ArrayList<Player>();
		this.forbidden = new ArrayList<Integer>();
	}

	public void run() {
		startRoom();
		setupGame();
	}

	/**
	 * Hold whilst there are not enough players
	 */
	public void startRoom() {

		while (players.size() < 2) {

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void setupGame() {
		
		TcpDrawer drawer = new TcpDrawer(this, players.get(0));
		drawer.start();
		connectClients();
		System.out.println("end");

	}

	private void connectClients() {
		System.out.println("players " + players.size());
		System.out.println(players);
		for (int i = 0; i < players.size(); i++) {
			System.out.println("PLAYER EMAIL " + players.get(i).getEmail());
			if (!(players.get(i).getEmail().equals(drawer.getEmail()))) {

				System.out.println("aqui dentro");
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

	public String getCategory() {
		return catg;
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
	
	public ArrayList<Player> getPresent(){
		return present;
	}
	
	public void setPresent(Player pl){
		present.add(pl);
	}

	public ArrayList<String> generateWordList() {

		ArrayList<String> words = new ArrayList<String>();

		String cat1;
		String cat2;
		String cat3;
		String cat4;
		String cat5;

		cat1 = catg;
		cat2 = category.getRandomCategory();
		cat3 = category.getRandomCategory();
		cat4 = category.getRandomCategory();
		cat5 = category.getRandomCategory();

		while (cat2.equals(catg))
			cat2 = category.getRandomCategory();
		while (cat3.equals(catg) && cat3.equals(cat2))
			cat3 = category.getRandomCategory();
		while (cat4.equals(catg) && cat4.equals(cat2) && cat4.equals(cat3))
			cat4 = category.getRandomCategory();
		while (cat5.equals(catg) && cat5.equals(cat2) && cat5.equals(cat3) && cat5.equals(cat4))
			cat5 = category.getRandomCategory();

		if(getRand() == 1){
			words.add(cat4);
			words.add(cat1);
			words.add(cat5);
			words.add(cat2);
			words.add(cat3);
		}else if(getRand() == 2){
			words.add(cat5);
			words.add(cat4);
			words.add(cat3);
			words.add(cat1);
			words.add(cat2);
		}else{
			words.add(cat3);
			words.add(cat5);
			words.add(cat2);
			words.add(cat4);
			words.add(cat1);
		}

		wordsList.add(cat1);
		wordsList.add(cat2);
		wordsList.add(cat3);

		return words;

	}

	private int getRand() {
		return (1 + (int)(Math.random() * 5));
	}

	public Player getDrawer() {
		return drawer;
	}

	public ArrayList<TcpGuesser> getGuessers() {
		return guessers;
	}
	
	public Map<String, String> getPlayersGuess(){
		return playersGuess;
	}

	public Boolean isOff() {
		return !on;
	}
	
	public void playerGuessed(String guess, Player player){
		playersGuess.put(player.getEmail(), guess);
	}
	
	public void addPlayerToRoom(Player player){
		players.add(player);
	}
	
	public void exitRoom(Player player){
		present.remove(player);
	}

	public Boolean unsignDrawer() {
		
		for(int i = 0; i < players.size(); i++){
			if(players.get(i) != drawer){
				drawer = players.get(i);
				return true;
			}
		}
		
		return false;
		
	}
	
	public ArrayList<Integer> getForbidden() {
		return forbidden;
	}

	public void closeSockets() {
		
		for(int i = 0; i < guessers.size(); i++){
			guessers.get(i).closeSocket();
		}
		
	}
	
	public void addForbidden(int port){
		forbidden.add(port);
	}

}
