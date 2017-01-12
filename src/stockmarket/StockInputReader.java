package stockmarket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class StockInputReader {
	public static void main(String[] args){
		Scanner in = null;
		try {
			in = new Scanner(new File("nasdaqlisted.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//ArrayList arry = new ArrayList<String>();
		PrintWriter out = null;
		try {
			out = new PrintWriter("stocks.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(in.hasNextLine()){
			String str = in.nextLine();
			//System.out.println(str);
			try{
				str = str.substring(0, str.indexOf("|"));
				//arry.add(str);
				System.out.println(str);
				out.println(str);
			}catch(Exception e){
				System.out.println(str);
				continue;
			}
		}
		in.close();
		out.flush();
		out.close();
		
		
		
	}
}
