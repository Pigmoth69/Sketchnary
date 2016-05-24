package backupServer;

import tcpConnection.Channel;
import utilities.Constants;

public class HandlerC1 implements Runnable {
	
	private Channel channel;
	private HandlerC2 handlerC2;

	public HandlerC1(Channel channel){
		this.channel = channel;
	}
	
	public void start(){
		new Thread(this).start();
	}
	
	public void assignHandlerC2(HandlerC2 handlerC2){
		this.handlerC2 = handlerC2;
	}

	@Override
	public void run() {
		
		String exchange_status;
		
		while(true){
			
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			exchange_status = handleExchange();
			if(exchange_status.equals(Constants.ERROR_BK_EX1))
				System.out.println("[BACKUP SERVER] [HANDLER C1] Error in exchange 1 | [STATUS] " + Constants.ERROR_BK_EX1);
			else
				System.out.println("[BACKUP SERVER] [HANDLER C1] Received a query [STATUS] " + Constants.OK);
		}
		
	}

	private String handleExchange() {
		
		String received;
		
		received = channel.exchangeC1(null);
		
		if(received.equals(Constants.OK) || received == null)
			return Constants.ERROR_BK_EX1;
		else
			handlerC2.addReceivedQuery(received);
		
		return received;
		
	}
	
}
