package connection;

import java.util.ArrayList;

public class DatabaseTracker implements Runnable{

	private Database database;
	
	public ArrayList<String> queries;
	
	public DatabaseTracker(Database database){
		this.database = database;
		
		this.queries = new ArrayList<String>();
	}

	@Override
	public void run() {
		
		while(true){
			
			
			
		}
		
	}
	
	
	
}
