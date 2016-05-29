package gameEngine;

import java.io.IOException;

import data.Player;
import tcpConnection.TCPServer;

public class TcpGuesser implements Runnable {

	private Player player;
	private TCPServer tcp;

	public TcpGuesser(Player player) {
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

	public void sendBuffer(String buffer) {

		try {
			tcp.send(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		startGame();
	}
	
	public void startGame() {
		try {
			tcp.send("start");
			System.out.println("Sent start on guesser");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeSocket() {
		tcp.closeSocket();
		
	}

}
