package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class TextFileReader {
	
	
	public static void main(String[] args) throws IOException{
	}
	public static void printText(List<String> text){
		for(int i = 0; i < text.size() ; i++){
			System.out.println(text.get(i));
		}
	}
	public static List<String> readFile(String path) throws IOException{
		List<String> text = null;;
		try {
			text = Files.readAllLines(Paths.get(path));
		}catch (IOException e) {
			e.printStackTrace();
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
