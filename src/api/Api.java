package api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import connection.Database;

public class Api implements HttpHandler {

	private Database database;

	public Api(Database database) {
		this.database = database;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("recebi!");
		
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
			System.out.println("entrei no erro!");
			e.printStackTrace();
			response(exchange, "Unknown error");
		}

	}

	private void process(HttpExchange exchange, String method, String[] paths, Map<String, String> filtered) {

		Headers headers = exchange.getResponseHeaders();
		headers.add("Content-Type", "application/json");

		String body = getBody(exchange); 

		if (paths[1].equals("event"))
			processEvent(exchange, method, body, paths, filtered);
		else
			response(exchange, "Not an event.");

	}

	/**
	 * Process all types of events
	 * 
	 * @param exchange
	 * @param method
	 * @param body
	 * @param paths
	 * @param filtered
	 */
	private void processEvent(HttpExchange exchange, String method, String body, String[] paths,
			Map<String, String> filtered) {
		
		System.out.println("processEvent");
		String email = filtered.get("email");
		String password = null;	
		String name = null;
		String username = null;
		String age = null;
		String country = null;
		
		if(filtered.containsKey("password"))
			password = filtered.get("password");
		if(filtered.containsKey("name"))
			name = filtered.get("name");
		if(filtered.containsKey("username"))
			username = filtered.get("username");
		if(filtered.containsKey("age"))
			age = filtered.get("age");
		if(filtered.containsKey("country"))
			country = filtered.get("country");

		switch (method) {
		case "GET":
			System.out.println("GET");
			if (email == null)
				response(exchange, "Invalid email!");
			else if (password == null)
				response(exchange, "Invalid password!");
			else
				handleGET(exchange, email, password);
			break;
		case "POST":
			System.out.println("POST");
			if (username == null)
				response(exchange, "Null Username!");
			else
				handlePOST(exchange, username, password, name, email, age, country);
			break;
		case "PUT":
			System.out.println("PUT");
			if (username == null)
				response(exchange, "Null Username!");
			else if (password == null)
				response(exchange, "Null Password!");
			else if(name == null)
				response(exchange, "Null Name!");
			else if(email == null)
				response(exchange, "Null Email!");
			else if(age == null)
				response(exchange, "Invalid Age!");
			else
				handlePUT(exchange, username, password, name, email, age, country);
			break;
		case "DELETE":
			System.out.println("DELETE");
			handleDELETE(exchange, username, password);
			break;
		default:
			System.out.println("nenhum dos HTTP");
			break;
		}
	}
	
	/*
	 URL url = new URL("wwwsaksaksa");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String inputLine;
        StringBuilder builder = new StringBuilder();
        while ((inputLine = in.readLine()) != null){
            builder.append(inputLine);
        }
        String htmlCode = builder.toString();*/

	/**
	 * Handle a GET request
	 * 
	 * @param exchange
	 * @param username
	 * @param password
	 */
	private void handleGET(HttpExchange exchange, String email, String password) {

		User user = new User(database, email, password);

		int response_code = user.UserGET();

		if (response_code == -1)
			response(exchange, "Invalid email!");
		else if(response_code == -2)
			response(exchange, "Invalid password!");
		else if(response_code == 2)
			response(exchange, "Login successful!");
		else
			response(exchange, "Unknown error!");

	}

	/**
	 * Handle a POST request
	 * 
	 * @param exchange
	 * @param username
	 * @param password
	 * @param name
	 * @param email
	 * @param age
	 * @param country
	 */
	private void handlePOST(HttpExchange exchange, String username, String password, String name, String email,
			String age, String country) {

		User user = new User(database, email, password);
		
		if(name != null)
			user.setName(name);
		if(username != null)
			user.setUsername(username);
		if(age != null)
			user.setAge(age);
		if(country != null)
			user.setCountry(country);

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
	 * @param age
	 * @param country
	 */
	private void handlePUT(HttpExchange exchange, String username, String password, String name, String email,
			String age, String country) {

		User user = new User(database, email, password);
		
		user.setName(name);
		user.setUsername(username);
		user.setAge(age);
		user.setCountry(country);

		int response_code = user.UserPUT();

		if (response_code == 200)
			response(exchange, "PUT request successful!");
		else
			response(exchange, "Not Found!");

	}

	/**
	 * Handle a DELETE request
	 * 
	 * @param username
	 */
	private void handleDELETE(HttpExchange exchange, String username, String password) {

		User user = new User(database, username, password);

		int response_code = user.UserDELETE();

		if (response_code == 200)
			response(exchange, "DELETE request successful!");
		else
			response(exchange, "Not Found!");

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
