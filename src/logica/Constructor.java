package logica;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import componentes.Map;
import componentes.Mech;

public class Constructor {

	Mech [] mechs;
	Map map;
	public Constructor(){
		
	}

	public Mech [] construct_mech(int n_mech, Map map) {
		
		ArrayList<String> reader = read_file("mechsJ"+n_mech+".sbt");
		
		if(reader.size()!=0){
			//Object[] input = reader.toArray();
			int num_mechs = Integer.parseInt(reader.get(1));
			mechs = new Mech [num_mechs];
			int k = 2;
			ArrayList<String> tmp = new ArrayList<String>();
			for(int i=0;i<num_mechs;i++){
				
				tmp.add(Integer.toString(num_mechs));
				int j=0;
				for(j=0;j<31;j++)
					tmp.add(reader.get(j+k));
				k += j;
				if(i==n_mech) {
					for(j=0;j<94;j++)
						tmp.add(reader.get(j+k));
					k += j;
				}
				
				for(j=0;j<2*num_mechs;j++)
					tmp.add(reader.get(j+k));
				k+=2*num_mechs;
				
				mechs[i] = new Mech(tmp, i == n_mech, map);
				tmp.clear();
				// DEFMECH
				ArrayList<String> def_reader = read_file("defmechJ"+n_mech+"-"+i+".sbt"); // NON SICURO
				if(def_reader.size()!=0){
					mechs[i].addEquipment(def_reader);
				}
			}
			
			return mechs;
		}
		else{
			System.out.print("fichero no encontrado");
			return null;
		}
	}
	
	public Map construct_map(String file) {

		ArrayList<String> reader = read_file(file);
		if(reader.size()!=0){

			return new Map(reader);
		}
		else return null;
	}
	
	public void construct_options(String file){
		
	}
	
	private ArrayList<String> read_file(String file) {
		
		FileInputStream fstream;
		ArrayList<String> tmp = new ArrayList<String>();
		try {
			fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        String strLine;
	        while ((strLine = br.readLine()) != null) {
	            tmp.add(strLine);
	        }
	        in.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmp;
	}
}
