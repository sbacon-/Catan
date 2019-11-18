package com.chancho.catan.map;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashSet;

import com.chancho.catan.Player;

public class SetNode {
	private int x, y;
	public ArrayList<Tile> tiles = new ArrayList<Tile>();	
	public HashSet<Road> roads = new HashSet<Road>();
	public Image nchit;
	public Player p;
	public boolean upgraded = false;
	
	public SetNode(int x, int y, Image nchit) {
		this.x=x;
		this.y=y;
		this.nchit = nchit;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public boolean hover(int mx, int my) {
		return
			(mx>getX()-nchit.getWidth(null)/2 && mx<getX()+nchit.getWidth(null)/2) &&
			(my>getY()-nchit.getWidth(null)/2 && my<getY()+nchit.getWidth(null)/2);
	}
	public void claim(Player p,Image nchit) {
		this.p = p;
		this.nchit = nchit;
	}
	public boolean isValid(Player pl) {
		for(Road r : roads) {
			if(r.connects(this)&&r.connectedTo(this).p!=null)return false;
		}
		if(pl.roads.size()>=2) {
			for(Road r : roads)if(r.p==pl)return true;
		}
		return pl.roads.size()<2;
	}
}
