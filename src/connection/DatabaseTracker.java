package connection;

import java.util.ArrayList;

import manager.Manager;

public class DatabaseTracker implements Runnable{

	private Manager manager;
	
	private int loop;
	private ArrayList<String> queries;
	
	public DatabaseTracker(){
		this.loop = 0;
		
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
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if((queries.size() > 5 || loop % 5 == 0) && queries.size() > 0){
				System.out.println("[DATABASE TRACKER] Loading new queries to the queue");
				addToSendQueue();
			}
			loop++;
			
		}
		
	}

	private void addToSendQueue() {
		
		for(int i = 0; i < queries.size(); i++){
			manager.addToQueue(queries.get(i));
			queries.remove(queries.get(i));
		}
		
	}
	
}
