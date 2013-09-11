package componentes;

public class Actuator {

	int code;
	String name;
	int item_location;
	boolean operative;
	int impacts_number;
	
	public Actuator(int code, String name, int item_location,
			boolean operative, int impacts_number) {
		super();
		this.code = code;
		this.name = name;
		this.item_location = item_location;
		this.operative = operative;
		this.impacts_number = impacts_number;
	}
}
