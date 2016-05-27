package data;

import java.util.ArrayList;

public class ServerData {

	private ArrayList<Player> players;
	private ArrayList<Friend> friends;

	public ServerData() {
		players = new ArrayList<Player>();
		friends = new ArrayList<Friend>();
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public ArrayList<Friend> getFriends() {
		return friends;
	}

	public void addPlayer(Player player) {
		players.add(player);
	}

	public void addFriend(Friend friend) {
		friends.add(friend);
	}

	public void updateFriendsList() {
		for (int i = 0; i < players.size(); i++) {
			for (int j = 0; j < friends.size(); j++) {
				if (friends.get(j).getIdPlayer() == players.get(i).getId()) {
					int player_index = getFriend(friends.get(j).getIdFriend());
					players.get(i).addFriend(players.get(player_index));
				}
			}
		}
	}

	public int getFriend(int id_friend) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getId() == id_friend)
				return i;
		}

		return -1;
	}

	public Player findPlayerThroughIp(String ip) {

		for (int i = 0; i < players.size(); i++) {

			if (players.get(i).getIp() != null) {
				if (players.get(i).getIp().equals(ip))
					return players.get(i);
			}

		}

		return null;
	}

	public Player findPlayer(String username) {

		for (int i = 0; i < players.size(); i++) {

			if (players.get(i).getUsername().equals(username))
				return players.get(i);

		}

		return null;

	}

	public void setPlayerIp(Player pl, String ip) {
		pl.setIpAddress(ip);
	}

}
