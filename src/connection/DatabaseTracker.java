package connection;

import java.util.ArrayList;

import manager.Manager;

public class DatabaseTracker implements Runnable{

	private Manager manager;

	private ArrayList<String> queries;
	
	public DatabaseTracker(){
		this.queries = new ArrayList<String>();
	}
	
	public void start(){
		new Thread(this).start();
	}
	
	public void setManager(Manager manager){
		this.manager = manager;
	}
	
	public void addQuery(String query){
		queries.add(query);
	}

	@Override
	public void run() {
		
		while(true){
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(queries.size() > 0){
				System.out.println("[DATABASE TRACKER] Loading new queries to the queue");
				addToSendQueue();
			}
			
		}
		
	}

	private void addToSendQueue() {
		
		for(int i = 0; i < queries.size(); i++){
			manager.addToQueue(queries.get(i));
			queries.remove(queries.get(i));
		}
		
	}
	
}
