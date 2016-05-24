package manager;

import java.util.ArrayList;

import tcpConnection.Channel;
import utilities.Constants;

public class Manager implements Runnable {

	private Channel channel;
	private ManagerAssistant managerAssistant;
	private FailureHandler failureHandler;

	private String hostname;
	private int port_c1;
	private int port_c2;
	private ArrayList<String> send_queue;

	public Manager(String hostname, int port_c1, int port_c2) {

		this.hostname = hostname;
		this.port_c1 = port_c1;
		this.port_c2 = port_c2;
		this.send_queue = new ArrayList<String>();

	}

	public void addToQueue(String query) {
		System.out.println("[MANAGER] Added to queue");
		send_queue.add(query);
	}

	public void start() {

		channel = new Channel(hostname, port_c1, port_c2, false);
		channel.createChannels();

		failureHandler = new FailureHandler(channel);
		managerAssistant = new ManagerAssistant(channel, failureHandler);
		
		System.out.println("[MANAGER] loading");
		new Thread(this).start();

	}

	@Override
	public void run() {

		String exchange_status;

		managerAssistant.start();
		failureHandler.start();

		while (true) {
			
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < send_queue.size(); i++) {

				exchange_status = channel.exchangeC1(send_queue.get(i));

				if (exchange_status.equals(Constants.OK)) {
					System.out.println("[MANAGER] Dispatched a query | [STATUS] " + exchange_status);
					managerAssistant.requiresConfirmation(send_queue.get(i));
					send_queue.remove(send_queue.get(i));
				} else {
					System.out.println("[MANAGER] Send failed | [STATUS] " + exchange_status);
					failureHandler.addFailedQuery(send_queue.get(i));
					send_queue.remove(send_queue.get(i));
				}

			}

		}

	}

}
