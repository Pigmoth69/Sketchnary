package api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gameEngine.GameRoom;
import gameEngine.RoomsEngine;
import server.Player;
import utilities.Constants;

public class Room {

	private RoomsEngine rooms;

	private String id;
	private ArrayList<Player> players;
	private Map<String, ArrayList<Player>> allRooms;

	public Room(RoomsEngine rooms, String id) {
		this.rooms = rooms;

		this.id = id;
		this.players = new ArrayList<Player>();
		this.allRooms = new HashMap<String, ArrayList<Player>>();
	}

	public String roomGET() {

		if (id.equals("all")) {

			getAllRooms();
			return Constants.OK;
			
		} else {

			GameRoom room = findRoom();

			if (room != null) {
				addPlayers(room);
				return Constants.OK;
			}
			
		}
		
		return Constants.ERROR_GR;

	}
	
	public String getID(){
		return id;
	}

	private void getAllRooms() {
		
		for(int i = 0; i < rooms.getRooms().size(); i++){
			allRooms.put(rooms.getRooms().get(i).getID(), rooms.getRooms().get(i).getPlayers());	
		}
		
	}

	private void addPlayers(GameRoom room) {
		
		for(int i = 0; i < room.getPlayers().size(); i++){
			players.add(room.getPlayers().get(i));
		}
		
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public GameRoom findRoom() {

		for (int i = 0; i < rooms.getRooms().size(); i++) {

			if (id.equals(rooms.getRooms().get(i).getID()))
				return rooms.getRooms().get(i);

		}

		return null;

	}
	
	public Map<String, ArrayList<Player>> getRooms(){
		return allRooms;
	}

}
