package gameEngine;

import java.io.IOException;

import data.Player;
import tcpConnection.TCPServer;

public class TcpDrawer implements Runnable {

	private GameRoom room;
	private Player player;
	private TCPServer tcp;

	private Boolean alive = true;

	public TcpDrawer(GameRoom room, Player player) {
		this.room = room;
		this.player = player;

		try {
			this.tcp = new TCPServer(this.player.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}

		tcp.acceptSocket();
	}

	public void start() {
		new Thread(this).start();
	}

	public void kill() {
		alive = false;
	}

	@Override
	public void run() {

		startGame();

		while (alive) {
			propagateBuffer();
		}

	}

	public void startGame() {
		try {
			tcp.send("start");
			System.out.println("sent start");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void propagateBuffer() {

		try {

			String buffer = tcp.receive();
			
			if (buffer != null) {
				
				System.out.println("Buffer " + buffer);
				
				for (int i = 0; i < room.getGuessers().size(); i++) {
					room.getGuessers().get(i).sendBuffer(buffer);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
