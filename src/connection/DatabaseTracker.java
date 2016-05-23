package connection;

import java.util.ArrayList;

public class DatabaseTracker implements Runnable{

	private Database database;
	private int loop;
	
	public ArrayList<String> queries;
	
	public DatabaseTracker(Database database){
		this.database = database;
		this.loop = 0;
		
		this.queries = new ArrayList<String>();
		
		new Thread(this).start();
	}

	@Override
	public void run() {
		
		while(true){
			
			if(queries.size() > 5 || loop % 5 == 0){
				
				
				
			}
				
			loop++;
			
		}
		
	}
	
	
	
}
