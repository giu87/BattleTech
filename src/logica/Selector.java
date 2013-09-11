/*TODO
 * 
 *	munizioni che mancano   
 *  cambiare criterio selezioni armi (potenza / calore)
 *  sparare su un braccio
 *  
 */

package logica;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import utility.Constants;

import componentes.Component;
import componentes.Hexagon;
import componentes.LdV;
import componentes.Log;
import componentes.Map;
import componentes.Mech;
import componentes.Road;

public class Selector {
		
	public static int n_mech;
	static Mech [] mechs;
	static Mech mech_target;
	static int strategy;
	static Map map;
	static ArrayList<Hexagon> destinations;
	static Log log = new Log();
	
	public static void main(String [] args){
		
		n_mech = Integer.parseInt(args[0]);
		String action = args[1];
		
		construct_environment();
		strategy = calculate_strategy();
		mech_target = mechs[calculate_target(mechs)];

		if(action.equals("Movimiento")){
			movment_phase();
		}
		else if (action.equals("Reaccion")) {
			reaction_phase();
		}
		else if (action.equals("AtaqueArmas")) {
			weapon_attack();
		}
		else if (action.equals("AtaqueFisico")) {
			physic_attack();
		}
		else end_turn();
	}
	
	public static void construct_environment(){
		
		Constructor c = new Constructor();
		
		map = c.construct_map("mapaJ"+n_mech+".sbt");
		mechs = c.construct_mech(n_mech, map);
		//c.construct_options("configJ"+n_mech+".sbt");

	}
	
	private static void movment_phase() {
		
		System.out.println("strategy mech "+n_mech+": "+strategy);
		log.appendToLog("\n\n***Fase di Movimento***\n");
		log.appendToLog("Posizione attuale: "+mechs[n_mech].getHexagon_actual().toString()+", encaramiento = "+ mechs[n_mech].getSide_facing());
		//log.appendToLog("Punti di movimento disponibili:"+mechs[n_mech].getWalking_points());
		if(strategy == 1) log.appendToLog("--- ATTENZIONE --- , MODALITA' DIFENSIVA");
		System.out.print(mech_target.getPlayer()+"");
		destinations = select_destination(mech_target);
		// vedere SALTO
		
        Road [] possible_roads = new Road[destinations.size()];
        int[] road_grade = new int[destinations.size()];
        int value = -200; 
        int destino = 0;
        for (int i = 0; i < destinations.size(); i++)
        {
        	possible_roads[i] = new Road(n_mech, destinations.get(i), map, strategy, mech_target.getPlayer(), mechs);
        	road_grade[i] = calculate_grade(possible_roads[i].final_hex(), mech_target);
            if (road_grade[i] > value && (inside_ldv(possible_roads[i].final_hex(), mech_target) || i == destinations.size() - 1))
			{
			    destino = i;
			    value = road_grade[i];
			}
        }
        
        possible_roads[destino].toFile(n_mech);
        log.appendToLog(possible_roads[destino].convert_string());
	}	

	/** devuelve 0 si ofensiva, 1 si difensiva **/ 
	private static int calculate_strategy(){
		
		if(mechs.length == 2) return 0; 
		if(mechs[n_mech].mech_power_grade() > 7)  // estrategia agresiva
			return 0;
		
        int max=-1;
        double nota = 0;
        for (int i = 0; i < mechs.length; i++){
            if (mechs[i].operating() && nota < mechs[i].mech_power_grade() ) {
                nota = mechs[i].mech_power_grade();
                max = i;
            }
        }
		if(max == -1) return 0;
        if (max == n_mech || Math.abs(mechs[n_mech].mech_power_grade() - mechs[max].mech_power_grade()) <= 0.4)
            return 0;
        return 1;
	}
	
	private static int calculate_target(Mech[] m) {
		
		/*calcoliamo il + debole/vicino*/
		if(m.length == 1) return 0;
		double min = 1000, max = 0;
		int i_min, i_max;
		if(n_mech != 0){
			i_min = 0; i_max = 0;
			//min = m[0].mech_power_grade() + map.distance_int(m[n_mech].getHexagon_actual(),m[0].getHexagon_actual());
			//max = min; i_max = 0; i_min = 0;
		}
		else{
			i_min = 1; i_max = 1;
			//min = m[1].mech_power_grade() + map.distance_int(m[n_mech].getHexagon_actual(),m[1].getHexagon_actual());
			//max = min; i_max = 1; i_min = 1;
		}

		for(int i=0; i<m.length;i++){
			if(i!= n_mech && m[i].operating()){
				double power = m[i].mech_power_grade();
				int distance =  map.distance_int(mechs[n_mech].getHexagon_actual(),m[i].getHexagon_actual());
				if (min > power + distance) {i_min = i; min = power + distance;}
				if(max < power + distance) {i_max = i; max = power + distance; }
			}
		}
		if(strategy == 0 )  //estrategia ofensiva	
			return  i_min;
		else return  i_max;
	}
	
	private static ArrayList<Hexagon> select_destination(Mech target) {
		
		ArrayList<Hexagon> hexagons_reachable;
		if(strategy == 0)
			hexagons_reachable = map.hexagons_reachable(mech_target.getHexagon_actual(), Constants.radio, mechs, n_mech);
		else{ // strategia difensiva
			hexagons_reachable = map.hexagons_reachable(mechs[n_mech].getHexagon_actual(), Constants.radio, mechs, n_mech);
		}
		
		int[] grades = new int[hexagons_reachable.size()];
		for(int i=0;i<hexagons_reachable.size();i++)
			grades[i] = calculate_grade(hexagons_reachable.get(i),mech_target);
		
        int max = 0;
        for (int i=0; i<grades.length;i++)
            if (grades[i] > max)
                max = grades[i];

        boolean salir = false; int tot = 0;
        ArrayList<Hexagon> choosedRoads = new ArrayList<Hexagon>();
        while (!salir)
        {
            for (int i=0; i<grades.length && !salir;i++)
            {
                if (grades[i] == max)
                {
                    choosedRoads.add(hexagons_reachable.get(i));
                    tot++;
                }
                if (tot >= Constants.number_routes)  salir = true;
            }
        }
        return choosedRoads;
	}
	
	private static int calculate_grade(Hexagon hexagon, Mech target) {
		
		int grade = 0;
		if(strategy == 0){
			
			if(map.distance_int(hexagon,target.getHexagon_actual()) <= Constants.radio) 
				grade += Constants.radio - map.distance_int(hexagon,target.getHexagon_actual());
			
			return grade + hexagon.getValuation(strategy);
		}
		else{
			grade -= hexagon.getLevel();
			grade += hexagon.getValuation(strategy);
			// raggiungibilità
			for(int i = 0; i < mechs.length;i++){
				if(i!= n_mech){
					int distance = map.distance_int(hexagon, mechs[i].getHexagon_actual());
					if(distance > mechs[i].max_large_distance())
						grade += Math.round(mechs[i].weight_grade()) * 1;

					if(distance > mechs[i].max_medium_distance())
						grade += Math.round(mechs[i].weight_grade()) * 2;

					if(distance > mechs[i].max_short_distance())
						grade += Math.round(mechs[i].weight_grade()) * 3;

					if(distance > mechs[i].max_minimum_distance())
						grade += Math.round(mechs[i].weight_grade()) * 4;
					
					if(mechs[n_mech].getHexagon_actual().bottom_cone(mechs[i].getHexagon_actual(), mechs[i].getSide_facing()))
						grade += 5;
					if(mechs[n_mech].getHexagon_actual().front_cone(mechs[i].getHexagon_actual(), mechs[i].getSide_facing()))
						grade -= 5;
				}
			}
		}
		System.out.println("hexagon: "+hexagon.convert_string()+", grade: "+grade);
		return grade;
	}
	
    private static boolean inside_ldv(Hexagon h, Mech objetivo) 
    {
        try {
			LdV line = new LdV(h, n_mech, objetivo, map);
			return line.getLdv();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return true;
    }

	private static void reaction_phase() {

		log.appendToLog("\n\n***Fase di Reaccion***\n");
		
		// permanece igual en caso de front cone y strtegia defensiva
		// derecha en caso de right_cone
		// izquierda en casos de left cone y bottom cone 
		if(strategy == 0 && !mechs[n_mech].getHexagon_actual().front_cone(mech_target.getHexagon_actual(), mechs[n_mech].getSide_facing())){
			if(mechs[n_mech].getHexagon_actual().right_cone(mech_target.getHexagon_actual(), mechs[n_mech].getSide_facing())){
				write_action("Derecha", "accionJ"+n_mech+".sbt");
				log.appendToLog("Derecha");
			}
			else {
				write_action("Izquierda", "accionJ"+n_mech+".sbt");
				log.appendToLog("Izquierda");
			}
					
		}
		else{ 
			write_action("Igual", "accionJ"+n_mech+".sbt");
			log.appendToLog("Nothing_to_do\n");
		}
	}

	private static void weapon_attack() {

		log.appendToLog("\n\n***Fase di Ataque con armas***\n");
		ArrayList<Mech> mechs_reachables = new ArrayList<Mech> ();
		double max_shot_distance = mechs[n_mech].max_large_distance();
		LdV line;
		
		for(int i=0;i<mechs.length;i++){ // encuentro los a que puedo desparar
			if(i != n_mech){				
				try {
					line = new LdV(mechs[n_mech].getHexagon_actual(),n_mech,mechs[i],map);
				
					if(line.getLdv() && 
							mechs[i].operating() &&
							map.distance(mechs[n_mech].getHexagon_actual(), mechs[i].getHexagon_actual()) <= max_shot_distance &&
							!mechs[n_mech].getHexagon_actual().bottom_cone(mechs[i].getHexagon_actual(), mechs[n_mech].getSide_facing())) //tengo LdV, posible objetivo			
						mechs_reachables.add(mechs[i]);				
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
		
		Mech target = null;
		//puedo ataquear con fisico?
		for(int i=0;i<mechs.length;i++){
			if(map.distance_int(mechs[n_mech].getHexagon_actual(), mechs[i].getHexagon_actual()) == 1 
				    && 
			   !mechs[n_mech].getHexagon_actual().bottom_cone(mechs[i].getHexagon_actual(), mechs[n_mech].getSide_facing())
			   		&&
			   mechs[n_mech].getHexagon_actual().getLevel() == mechs[i].getHexagon_actual().getLevel()
			)  // puedo ataquar el mech
				target = mechs[i];
		}
		
		boolean attBD = false,attBI = false, attPI = false, attPD = false;
		Double att_fis = (double)mechs[n_mech].getTon() / 10;
		if(target!= null){
			attBD = att_fis >= better_fis_arma(5);
			attBI = att_fis >= better_fis_arma(0);
			if(attBI || attBD){
				attPI = false; attPD = false;
			}
			else{				
				if(better_fis_arma(2) > better_fis_arma(3))	
					attPI = att_fis  >= better_fis_arma(2);
				else
					attPD = att_fis >= better_fis_arma(3);
			}
		}

		createFileForPhysicalAttack(attBD,attBI,attPI,attPD);

		ArrayList<Component> shooting_weapons = new ArrayList<Component>();
		ArrayList<Mech> targets = new ArrayList<Mech>();
			
		Mech mechs_reachables_ordered [] = ording_mechs(mechs_reachables);
		boolean [] weapons_used = new boolean [mechs[n_mech].getWeapons().size()];
		for(int i=0;i<weapons_used.length;i++)
			weapons_used[i] = false;
		for (int i=0;i<mechs_reachables_ordered.length;i++) {
			
			Component [] weapons_ordered = ording_weapons(mechs[n_mech]);
			int accumulate_heat = 0;
			for (int j=0;j<weapons_ordered.length;j++) { 
				if(accumulate_heat + weapons_ordered[j].getHeat() < mechs_reachables_ordered[i].getRadiators()
						&&
					map.distance(mechs[n_mech].getHexagon_actual(), mechs_reachables_ordered[i].getHexagon_actual()) < weapons_ordered[j].getLong_distance()
						&&
					mechs[n_mech].hasMunitions(weapons_ordered[j])
						&&
					!weapons_used[j]) {
					
					if(! (
							mechs[n_mech].getHexagon_actual().bottom_cone(mechs_reachables_ordered[i].getHexagon_actual(), mechs[n_mech].getSide_facing())
								||
							weapons_ordered[j].convert_location().equals("BD") && (mechs[n_mech].getHexagon_actual().left_cone(mechs_reachables_ordered[i].getHexagon_actual(), mechs[n_mech].getSide_facing()) || attBD)
								||
							weapons_ordered[j].convert_location().equals("BI") && (mechs[n_mech].getHexagon_actual().right_cone(mechs_reachables_ordered[i].getHexagon_actual(), mechs[n_mech].getSide_facing()) || attBI)
						  		||
						  	weapons_ordered[j].convert_location().equals("PD") && attPD
						  		||
						  	weapons_ordered[j].convert_location().equals("PI") && attPI
						  		||
					  		!weapons_ordered[j].convert_location().equals("BD") && !weapons_ordered[j].convert_location().equals("BI") && mechs[n_mech].getHexagon_actual().right_cone(mechs_reachables_ordered[i].getHexagon_actual(), mechs[n_mech].getSide_facing())
					  			||
					  		!weapons_ordered[j].convert_location().equals("BD") && !weapons_ordered[j].convert_location().equals("BI") && mechs[n_mech].getHexagon_actual().left_cone(mechs_reachables_ordered[i].getHexagon_actual(), mechs[n_mech].getSide_facing())
						  )
					){
						shooting_weapons.add(weapons_ordered[j]);
						targets.add(mechs_reachables_ordered[i]);
						weapons_used[j] = true;
						accumulate_heat += weapons_ordered[j].getHeat();
					}
				}
			}
		}
		write_action(convert_string(shooting_weapons, targets),"accionJ"+n_mech+".sbt");
		String str = convert_string(shooting_weapons, targets);
		log.appendToLog(str);
	}

	private static Double better_fis_arma(int location) {
		
		Double sum = new Double(0);
		for(int i=0; i<mechs[n_mech].getWeapons().size();i++){
			if(mechs[n_mech].getWeapons().get(i).getItem_location() == location){
				sum += 5 * (double) mechs[n_mech].getWeapons().get(i).getHarm() / (double) mechs[n_mech].getWeapons().get(i).getHeat();
			}
		}
		return sum;
	}

	private static Component[] ording_weapons(Mech mech) {

		Component [] tmp_array = new Component[mech.getWeapons().size()];
		tmp_array = mech.getWeapons().toArray(tmp_array);
		Component tmp;
		for(int i=0;i<tmp_array.length-1;i++){
			for (int j=i+1;j<tmp_array.length;j++) {
				if(tmp_array[j].getHarm() * tmp_array[j].getShots_per_turn() > tmp_array[i].getHarm() * tmp_array[i].getShots_per_turn()) {
					tmp = tmp_array[i];
					tmp_array[i] = tmp_array[j];
					tmp_array[j] = tmp;
				}
			}
		}
		return tmp_array;
	}

	private static Mech[] ording_mechs(ArrayList<Mech> mechs_reachables) {
		
		Mech ret [] = new Mech [mechs_reachables.size()];
		Mech[] tmp = new Mech [mechs_reachables.size()];
		tmp = mechs_reachables.toArray(tmp);
		int index;
		for (int i=0;i<mechs_reachables.size();i++) {
			index = calculate_target(tmp);
			ret[i] = tmp[index];
			Mech[] tmp2 = tmp;
			tmp = new Mech [tmp2.length - 1];
			int k = 0;
			for(int j = 0;j<tmp2.length;j++){
				if(j!= index){
					tmp[k] = tmp2[k];
					k++;
				}
			}
			/*tmp = new Mech [tmp2.length];
			for(int j=0;j< tmp2.length;j++)
				tmp[j] = tmp2[j];*/
		}
		
		return ret;
		/*Mech tmp_array [] = new Mech [mechs_reachables.size()];
		tmp_array = mechs_reachables.toArray(tmp_array);
		Mech tmp;
			
		for (int i=0;i<tmp_array.length-1;i++) { //ordenamiento mechs
			for (int j=i+1;j<tmp_array.length;j++) {
				if(tmp_array[j].mech_power_grade() > tmp_array[i].mech_power_grade()) {
					tmp = tmp_array[i];
					tmp_array[i] = tmp_array[j];
					tmp_array[j] = tmp;
				}
			}
		}
		return tmp_array; */
	}
	
	private static void createFileForPhysicalAttack(boolean attBD,
			boolean attBI, boolean attPI, boolean attPD) {

		String s = "";
		if(attBD) s+= "True\n"; else s+="False\n";
		if(attBI) s+= "True\n"; else s+="False\n";
		if(attPD) s+= "True\n"; else s+="False\n";
		if(attPI) s+= "True\n"; else s+="False";
		write_action(s, "physical_attack.sbt");
	}
	
	private static String convert_string(ArrayList<Component> shooting_weapons,
			ArrayList<Mech> targets) {

		String str="";
		str += "False"  +"\n"; //garrote
		str += mech_target.getHexagon_actual().convert_string() +"\n"; // objetivo primario
		str += shooting_weapons.size() +"\n";
		
		int i = 0;
		for (Component weapon : shooting_weapons) {
			
			str += weapon.convert_location() + "\n";
			str += mechs[n_mech].weapon_slot(weapon) + "\n";
			str += "False" + "\n"; //doble cadencia
			str += mechs[n_mech].location_ammunition(weapon) + "\n";
			str += mechs[n_mech].munition_slot(weapon) + "\n";
			str += targets.get(i).getHexagon_actual().convert_string() + "\n";
			str += "Mech" + "\n"; // no ataques a hexagones
			i++;		
		}
		return str;
	}


	private static void physic_attack() {

		log.appendToLog("\n\n***Fase di Ataque Fisico***\n");
		String where,str ="";
		Mech target = null;
		Mech my = mechs[n_mech];
		int num = 0;
		boolean c = false;
		
		boolean [] possibly_attack = read_possibility();
		for(int i=0;i<mechs.length;i++){
			if(mechs[i].operating() && map.distance_int(mechs[n_mech].getHexagon_actual(), mechs[i].getHexagon_actual()) == 1 && !mechs[n_mech].getHexagon_actual().bottom_cone(mechs[i].getHexagon_actual(), mechs[n_mech].getSide_facing()))  // puedo ataquar el mech
				target = mechs[i];
		}
			
		if(target!= null && !my.isIn_ground()){
			
			if(my.getHexagon_actual().right_cone(target.getHexagon_actual(), my.getSide_facing())){
				where = "Derecha";
			}
			else if(my.getHexagon_actual().left_cone(target.getHexagon_actual(), my.getSide_facing())){
				where = "Izquierda";
			}
			else where = "Adelante";
			
			int dlevel = my.getHexagon_actual().getLevel() - target.getHexagon_actual().getLevel();
			
			if(dlevel == 0 && possibly_attack[0] && (where.equals("Derecha") || where.equals("Adelante"))){
				num++;
				str += "BD\n";
				str += "1000\n";
				str += target.getHexagon_actual().convert_string() +"\n";
				str += "Mech\n";
			}
			
			if(dlevel == 0 && possibly_attack[1] &&(where.equals("Izquierda") || where.equals("Adelante"))){
				num++;
				str += "BI\n";
				str += "1000\n";
				str += target.getHexagon_actual().convert_string() +"\n";
				str += "Mech\n";
			}
			
			if(dlevel == 0 && possibly_attack[2] && where.equals("Adelante")){
				num++;
				str += "PD\n";
				str += "2000\n";
				str += target.getHexagon_actual().convert_string() +"\n";
				str += "Mech\n";
				c = true;
			}
			
			if(dlevel == 0 && possibly_attack[3] && where.equals("Adelante") && !c){
				num++;
				str += "PI\n";
				str += "2000\n";
				str += target.getHexagon_actual().convert_string() +"\n";
				str += "Mech\n";
			}
		}
		else str = "0\n";
		
		write_action(num+"\n"+str, "accionJ"+n_mech+".sbt");
		log.appendToLog(str);
	}
	

	private static boolean[] read_possibility() {

		FileInputStream fstream;
		boolean [] b = new boolean[4];
		try {
			fstream = new FileInputStream("physical_attack.sbt");
			DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        String strLine;
	        int i = 0;
	        while (((strLine) = br.readLine()) != null) {
	        	System.out.println(strLine);
	            if(strLine.equals("True")) b[i] = true;
	            else b[i]=false;
	            i++;
	        }
	        in.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;

	}

	private static void end_turn() {

		log.appendToLog("\n\n***Fase di Fin de turno***\n");
		String s = "";
		s += "0" +"\n";
		s += "0" +"\n";
		s += "False" +"\n";
		s += "0" +"\n";
		write_action(s, "accionJ"+n_mech+".sbt");
		log.appendToLog(s);

	}
	
	private static void write_action(String str, String file){
        FileWriter fileout;
		try {
			fileout = new FileWriter(file);
	        BufferedWriter filebuf = new BufferedWriter(fileout);
	        PrintWriter printout = new PrintWriter(filebuf);
			printout.println(str);
	        printout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
