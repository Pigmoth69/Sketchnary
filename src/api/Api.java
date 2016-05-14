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

	public Api(Database database){
		this.database = database;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {

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
			response(exchange, "Unknown error");
		}

	}

	private void process(HttpExchange exchange, String method, String[] paths, Map<String, String> filtered) {

		Headers headers = exchange.getRequestHeaders();
		headers.add("Content-Type", "application/json");

		String body = getBody(exchange);

		if (paths[1].equals("event"))
			processEvent(exchange, method, body, paths, filtered);
		else
			response(exchange, "Not an event.");

	}

	/**
	 * Process all types of events
	 * @param exchange
	 * @param method
	 * @param body
	 * @param paths
	 * @param filtered
	 */
	private void processEvent(HttpExchange exchange, String method, String body, String[] paths,
			Map<String, String> filtered) {
		
		String username = filtered.get("username");
		String password = filtered.get("password");
		
		if(username == null)
			response(exchange, "Null Username!");
		if(password == null)
			response(exchange, "Null Password!");
		
		switch(method){
		
		case "GET":
			handleGET(exchange, username, password);
			break;
		case "POST":
			//handlePOST(exchange, username, password);
			break;
		case "PUT":
			//handlePUT();
			break;
		case "DELETE":
			//handleDELETE();
			break;
		default:
			break;
		
		}
		
	}

	private void handleGET(HttpExchange exchange, String username, String password) {
	
		User user = new User(database, username, password);
		
		int response_code = user.UserGET();
		
		if(response_code == 200)
			response(exchange, "GET request successful!");
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

		scanner.useDelimiter("\\");

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
