package api;

import java.io.IOException;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import connection.Database;
import data.Online;
import gameEngine.RoomsEngine;
import utilities.Constants;

public class Api implements HttpHandler {

	private Database database;
	private RoomsEngine roomsEngine;
	private Online online;
	private ApiUtilities apiUt;

	public Api(Database database, RoomsEngine roomsEngine, Online online) {
		this.database = database;
		this.roomsEngine = roomsEngine;
		this.online = online;
		
		this.apiUt = new ApiUtilities(this.online);
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

		Map<String, String> filtered = apiUt.filter(query);

		try {
			process(exchange, method, paths, filtered);
		} catch (Exception e) {
			e.printStackTrace();
			apiUt.response(exchange, "[ERROR] Unknown error");
		}

	}

	private void process(HttpExchange exchange, String method, String[] paths, Map<String, String> filtered) {

		Headers headers = exchange.getResponseHeaders();
		headers.add("Content-Type", "application/json");

		String body = apiUt.getBody(exchange);

		if (paths[1].equals("user"))
			processEventUser(exchange, method, body, paths, filtered);
		else if (paths[1].equals("room"))
			processEventRoom(exchange, method, body, paths, filtered);
		else
			apiUt.response(exchange, "[EVENT] Not an event");

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
				apiUt.response(exchange, "Invalid email!");
			else if (password == null)
				apiUt.response(exchange, "Invalid password!");
			else
				handleUserGET(exchange, email, password);
			break;
		case "POST":
			System.out.println("[USER EVENT] Processing POST request");
			if (username == null)
				apiUt.response(exchange, "Null Username!");
			else
				handleUserPOST(exchange, username, password, name, email, birthdate, country, points);
			break;
		case "PUT":
			System.out.println("[USER EVENT] Processing PUT request");
			if (username == null)
				apiUt.response(exchange, "Null Username!");
			else if (password == null)
				apiUt.response(exchange, "Null Password!");
			else if (name == null)
				apiUt.response(exchange, "Null Name!");
			else if (email == null)
				apiUt.response(exchange, "Null Email!");
			else if (birthdate == null)
				apiUt.response(exchange, "Invalid Birthdate!");
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
					json = apiUt.buildJsonLogin(Constants.ERROR, "Invalid email!", null, null, null, null, null);
					apiUt.response(exchange, json.toString());
				} else if (response_code.equals(Constants.ERROR_USER_PASSWORD)) {
					json = apiUt.buildJsonLogin(Constants.ERROR, "Invalid password!", null, null, null, null, null);
					apiUt.response(exchange, json.toString());
				} else if (response_code.equals(Constants.OK)) {
					json = apiUt.buildJsonLogin(Constants.OK, null, result.getString("username"), result.getString("name"),
							result.getString("birthdate"), result.getString("country"), result.getString("points"));
					
					apiUt.setupOnline(result, exchange);
					apiUt.response(exchange, json.toString());
				} else {
					json = apiUt.buildJsonLogin(Constants.ERROR, "Unknown Error!", null, null, null, null, null);
					apiUt.response(exchange, json.toString());
				}

			}
		} catch (SQLException e) {
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

		String rooms = filtered.get("rooms");

		switch (method) {
		case "GET":
			System.out.println("[ROOM EVENT] Processing GET request");
			if (rooms == null)
				apiUt.response(exchange, "Invalid room request!");
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
			json = apiUt.buildJsonRooms(Constants.ERROR, "No rooms found!");
			apiUt.response(exchange, json.toString());
		} else {

			if (rooms.equals("all")) {
				json = apiUt.buildAllRoomsJson(room);
				apiUt.response(exchange, json.toString());
			} else {
				json = apiUt.createRoomObject(room.getID(), room.getPlayers());
				apiUt.response(exchange, json.toString());
			}
		}

	}

}
