package https;

import server.*;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class HttpsServer {

	private Server server;
	private int port;

	private boolean isServerDone = false;

	public HttpsServer(Server server, int port) {
		this.server = server;
		this.port = port;
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

	// Start to run the server
	public void run() {
		// HttpsServer httpserver = HttpsServer.create(new
		// InetSocketAddress(443), 0);
		// httpserver.setHttpsConfigurator(new
		// HttpsConfigurator(createSSLContext()));
		// httpserver.createContext("/api", new API());
		// httpserver.setExecutor(null);
		// httpserver.start();
	}

	public void httpsServer() {

	}

}
