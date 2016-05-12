package https;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Scanner;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HttpsConnection implements HttpHandler {

	public HttpsConnection() {
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		String method = exchange.getRequestMethod();
		URI uri = exchange.getRequestURI();

		String[] paths = uri.getPath().replaceFirst("^/", "").split("/");
		String query = uri.getQuery();

		if (query == null)
			query = "";

		HashMap<String, String> filtered = filter(query);

		try {
			process(exchange, method, paths, filtered);
		} catch (Exception e) {
			response(exchange, error(method, filtered, "Unknown error."), 500);
		}

	}

	private void process(HttpExchange exchange, String method, String[] paths, HashMap<String, String> filtered) {

		Headers headers = exchange.getRequestHeaders();
		headers.add("Content-Type", "application/json");

		String body = getBody(exchange);

		if (paths[1].equals("event"))
			processEvent(exchange, method, body, paths, filtered);
		else
			response(exchange, "Not an event.", 200);

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
			HashMap<String, String> filtered) {
		
		switch(method){
		
		case "GET":
			break;
		case "POST":
			break;
		case "PUT":
			break;
		case "DELETE":
			break;
		default:
			break;
			
		
		}
		
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

	private void response(HttpExchange exchange, Object error, int i) {
		// TODO Auto-generated method stub

	}

	private String error(String method, HashMap<String, String> filtered, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Transform a string query into an hashmap
	 * 
	 * @param query
	 * @return hashmap
	 */
	public HashMap<String, String> filter(String query) {

		HashMap<String, String> map = new HashMap<String, String>();

		for (String keyValue : query.split("&")) {

			String[] pairs = keyValue.split("=");
			map.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);

		}

		return map;

	}

}
