package componentes;

public class Node {

	int g;
	int f;
	int h;
	int direction;
	Hexagon hexagon;
	Node father;
	
	public Node() {
		g = 0;
		f = 0;
		h = 0;
		direction = 1;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getF() {
		return f;
	}

	public void setF(int f) {
		this.f = f;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public Hexagon getHexagon() {
		return hexagon;
	}

	public void setHexagon(Hexagon hexagon) {
		this.hexagon = hexagon;
	}

	public void setFather(Node element) {
		// TODO Auto-generated method stub
		this.father = element;
	}
	
	public Node getFather() {
		// TODO Auto-generated method stub
		return father;
	}

}
