package componentes;

public class Slot {
	
		String type;
		int amount;
		int code;
		String name;
		int component_index;
		int actuator_index;
		int ammunition_damage;
		
		public Slot(String type, int amount, int code, String name,
				int component_index, int actuator_index, int ammunition_damage) {
			super();
			this.type = type;
			this.amount = amount;
			this.code = code;
			this.name = name;
			this.component_index = component_index;
			this.actuator_index = actuator_index;
			this.ammunition_damage = ammunition_damage;
		}

		public int getAmount() {
			return amount;
		}
		
		
}