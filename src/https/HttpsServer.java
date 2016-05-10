package https;

import server.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.sun.net.httpserver.HttpsConfigurator;

public class HttpsServer {

	private Server server;
	private int port;

	private boolean isServerDone = false;

	public HttpsServer(Server server, int port) {
		this.server = server;
		this.port = port;
	}

	private SSLContext createSSLContext() {
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream("test.jks"), "passphrase".toCharArray());

			// Create key manager
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyManagerFactory.init(keyStore, "passphrase".toCharArray());
			KeyManager[] km = keyManagerFactory.getKeyManagers();

			// Create trust manager
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
			trustManagerFactory.init(keyStore);
			TrustManager[] tm = trustManagerFactory.getTrustManagers();

			// Initialize SSLContext
			SSLContext sslContext = SSLContext.getInstance("TLSv1");
			sslContext.init(km, tm, null);

			return sslContext;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	// Start to run the server
	public void run() {
		//HttpsServer httpserver = HttpsServer.create(new InetSocketAddress(443), 0);
		//httpserver.setHttpsConfigurator(new HttpsConfigurator(createSSLContext()));
		//httpserver.createContext("/api", new API());
		//httpserver.setExecutor(null);
		//httpserver.start();
	}

	public void httpsServer() {

	}

}
