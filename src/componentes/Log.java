package componentes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log {

    FileWriter fileout;
	
	public void appendToLog(String str){
		
		try {
			fileout = new FileWriter("log.txt",true);
	        BufferedWriter filebuf = new BufferedWriter(fileout);
	        PrintWriter printout = new PrintWriter(filebuf);
	        
			printout.println(str);
	        printout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
