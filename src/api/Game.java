package api;

import gameEngine.RoomsEngine;

public class Game {
	
	private RoomsEngine roomsEngine;
	
	public Game(RoomsEngine roomsEngine){
		this.roomsEngine = roomsEngine;
	}
	
	public Boolean isCorrect(String room_name, String category){
		
		for(int i = 0; i < roomsEngine.getRooms().size(); i++){
			
			if(roomsEngine.getRooms().get(i).getID().equals(room_name)){
				if(roomsEngine.getRooms().get(i).getCategory().equals(category))
					return true;
			}
			
		}
		
		return false;
		
	}

}
