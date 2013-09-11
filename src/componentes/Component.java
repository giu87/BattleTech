package componentes;

public class Component {

	int code;
	String name;
	String type;
	boolean weapon_in_back;
	int item_location;
	int secondary_item_location;
	String weapon_type;
	int heat;
	int harm;
	int shots_per_turn;
	int minimum_distance;
	int short_distance;
	int medium_distance;
	int long_distance;
	boolean operative;
	int weapon_code;
	int amount;
	String special_ammunition;
	int trigger_switch;
	int total_munition = 0;
	int num_munition = 0;
	
	public Component(int code, String name, String type,
			boolean weapon_in_back, int item_location,
			int secondary_item_location, String weapon_type, int heat,
			int harm, int shots_per_turn, int minimum_distance,
			int short_distance, int medium_distance, int long_distance, boolean operative,
			int weapon_code, int amount, String special_ammunition,
			int trigger_switch) {
		super();
		this.code = code;
		this.name = name;
		this.type = type;
		this.weapon_in_back = weapon_in_back;
		this.item_location = item_location;
		this.secondary_item_location = secondary_item_location;
		this.weapon_type = weapon_type;
		this.heat = heat;
		this.harm = harm;
		this.shots_per_turn = shots_per_turn;
		this.minimum_distance = minimum_distance;
		this.short_distance = short_distance;
		this.medium_distance = medium_distance;
		this.long_distance = long_distance;
		this.operative = operative;
		this.weapon_code = weapon_code;
		this.amount = amount;
		this.special_ammunition = special_ammunition;
		this.trigger_switch = trigger_switch;
	}

	public int getItem_location() {
		return item_location;
	}

	public int getAmount() {
		return amount;
	}

	public int getMinimum_distance() {
		return minimum_distance;
	}

	public int getShort_distance() {
		return short_distance;
	}

	public int getMedium_distance() {
		return medium_distance;
	}

	public int getLong_distance() {
		return long_distance;
	}
	
	public int getHeat(){
		return heat;
	}

	public int getShots_per_turn() {
		return shots_per_turn;
	}
	
	public String getName() {
		return name;
	}

	public int getHarm() {
		return harm;
	}

	public String convert_location(){
		String location;
		
        switch (item_location) { 
        case 0:
            location="BI";
            break;
        case 1:
            location="TI";
            break;
        case 2:
            location="PI";
            break;
        case 3:
            location="PD";
            break;
        case 4:
            location="TD";
            break;
        case 5:
            location="BD";
            break;
        case 6:
            location="TC";
            break;
        case 7:
            location="CAB";
            break;
        case 8:
            location="TIa";
            break;
        case 9:
            location="TDa";
            break;
        case 10:
            location="TCa";
            break;
        default:
            location=null;
            break;
        }
        return location;
	}
	
	public int getTotal_munition() {
		return total_munition;
	}

	public void addMunition(int munitions) {
		total_munition += munitions;
	}

	public int getNum_munition() {
		return num_munition;
	}

	public void addNumMunition() {
		this.num_munition++;
	}
}
