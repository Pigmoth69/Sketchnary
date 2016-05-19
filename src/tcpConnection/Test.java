package tcpConnection;

import java.io.IOException;
import java.util.Scanner;

public class Test {
	public static void main(String args[]){
		
		String type, message;
		
		System.out.println("inicio");
		
		if (args.length != 2){
			Scanner scanner = new Scanner(System.in);
			System.out.print("\"server\" ou \"client\": ");
			type = scanner.nextLine();
			System.out.print("mensagem a enviar: ");
			message = scanner.nextLine();
			scanner.close();
		}
		else{
			type = args[0];
			message = args[1];
		}
		
		if (type.equals("server")){
			try {
				TCPServer sv = new TCPServer(1234);
				String received = sv.receive();
				System.out.println("server recebeu: " + received);
				sv.send(message);
				
				System.out.println("server terminado");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (type.equals("client")){
			try {
				TCPClient cl = new TCPClient("127.0.0.1", 1234);
				
				cl.send(message);
				String received = cl.receive();
				System.out.println("client recebeu: " + received);
				System.out.println("client terminado");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			System.out.println(type);
		}
		
	}
}
