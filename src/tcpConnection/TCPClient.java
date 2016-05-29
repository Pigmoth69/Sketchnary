package tcpConnection;

import java.io.*;
import java.net.*;

public class TCPClient {
	
	Socket clientSocket;
	String host;
	int port;
	
	public TCPClient(String host, int port) throws UnknownHostException, IOException, ConnectException {
		this.host = host;
		this.port = port;
		
		clientSocket = new Socket(host,port);
	}
	
	public String receive() throws UnknownHostException, IOException{
		
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		String sentence = inFromServer.readLine();
		
		return sentence;
	}
	
	public void send(String sentence) throws UnknownHostException, IOException{
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		
		outToServer.writeBytes(sentence + '\n');
		
		System.out.println("SENT TCP " + sentence);

	}
	
	public void sendFile(String filepath) {

		DataOutputStream outToClient;
		BufferedOutputStream out = null;

		try {
			outToClient = new DataOutputStream(clientSocket.getOutputStream());

			out = new BufferedOutputStream(outToClient);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File myFile = new File(filepath);
		byte[] mybytearray = new byte[(int) myFile.length()];
		System.out.print("SIZE ---> " + mybytearray.length);

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(myFile);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}

		BufferedInputStream bis = new BufferedInputStream(fis);

		try {
			bis.read(mybytearray, 0, mybytearray.length);
			out.write(mybytearray, 0, mybytearray.length);
			out.flush();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

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
	
	public void closeSocket(){
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
