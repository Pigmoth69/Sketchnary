package manager;

import java.util.ArrayList;

import tcpConnection.Channel;
import utilities.Constants;

public class FailureHandler implements Runnable {

	private Channel channel;

	private ArrayList<String> failed_sends;
	private ArrayList<String> refailed_sends;

	public FailureHandler(Channel channel) {

		this.channel = channel;

		failed_sends = new ArrayList<String>();
		refailed_sends = new ArrayList<String>();
	}

	public void addFailedQuery(String query) {
		failed_sends.add(query);
	}

	public void start() {
		System.out.println("[MANAGER HANDLER] loading");
		new Thread(this).start();
	}

	@Override
	public void run() {

		String resend_status = null;
		int tries = 0;

		while (true) {
			
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < failed_sends.size(); i++) {

				while (resend_status != null && !resend_status.equals(Constants.OK) && tries < 3) {
					resend_status = resendQuery(failed_sends.get(i));
					tries++;
				}
				if (tries == 3 && !resend_status.equals(Constants.OK)) {
					System.out.println(
							"[MANAGER HANDLER] Query couldn't be sent | [STATUS] Code " + Constants.ERROR_HD_RESEND);
					refailed_sends.add(failed_sends.get(i));
				}

				failed_sends.remove(i);

			}

		}

	}

	/**
	 * Resend a query
	 * @param query
	 * @return
	 */
	public String resendQuery(String query) {

		String exchange_c1;
		String exchange_c2;

		exchange_c1 = resendC1(query);
		if (exchange_c1.equals(Constants.OK)) {

			exchange_c2 = resendC2(query);
			if (exchange_c2.equals(Constants.OK))
				return Constants.OK;
			else
				return Constants.ERROR_HD_RESEND2;

		}else
			return Constants.ERROR_HD_RESEND1;

	}

	/**
	 * Exchange 1 again
	 * @param query
	 * @return
	 */
	public String resendC1(String query) {

		String exchange_status = channel.exchangeC1(query);

		if (exchange_status.equals(Constants.OK)) {
			System.out.println("[MANAGER HANDLER] Dispatched a query | [STATUS] Code " + exchange_status);
			return Constants.OK;

		} else {
			System.out.println("[MANAGER HANDLER] Send failed | [STATUS] Code " + exchange_status);
			return Constants.ERROR_HD_RESEND1;
		}

	}

	/**
	 * Exchange 2 again
	 * @param query
	 * @return
	 */
	private String resendC2(String query) {

		String exchange_status = channel.exchangeC2(query);

		if (exchange_status.equals(Constants.OK)) {
			System.out.println("[MANAGER HANDLER] Dispatched a query | [STATUS] Code " + exchange_status);
			return Constants.OK;

		} else {
			System.out.println("[MANAGER HANDLER] Send failed | [STATUS] Code " + exchange_status);
			return Constants.ERROR_HD_RESEND2;
		}
		
	}

	public void freezeHandler() {
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void defrostHandler() {
		this.notify();
	}

}
