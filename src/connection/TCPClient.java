package connection;

import java.io.*;
import java.net.*;

class TCPClient {
	
	Socket clientSocket;
	String host;
	int port;
	
	public TCPClient(String host, int port) throws Exception {
		this.host = host;
		this.port = port;
		clientSocket = new Socket(host, port);
	}
	
	public String receive() throws UnknownHostException, IOException{
		clientSocket = new Socket(this.host, this.port);
		
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		String sentence = inFromServer.readLine();
		
		clientSocket.close();
		
		return sentence;
	}
	
	public void send(String sentence) throws UnknownHostException, IOException{
		clientSocket = new Socket(this.host, this.port);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		
		outToServer.writeBytes(sentence + '\n');
		
		clientSocket.close();
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
