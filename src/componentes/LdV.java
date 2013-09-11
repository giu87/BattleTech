package componentes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LdV {

	String hexagons;
	boolean in_ldv, hiding_parcial; 
	public LdV(Hexagon h, int n_mech, Mech target, Map map) throws IOException {

		try{
			String line;
	    	Process process = new ProcessBuilder("LDVyC.exe","mapaJ"+n_mech+".sbt",h.convert_string(),"1",target.getHexagon_actual().convert_string(),"1").start();
	    	InputStream is = process.getInputStream();
	    	InputStreamReader isr = new InputStreamReader(is);
	    	BufferedReader br = new BufferedReader(isr);

	    	int i = 0;
	    	while ((line = br.readLine()) != null) {
	    		i++;
	    		if(i == 2) hexagons = line;
	    		if(i == 4) in_ldv = str2bool(line);
	    		if(i == 6) hiding_parcial = str2bool(line);
	    	}
	    }
	    catch(IOException e){
	    }
	}
	
	private static boolean str2bool(String input){
		if(input.equals("True")) return true; else return false;
	}
	
	public boolean getLdv(){
		return in_ldv;
	}
}
