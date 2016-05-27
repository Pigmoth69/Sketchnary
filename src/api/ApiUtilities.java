package api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import data.Online;
import data.Player;
import utilities.Constants;

public class ApiUtilities {

	private Online online;

	public ApiUtilities(Online online) {
		this.online = online;
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

	public JSONObject buildJsonLogin(String status, String reason, String username, String name, String birthdate,
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
