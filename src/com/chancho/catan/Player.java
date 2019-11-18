package com.chancho.catan;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;

import com.chancho.catan.map.Development;
import com.chancho.catan.map.Harbor;
import com.chancho.catan.map.Resource;
import com.chancho.catan.map.Resource.ResType;
import com.chancho.catan.map.Road;
import com.chancho.catan.map.SetNode;
import com.chancho.catan.map.Tile.TileType;

public class Player {
	public enum PlayerColor{
		WHITE, RED, BLUE, ORANGE
	}
	public boolean hasRolled=false, endTurn=true, moveRobber = false;
	public PlayerColor color;
	public int VP = 0;
	public ResType tradeA,tradeB;	
	
	public ArrayList<Harbor> harbors = new ArrayList<Harbor>();
	public ArrayList<Development> devs = new ArrayList<Development>();
	public ArrayList<Resource> res = new ArrayList<Resource>();
	public ArrayList<SetNode> nodes = new ArrayList<SetNode>(); // MAX 5 Settlements : 1VP //MAX 4 Cities : 2VP
	public ArrayList<Road> roads = new ArrayList<Road>(); //MAX 15
	public HashSet<Road> validRoads = new HashSet<Road>(); //MAX 15
	public Player(PlayerColor color) {
		this.color=color;
	}
	public void fin() {
		endTurn=true;
		hasRolled=false;
	}
	public void addRes(TileType type) {
		switch(type) {
		case FIELD:
			res.add(new Resource(ResType.GRAIN));
			break;
		case FOREST:
			res.add(new Resource(ResType.LUMBER));
			break;
		case MOUNTAIN:
			res.add(new Resource(ResType.ORE));
			break;
		case HILL:
			res.add(new Resource(ResType.BRICK));
			break;
		case PASTURE:
			res.add(new Resource(ResType.WOOL));
			break;
		default:
			break;
		}
	}
	public int getRes(int i) {
		if(i==5)return res.size();
		int result = 0;
		for(Resource r : res) {
			if(r.type==ResType.values()[i])result++;
		}
		return result;
	}

	public int getRes(ResType r) {
		if(r==ResType.NULL)return res.size();
		int result = 0;
		for(Resource rs : res) {
			if(r==rs.type)result++;
		}
		return result;
	}
	public void removeRes(ResType r) {
		int index = 0;
		for(Resource rs : res) {
			if(r==rs.type)index=res.indexOf(rs);
		}
		res.remove(index);
	}
	
	public int chitIndex() {
		switch(color) {
		case WHITE:
			return 0;
		case RED:
			return 1;
		case BLUE:
			return 2;
		case ORANGE:
			return 3;
		}
		return -1;
	}
	public Color primaryColor() {
		switch(color) {
		case WHITE:
			return Color.decode("#fafafa");
		case RED:
			return Color.decode("#ef5350");
		case BLUE:
			return Color.decode("#42a5f5");
		case ORANGE:
			return Color.decode("#f57c00");
		}
		return Color.pink;
	}
	public Color secondaryColor() {
		switch(color) {
		case WHITE:
			return Color.decode("#c7c7c7");
		case RED:
			return Color.decode("#b61827");
		case BLUE:
			return Color.decode("#0077c2");
		case ORANGE:
			return Color.decode("#bb4d00");
		}
		return Color.pink;
	}
	public boolean canBuild(int index) {
		switch(index) {
		case 0:
			return(getRes(ResType.BRICK)>=1 && getRes(ResType.LUMBER)>=1);

		case 1:
			return(getRes(ResType.BRICK)>=1 && getRes(ResType.LUMBER)>=1 && getRes(ResType.GRAIN)>=1 &&getRes(ResType.WOOL)>=1);

		case 2:
			return(getRes(ResType.ORE)>=3 && getRes(ResType.GRAIN)>=2);

		case 3:
			return(getRes(ResType.ORE)>=1 && getRes(ResType.GRAIN)>=1 &&getRes(ResType.WOOL)>=1);
		}
		return false;
	}
	public void build(int i) {
		if(roads.size()>2) {
		switch(i) {
		case 0:
			removeRes(ResType.BRICK);
			removeRes(ResType.LUMBER);
			break;			
		case 1:
			removeRes(ResType.BRICK);
			removeRes(ResType.LUMBER);
			removeRes(ResType.GRAIN);
			removeRes(ResType.WOOL);
			break;
		case 2:
			removeRes(ResType.ORE);
			removeRes(ResType.ORE);
			removeRes(ResType.ORE);
			removeRes(ResType.GRAIN);
			removeRes(ResType.GRAIN);
			break;
		case 3:
			removeRes(ResType.ORE);
			removeRes(ResType.GRAIN);
			removeRes(ResType.WOOL);
			break;
		}
		}
	}
	public void updateValid() {
		validRoads.clear();
		HashSet<SetNode> nodelist = new HashSet<SetNode>();
		if(this.roads.size()>=2) {
			for(SetNode s: nodes)nodelist.add(s);
			for(Road r: this.roads) {nodelist.add(r.nA);nodelist.add(r.nB);}
		}else if(nodes.size()>=1 && roads.size()<nodes.size()){
			nodelist.add(nodes.get(nodes.size()-1));
		}
		for(SetNode n: nodelist) {for(Road r: n.roads){if(!roads.contains(r))validRoads.add(r);}}
	}
	public boolean canTrade(int harborType,ResType res) {
		switch(harborType) {
		case 0:
			if(getRes(res)>=4) {tradeA=res;return true;}
		case 1:
			if(getRes(res)>=3) {tradeA=res;return true;}
		case 2:
			if(getRes(ResType.GRAIN)>=2) {tradeA=ResType.GRAIN;return true;}
			break;
		case 3:
			if(getRes(ResType.LUMBER)>=2) {tradeA=ResType.LUMBER;return true;}
			break;
		case 4:
			if(getRes(ResType.ORE)>=2) {tradeA=ResType.ORE;return true;}
			break;
		case 5:
			if(getRes(ResType.BRICK)>=2) {tradeA=ResType.BRICK;return true;}
			break;
		case 6:
			if(getRes(ResType.WOOL)>=2) {tradeA=ResType.WOOL;return true;}
			break;
		}
		return false;
	}
	public void resetTrades() {
		tradeA=null;
		tradeB=null;
	}
}
