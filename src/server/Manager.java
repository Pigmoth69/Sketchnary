package server;

import java.util.ArrayList;

import connection.Database;
import tcpConnection.Channel;
import tcpConnection.TCPClient;
import utilities.Constants;

public class Manager implements Runnable{
	
	private Database database;
	private Server server;
	private ServerData serverData;
	private TCPClient tcpClient;
	private Channel channel;
	
	private String hostname;
	private int port_c1;
	private int port_c2;
	private ArrayList<String> send_queue;
	
	public Manager(Database database, Server server, ServerData serverData, String hostname, int port_c1, int port_c2){
		this.database = database;
		this.server = server;
		
		this.hostname = hostname;
		this.port_c1 = port_c1;
		this.port_c2 = port_c2;
		this.send_queue = new ArrayList<String>();
		
		start();
	}
	
	public void start(){
		new Thread(this).start();
		
		channel = new Channel(hostname, port_c1, port_c2);
		channel.createChannels(false);
	}

	@Override
	public void run() {
		
		while(true){
			
			for(int i = 0; i < send_queue.size(); i++){
				if(channel.exchangeC1(false, send_queue.get(i)).equals(Constants.OK))
					System.out.println("Sent successfully!");
					
					
			}
			
		}
		
	}
	
	

}
