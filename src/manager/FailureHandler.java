package manager;

import java.util.ArrayList;

import tcpConnection.Channel;
import utilities.Constants;

public class FailureHandler implements Runnable {
	
	private Channel channel;
	
	private ArrayList<String> failed_sends;

	public FailureHandler(Channel channel){
		
		this.channel = channel;
		
		failed_sends = new ArrayList<String>();
	}
	
	public void addFailedQuery(String query){
		failed_sends.add(query);
	}
	
	public void start(){
		
		new Thread(this).start();
		
	}

	@Override
	public void run() {
		
		String resend_status = null;
		int tries = 0;
		
		while(true){
			
			for(int i = 0; i < failed_sends.size(); i++){
				
				while(!resend_status.equals(Constants.OK) && tries < 3){
					resend_status = resendQuery(failed_sends.get(i));
					tries++;
				}
				if(tries == 3){
					System.out.println("Failed");
				}
				
			}
			
		}
		
	}
	
	public String resendQuery(String query){
		
		String exchange_status = channel.exchangeC1(false, query);

		if (exchange_status.equals(Constants.OK)){
			System.out.println("[MANAGER] Dispatched a query | [STATUS] Code " + exchange_status);
			
		}
		else {
			System.out.println("[MANAGER] Send failed | [STATUS] Code " + exchange_status);
			
		}
		
		return exchange_status;
		
	}
	
	public void freezeHandler(){
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void defrostHandler(){
		this.notify();
	}
	
}
