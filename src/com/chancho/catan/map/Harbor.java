package com.chancho.catan.map;

public class Harbor {
	private Tile t;
	public SetNode nA;
	public SetNode nB;
	public int type;
	public Harbor(Tile t, SetNode nA, SetNode nB, int type) {
		this.t = t;
		this.nA = nA;
		this.nB = nB;
		this.type = type;
	}
	public int getX() {
		return t.getX()+t.HEX_SIZE/2;
	}
	public int getY() {
		return t.getY()+t.HEX_SIZE/2;
	}
	public int deltaX(SetNode n) {
		return((n.getX()-getX())/2);
	}
	public int deltaY(SetNode n) {
		return((n.getY()-getY())/2);
	}
	public boolean connects(SetNode s) {
		return ((s==nA)||(s==nB));
	}

	
}
