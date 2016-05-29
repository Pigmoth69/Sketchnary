package gameEngine;

import java.io.IOException;

import data.Player;
import tcpConnection.TCPServer;

public class TcpGuesser implements Runnable {

	private GameRoom gameRoom;
	private Player player;
	private TCPServer tcp;

	public TcpGuesser(GameRoom gameRoom, Player player) {
		this.gameRoom = gameRoom;
		this.player = player;

		try {
			this.tcp = new TCPServer(this.gameRoom.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tcp.acceptSocket();
	}

	public void start() {
		new Thread(this).start();
	}

	public void sendBuffer(String buffer) {

		String info = player.getEmail() + "&";
		
		String toSend = info + buffer;
		
		try {
			tcp.send(toSend);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		startGame();
	}
	
	public void startGame() {
		
		String info = player.getEmail() + "&";
		String toSend = info + "start";
		
		try {
			tcp.send(toSend);
			System.out.println("Sent start on guesser");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
