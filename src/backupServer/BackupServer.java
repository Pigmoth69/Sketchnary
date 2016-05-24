package backupServer;

import connection.Database;
import tcpConnection.Channel;

public class BackupServer implements Runnable {

	private Database database;
	private Channel channel;
	private HandlerC1 handlerC1;
	private HandlerC2 handlerC2;

	private int port_c1;
	private int port_c2;

	public BackupServer(Database database, int port_c1,
			int port_c2) {
		this.database = database;

		this.port_c1 = port_c1;
		this.port_c2 = port_c2;
	}
	
	public void start(){
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		
		manager();
		
		handlerC1.start();
		handlerC2.start();
		
	}

	public void manager() {

		try {

			channel = new Channel(null, port_c1, port_c2, true);
			channel.createChannels();
			
			handlerC1 = new HandlerC1(channel);
			handlerC2 = new HandlerC2(database, channel);
			
			handlerC1.assignHandlerC2(handlerC2);
			
			System.out.println("[BACKUP SERVER] Created channels");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
