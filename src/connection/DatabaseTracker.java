package connection;

import java.util.ArrayList;

import manager.Manager;

public class DatabaseTracker implements Runnable{

	private Manager manager;
	
	private int loop;
	private ArrayList<String> queries;
	
	public DatabaseTracker(Manager manager){
		this.manager = manager;
		this.loop = 0;
		
		this.queries = new ArrayList<String>();
	}
	
	public void start(){
		new Thread(this).start();
	}
	
	public void addQuery(String query){
		queries.add(query);
	}

	@Override
	public void run() {
		
		while(true){
			System.out.println("Inside the loop");
			if(queries.size() > 5 || loop % 5 == 0)
				addToSendQueue();
			loop++;
			
		}
		
	}

	private void addToSendQueue() {
		System.out.println("Inside the for");
		for(int i = 0; i < queries.size(); i++){
			manager.addToQueue(queries.get(i));
			queries.remove(queries.get(i));
		}
		
	}
	
}
