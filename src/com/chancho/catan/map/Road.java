package com.chancho.catan.map;

import java.awt.Color;

import com.chancho.catan.Player;

public class Road {
	public SetNode nA, nB;
	public Player p;
	
	public Color primary = Color.decode("#bcaaa4");
	public Color secondary = Color.decode("#8c7b75");
	
	public Road(SetNode nA, SetNode nB) {
		this.nA=nA;
		this.nB=nB;
	}
	public void claim(Player p) {
		this.p=p;
		primary = p.primaryColor();
		secondary = p.secondaryColor();
	}	
	public boolean connects(SetNode s) {
		return ((s==nA)||(s==nB));
	}

	public SetNode connectedTo(SetNode s) {
		if(s==nA)return nB;
		if(s==nB)return nA;
		return null;
	}	
	public boolean hover(int mx,int my) {
		int minX = (nA.getX()<nB.getX())?nA.getX():nB.getX();
		int maxX = (nA.getX()<nB.getX())?nB.getX():nA.getX();
		int minY = (nA.getY()<nB.getY())?nA.getY():nB.getY();
		int maxY = (nA.getY()<nB.getY())?nB.getY():nA.getY();
		if(minX==maxX)
			return (mx>minX-7 && mx<maxX+7) && (my>minY && my<maxY);
		return (mx>minX && mx<maxX) && (my>minY && my<maxY);		
	}
	
	public void resetColors() {
		if(this.p==null) {
		primary = Color.decode("#bcaaa4");
		secondary = Color.decode("#8c7b75");
		}
	}
	public void resetColors(int ticks) {
		while(ticks>240)ticks-=240;
		if(ticks<120) {
			primary = Color.decode("#bcaaa4");
			secondary = Color.decode("#8c7b75");
		}else if(ticks<240) {
			primary = Color.decode("#8c7b75");
			secondary = Color.decode("#bcaaa4");
		}
	}
}
