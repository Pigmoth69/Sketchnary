package https;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import api.Api;
import connection.Database;
import data.Online;
import gameEngine.RoomsEngine;

public class HttpsConnection {

	private Database database;
	private RoomsEngine roomsEngine;
	private Online online;

	public HttpsConnection(Database database, RoomsEngine roomsEngine, Online online) {
		this.database = database;
		this.roomsEngine = roomsEngine;
		this.online = online;
	}

	private SSLContext createSSLContext() {

		SSLContext sslContext = null;

		try {
			sslContext = SSLContext.getInstance("TLS");
			char[] keystorePassword = "123456".toCharArray();
			KeyStore ks = KeyStore.getInstance("JKS");

			ks.load(new FileInputStream("keys/server.keys"), keystorePassword);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, keystorePassword);
			sslContext.init(kmf.getKeyManagers(), null, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sslContext;
	}

	public void setup() {

		HttpsServer httpserver;

		try {

			httpserver = HttpsServer.create(new InetSocketAddress(443), 0);
			httpserver.setHttpsConfigurator(new HttpsConfigurator(createSSLContext()));

			httpserver.createContext("/api", new Api(database, roomsEngine, online));
			httpserver.setExecutor(null);
			httpserver.start();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
