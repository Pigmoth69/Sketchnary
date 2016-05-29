package api;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import connection.Database;
import data.ServerData;
import gameEngine.RoomsEngine;
import gameEngine.GameRoom;
import utilities.Constants;

public class ApiHandler implements Runnable {

	private ApiUtilities apiUt;
	private Database database;
	private RoomsEngine roomsEngine;
	private ServerData serverData;

	private HttpExchange exchange;
	private String method;
	private String[] paths;
	private Map<String, String> filtered;

	public ApiHandler(ApiUtilities apiUt, Database database, RoomsEngine roomsEngine, ServerData serverData) {
		this.apiUt = apiUt;
		this.database = database;
		this.roomsEngine = roomsEngine;
		this.serverData = serverData;
	}

	public void setExchange(HttpExchange exchange) {
		this.exchange = exchange;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setPaths(String[] paths) {
		this.paths = paths;
	}

	public void setFiltered(Map<String, String> filtered) {
		this.filtered = filtered;
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		process();
	}

	private void process() {

		Headers headers = exchange.getResponseHeaders();
		headers.add("Content-Type", "application/json");

		String body = apiUt.getBody(exchange);

		if (paths[1].equals("user"))
			processEventUser(exchange, method, body, paths, filtered);
		else if (paths[1].equals("room"))
			processEventRoom(exchange, method, body, paths, filtered);
		else if (paths[1].equals("game"))
			processEventGame(exchange, method, body, paths, filtered);
		else
			apiUt.response(exchange, "[EVENT] Not an event");

	}

	private void processEventGame(HttpExchange exchange, String method, String body, String[] paths,
			Map<String, String> filtered) {

		switch (method) {
		case "GET":
			String room = filtered.get("room");

			System.out.println("[GAME EVENT] Processing GET request");
			if (room == null)
				apiUt.response(exchange, "Invalid game request!");
			else
				handleGameGET(exchange, room);
			break;
		case "POST":
			System.out.println("[GAME EVENT] Processing POST request");
			handleGamePOST(exchange, body);
			break;
		case "PUT":
			break;
		case "DELETE":
			break;
		default:
			System.out.println("[GAME EVENT] Unknow request");
			break;
		}

	}

	private void handleGamePOST(HttpExchange exchange, String body) {

		JSONObject income;
		String room = null;
		String category = null;

		try {
			income = new JSONObject(body);

			room = income.getString("room");
			category = income.getString("word");

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		Game game = new Game(roomsEngine, serverData);
		String ip = apiUt.getIPAddress(exchange);

		JSONObject json = new JSONObject();

		try {
			if (game.savePlayerAnswer(room, ip, category)) {
				json.put("status", Constants.OK);
				apiUt.response(exchange, json.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void handleGameGET(HttpExchange exchange, String room) {

		Game game = new Game(roomsEngine, serverData);
		
		JSONObject json = new JSONObject();
		JSONArray jArray = new JSONArray();
		
		roomsEngine.findRoom(room).closeSockets();

		try {
			Map<String, Integer> results = game.getResults(room);

			for (Map.Entry<String, Integer> entry : results.entrySet()) {

				JSONObject obj = new JSONObject();
				obj.put("email", entry.getKey());
				obj.put("points", entry.getValue());
				jArray.put(obj);

			}
			
			json.put("leaderboard", jArray);
			json.put("status", Constants.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		apiUt.response(exchange, json.toString());

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

		switch (method) {
		case "GET":
			System.out.println("[USER EVENT] Processing GET request");
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

			if (email == null)
				apiUt.response(exchange, "Invalid email!");
			else if (password == null)
				apiUt.response(exchange, "Invalid password!");
			else
				handleUserGET(exchange, email, password);
			break;
		case "POST":
			System.out.println("[USER EVENT] Processing POST request");
			handleUserPOST(exchange, username, password, name, email, birthdate, country, points);
			break;
		case "PUT":
			System.out.println("[USER EVENT] Processing PUT request");
			handleUserPUT(exchange, body);
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

		if (response_code.equals(Constants.ERROR_USER_EMAIL)) {
			json = apiUt.buildJsonLogin(exchange, Constants.ERROR, "Invalid email!", null, null, null, null, null);
			apiUt.response(exchange, json.toString());
		} else if (response_code.equals(Constants.ERROR_USER_PASSWORD)) {
			json = apiUt.buildJsonLogin(exchange, Constants.ERROR, "Invalid password!", null, null, null, null, null);
			apiUt.response(exchange, json.toString());
		} else {

			result = user.getPlayerInfo(email);

			try {
				if (result.next()) {

					if (response_code.equals(Constants.OK)) {
						json = apiUt.buildJsonLogin(exchange, Constants.OK, null, result.getString("username"),
								result.getString("name"), result.getString("birthdate"), result.getString("country"),
								result.getString("points"));

						apiUt.setupOnline(result, exchange);
						apiUt.response(exchange, json.toString());
					} else {
						json = apiUt.buildJsonLogin(exchange, Constants.ERROR, "Unknown Error!", null, null, null, null,
								null);
						System.out.println(password);
						apiUt.response(exchange, json.toString());
					}

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
			apiUt.response(exchange, "POST request successful!");
		else
			apiUt.response(exchange, "Not Found!");

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
	private void handleUserPUT(HttpExchange exchange, String body) {

		User user = new User(database);

		JSONObject json = null;
		
		try {
			json = new JSONObject(body);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			user.setEmail(json.getString("email"));
			user.setPassword(json.getString("password"));
			user.setUsername(json.getString("username"));
			user.setName(json.getString("name"));
			user.setBirthdate(json.getString("birthdate"));
			user.setCountry(json.getString("country"));
			user.setPoints(10);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String response_code = user.UserPUT();

		if (response_code.equals(Constants.ERROR_DB_DUPLICATE_EMAIL)) {
			json = apiUt.buildJsonSignUp("error", "Email already exists!");
			apiUt.response(exchange, json.toString());
		} else if (response_code.equals(Constants.ERROR_DB_DUPLICATE_USERNAME)) {
			json = apiUt.buildJsonSignUp("error", "Username already taken!");
			apiUt.response(exchange, json.toString());
		} else {
			json = apiUt.buildJsonSignUp("ok", null);
			apiUt.response(exchange, json.toString());
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
			apiUt.response(exchange, "DELETE request successful!");
		else
			apiUt.response(exchange, "Not Found!");

	}

	private void processEventRoom(HttpExchange exchange, String method, String body, String[] paths,
			Map<String, String> filtered) {

		System.out.println("[ROOM EVENT] Processing event");

		switch (method) {
		case "GET":
			String rooms = filtered.get("rooms");
			String room = filtered.get("room");
			String exit = filtered.get("exit");
			System.out.println("[ROOM EVENT] Processing GET request");
			if (rooms == null && exit == null)
				handleRoomGETEntry(exchange, room);
			else if (room == null && rooms == null)
				handleRoomGETExit(exchange, exit);
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
			System.out.println("[ROOM EVENT] Unknow request");
			break;
		}

	}

	private void handleRoomGETExit(HttpExchange exchange, String room) {

		Room r = new Room(roomsEngine, room);
		String ip = apiUt.getIPAddress(exchange);

		JSONObject json = new JSONObject();
		try {
			if (r.exit(room, ip, serverData)) {
				json.put("status", Constants.OK);
				apiUt.response(exchange, json.toString());
			} else {
				json.put("status", Constants.ERROR);
				apiUt.response(exchange, json.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	private void handleRoomGETEntry(HttpExchange exchange, String room) {

		Room r = new Room(roomsEngine, room);
		String ip = apiUt.getIPAddress(exchange);

		JSONObject json = new JSONObject();
		try {
			if (r.entry(room, ip, serverData)) {
				json.put("status", Constants.OK);
				apiUt.response(exchange, json.toString());
			} else {
				json.put("status", Constants.ERROR);
				apiUt.response(exchange, json.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void handleRoomGET(HttpExchange exchange, String rooms) {

		Room room = new Room(roomsEngine, rooms);
		String ip = apiUt.getIPAddress(exchange);

		JSONObject json;

		String response_code = room.roomGET();

		if (response_code.equals(Constants.ERROR_GR)) {
			json = apiUt.buildJsonRooms(Constants.ERROR, "No rooms found!");
			apiUt.response(exchange, json.toString());
		} else {

			if (rooms.equals("all")) {
				json = apiUt.buildAllRoomsJson(room);
				apiUt.response(exchange, json.toString());
			} else {
				GameRoom gr = room.findRoom();
				if (gr.isOff())
					gr.start();
				json = apiUt.startRoom(exchange, room, ip, gr);
				apiUt.response(exchange, json.toString());
			}
		}

	}

}
