package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class TextFileReader {


	public static void main(String[] args) throws IOException{
	}
	public static void printText(List<String> text){
		for(int i = 0; i < text.size() ; i++){
			System.out.println(text.get(i));
		}
	}
	public static List<String> readFile() throws IOException{
		List<String> text = new ArrayList<String>();
		InputStream in = MainClass.class.getResourceAsStream("text.txt"); 
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		while (line != null) {
			text.add(line);
			line = reader.readLine();
		}
		return text;
	}
	public static void writeFile(String path,String text) throws IOException{
		try {
			Files.write(Paths.get(path), text.getBytes(), StandardOpenOption.WRITE);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void createFile(String path) throws IOException{
		try {
			Files.write(Paths.get(path), " ".getBytes(), StandardOpenOption.CREATE_NEW);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void appendFile(String path, String text) throws IOException{
		try {
			Files.write(Paths.get(path), text.getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void eraseFile(String path) throws IOException{
		try{
			PrintWriter writer = new PrintWriter(path);
			writer.print("");
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}

	}

}
