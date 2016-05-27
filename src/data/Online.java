package data;

import java.util.ArrayList;

public class Online {
	
	private ArrayList<Player> onlinePlayers;

	public Online(){
		onlinePlayers = new ArrayList<Player>();
	}
	
	public void addPlayer(Player player, String ip){
		onlinePlayers.add(player);
		player.setIpAddress(ip);
	}
	
	public Boolean isPlayerOnline(Player player){
		
		for(int i = 0; i < onlinePlayers.size(); i++){
			
			if(onlinePlayers.get(i).getId() == player.getId()){
				System.out.println("[PLAYERS] One Player online!");
				return true;
			}
			
		}
		
		return false;
		
	}
	
	public ArrayList<Player> findFriends(Player player){
		
		ArrayList<Player> players = new ArrayList<Player>();
		
		for(int i = 0; i < onlinePlayers.size(); i++){
			
			if(isFriend(onlinePlayers.get(i), player))
				players.add(onlinePlayers.get(i));
			
		}
		
		return players;
		
	}

	private boolean isFriend(Player player, Player player2) {
		
		for(int i = 0; i < player.getFriends().size(); i++){
			
			if(player.getFriends().get(i).getId() == player2.getId())
				return true;
			
		}
		
		return false;
	}
	
}
