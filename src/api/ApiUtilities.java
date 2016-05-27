package api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import data.Online;
import data.Player;
import data.ServerData;
import gameEngine.GameRoom;
import utilities.Constants;

public class ApiUtilities {

	private Online online;
	private ServerData serverData;

	private ArrayList<Integer> ports;

	public ApiUtilities(Online online, ServerData serverData) {
		this.online = online;
		this.serverData = serverData;
		
		ports = new ArrayList<Integer>();
		fillPorts();
	}

	/**
	 * Respond to the client
	 * 
	 * @param exchange
	 * @param message
	 */
	public void response(HttpExchange exchange, String message) {

		try {
			exchange.sendResponseHeaders(200, message.getBytes().length);
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		OutputStream output = exchange.getResponseBody();

		System.out.println(message);

		try {
			output.write(message.getBytes());
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void fillPorts() {

		ports.add(1735);
		ports.add(1761);
		ports.add(1776);
		ports.add(1792);
		ports.add(1801);
		ports.add(1812);
		ports.add(1813);
		ports.add(1863);
		ports.add(1886);
		ports.add(1920);
		ports.add(1935);
		ports.add(1947);

	}

	public JSONObject buildAllRoomsJson(Room room) {

		JSONObject json = new JSONObject();
		JSONObject temp = new JSONObject();
		JSONArray jArray = new JSONArray();

		String id;
		ArrayList<Player> players;

		for (Map.Entry<String, ArrayList<Player>> entry : room.getRooms().entrySet()) {
			id = entry.getKey();
			players = entry.getValue();

			temp = createRoomObject(id, players);
			jArray.put(temp);
		}

		try {
			json.put("rooms", jArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	public JSONObject createRoomObject(String id, ArrayList<Player> players) {

		JSONObject json = new JSONObject();
		JSONArray jArray = new JSONArray();

		try {
			json.put("room", id);

			for (int i = 0; i < players.size(); i++) {

				JSONObject temp = new JSONObject();
				temp.put(new Integer(i).toString(), players.get(i).getEmail());
				jArray.put(temp);

			}

			json.put("players", jArray);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;

	}

	public JSONObject startRoom(HttpExchange exchange, Room room, String ip) {

		JSONObject json = new JSONObject();
		GameRoom gr = null;

		try {
			int port = getPort();
			json.put("port", port);
			InetAddress addr = InetAddress.getLocalHost();
			String hostaddress = addr.getHostAddress();
			json.put("host", hostaddress);
			gr = room.findRoom();
			
			Player pl = serverData.findPlayerThroughIp(ip);
			pl.setPort(port);
			gr.addPlayer(pl);

			if (gr.getDrawer() == null) {
				json.put("role", "drawer");
				json.put("word", gr.generateWord());
				gr.setDrawer(pl);
			} else {
				json.put("role", "guesser");
				ArrayList<String> words = gr.generateWordList();
				JSONObject jo1 = new JSONObject();
				JSONObject jo2 = new JSONObject();
				JSONObject jo3 = new JSONObject();
				jo1.put("1", words.get(0));
				jo2.put("2", words.get(1));
				jo3.put("3", words.get(2));

				JSONArray ja = new JSONArray();
				ja.put(jo1);
				ja.put(jo2);
				ja.put(jo3);

				json.put("words", ja);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		gr.start();
		return json;

	}

	public int getPort() {

		Random rand = new Random();

		int nr = rand.nextInt((ports.size() - 1) + 1);

		return ports.get(nr);

	}

	public JSONObject buildJsonRooms(String status, String reason) {

		JSONObject json = new JSONObject();

		try {
			json.put("status", status);
			json.put("reason", reason);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	public JSONObject buildJsonLogin(HttpExchange exchange, String status, String reason, String username, String name, String birthdate,
			String country, String points) {

		JSONObject json = new JSONObject();

		if (status.equals(Constants.ERROR)) {
			try {
				json.put("status", status);
				json.put("reason", reason);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {

			try {
				json.put("status", status);
				json.put("username", username);
				json.put("name", name);
				json.put("birthdate", birthdate);
				json.put("country", country);
				json.put("points", points);
				
				serverData.setPlayerIp(serverData.findPlayer(username), getIPAddress(exchange));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		return json;
	}

	public JSONObject buildJsonSignUp(String status, String reason) {

		JSONObject json = new JSONObject();

		if (status.equals(Constants.ERROR)) {
			try {
				json.put("status", status);
				json.put("reason", reason);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {

			try {
				System.out.println("aqui");
				json.put("status", status);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		return json;

	}

	/**
	 * Get the message's body
	 * 
	 * @param exchange
	 * @return body
	 */
	public String getBody(HttpExchange exchange) {

		String body;

		InputStream input = exchange.getRequestBody();
		Scanner scanner = new Scanner(input);

		scanner.useDelimiter("\\A");

		body = scanner.hasNext() ? scanner.next() : "";

		try {
			scanner.close();
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return body;

	}

	/**
	 * Transform a string query into an hashmap
	 * 
	 * @param query
	 * @return hashmap
	 */
	public Map<String, String> filter(String query) {

		Map<String, String> map = new HashMap<String, String>();

		for (String keyValue : query.split("&")) {

			String[] pairs = keyValue.split("=");
			map.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);

		}
		return map;

	}

	/**
	 * Gets the ip address of the client that made the request
	 */
	public String getIPAddress(HttpExchange exchange) {

		InetSocketAddress remoteAddress = exchange.getRemoteAddress();
		return remoteAddress.getHostName();

	}

	/**
	 * Notifies the friends of the player that is online now
	 * 
	 * @param findFriends
	 */
	public void notifyFriends(ArrayList<Player> friends) {

		for (int i = 0; i < friends.size(); i++)
			notifyFriend(friends.get(i));

	}

	public void notifyFriend(Player friend) {

		try {

			URL url = new URL("https://" + friend.getIp() + "/api/notification/?online=");
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("GET");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setupOnline(ResultSet result, HttpExchange exchange) {

		Player player = null;
		try {
			player = new Player(result.getInt("id"), result.getString("username"), result.getString("password"),
					result.getString("name"), result.getString("email"), result.getString("birthdate"),
					result.getString("country"), result.getInt("points"));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String ip = getIPAddress(exchange);
		online.addPlayer(player, ip);
		notifyFriends(online.findFriends(player));

	}

}
