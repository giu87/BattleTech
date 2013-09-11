package componentes;

import java.util.ArrayList;

public class Map {
	
	Hexagon [][] hexagons;
	int height;
	int width;
	
	public Map(ArrayList<String> data) {
		
		height = Integer.parseInt(data.get(1));
		width = Integer.parseInt(data.get(2));
		
		hexagons = new Hexagon [height][width];
		int k = 3;
		for (int i=0;i<width;i++) {
			for(int j=0;j<height;j++){
			
				boolean[] rio = new boolean[6];
				boolean[] street = new boolean [6];

				for(int z=0;z<6;z++)
					if(data.get(k+8+z).equals("True")) rio[z] = true; else rio[z] = false;

				for(int z=0;z<6;z++)
					if(data.get(k+14+z).equals("True")) street[z] = true; else street[z] = false;
				
				boolean broken_building;
				boolean fire;
				boolean smoke;
				
				if(data.get(k+4).equals("True")) broken_building = true; else broken_building = false;
				if(data.get(k+5).equals("True")) fire = true; else fire = false;
				if(data.get(k+6).equals("True")) smoke = true; else smoke = false;

				
				hexagons[j][i] = new Hexagon( 
										  Integer.parseInt(data.get(k)),
										  Integer.parseInt(data.get(k+1)),
										  Integer.parseInt(data.get(k+2)),
										  data.get(k+3),
										  broken_building,
										  fire,
										  smoke,											  
										  Integer.parseInt(data.get(k+7)),
										  rio,
										  street,
										  j+1,
										  i+1);		
				
				k+=20;
			}
		}	
	
		System.out.print("");
	}
	
	public double distance(Hexagon a, Hexagon b){
		
        int dx = Math.abs(a.column - b.column) + 1;
        int dy = Math.abs(b.row - a.row) + 1;
        
        return Math.pow((Math.pow(dx, 2) + Math.pow(dy, 2)), 0.5);
	}
	
	public int distance_int(Hexagon a, Hexagon b){
		
        int dx = Math.abs(a.column - b.column);
        int d = dx/2;

        if (((a.column % 2) == 1) && ((b.column % 2) == 0)) d += 1;

        int fmin = a.row - d;
        int fmax = fmin + dx;
        int fmod = 0;

        if (b.row < fmin) fmod = fmin - b.row;
        if (b.row > fmax) fmod = b.row - fmax;

        return dx + fmod;
	}
	
    public ArrayList<Hexagon> hexagons_reachable( Hexagon current, int radio , Mech[] mechs , int mine ) {
        ArrayList<Hexagon> hexagon = new ArrayList<Hexagon>();
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (distance_int(hexagons[i][j], current) <= radio && !occupied_hexagon(hexagons[i][j], mechs, mine))
                    hexagon.add(hexagons[i][j]);
            }

        return hexagon;
    }

	public boolean occupied_hexagon(Hexagon h, Mech[] mechs, int mine) {
	
		boolean oc = false;
		
		for(int i=0;i<mechs.length;i++)
			if(mechs[i].getPlayer()!= mine && mechs[i].getHexagon_actual() == h) oc = true;
		
		return oc;
	}
	
	public Hexagon adjacent(Hexagon h, int direction) {
		
        Hexagon tmp = null;
        int column = h.column - 1;
        int row = h.row - 1;
	    if (direction == 1) tmp = hexagons[row-1][column];
        else {
        	if(direction == 4) tmp = hexagons[row+1][column];
        	else { 
        		
	        	if (((column+1 ) % 2) == 0)
	        	{
		            switch (direction)
		            {
		                case 2:
		                    tmp = hexagons[row][column+1];
		                    break;
		                case 3:
		                    tmp = hexagons[row+1][column+1];
		                    break;
		                case 5:
		                    tmp = hexagons[row+1][column-1];
		                    break;
		                case 6:
		                    tmp = hexagons[row][column-1];
		                    break;
		            }
		        }
		        else  //impar
		        {
		            switch (direction)
		            {
		                case 2:
		                    tmp = hexagons[row-1][column+1];
		                    break;
		                case 3:
		                    tmp = hexagons[row][column+1];
		                    break;
		                case 5:
		                    tmp = hexagons[row][column-1];
		                    break;
		                case 6:
		                    tmp = hexagons[row-1][column-1];
		                    break;
		            }
		        }
        	}
        }
        return tmp;
	}

	public int movment_cost(Hexagon a, Hexagon b) {

		int cost = 0;
		if(b.type_terreno == 2) cost+=5;
		
		if(Math.abs(a.column - b.column) >= 2 || Math.abs(a.row - b.row) >= 2)			
			cost +=1000;

		//if(a.type_terreno == 2 && b.type_terreno != 2){
			//if()
		//}
			
		switch(Math.abs(a.getLevel() - b.getLevel())){
			case 0: break;
			case 1: cost++; break;
			case 2: //if(a.type_terreno == 2) cost += 1000;
					//else 
						cost += 2; break; 
			default: cost = 1000;
		}
		return cost;
	}
	
    public int side_facing_cost(Hexagon a, int direction, Hexagon b)
    {
    	int c[]= new int [7];
    	for(int i=1;i<7;i++){
    		if(((direction + i)%7) == 0)
    			c[i] = 1;
    		else c[i] = 0;
    	}
    	if(reachable(a.getColumn(), a.getRow(), direction ) && adjacent(a, direction) == b)
            return 0;
        else if ((reachable(a.getColumn(), a.getRow(), c[1]+(direction + 1)%7 ) && adjacent(a, c[1]+(direction + 1)%7) == b) || 
        		(reachable(a.getColumn(), a.getRow(), c[5]+(direction + 5)%7 ) && adjacent( a, c[5] +(direction + 5)%7) == b))
            return 1;
        else if ((reachable(a.getColumn(), a.getRow(), c[2]+(direction + 2)%7 ) && adjacent(a, c[2]+(direction + 2)%7) == b) || 
        		(reachable(a.getColumn(), a.getRow(), c[4]+(direction + 4)%7) && adjacent(a, c[4] +(direction + 4)%7) == b))
            return 2;
        else
            return 3;
    }
    
	public boolean reachable(int c, int r, int side){
		
    	if(c == 1 && side==5 ||
         	   c == 1 && side==6 ||
         	   c == width-1 && side==2 ||
         	   c == width-1 && side==3 ||
         	   r == 1 && side==1 ||
         	   r == 1 && side==2 && c % 2 == 1 ||
         	   r == 1 && side==6 && c % 2 == 1 ||
         	   r == height-1 && side== 3 ||
         	   r == height-1 && side== 4 ||
         	   r == height-1 && side== 5)
   	
    		return false;
    	
    	return true;

	}

}
