package componentes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Mech {

	/**** PARAMETROS PARA TODOS ****/
	
	int num_players;
	int player;
	boolean operating;
	boolean disconnected;
	boolean in_swamp; //paludoso
	boolean in_ground;

	Hexagon hexagon_actual;
	int side_facing; //encaramiento
	int side_torso;
	int temperature;
	boolean in_burn;
	boolean garrote;
	int garrote_type = 0;
	int [] shield_points = new int [11];
	int [] internal_points = new int[8];
	
	
	/**** PARAMETROS PARA ACTUAL ****/
	
	int walking_points;

	int running_points;
	int jumping_points;
	int radiators_on;
	int radiators_off;
	int injuries;
	boolean conscious;
	boolean [] slots_impacted = new boolean [78];
	boolean [] location_shot = new boolean [8];
	int num_municiones;
	
	/* FALTAN: municiones a expulsar */
	
	boolean [] narc;
	boolean [] i_narc;
	
	
	
	/**** PARAMETROS DE DEFMECH ****/
	
	String name;
	String model;
	
	int ton;
	int power;
	int internal_radiators;
	int radiators;
	boolean masc;
	boolean dacmtd;
	boolean dacmti;
	boolean dacmtc;
	int heat;
	boolean arms;
	boolean l_shoulder;
	boolean l_arm;
	boolean l_forearm;
	boolean l_hand;
	boolean r_shoulder;
	boolean r_arm;
	boolean r_forearm;
	boolean r_hand;
	int l_arm_armor;
	int l_torso_armor;
	int l_leg_armor;
	int r_leg_armor;
	int r_torso_armor;
	int r_arm_armor;
	int c_torso_armor;
	int head_armor;
	int l_back_torso_armor;
	int r_back_torso_armor;
	int c_back_torso_armor;
	int l_internal_arm_points;
	int l_internal_torso_points;
	int l_internal_leg_points;
	int r_internal_leg_points;
	int r_internal_torso_points;
	int r_internal_arm_points;
	int c_internal_torso_points;
	int internal_head_points;
	
	int num_equipped_components;
	
	Component [] components;
	ArrayList<Component> weapons;
	
	int weapons_number;
	int actuators_number;
	
	Actuator [] actuators;
	
	ArrayList<ArrayList<Slot>> slots = new ArrayList<ArrayList<Slot>>();
	
	// walking, running, jumping
	
	int radiators_type;
	
	
	int [] starting_shield = new int[11];
	int [] starting_internal = new int[8];
	
	public Mech(ArrayList<String> data, boolean active, Map map) {
		
		num_players = Integer.parseInt(data.get(0));
		player = Integer.parseInt(data.get(1));
		operating = str2bool(data.get(2));
		disconnected = str2bool(data.get(3));
		in_swamp = str2bool(data.get(4));
		in_ground = str2bool(data.get(5));
		System.out.println(Integer.parseInt(data.get(6).substring(0,2))+","+Integer.parseInt(data.get(6).substring(2,4)));
		hexagon_actual = map.hexagons[Integer.parseInt(data.get(6).substring(2,4)) - 1][Integer.parseInt(data.get(6).substring(0,2)) - 1];
		side_facing = Integer.parseInt(data.get(7));
		side_torso = Integer.parseInt(data.get(9));
		temperature = Integer.parseInt(data.get(8));
		
		in_burn = str2bool(data.get(10));
		in_burn = str2bool(data.get(11));

		garrote_type = Integer.parseInt(data.get(12));

		for(int i=0;i<11;i++)
			shield_points[i] = Integer.parseInt(data.get(i+13));
		for(int i=0;i<8;i++)
			internal_points[i] = Integer.parseInt(data.get(i+24));
		
		int k = 32;
		if(active){
			walking_points = Integer.parseInt(data.get(32));
			running_points = Integer.parseInt(data.get(33));
			jumping_points = Integer.parseInt(data.get(34));
			radiators_on = Integer.parseInt(data.get(35));
			radiators_off = Integer.parseInt(data.get(36));
			injuries = Integer.parseInt(data.get(37));
			conscious = str2bool(data.get(38));
			
			for(int i=0; i<78;i++)
				slots_impacted[i] = str2bool(data.get(i+39));
			for(int i=0; i<8;i++)
				location_shot[i] = str2bool(data.get(i+117));

			
			num_municiones = Integer.parseInt(data.get(125));
			k = 126;
		}
		
		narc = new boolean[num_players];
		for(int i=0;i<num_players;i++)
			narc[i] = str2bool(data.get(i+k));
		k+=num_players;
		i_narc = new boolean[num_players];
		for(int i=0;i<num_players;i++)
			i_narc[i] = str2bool(data.get(i+k));
	}
	
	public void addEquipment(ArrayList<String> data){
		
		int k = 1;
		name = data.get(k++);
		model = data.get(k++);
		ton = Integer.parseInt(data.get(k++));
		power = Integer.parseInt(data.get(k++));
		internal_radiators = Integer.parseInt(data.get(k++));
		radiators = Integer.parseInt(data.get(k++));
		masc = str2bool(data.get(k++));
		dacmtd = str2bool(data.get(k++));
		dacmti = str2bool(data.get(k++));
		dacmtc = str2bool(data.get(k++));
		heat = Integer.parseInt(data.get(k++));
		arms  = str2bool(data.get(k++));
		l_shoulder = str2bool(data.get(k++));
		l_arm = str2bool(data.get(k++));
		l_forearm = str2bool(data.get(k++));
		l_hand = str2bool(data.get(k++));
		r_shoulder = str2bool(data.get(k++));
		r_arm = str2bool(data.get(k++));
		r_forearm = str2bool(data.get(k++));
		r_hand = str2bool(data.get(k++));

		/*l_arm_armor = Integer.parseInt(data.get(k++));
		l_torso_armor = Integer.parseInt(data.get(k++));
		l_leg_armor = Integer.parseInt(data.get(k++));
		r_leg_armor = Integer.parseInt(data.get(k++));
		r_torso_armor = Integer.parseInt(data.get(k++));
		r_arm_armor = Integer.parseInt(data.get(k++));
		c_torso_armor = Integer.parseInt(data.get(k++));
		head_armor = Integer.parseInt(data.get(k++));
		l_back_torso_armor = Integer.parseInt(data.get(k++));
		r_back_torso_armor = Integer.parseInt(data.get(k++));
		c_back_torso_armor = Integer.parseInt(data.get(k++));
		l_internal_arm_points = Integer.parseInt(data.get(k++));
		l_internal_torso_points = Integer.parseInt(data.get(k++));
		l_internal_leg_points = Integer.parseInt(data.get(k++));
		r_internal_leg_points = Integer.parseInt(data.get(k++));
		r_internal_torso_points = Integer.parseInt(data.get(k++));
		r_internal_arm_points = Integer.parseInt(data.get(k++));
		c_internal_torso_points = Integer.parseInt(data.get(k++));
		internal_head_points = Integer.parseInt(data.get(k++)); */ k+=19;
		
		
		num_equipped_components = Integer.parseInt(data.get(k++)); 
		components = new Component[num_equipped_components];
		
		weapons = new ArrayList<Component>(); 
		for(int i=0;i< num_equipped_components;i++){
			components[i] = new Component(
											Integer.parseInt(data.get(k)),
											data.get(k+1),
											data.get(k+2),
											str2bool(data.get(k+3)),
											Integer.parseInt(data.get(k+4)),
											Integer.parseInt(data.get(k+5)),
											data.get(k+6),
											Integer.parseInt(data.get(k+7)),
											Integer.parseInt(data.get(k+8)),
											Integer.parseInt(data.get(k+9)),
											Integer.parseInt(data.get(k+10)),
											Integer.parseInt(data.get(k+11)),
											Integer.parseInt(data.get(k+12)),
											Integer.parseInt(data.get(k+13)),
											str2bool(data.get(k+14)),
											Integer.parseInt(data.get(k+15)),
											Integer.parseInt(data.get(k+16)),
											data.get(k+17),
											Integer.parseInt(data.get(k+19))
											);
			
			if(data.get(k+2).equals("ARMA"))
				weapons.add(components[i]);
			if(data.get(k+2).equals("MUNICION")){
				for (Component w : weapons) {
					if(w.code == components[i].weapon_code){
						w.addNumMunition();
						w.addMunition(components[i].amount);
					}
				}
			}
			
			k+=19;
		}
		
		weapons_number = Integer.parseInt(data.get(k++));
		actuators_number = Integer.parseInt(data.get(k++));
		
		actuators = new Actuator [actuators_number];
		for(int i=0; i < actuators_number;i++){
			
			actuators[i] = new Actuator(
										Integer.parseInt(data.get(k)),
										data.get(k+1),
										Integer.parseInt(data.get(k+2)),
										str2bool(data.get(k + 3)),
										Integer.parseInt(data.get(k+4))										
										);
			
			k+=5;
		}
		
		for(int i=0;i<8;i++){
			
			ArrayList<Slot> tmp = new ArrayList<Slot>();
			int num_slots = Integer.parseInt(data.get(k++));
			for(int j=0;j<num_slots;j++){
				
				tmp.add( new Slot(
								data.get(k),
								Integer.parseInt(data.get(k+1)),
								Integer.parseInt(data.get(k+2)),									
								data.get(k+3),
								Integer.parseInt(data.get(k+4)),
								Integer.parseInt(data.get(k+5)),
								Integer.parseInt(data.get(k+6))
								 )
				 	    );
				k+=7;
			}
			slots.add(tmp);
		}
		
		/*walking_points = Integer.parseInt(data.get(k++));
		running_points = Integer.parseInt(data.get(k++));
		jumping_points = Integer.parseInt(data.get(k++));
		radiators_type = Integer.parseInt(data.get(k++));*/
		
		save_starting_info();
		
	}
	
	private void save_starting_info(){
		
		try {
			
			if(!new File("armaduraInicialJ"+player+".sbt").exists()){
	            FileWriter fileout = new FileWriter("armaduraInicialJ"+player+".sbt");
	            BufferedWriter filebuf = new BufferedWriter(fileout);
	            PrintWriter printout = new PrintWriter(filebuf);
	            
				for(int i=0;i<11;i++)
					printout.println(shield_points[i]);
	
	            printout.close();
	
	            fileout = new FileWriter("estructuraInicialJ"+player+".sbt");
	            filebuf = new BufferedWriter(fileout);
	            printout = new PrintWriter(filebuf);
	
				for(int i=0;i<8;i++)
					printout.println(internal_points[i]);
	
	            printout.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void read_starting_info(){
		
		FileInputStream fstream;
		try {
			fstream = new FileInputStream("armaduraInicialJ"+player+".sbt");
			DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        
	        for(int i=0;i<11;i++)
	        	starting_shield[i] = Integer.parseInt(br.readLine());
	        
	        in.close();

			fstream = new FileInputStream("estructuraInicialJ"+player+".sbt");
			in = new DataInputStream(fstream);
	        br = new BufferedReader(new InputStreamReader(in));

	        for(int i=0;i<8;i++)
	        	starting_internal[i] = Integer.parseInt(br.readLine());
	        
	        in.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean str2bool(String input){
		if(input.equals("True")) return true; else return false;
	}
	
	/* entre 0 y 10 */
	public double mech_power_grade(){
		
		read_starting_info();
		return Math.round(0.3 * weight_grade() + 0.4 * armor_grade() + 0.3 * body_grade());
	}
	
	public double state_grade(){
		
		return 0.4 * body_grade() + 0.6 * armor_grade();
	}
	
	public double weight_grade(){
		
		if(ton < 35) return 1.0;
		if(ton >= 35 && ton < 55) return 4.0;
		if(ton >= 55 && ton < 75) return 7.0;
		return 10.0;
	}
	
	private double armor_grade(){
		double values [] = {0.08,0.1,0.05,0.05,0.1,0.08,0.15,0.15,0.08,0.08,0.08};
		
		double sum = 0;
		for(int i=0;i<11;i++){
			sum += values[i] * 10 * shield_points[i] / starting_shield[i];
		}
		
		return sum;
	}
	
	private double body_grade(){
		
		double values [] = {0.1,0.15,0.05,0.05,0.15,0.1,0.2,0.2};
		
		double sum = 0;
		for(int i=0;i<8;i++){
			sum += values[i] * 10 * internal_points[i] / starting_internal[i];
		}
		return sum;
	}
	
	public int max_large_distance(){
		int max = 0;
		for(int i=0;i<weapons.size();i++)
			if(max < weapons.get(i).getLong_distance())
				max = weapons.get(i).getLong_distance();
	
		return max;
	}
	
	public int max_minimum_distance(){
		int max = 0;
		for(int i=0;i<weapons.size();i++)
			if(max < weapons.get(i).getMinimum_distance())
				max = weapons.get(i).getMinimum_distance();
	
		return max;
	}
	
	public int max_medium_distance(){
		int max = 0;
		for(int i=0;i<weapons.size();i++)
			if(max < weapons.get(i).getMedium_distance())
				max = weapons.get(i).getMedium_distance();
	
		return max;
	}
	
	public int max_short_distance(){
		int max = 0;
		for(int i=0;i<weapons.size();i++)
			if(max < weapons.get(i).getShort_distance())
				max = weapons.get(i).getShort_distance();
	
		return max;
	}
	
	/** GETTERS AND SETTERS **/
	
	public boolean operating() {
		return operating;
	}

	public void set_operating(boolean operating) {
		this.operating = operating;
	}

	public Hexagon getHexagon_actual() {
		return hexagon_actual;
	}
	
	public int getPlayer() {
		return player;
	}

	public int getWalking_points() {
		return walking_points;
	}
	
	public int getSide_facing() {
		return side_facing;
	}

	public void setSide_facing(int side_facing) {
		this.side_facing = side_facing;
	}
	
	public ArrayList<Component> getWeapons() {
		return weapons;
	}

	public int getRadiators() {
		return radiators;
	}

	public String location_ammunition(Component weapon){
		
		String tmp ="";
		boolean exit = false;
		if(weapon.weapon_type.equals("Energía") || weapon.weapon_type.equals("Nada"))
			tmp = "-1";
		else {
            for (int i = 0; i < components.length && !exit; i++)
            {
                if (components[i].type.equals("MUNICION") && components[i].weapon_code == weapon.code && components[i].amount > weapon.shots_per_turn && components[i].operative)
                {
                    tmp = components[i].convert_location();
                    exit = true;
                }
            }
		}
		return tmp;
	}
	
    public boolean hasMunitions(Component weapon)
    {
        boolean municion = false;
        if (weapon.weapon_type.equals("Energía") || weapon.weapon_type.equals("Nada"))
        {
            municion = true;
        }
        else
        {
            for (int i = 0; i < components.length && !municion; i++)
            {
                if (components[i].type.equals("MUNICION") && components[i].weapon_code == weapon.code && components[i].amount > weapon.shots_per_turn && components[i].operative ) {
                    municion = true;
                }
                else municion = false;
            }
        }
        return municion;
    }

    public int weapon_slot (Component w) {
    	
    	int i = 0;
    	for (Slot s : slots.get(w.getItem_location())) {
    		if(w.code == s.code)
    			return i;
    		i++;
		}
    	return -1;
    }

	public int munition_slot(Component weapon) {

		int slot = 0, code = 0; //localization;
		boolean exit = false;
		int localization = -1;
        if (weapon.weapon_type.equals("Energía") || weapon.weapon_type.equals("Nada"))
            slot = -1;
        else {
            for (int i=0; i<components.length && !exit; i++){
            	if (components[i].type.equals("MUNICION") 
            			&& components[i].weapon_code == weapon.code 
            			&& components[i].amount > weapon.shots_per_turn 
            			&& components[i].operative 
            		) {
                    localization = components[i].item_location;
                    code = components[i].code;
                    exit = true;
            	}
            }
            exit = false;
          /*  int cont_slot = 1;
            int unity_first_slot_full = 0;
            for (int i=0; i<slots.get(localization).size() && !exit; i++)
            {
                if (code == slots.get(localization).get(i).code)
                {
                	if()
                		unity_first_slot_full = weapon.getTotal_munition() % 8;
                	else
                		unity_first_slot_full = slots.get(localization).get(i).getAmount();
                	cont_slot = (slots.get(localization).get(i).getAmount() * weapon.getNum_munition() - weapon.getTotal_munition()) / 8;
                	
                		
                	
                	if(unity_first_slot_full < weapon.shots_per_turn)
                		cont_slot++;
                	exit = true;
                }
            }
            */
            //Log log = new Log();
            //log.appendToLog("localization = "+localization+"\n");            
            
            int j = 0;
            for (int i=0; i<slots.get(localization).size(); i++)
            {
                if (code == slots.get(localization).get(i).code)
                {
                    //log.appendToLog("izq = "+(slots.get(localization).get(i).getAmount() * weapon.getNum_munition() - j*weapon.getTotal_munition() - weapon.getTotal_munition() + weapon.shots_per_turn)+"\n");
                	if(slots.get(localization).get(i).getAmount() * (weapon.getNum_munition() - j) - weapon.getTotal_munition() + weapon.shots_per_turn <= slots.get(localization).get(i).getAmount()){
                		weapon.addMunition(-weapon.getShots_per_turn());
                		return i;
                	}
                	else
                		j++;
                }
            }
        }
        return slot;
	}
	
	public int getTon() {
		return ton;
	}

	public boolean isIn_ground() {
		return in_ground;
	}

	public void setIn_ground(boolean in_ground) {
		this.in_ground = in_ground;
	}

}
