package api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import data.Player;
import data.ServerData;
import gameEngine.GameRoom;
import gameEngine.RoomsEngine;
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

	public String getID() {
		return id;
	}

	private void getAllRooms() {

		for (int i = 0; i < rooms.getRooms().size(); i++) {
			allRooms.put(rooms.getRooms().get(i).getID(), rooms.getRooms().get(i).getPresent());
		}

	}

	private void addPlayers(GameRoom room) {

		for (int i = 0; i < room.getPlayers().size(); i++) {
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

	public Map<String, ArrayList<Player>> getRooms() {
		return allRooms;
	}

	public boolean entry(String room, String ip, ServerData serverData) {

		for (int i = 0; i < rooms.getRooms().size(); i++) {

			if (rooms.getRooms().get(i).getID().equals(room)) {
				Player player = serverData.findPlayerThroughIp(ip);
				rooms.getRooms().get(i).setPresent(player);
				return true;
			}

		}
		return false;
	}

	public boolean exit(String room, String ip, ServerData serverData) {
		
		for (int i = 0; i < rooms.getRooms().size(); i++) {

			if (rooms.getRooms().get(i).getID().equals(room)) {
				
				Player player = serverData.findPlayerThroughIp(ip);
				if(player == rooms.getRooms().get(i).getDrawer()){
					rooms.getRooms().get(i).unsignDrawer();
					rooms.getRooms().get(i).exitRoom(player);
					return true;
				}else{
					rooms.getRooms().get(i).exitRoom(player);
					return true;
				}
			}

		}
		return false;
	}

}
