package componentes;

public class Hexagon {

	int level;
	int type_terreno;
	int object;
	String fce;
	boolean broken_building;
	boolean fire;
	boolean smoke;
	int garrotes;
	boolean [] rio = new boolean[6];
	boolean [] street = new boolean[6];
	
	int column;
	int row;
	
	public Hexagon(int level, int type_terreno, int object, String fce,
			boolean broken_building, boolean fire, boolean smoke, int garrotes,
			boolean[] rio, boolean[] street, int row, int column) {
		super();
		this.level = level;
		this.type_terreno = type_terreno;
		if(type_terreno == 2) this.level --;
		this.object = object;
		this.fce = fce;
		this.broken_building = broken_building;
		this.fire = fire;
		this.smoke = smoke;
		this.garrotes = garrotes;
		this.rio = rio;
		this.street = street;
		this.column = column;
		this.row = row;
	}
	
	public int getLevel() {
		return level;
	}

	public int getObject() {
		return object;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public int getValuation(int strategy) {

		int grade = 0;
		
		if(strategy == 0) {
			switch(type_terreno){
			
				case 0: 
				case 1: grade +=2; break;
				case 3: grade +=1; break;
			}
			
			switch(object){
			
				case 1:					 //bosque ligero
				case 2: grade+=2; break; //bosque denso
				case 3:
				case 4:
				case 5:
				case 6: grade+=1; break;//edificios
				case 7: grade+=2; break; // bunker
			}
		}
		else {
			
			switch(type_terreno){
			
				case 2: grade -= 2; break; // agua
				case 3: grade -=1; break; // pantanoso
			}
			
			switch(object){
			
				case 1:	grade+=2; break; //bosque ligero
				case 2: grade+=3; break; //bosque denso
				case 3: case 4: case 5: case 6: case 7: grade++; break;
			}

		}
		
		return grade;
	}
	
	public int movment_modifiers(){
		int tmp = 1;
		switch(type_terreno){

	       /* case 0: //despejado
	            tmp++;
	            break;
	        case 1: //pavimentado
	            tmp ++;
	            break; */
	        case 2: //agua
	        	switch(level){
	        		//case 0: tmp++;
	        		case -1: tmp++; //tmp+=2;
	        		default: tmp+=3; //tmp +=4;
	        	}
	            break;
	        case 3: // pantanoso
	        	tmp++; break; //tmp+=2; break;
		}
		
		switch(object){
			case 0:
			case 1:
			case 3: tmp ++;
			break;
			case 2:
			case 4: tmp += 2; break;
			case 5: tmp += 3; break;
			case 6: tmp += 4; break;
		}
		
		return tmp;
	}
	
	public boolean front_cone(Hexagon h, int side){
		
        Boolean inside = false;
        int l_diag, r_diag;
        
        //diagonales del cono
        l_diag = column - Math.abs(h.getRow() - row) * 2;
        r_diag = column + Math.abs(h.getRow() - row) * 2;
        if (l_diag <= 0) l_diag = 1;

        switch(side){
        case 1:
            if (h.getRow() > row)
                inside = false;
            else
            {
                if (column % 2 == 0)
                {//posiciones pares
                    if ((h.getColumn() >= l_diag || h.getColumn() >= l_diag - 1) && (h.getColumn() <= r_diag || h.getColumn() <= r_diag + 1))
                        inside = true;
                }
                else
                {//posiciones impar
                    if ((h.getColumn() >= l_diag || h.getColumn() >= l_diag + 1) && (h.getColumn() <= r_diag || h.getColumn() <= r_diag - 1))
                        inside = true;
                }
            }
            break;
        case 2:
            if (h.getColumn() < column)
                inside = false;
            else if (h.getRow() <= row)
                inside = true;
            else
            {
                if (column % 2 != 0)
                {//posicion impares
                    if (h.getColumn() >= r_diag || h.getColumn() >= r_diag + 1)
                        inside = true;
                }
                else
                {//posicion pares
                    if (h.getColumn() >= r_diag || h.getColumn() >= r_diag - 1)
                        inside = true;
                }
            }
            break;
        case 3:
            if (h.getColumn() < column)
                inside = false;
            else if (h.getRow() >= row)
                inside = true;
            else
            {
                if (column % 2 == 0)
                {//posicion pares
                    if (h.getColumn() >= r_diag || h.getColumn() >= r_diag + 1)
                        inside = true;
                }
                else
                {//posicion impar
                    if (h.getColumn() >= r_diag || h.getColumn() >= r_diag - 1)
                        inside = true;
                }
            }
            break;
        case 4:
            if (h.getRow() < row)
                inside = false;
            else {
                if (column % 2 != 0)
                {//posicion impares
                    if ((h.getColumn() >= l_diag || h.getColumn() >= l_diag - 1) && (h.getColumn() <= r_diag || h.getColumn() <= r_diag + 1))
                        inside = true;
                }
                else
                {//posicion pares
                    if ((h.getColumn() >= l_diag || h.getColumn() >= l_diag + 1) && (h.getColumn() <= r_diag || h.getColumn() <= r_diag - 1))
                        inside = true;
                }
            }
            break;
        case 5:
            if (h.getColumn() > column)
                inside = false;
            else if (h.getRow() >= row)
                inside = true;
            else
            {
                if (column % 2 == 0)
                {//posicion pares
                    if ((h.getColumn() <= l_diag || h.getColumn() <= l_diag - 1))
                        inside = true;
                }
                else
                {//posicion impar
                    if ((h.getColumn() <= l_diag || h.getColumn() <= l_diag + 1))
                        inside = true;
                }
            }

        break;
        case 6:
            if (h.getColumn() > column)
                inside = false;
            else if (h.getRow() <= row)
                inside = true;
            else
            {
                if (column % 2 != 0)
                {//posicion impares
                    if (h.getColumn() <= l_diag || h.getColumn() <= l_diag - 1)
                        inside = true;
                }
                else
                {//posicion pares
                    if (h.getColumn() <= l_diag || h.getColumn() <= l_diag + 1)
                        inside = true;
                }
            }
            break;
        }

        return inside;
	}
	
	public boolean bottom_cone(Hexagon h, int side){
        Boolean inside = false;
        int l_diag, r_diag;
        
        //diagonales del cono
        l_diag = column - Math.abs(h.getRow() - row) * 2;
        r_diag = column + Math.abs(h.getRow() - row) * 2;
        if (l_diag <= 0) l_diag = 1;

        switch(side){
        case 1:
            if (h.getRow() < row)
                inside = false;
            else
            {
                if (column % 2 != 0)
                {//posicion impares
                    if (h.getColumn() > l_diag && h.getColumn() < r_diag)
                        inside = true;
                }
                else
                {//posicion pares
                    if (h.getColumn() > l_diag + 1 && h.getColumn() < r_diag - 1)
                        inside = true;
                }
            }


            break;
        case 2:
        	if (h.getColumn() >= column)
                inside = false;
            else if (h.getRow() > row)
                inside = true;
            else
            {
                //Vemos si la casilla a observar esta dentro de esos limites
                if (column % 2 == 0)
                {//posicion pares
                    if (h.getColumn() < l_diag - 1)
                        inside = true;
                }
                else
                {//posicion impar
                    if (h.getColumn() < l_diag)
                        inside = true;
                }
            }
        	break;
        case 3:
        	if (h.getColumn() >= column)
                inside = false;
            else if (h.getRow() < row)
                inside = true;
            else
            {
                if (column % 2 != 0)
                {//posicion impares
                    if (h.getColumn() < l_diag - 1)
                        inside = true;
                }
                else
                {//posicion pares
                    if (h.getColumn() < l_diag)
                        inside = true;
                }
            }
        	break;
        case 4:
        	if (h.getRow() > row)
                inside = false;
            else {
                if (column % 2 == 0)
                {//posicion pares
                    if (h.getColumn() > l_diag && h.getColumn() < r_diag)
                        inside = true;
                }
                else
                {//posicion impar
                    if (h.getColumn() > l_diag + 1 && h.getColumn() < r_diag - 1)
                        inside = true;
                }
            }
        	break;
        case 5:
        	if (h.getColumn() <= column)
                inside = false;
            else if (h.getRow() < row)
                inside = true;
            else
            {
                if (column % 2 != 0)
                {//posicion impares
                    if (h.getColumn() > r_diag + 1)
                        inside = true;
                }
                else
                {//posicion pares
                    if (h.getColumn() > r_diag)
                        inside = true;
                }
            }
        	break;
        case 6:
        	if (h.getColumn() <= column)
                inside = false;
            else if (h.getRow() > row)
            {
                inside = true;
            }
            else
            {
                if (column % 2 == 0)
                {//posicion pares
                    if (h.getColumn() > r_diag + 1)
                        inside = true;
                }
                else
                {//posicion impar
                    if (h.getColumn() > r_diag)
                        inside = true;
                }
            }
        }

        return inside;
	}

	public Boolean right_cone(Hexagon h, int side)
	{
	    Boolean inside = false;
	
	    if (!front_cone(h, side) && !bottom_cone(h, side))
	    {
	        if (side == 1)
	        {
	            if (column < h.getColumn())
	                inside = true;
	        }
	        else if (side == 2)
	        {
	            if (row < h.getRow())
	                inside = true;
	        }
	        else if (side == 3)
	        {
	            if (row < h.getRow())
	                inside = true;
	        }
	        else if (side == 4)
	        {
	            if (column > h.getColumn())
	                inside = true;
	        }
	        else if (side == 5)
	        {
	            if (row > h.getRow())
	                inside = true;
	        }
	        else
	        {
	            if (row >= h.getRow())
	                inside = true;
	        }
	    }
	    return inside;
	}
	
	public Boolean left_cone(Hexagon h, int side)
	{
	    return !front_cone(h, side) && !bottom_cone(h, side) && !right_cone(h, side);
	}

	public String convert_string() {
		String s = "";
		if(column < 10) s+="0";
		s+=column;
		if(row < 10) s+="0";
		s+=row;
		return s;
	}
	
	public boolean egual(Hexagon h){
		if(column == h.getColumn() && row == h.getRow())
			return true;
		return false;
	}
	
	public String toString(){
	
		return "("+row+","+column+")";
	}
}