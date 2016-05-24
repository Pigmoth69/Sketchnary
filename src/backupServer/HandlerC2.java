package backupServer;

import java.util.ArrayList;

import connection.Database;
import tcpConnection.Channel;
import utilities.Constants;

public class HandlerC2 implements Runnable {

	private Database database;
	private Channel channel;
	
	private ArrayList<String> received_queries;

	public HandlerC2(Database database, Channel channel) {
		this.database = database;
		this.channel = channel;

		received_queries = new ArrayList<String>();
	}

	public void addReceivedQuery(String query) {
		received_queries.add(query);
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {

		String exchange_status;

		while (true) {
			
			try {
				Thread.sleep(400);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			for(int i = 0; i < received_queries.size(); i++){
				
				exchange_status = handleExchange(received_queries.get(i));
				
				if (exchange_status.equals(Constants.ERROR_BK_EX2))
					System.out.println("[BACKUP SERVER] [HANDLER C2] Error in exchange 2 | [STATUS] " + Constants.ERROR_BK_EX2);
				else
					System.out.println("[BACKUP SERVER] [HANDLER C2] Server replied [STATUS] " + exchange_status);
				
				received_queries.remove(i);
				
			}
		}

	}

	private String handleExchange(String query) {
		
		String exchange_status;

		System.out.println("[BACKUP SERVER] [HANDLER C2] Loading exchange");
		exchange_status = channel.exchangeC2(query);
		System.out.println("[BACKUP SERVER] [HANDLER C2] Exchange finished");
		
		return exchange_status;
	}

	public String updateDatabase(String query) {
		if (database.updateDatabase(query))
			return Constants.BACKUP_DB;
		else
			return Constants.ERROR_BK_DB;
	}

}
