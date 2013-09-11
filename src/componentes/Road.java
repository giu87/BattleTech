package componentes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Road {

	int strategy;
	ArrayList<Node> road = new ArrayList<Node>();
	int ending_side_facing;
	boolean get_up;
	String debug = "";
	List<Node> road_choosed;
	
	@SuppressWarnings("null")
	public Road(int n_mech, Hexagon destination, Map map, int strategy, int target,
			Mech[] mechs) {

		get_up = false;
		List<Node> road = null, tmp;
		this.strategy = strategy;
		
        if ((mechs[n_mech]).getWalking_points() != 0){
        	
        	if(this.strategy == 1){
        		road = pathfinder(n_mech, destination, map, target, mechs);
        		if(road == null){
        			this.strategy = 0;
        			road = pathfinder(n_mech, destination, map, target, mechs);
        		}
        	}
        	else{
                tmp = pathfinder(n_mech, destination, map, target, mechs);
                
                
                /*if (mechs[n_mech].getHexagon_actual()!= destination && tmp.size() == 1)
                {
                    //tmpstring = debug;
                    // debug = "";

                    this.strategy = 1;
                    if ((road = pathfinder(n_mech, destination, map, target, mechs)) == null)
                    {
                        // debug = tmpstring;
                        road = tmp;
                    }
                }
                else */
                    road = tmp;
        	}
        }
        else{
        	Node e = new Node();
        	e.setHexagon(mechs[n_mech].getHexagon_actual());
        	e.setDirection(mechs[n_mech].getSide_facing());
        	road.add(e);
        	ending_side_facing = e.getDirection();
        	get_up = false;
        }
	}

	private List<Node> pathfinder(int n_mech, Hexagon dest, Map map,
			int target, Mech[] mechs) {

        ArrayList<Node> closed = new ArrayList<Node>();
        ArrayList<Node> opened = new ArrayList<Node>();
       // ArrayList<Node> roads = new ArrayList<Node>();
        Node element = new Node();
        int accumulate_g = 0, i_value = 0, better = 0, value = 0;
        //boolean nueva = false;
        
        element.setHexagon(mechs[n_mech].getHexagon_actual());
        element.setG(0);
        element.setH(map.distance_int(mechs[n_mech].getHexagon_actual(), dest));
        element.setF(element.getH());
        element.setDirection(mechs[n_mech].getSide_facing());
        element.setFather(element);
        
        // si le faltan las piernas
        if(!(mechs[n_mech].internal_points[3]>0) || !(mechs[n_mech].internal_points[3]>0)){ //fermo a terra
        	
        	road.add(element);
        	ending_side_facing = better_side_facing(mechs[n_mech], mechs[target], mechs[n_mech].getHexagon_actual());
        	if(side_facing_cost(element.getDirection(), ending_side_facing) > mechs[n_mech].walking_points)
        		ending_side_facing = element.getDirection();
        	
        	return road;
        }
        
        // si me levanto y no hago nada
        if(mechs[n_mech].isIn_ground() && mechs[n_mech].getWalking_points() > 1){
        	get_up = true;
        	ending_side_facing = better_side_facing(element, mechs[target].getHexagon_actual(), map, mechs[n_mech].getSide_facing());
        	element.setDirection(better_side_facing(element, mechs[target].getHexagon_actual(), map, mechs[n_mech].getSide_facing()));
        	road.add(element);
        	return road;
        }
        
        closed.add(element);
        int side = mechs[n_mech].getSide_facing(); 
        Node actual = element;
        
        if(element.getHexagon()!= dest){
        	
        	while(!actual.getHexagon().egual(dest)){
        		
        		for(int i=1; i<7; i++){
        			
        			element = new Node();
                    	if(!map.reachable(actual.getHexagon().column, actual.getHexagon().row, i))
                    		continue;
                    	element.setHexagon(map.adjacent(actual.getHexagon(),i));

                    if(already_visited(closed,element.getHexagon()))
        				continue;
        			
        			value = map.movment_cost(actual.getHexagon(),element.getHexagon());
        			value 
        			+= map.side_facing_cost(actual.getHexagon(),side,element.getHexagon());
        			
        			if(
        				value > 100 ||
        				map.occupied_hexagon(element.getHexagon(),mechs,n_mech) ||
        				element.getHexagon().getLevel() < 0 && element.getHexagon().type_terreno == 2 && strategy == 1
        			)		continue;
        			
        			if(value >= 0){
        				
        				element.setG(element.getHexagon().movment_modifiers() + value + accumulate_g);  
                        if ((i_value = in(opened, element.getHexagon())) >= 0)
                        {
                            if (element.getG() >= (((Node)opened.get(i_value)).getG()))
                                continue;
                            else
                            {
                                element.setH((((Node)opened.get(i_value)).getH()));
                                element.setF(element.getG() + element.getH());
                                element.setFather(actual);
                                element.setDirection(i);
                                opened.set(i_value, element);
                            }
                        }
                        else{
                            element.setH(map.distance_int(element.getHexagon(), dest)); // rivedere
                            element.setF(element.getG() + element.getH());
                            element.setFather(actual);
                            element.setDirection(i);
                            opened.add(element);
                        }
        				
        			}
        		}
        		
                if (opened.size() != 0)
                {
                    better = better_opened_node(opened, actual.getHexagon(), dest);
                    side = calculate_side(actual, (Node)opened.get(better), map);
                    accumulate_g = (((Node)opened.get(better)).getG());
                    closed.add(opened.get(better));
                    actual = ((Node)opened.get(better));                    
                    opened.remove(better);
                }
        	}
        }
        
        if( actual.getHexagon().egual(dest)){
        	
        	element = new Node();
        	element.setHexagon(dest);
        	element.setH(0);
        	element.setF(0);
        	element.setFather((Node)(closed.get(closed.size() - 1)));
        	element.setDirection(element.getDirection());
        	element.setG(element.getFather().getG() + element.getHexagon().movment_modifiers() + map.distance_int(element.getFather().getHexagon(),element.getHexagon()));

        	road.add((Node)(closed.get(closed.size() - 1)));
        	
        	Node father = (Node)(closed.get(closed.size() - 1));
            do
            {
                road.add(father.getFather());
                father = father.getFather();
            } while (father.getHexagon() != mechs[n_mech].getHexagon_actual());

           remove_water_penalty(road);

            value = calculater_road(road, dest, mechs[n_mech], map, mechs[target]);
            
            for(int i = value - 1;i>=0;i--){
            	road.remove(i);
            }
            
            road_choosed = road;            
            Collections.reverse(road_choosed);
            
            if(road_choosed.size()>1){
                //Para caminos donde el destino y inicio coinciden
                if(road_choosed.get(0)==road_choosed.get(1))
                	road_choosed.remove(1);
            }
            else {
            	ending_side_facing = better_side_facing(road_choosed.get(0), mechs[target].getHexagon_actual(), map, road_choosed.get(0).getDirection());
            }
        }
        else
        	road_choosed.add((Node)closed.get(0));

        debugString(road_choosed, n_mech, target, mechs, false); 
        return road_choosed;
	}
	
	private int calculate_side(Node actual, Node node, Map map) {
		
		for(int i=1;i<7;i++){
			if(map.reachable(actual.getHexagon().column,actual.getHexagon().row, i))
				if(map.adjacent(actual.getHexagon(),i) == node.getHexagon())
					return i;  
		}
		return 0;
	}

	/*private boolean possibly_running(List<Node> road) {
		for (Node i : road) {
			if ((i.getHexagon().type_terreno == 2 && i.getHexagon().getLevel() < 0) && strategy==1)
                return false;
        }
        return true;
    }*/	
	
	private int calculater_road(ArrayList<Node> road, Hexagon dest, Mech mine, Map map, Mech target) {

        int mov_points, suelo = 0;
        int tmp= 1;

       // accumulated_cost_side_facing(road);
        //Determinamos los puntos de movimiento de los que disponemos
        if (strategy == 1)
        {
        	mov_points = mine.running_points - suelo;
        }
        else {
            mov_points= mine.walking_points - suelo;
        }

        if (mov_points < 0)
        {
            get_up = false;
            ((Node)road.get(0)).setDirection(tmp);
        }
        else
        {
            for (int i = 0; i < road.size(); i++)
            {
                if (((Node)road.get(i)).getG() <= mov_points)
                {
                    if(i!=0){
                        tmp = better_side_facing((Node)road.get(i), target.getHexagon_actual(), map, ((Node)road.get(i-1)).getDirection());
                    }else
                        tmp = better_side_facing(((Node)road.get(i)), target.getHexagon_actual(), map, 0);
                    
                    if ((((Node)road.get(i)).getG() + side_facing_cost(((Node)road.get(i)).getDirection(), tmp) <= mov_points))
                    {
                        ending_side_facing = tmp;
                        return i;
                    }
                }
            }
        }
        return -1;	
    }

	/*private void accumulated_cost_side_facing(ArrayList<Node> road2) {
        int accumulated=0;
        for (int i = road.size() - 2; i != -1; i--) { 
            if(((Node)road.get(i+1)).getDirection()!=((Node)road.get(i)).getDirection()){
                accumulated += side_facing_cost(((Node)road.get(i+1)).getDirection(), ((Node)road.get(i)).getDirection());
            }
            ((Node)road.get(i)).setG(accumulated + ((Node)road.get(i)).getG());
        }
    }*/
	
	private ArrayList<Node> remove_water_penalty(ArrayList<Node> road) {
        
		int v = 0;
        for (int i = road.size() - 2; i > 1; i--)
        {
            ((Node)road.get(i)).setG( ((Node)road.get(i)).getG() - 5 * v ) ;
            if (((Node)road.get(i)).getHexagon().type_terreno == 2)
                v++;
        }
        return road;		
	} 
	
	private int better_opened_node(ArrayList<Node> opened, Hexagon father, Hexagon dest) {
		
        int max = 0;
        for (int i=1; i < opened.size(); i++)
        {
            if ((((Node)opened.get(i)).getHexagon() == dest) && (((Node)opened.get(i)).getFather().getHexagon() == father))
            	return i;
            if (((Node)opened.get(max)).getF() > (((Node)opened.get(i)).getF()))
            	max = i;
        }
        return max;	
    }
	
	private int side_facing_cost(int now, int after) {
		
		int ret;
		if(Math.abs(now - after) >=4) ret =  (Math.abs(now - after) * 2) % 3;
		else ret = Math.abs(now - after);
		return ret;
	}
	
	private int better_side_facing(Mech mine, Mech enemy, Hexagon h) {
		
        for (int i = 1; i < 7; i++)
        {
            if (h.front_cone(enemy.getHexagon_actual(), i))
            {
                return i;
            }
        }
        return 0;
    }
	
	private int better_side_facing(Node node, Hexagon h_target, Map map, int direction) {

        ArrayList<Integer> l = new ArrayList<Integer>();
        int min, tmp, i_tmp = 0; direction = 0;

        l = possible_side_facing(node, h_target, map);
        min = side_facing_cost(node.getDirection(), l.get(0).intValue());

        for (int c = 1; c < l.size(); c++)
        {
            tmp = side_facing_cost(node.getDirection(), l.get(c).intValue());
            
            if (c == direction)
                return c;
            
            if (tmp < min)
            {
                i_tmp = c;
                min = tmp;
            }
        }

        return l.get(i_tmp).intValue();	
	}

	private ArrayList<Integer> possible_side_facing(Node o, Hexagon dest, Map map) {

        int min =10000, tmp = 0;
        Hexagon h;
        ArrayList<Integer> l = new ArrayList<Integer>();
        //cal
        for (int i = 1; i < 7; i++)
        {
            try
            {
                h = map.adjacent(o.getHexagon(), i);

                if (h == dest)
                {
                    l.add(i);
                    return l;
                }

                tmp = map.distance_int(h,dest);
                if (min > tmp)
                    min = tmp;
            }
            catch (Exception e) { }
        }


        for (int i = 1; i < 7; i++)
        {
            try { 
                tmp = map.distance_int(map.adjacent(o.getHexagon(), i),dest);

                if (tmp == min)
                {
                    min = tmp;
                    l.add(i);
                }
            }
            catch(Exception e) { }
        }
        return l;
	}
	
	private int in(ArrayList<Node> list, Hexagon h) {

		int num=0;
		for (Node i : list) {
			if(i.getHexagon() == h)
				return num;
			num++;
		}
		return -1;
	}
	private boolean already_visited(ArrayList<Node> list, Hexagon h) {

		for (Node i : list) {
			if(i.getHexagon() == h)
				return true;
		}
		return false;
	}
	
    public Hexagon final_hex() {
        return road.get(road.size() - 1).getHexagon();
    }
	
	
    private void debugString(List<Node> camino, int my, int objetivo, Mech[] mechs, boolean ideal) {
        int j = 0;
        
        if (ideal) {
            debug += "\tIntenta ir a el destino: " +((Node)camino.get(camino.size()-1)).getHexagon().toString()+" con " + 
                (strategy == 1 ? (mechs[my]).running_points : (mechs[my]).walking_points)
                + "PM de " + (strategy == 1 ? "correr" : "andar") + ". Encarandose al mech J-" + mechs[objetivo].player +": "+ mechs[objetivo].name + "\n";


        }
        else
        {
            debug += "\tEl Mech Jugador hace " + (strategy == 1 ? "corriendo" : "caminando") + " el camino hasta la casilla: ";
            debug += ((Node)camino.get(camino.size() - 1)).getHexagon().toString() + ", con encaramiento: " + ending_side_facing + ".\n";
            debug += "\tEl camino realizado ha sido:\n";

            for (Node i : camino) {
				
                debug += "\t\t(" + i.getHexagon().toString() + ", " + i.getDirection() + ", " + ((Node)camino.get(j)).getG() + "PM )\n";
                debug += "\t\t\t\t\t|\n";
                j++;
            }
        }
        if (ending_side_facing == 0)
        {
            ending_side_facing = ((Node)camino.get(camino.size() - 1)).getDirection();
        }
        debug += (ideal ? "" : "\t\tEncarandose hacia: "+ending_side_facing) + "\n";
    }
    
    public String convert_string(){
        int i = 0, pasos=0;
        String str,str2="";
         // salto
        
    	if (strategy == 0 || get_up )
            str = "Andar\n";
        else
            str = "Correr\n";

        str += road.get(road.size() - 1).getHexagon().convert_string() + "\n";
        
        if (ending_side_facing == 0)
            ending_side_facing = road.get(road.size() - 1).getDirection();
        str += ending_side_facing + "\n";
        str += "False\n";

        if (get_up)
        {
            str2 += "Levantarse\n";
            str2 += ((Node)road.get(0)).getDirection() + "\n";//((int)_final).ToString() + "\n";

            pasos++;
            if (road.size() == 1) {
                str += pasos+"\n";
                str += str2;
                return str;
            }
        }
        int tmp;
        
        i++;
        while (i != road.size())
        {

            if (((Node)road.get(i - 1)).getDirection() != road.get(i).getDirection())
            {
                tmp = side_facing_cost(road.get(i - 1).getDirection(), road.get(i).getDirection());
                str2 += print_rl(road.get(i - 1).getDirection(), road.get(i).getDirection()) + "\n";
                str2 += tmp + "\n";
                pasos++;
            }
            str2 += "Adelante\n";
            str2 += "1\n";
            pasos++;
            i++;

        }

        if (road.get(i - 1).getDirection() != ending_side_facing)
        {
            str2 += print_rl(road.get(i - 1).getDirection(), ending_side_facing) + "\n";
            str2 += side_facing_cost(road.get(i - 1).getDirection(), ending_side_facing) + "\n";
            pasos++;
        }

        if (pasos == 0)
            return "Inmovil\n";


        str += pasos + "\n";

        str += str2;
        System.out.println(str);
        return str;
    }
    
    public String print_rl(int a, int b){
    	String str = "";
    	switch (a-b){
    	case -5:
    	case -4:
    	case 1:
    	case 2: 
    	case 3: str = "Izquierda"; break;
    	case -2:
    	case -1:
    	case -3:
    	case 4:
    	case 5: str = "Derecha"; break;
    	default:  str = "";
    	}
    	return str;
    }

	public Object ToDebug() {
		return debug;
	}
	
	public void toFile(int n_mech){
     
		
        FileWriter fileout;
		try {
			fileout = new FileWriter("accionJ"+n_mech+".sbt");
	        BufferedWriter filebuf = new BufferedWriter(fileout);
	        PrintWriter printout = new PrintWriter(filebuf);
	        
			printout.println(convert_string());
	        printout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			fileout = new FileWriter("debug.sbt");
	        BufferedWriter filebuf = new BufferedWriter(fileout);
	        PrintWriter printout = new PrintWriter(filebuf);
	        
			printout.println(convert_string());
	        printout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
