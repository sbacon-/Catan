package com.chancho.catan.map;

import java.awt.Color;

public class Development {
	public enum DevType{
		KNIGHT,PROG,VP;
	}
	public String str,info, title;
	public Color color,color2,  
	red = Color.decode("#851a14"),
	red2 = Color.decode("#bb4b3b"),
	green = Color.decode("#203a26"), 
	green2 = Color.decode("#49644e"), 
	yellow = Color.decode("#b8bb6c"),
	yellow2 = Color.decode("#ebed9b");
	public int index,textcolor,turn;
	public DevType type;
	public Development(DevType type,String str){
		this.type=type;
		this.str=str;
		this.turn=0;
		this.title=getTitle();
		switch(type) {
		case KNIGHT:
			this.color = red;
			this.color2 = red2;
			this.textcolor = 1;
			break;
		case PROG:
			color = green;
			color2 = green2;
			textcolor = 1;
			break;
		case VP:
			color = yellow;
			color2 = yellow2;
			textcolor = 0;
			info = "1 Victory Point!";
		}
		
	}
	public String getTitle() {
		switch(str) {
		case "K":
			info = "Move the robber. Steal 1 resource card from the owner of an adjacent settlement or city.";
			index = 0;
			return "Knight";
		case "MON":
			info = "When you play this card, announce 1 type of resource. All other players must give you all their resource cards of that type.";
			index = 1;
			return "Monopoly";
		case "RB":
			info = "Place 2 new roads as if you had just built them.";
			index = 2;
			return "Road Building";
		case "YOP":
			index = 3;
			info = "Take any 2 resources from the bank. Add them to your hand. They can be 2 of the same resource or 2 different resources.";
			return "Year of Plenty";
		case "C":
			index = 4;
			return "Chapel";
		case "L":
			index = 5;
			return "Library";
		case "M":
			index = 6;
			return "Market";
		case "P":
			index = 7;
			return "Palace";
		case "U":
			index = 8;
			return "University";
		}
		return "";
	}
}
