package server;

public class Friend {
	
	private int id_player;
	private int id_friend;

	public Friend(int id_player, int id_friend){
		
		this.id_player = id_player;
		this.id_friend = id_friend;
		
	}
	
	public int getIdPlayer(){
		return id_player;
	}
	
	public int getIdFriend(){
		return id_friend;
	}
	
}
