package api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import connection.Database;
import data.Online;
import data.Player;
import gameEngine.RoomsEngine;
import utilities.Constants;

public class Api implements HttpHandler {

	private Database database;
	private RoomsEngine roomsEngine;
	private Online online;

	public Api(Database database, RoomsEngine roomsEngine, Online online) {
		this.database = database;
		this.roomsEngine = roomsEngine;
		this.online = online;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		System.out.println("[HANDLER] Detected an https request");

		String method = exchange.getRequestMethod();
		URI uri = exchange.getRequestURI();

		String[] paths = uri.getPath().replaceFirst("^/", "").split("/");
		String query = uri.getQuery();

		if (query == null)
			query = "";

		Map<String, String> filtered = filter(query);

		try {
			process(exchange, method, paths, filtered);
		} catch (Exception e) {
			e.printStackTrace();
			response(exchange, "[ERROR] Unknown error");
		}

	}

	private void process(HttpExchange exchange, String method, String[] paths, Map<String, String> filtered) {

		Headers headers = exchange.getResponseHeaders();
		headers.add("Content-Type", "application/json");

		String body = getBody(exchange);

		if (paths[1].equals("user"))
			processEventUser(exchange, method, body, paths, filtered);
		else if (paths[1].equals("room"))
			processEventRoom(exchange, method, body, paths, filtered);
		else
			response(exchange, "[EVENT] Not an event");

	}

	/**
	 * Process user account requests
	 * 
	 * @param exchange
	 * @param method
	 * @param body
	 * @param paths
	 * @param filtered
	 */
	private void processEventUser(HttpExchange exchange, String method, String body, String[] paths,
			Map<String, String> filtered) {

		System.out.println("[USER EVENT] Processing event");

		String email = filtered.get("email");
		String password = null;
		String name = null;
		String username = null;
		String birthdate = null;
		String country = null;
		String points = null;

		if (filtered.containsKey("password"))
			password = filtered.get("password");
		if (filtered.containsKey("name"))
			name = filtered.get("name");
		if (filtered.containsKey("username"))
			username = filtered.get("username");
		if (filtered.containsKey("birthdate"))
			birthdate = filtered.get("birthdate");
		if (filtered.containsKey("country"))
			country = filtered.get("country");
		if (filtered.containsKey("points"))
			points = filtered.get("points");

		switch (method) {
		case "GET":
			System.out.println("[USER EVENT] Processing GET request");
			if (email == null)
				response(exchange, "Invalid email!");
			else if (password == null)
				response(exchange, "Invalid password!");
			else
				handleUserGET(exchange, email, password);
			break;
		case "POST":
			System.out.println("[USER EVENT] Processing POST request");
			if (username == null)
				response(exchange, "Null Username!");
			else
				handleUserPOST(exchange, username, password, name, email, birthdate, country, points);
			break;
		case "PUT":
			System.out.println("[USER EVENT] Processing PUT request");
			if (username == null)
				response(exchange, "Null Username!");
			else if (password == null)
				response(exchange, "Null Password!");
			else if (name == null)
				response(exchange, "Null Name!");
			else if (email == null)
				response(exchange, "Null Email!");
			else if (birthdate == null)
				response(exchange, "Invalid Birthdate!");
			else
				handleUserPUT(exchange, username, password, name, email, birthdate, country, points);
			break;
		case "DELETE":
			System.out.println("[USER EVENT] Processing DELETE request");
			handleUserDELETE(exchange, username, password);
			break;
		default:
			System.out.println("[USER EVENT] Unknow request");
			break;
		}
	}

	/**
	 * Handle a GET request
	 * 
	 * @param exchange
	 * @param username
	 * @param password
	 */
	private void handleUserGET(HttpExchange exchange, String email, String password) {

		User user = new User(database, email, password);

		JSONObject json;
		ResultSet result;

		String response_code = user.UserGET();
		result = user.getPlayerInfo(email);

		try {
			if (result.next()) {

				if (response_code.equals(Constants.ERROR_USER_EMAIL)) {
					json = buildJsonLogin(Constants.ERROR, "Invalid email!", null, null, null, null, null);
					response(exchange, json.toString());
				} else if (response_code.equals(Constants.ERROR_USER_PASSWORD)) {
					json = buildJsonLogin(Constants.ERROR, "Invalid password!", null, null, null, null, null);
					response(exchange, json.toString());
				} else if (response_code.equals(Constants.OK)) {
					json = buildJsonLogin(Constants.OK, null, result.getString("username"), result.getString("name"),
							result.getString("birthdate"), result.getString("country"), result.getString("points"));
					Player player = new Player(result.getInt("id"), result.getString("username"),
							result.getString("password"), result.getString("name"), email,
							result.getString("birthdate"), result.getString("country"), result.getInt("points"));
					online.addPlayer(player);
					notifyFriends(online.findFriends(player));
					response(exchange, json.toString());
				} else {
					json = buildJsonLogin(Constants.ERROR, "Unknown Error!", null, null, null, null, null);
					response(exchange, json.toString());
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void notifyFriends(ArrayList<Player> findFriends) {

		for(int i = 0; i < findFriends.size(); i++)
			notifyFriend(findFriends.get(i));
		
	}

	public void notifyFriend(Player friend){
	
		try{
			URL url = new URL("http://www.example.com/resource");
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("GET");
		
			OutputStreamWriter out = new OutputStreamWriter(
		    httpCon.getOutputStream());
			out.write("Player online");
			out.close();
			httpCon.getInputStream();
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}

	/**
	 * Handle a POST request
	 * 
	 * @param exchange
	 * @param username
	 * @param password
	 * @param name
	 * @param email
	 * @param birthdate
	 * @param country
	 */
	private void handleUserPOST(HttpExchange exchange, String username, String password, String name, String email,
			String birthdate, String country, String points) {

		User user = new User(database, email, password);

		if (name != null)
			user.setName(name);
		if (username != null)
			user.setUsername(username);
		if (birthdate != null)
			user.setBirthdate(birthdate);
		if (country != null)
			user.setCountry(country);
		if (points != null)
			user.setPoints(Integer.parseInt(points));

		int response_code = user.UserPOST();

		if (response_code == 200)
			response(exchange, "POST request successful!");
		else
			response(exchange, "Not Found!");

	}

	/**
	 * Handle a PUT request
	 * 
	 * @param exchange
	 * @param username
	 * @param password
	 * @param name
	 * @param email
	 * @param birthdate
	 * @param country
	 */
	private void handleUserPUT(HttpExchange exchange, String username, String password, String name, String email,
			String birthdate, String country, String points) {

		User user = new User(database, email, password);

		user.setName(name);
		user.setUsername(username);
		user.setBirthdate(birthdate);
		user.setCountry(country);
		user.setPoints(Integer.parseInt(points));

		JSONObject json;

		String response_code = user.UserPUT();

		if (response_code.equals(Constants.ERROR_DB_DUPLICATE_EMAIL)) {
			json = buildJsonSignUp("error", "Email already exists!");
			response(exchange, json.toString());
		} else if (response_code.equals(Constants.ERROR_DB_DUPLICATE_USERNAME)) {
			json = buildJsonSignUp("error", "Username already taken!");
			response(exchange, json.toString());
		} else {
			json = buildJsonSignUp("ok", null);
			response(exchange, json.toString());
		}

	}

	/**
	 * Handle a DELETE request
	 * 
	 * @param username
	 */
	private void handleUserDELETE(HttpExchange exchange, String username, String password) {

		User user = new User(database, username, password);

		int response_code = user.UserDELETE();

		if (response_code == 200)
			response(exchange, "DELETE request successful!");
		else
			response(exchange, "Not Found!");

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
				json.put("status", status);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		return json;

	}

	private void processEventRoom(HttpExchange exchange, String method, String body, String[] paths,
			Map<String, String> filtered) {

		System.out.println("[ROOM EVENT] Processing event");

		String rooms = filtered.get("rooms");

		switch (method) {
		case "GET":
			System.out.println("[ROOM EVENT] Processing GET request");
			if (rooms == null)
				response(exchange, "Invalid room request!");
			else
				handleRoomGET(exchange, rooms);
			break;
		case "POST":
			break;
		case "PUT":
			break;
		case "DELETE":
			break;
		default:
			System.out.println("[USER EVENT] Unknow request");
			break;
		}

	}

	private void handleRoomGET(HttpExchange exchange, String rooms) {

		Room room = new Room(roomsEngine, rooms);

		JSONObject json;

		String response_code = room.roomGET();

		if (response_code.equals(Constants.ERROR_GR)) {
			json = buildJsonRooms(Constants.ERROR, "No rooms found!");
			response(exchange, json.toString());
		} else {

			if (rooms.equals("all")) {
				json = buildAllRoomsJson(room);
				response(exchange, json.toString());
			} else {
				json = createRoomObject(room.getID(), room.getPlayers());
				response(exchange, json.toString());
			}
		}

	}

	private JSONObject buildAllRoomsJson(Room room) {

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

	private JSONObject createRoomObject(String id, ArrayList<Player> players) {

		JSONObject json = new JSONObject();
		JSONArray jArray = new JSONArray();

		try {
			json.put("room", id);

			for (int i = 0; i < players.size(); i++) {

				JSONObject temp = new JSONObject();
				temp.put("player", players.get(i).getEmail());
				jArray.put(temp);

			}

			json.put("players", jArray);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;

	}

	private JSONObject buildJsonRooms(String status, String reason) {

		JSONObject json = new JSONObject();

		try {
			json.put("status", status);
			json.put("reason", reason);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	/**
	 * Get the message's body
	 * 
	 * @param exchange
	 * @return body
	 */
	private String getBody(HttpExchange exchange) {

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

	private void response(HttpExchange exchange, String message) {

		try {
			exchange.sendResponseHeaders(200, message.getBytes().length);
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		OutputStream output = exchange.getResponseBody();

		try {
			output.write(message.getBytes());
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

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

}
