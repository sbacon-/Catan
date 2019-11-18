package com.chancho.catan.map;

public class Tile {
	public enum TileType{
		DESERT, FIELD,FOREST,MOUNTAIN,HILL,PASTURE, OCEAN
	}
	private TileType type;
	private int val, x, y;
	public int HEX_SIZE;
	
	public boolean robber;
	
	
	public Tile(TileType type,int HEX_SIZE){
		this.type=type;
		this.val=0;
		this.HEX_SIZE=HEX_SIZE;
	}
	public TileType getType(){
		return this.type;
	}
	public int getTypeID() {
		switch(this.type) {
		case OCEAN:
			return 0;
		case DESERT:
			return 1;
		case FIELD:
			return 2;
		case FOREST:
			return 3;
		case MOUNTAIN:
			return 4;
		case HILL:
			return 5;
		case PASTURE:
			return 6;
		}
		return 0;
	}
	public int getVal() {
		return this.val;
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
	public void setVal(int val) {
		this.val=val;
	}
	public void setCoords(int x, int y) {
		this.x=x;
		this.y=y;
	}
	public boolean hover(int mx, int my) {
		return
			(mx>getX()+(HEX_SIZE/2)-(HEX_SIZE/6) && mx<getX()+(HEX_SIZE/2)+(HEX_SIZE/6))  &&
			(my>getY()+(HEX_SIZE/2)-(HEX_SIZE/6) && my<getY()+(HEX_SIZE/2)+(HEX_SIZE/6));
	}
}
