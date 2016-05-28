package api;

import java.util.HashMap;
import java.util.Map;

import data.Player;
import data.ServerData;
import gameEngine.RoomsEngine;

public class Game {

	private RoomsEngine roomsEngine;
	private ServerData serverData;

	public Game(RoomsEngine roomsEngine, ServerData serverData) {
		this.roomsEngine = roomsEngine;
		this.serverData = serverData;
	}

	public Boolean isCorrect(String room_name, String category) {

		for (int i = 0; i < roomsEngine.getRooms().size(); i++) {

			if (roomsEngine.getRooms().get(i).getID().equals(room_name)) {
				if (roomsEngine.getRooms().get(i).getCategory().equals(category))
					return true;
			}

		}

		return false;

	}

	public Boolean savePlayerAnswer(String room_name, String ip, String category) {

		for (int i = 0; i < roomsEngine.getRooms().size(); i++) {

			if (roomsEngine.getRooms().get(i).getID().equals(room_name)) {
				System.out.println("Found room");
				Player player = serverData.findPlayerThroughIp(ip);
				System.out.println("Found player");
				roomsEngine.getRooms().get(i).playerGuessed(category, player);
				System.out.println("set answer");
				return true;
			}

		}

		return false;

	}

	public Map<String, Integer> getResults(String room) {

		Map<String, Integer> points = new HashMap<String, Integer>();

		for (int i = 0; i < roomsEngine.getRooms().size(); i++) {

			if (roomsEngine.getRooms().get(i).getID().equals(room)) {

				Map<String, String> guesses = roomsEngine.getRooms().get(i).getPlayersGuess();
				points = calculatePoints(guesses, roomsEngine.getRooms().get(i).getCategory());
				
			}

		}

		return points;

	}

	private Map<String, Integer> calculatePoints(Map<String, String> guesses, String answer) {

		Map<String, Integer> points = new HashMap<String, Integer>();

		for (Map.Entry<String, String> entry : guesses.entrySet()) {
			String player = entry.getKey();
			String guess = entry.getValue();
			
			if(guess.equals(answer)){
				points.put(player, 50);
			}else{
				points.put(player, 15);
			}
			
		}
		
		return points;

	}

}
