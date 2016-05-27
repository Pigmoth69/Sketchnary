package gameEngine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Category {
	
	public Category() {
	}

	public String getRandomCategory() {
		return readWordFile();
	}

	public String readWordFile() {

		try (BufferedReader br = new BufferedReader(
				new FileReader("C:\\Users\\utilizador\\git\\Sketchnary\\Words\\words.txt"))) {
			
			String line;
			int counter = 1;
			int line_number = generateRandom(5001, 1);
			
			while ((line = br.readLine()) != null & counter != line_number) {
				counter++;
			}
			
			return line;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;

	}

	public int generateRandom(int max, int min) {

		Random rand = new Random();

		return rand.nextInt((max - min) + 1) + min;

	}

}
