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

	public void kill() {
		alive = false;
	}
	
	public void start(){
		new Thread(this).start();
	}

	@Override
	public void run() {

		while (alive) {
			startGame();
			propagateBuffer();
		}

	}

	public void startGame() {
		try {
			tcp.send("start");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void propagateBuffer(){
		
		try {
			System.out.println("Receber agora");
			String buffer = tcp.receive();
			System.out.println("RECEBI");
			
			for(int i = 0; i < room.getGuessers().size(); i++){
				System.out.println(i);
				room.getGuessers().get(i).sendBuffer(buffer);
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
