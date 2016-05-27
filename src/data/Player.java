package data;

import java.util.ArrayList;

public class Player {
	
	private int id;
	private String username;
	private String password;
	private String name;
	private String email;
	private String birthdate;
	private String country;
	private int points;
	
	private String ip_address;
	private int port;
	
	private ArrayList<Player> friends;

	public Player(int id, String username, String password, String name, String email, String birthdate, String country, int points){
		
		this.id = id;
		this.username = username;
		this.password = password;
		this.name = name;
		this.email = email;
		this.birthdate = birthdate;
		this.country = country;
		this.points = points;
		
		this.ip_address = null;
		
		friends = new ArrayList<Player>();
		
	}
	
	public int getId(){
		return id;
	}
	
	public int getPort(){
		return port;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPassword(){
		return password;
	}
	
	public String getName(){
		return name;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getBirthdate(){
		return birthdate;
	}
	
	public String getCountry(){
		return country;
	}
	
	public int getPoints(){
		return points;
	}
	
	public String getIp(){
		return ip_address;
	}
	
	public void setIpAddress(String ip){
		ip_address = ip;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public ArrayList<Player> getFriends(){
		return friends;
	}
	
	public void fillFriendsList(ArrayList<Player> players){
		for(int i = 0; i < players.size(); i++){
			friends.add(players.get(i));
		}
	}
	
	public void addFriend(Player player){
		friends.add(player);
	}
	
}
