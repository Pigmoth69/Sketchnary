package tcpConnection;

public class Channel {
	
	private String hostname;
	private int port_c1;
	private int port_c2;
	
	private TCPClient client_c1;
	private TCPClient client_c2;
	private TCPServer server_c1;
	private TCPServer server_c2;
	
	public Channel(String hostname, int port_c1, int port_c2){
		this.hostname = hostname;
		this.port_c1 = port_c1;
		this.port_c2 = port_c2;
	}
	
	public void createChannels(Boolean client){
		
		if(client){
			try {
				client_c1 = new TCPClient(hostname, port_c1);
				client_c2 = new TCPClient(hostname, port_c2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			try{
				server_c1 = new TCPServer(port_c1);
				server_c2 = new TCPServer(port_c2);
			} catch (Exception e){
				e.printStackTrace();
			}
			
		}
		
	}

}
