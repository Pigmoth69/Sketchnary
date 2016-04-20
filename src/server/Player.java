package server;

import java.util.ArrayList;

public class Player {
	
	private int id;
	private String username;
	private String password;
	private String name;
	private String email;
	private int age;
	private String country;
	
	private ArrayList<Player> friends;

	public Player(int id, String username, String password, String name, String email, int age, String country){
		
		this.id = id;
		this.username = username;
		this.password = password;
		this.name = name;
		this.email = email;
		this.age = age;
		this.country = country;
		
		friends = new ArrayList<Player>();
		
	}
	
	public int getId(){
		return id;
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
	
	public int getAge(){
		return age;
	}
	
	public String getCountry(){
		return country;
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
