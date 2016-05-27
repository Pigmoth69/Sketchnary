package api;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import connection.Database;
import data.Online;
import data.ServerData;
import gameEngine.RoomsEngine;

public class Api extends Thread implements HttpHandler{

	private Database database;
	private RoomsEngine roomsEngine;
	private Online online;
	private ApiUtilities apiUt;
	private ServerData serverData;
	private ApiHandler handler;

	public Api(Database database, RoomsEngine roomsEngine, Online online, ServerData serverData) {
		this.database = database;
		this.roomsEngine = roomsEngine;
		this.online = online;
		this.serverData = serverData;

		this.apiUt = new ApiUtilities(this.online, this.serverData);
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
			this.handler = new ApiHandler(apiUt, database, roomsEngine);
			
			handler.setExchange(exchange);
			handler.setFiltered(filtered);
			handler.setMethod(method);
			handler.setPaths(paths);
			
			handler.start();
		} catch (Exception e) {
			e.printStackTrace();
			apiUt.response(exchange, "[ERROR] Unknown error");
		}

	}

	
}
