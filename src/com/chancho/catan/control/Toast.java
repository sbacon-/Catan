package com.chancho.catan.control;

import java.awt.Image;
import java.util.ArrayList;

import com.chancho.catan.Etch;
import com.chancho.catan.Player;
import com.chancho.catan.map.Development;
import com.chancho.catan.map.Harbor;

public class Toast {
	public enum ToastType{
		MAIN, BUY, TRADE,TRADEPICK,TRADECUSTOM, CARDS, DEV,DEVPICK, ROBBER, OPTIONS, NULL
	}
	public int HEX_SIZE,x,y,width,height,margin;
	public int deltay = 1000;
	public ToastType type;
	public Etch e;
	public Player p;
	public ArrayList<Image> image = new ArrayList<Image>();
	public ArrayList<int[]> imagePoints = new ArrayList<int[]>();
	public ArrayList<String> string = new ArrayList<String>();
	public ArrayList<int[]> stringPoints = new ArrayList<int[]>();
	public ArrayList<Button> buttons = new ArrayList<Button>();
	public boolean quitable=true;
	
	public Toast(int HEX_WIDTH, int width, int height, ToastType type, Etch e) {
		this.HEX_SIZE = HEX_WIDTH;
		this.x=HEX_WIDTH;
		this.y=HEX_WIDTH+deltay;
		this.width=width-x;
		this.height=height-(2*x);
		this.type=type;
		this.e=e;
		this.margin = x/8;
		generateToast();
	}
	public Toast(int HEX_WIDTH, int width, int height, ToastType type, Etch e,Player p, boolean noDelta) {
		this.HEX_SIZE = HEX_WIDTH;
		this.x=HEX_WIDTH;
		this.y=HEX_WIDTH;
		this.width=width-x;
		this.height=height-(2*x);
		this.type=type;
		this.deltay=0;
		this.e=e;
		this.margin = x/8;
		generateToast();
	}
	public Toast(int HEX_WIDTH, int width, int height, ToastType type, Etch e,Player p) {
		this.HEX_SIZE = HEX_WIDTH;
		this.x=HEX_WIDTH +(type==ToastType.DEV? width/2:0);
		this.y=HEX_WIDTH+deltay;
		this.width=width-HEX_WIDTH;
		this.height=height-(2*HEX_WIDTH);
		this.type=type;
		this.e=e;
		this.p=p;
		this.margin = HEX_WIDTH/8;
		generateToast();
	}
	private void generateToast() {
		switch(type) {
		case MAIN:
			quitable=false;
			image.add(e.main);
			imagePoints.add(new int[]{(width/2)-(image.get(0).getWidth(null)/2),margin});
			break;
			
			
		case BUY:
			for(Image i : e.build) {
				image.add(i);
				imagePoints.add(new int[] {margin*2,((x/2+margin)*(e.build.indexOf(i)+2))+margin/2-(2*x/3)+30});
				int[]road = {5,3};
				int index = 0;
				for(int r : road) {
					index++;
					image.add(e.icons.get(r));
					imagePoints.add(new int[]{width-margin*4-e.icons.get(r).getWidth(null)/2-((e.icons.get(r).getWidth(null)-margin)*(road.length-index)),((x/2+margin)*2)-(2*x/3)});
				}
				int[] set = {5,3,2,6};
				index = 0;
				for(int r : set) {
					index++;
					image.add(e.icons.get(r));
					imagePoints.add(new int[]{width-margin*4-e.icons.get(r).getWidth(null)/2-((e.icons.get(r).getWidth(null)-margin)*(set.length-index)),((x/2+margin)*3)-(2*x/3)});
				}
				int[] city = {2,9,4,11,18};
				index = 0;
				for(int r : city) {
					index++;
					int gettr=r;
					while(gettr>7)gettr-=7;
					image.add(e.icons.get(gettr));
					imagePoints.add(new int[]{width-margin*4-e.icons.get(gettr).getWidth(null)/2-((e.icons.get(gettr).getWidth(null)-margin)*(city.length-index)),((x/2+margin)*4)-(2*x/3)});
				}
				int[] dev = {6,2,4};
				index = 0;
				for(int r : dev) {
					index++;
					image.add(e.icons.get(r));
					imagePoints.add(new int[]{width-margin*4-e.icons.get(r).getWidth(null)/2-((e.icons.get(r).getWidth(null)-margin)*(dev.length-index)),((x/2+margin)*5)-(2*x/3)});
				}
			}
			string.add("BUILDING COSTS");
			stringPoints.add(new int[]{(width/2),(margin),(40),1});
			
			//BUTTON LABELS
			string.add("Road");
			stringPoints.add(new int[]{margin*2,((x/2+margin)*2)-(2*x/3),(25),0});
			string.add("Settlement");
			stringPoints.add(new int[]{margin*2,((x/2+margin)*3)-(2*x/3),(25),0});
			string.add("City");
			stringPoints.add(new int[]{margin*2,((x/2+margin)*4)-(2*x/3),(25),0});
			string.add("Development Card");
			stringPoints.add(new int[]{margin*2,((x/2+margin)*5)-(2*x/3),(25),0});
			string.add(" = 0 Victory Points");
			stringPoints.add(new int[]{(2*margin)+image.get(0).getWidth(null),((x/2+margin)*2)+margin/2-(2*x/3)+30,(18),0});
			string.add(" = 1 VP");
			stringPoints.add(new int[]{(2*margin)+image.get(0).getWidth(null),((x/2+margin)*3)+margin/2-(2*x/3)+30,(18),0});
			string.add(" = 2 VPs");
			stringPoints.add(new int[]{(2*margin)+image.get(0).getWidth(null),((x/2+margin)*4)+margin/2-(2*x/3)+30,(18),0});
			string.add(" = ? VPs");
			stringPoints.add(new int[]{(2*margin)+image.get(0).getWidth(null),((x/2+margin)*5)+margin/2-(2*x/3)+30,(18),0});
			
			string.add("* A City replaces an already-built Settlement");
			stringPoints.add(new int[]{width/2,height-margin-20*4,(18),1});
			string.add("* Usually, you only play 1 Development Card per turn, and");
			stringPoints.add(new int[]{width/2,height-margin-20*3,(18),1});
			string.add("  you cannot play a Development Card on the turn it's built");
			stringPoints.add(new int[]{width/2,height-margin-20*2,(18),1});
			//BUILD IMAGES
			break;
			
			
		case TRADE:
			image.add(e.icons.get(0));
			imagePoints.add(new int[]{margin*2, margin*6});
			boolean three=false;
			int hcount=1;
			for(Harbor h : p.harbors) {
				if(h.type==1) {
					three=true;
				}else {
					image.add(e.icons.get(h.type));
					imagePoints.add(new int[]{margin*2+(hcount*HEX_SIZE), margin*6});
					string.add("2:1");
					stringPoints.add(new int[] {margin*2+(hcount*HEX_SIZE)+e.icons.get(0).getWidth(null)/2, margin*8+HEX_SIZE/4,HEX_SIZE/6,1});
					hcount++;
				}
			}
			string.add(three?"3:1":"4:1");
			stringPoints.add(new int[] {margin*2+e.icons.get(0).getWidth(null)/2, margin*8+HEX_SIZE/4,HEX_SIZE/6,1});
			string.add("TRADE");
			stringPoints.add(new int[]{(width/2),(margin),(40),1});
			break;
			
		
		case TRADECUSTOM:
				for(int i = 2; i<e.icons.size(); i++) {
					image.add(e.icons.get(i));
					imagePoints.add(new int[]{margin, 3*margin/2+(e.icons.get(i).getHeight(null)*(i-1))});
				}
				for(int i = 2; i<e.icons.size(); i++) {
					image.add(e.icons.get(i));
					imagePoints.add(new int[]{width-margin-e.icons.get(i).getWidth(null), 3*margin/2+(e.icons.get(i).getHeight(null)*(i-1))});
				}
				for(int i = 2; i<e.icons.size(); i++) {
					string.add("0");
					stringPoints.add(new int[] {
							e.icons.get(i).getWidth(null)+7*margin,
							3*margin/2+(e.icons.get(i).getHeight(null)*(i-1)),
							60,
							0
					});
				}
				for(int i = 2; i<e.icons.size(); i++) {
					string.add("0");
					stringPoints.add(new int[] {
							width-margin-e.icons.get(i).getWidth(null)-(7*margin+e.icons.get(i).getWidth(null)/2)+margin,
							3*margin/2+(e.icons.get(i).getHeight(null)*(i-1)),
							60,
							0
					});
				}
				string.add("TRADE");
				stringPoints.add(new int[]{(width/2),(margin),(40),1});
				
				string.add("I HAVE");
				stringPoints.add(new int[]{(width/8),(margin*3),(30),1});
				
				string.add("I WANT");
				stringPoints.add(new int[]{7*(width/8),(margin*3),(30),1});
				
				string.add(":");
				stringPoints.add(new int[]{(width/2),(height/2)-255,(255),1});
							
				string.add("* All other Trades will be sent to all players");
				stringPoints.add(new int[]{width/2,height-margin-20*3,(18),1});
				string.add("  via the Blockchain Chat Window");
				stringPoints.add(new int[]{width/2,height-margin-20*2,(18),1});
				break;
			
				
		case TRADEPICK:
			this.y=x*2;
			
			int imgx=HEX_SIZE,imgy=height-HEX_SIZE-2*margin;
			for(int i=2;i<7;i++) {
				image.add(e.icons.get(i));
				imagePoints.add(new int[]{(i-2)*imgx+e.icons.get(0).getWidth(null)/2,imgy+e.icons.get(0).getHeight(null)/2});
			}
			
			string.add("TRADE");
			stringPoints.add(new int[]{(width/2),(margin),(40),1});

			break;
		case CARDS:
			int titlex = (width/2)-(x+margin)-(x/2);
			int titley = (margin*4);
			int index=0;
			for(Development d : p.devs) {
				
				string.add(d.title);
				stringPoints.add(new int[] {
						titlex,
						titley+(3*(x/2)*(index/4)),
						(20),
						1,
						d.textcolor
				});
				
				image.add(e.dev.get(d.index));
				imagePoints.add(new int[] {
						titlex-x/2,
						titley+(3*(x/2)*(index/4))+3*(margin/2)
				});
				
				index++;
				if(index%4==0) titlex = (width/2)-(x+margin)-(x/2);
				else titlex+=x+margin;
			}
			string.add("DEVELOPMENT CARDS");
			stringPoints.add(new int[]{(width/2),(margin),(40),1});
			if(!(p.devs.size()>12)) {
			string.add("* Usually, you only play 1 Development Card per turn, and");
			stringPoints.add(new int[]{width/2,height-margin-20*3,(18),1});
			string.add("  you cannot play a Development Card on the turn it's built");
			stringPoints.add(new int[]{width/2,height-margin-20*2,(18),1});
			}
			break;
			
			
		case DEV:
			string.add(p.devs.get(p.devs.size()-1).title);
			stringPoints.add(new int[]{
					(width/2),
					(margin),
					(30),
					1,
					p.devs.get(p.devs.size()-1).textcolor});
			String str[] = p.devs.get(p.devs.size()-1).info.split(" ");
			ArrayList<String> info = new ArrayList<String>();
			String current="";
			for(int i = 0; i<str.length; i++) {
				current += str[i]+" ";
				if(current.length()>15 || i==str.length-1) {info.add(current.trim());current="";}
			}
			info.add("");
			info.add("This card can be played next turn.");
			if(p.devs.get(p.devs.size()-1).type==Development.DevType.VP) {
				info.add("");
				info.add("Will be played automatically,");
				info.add("if you have enough points to win.");
			}
			int i = 1;
			for(String s : info) {
				string.add(s);
				stringPoints.add(new int[]{
						(width/2),
						(margin*5)+e.dev.get(0).getHeight(null)+(e.dev.get(0).getWidth(null)/8)*i,
						(e.dev.get(0).getWidth(null)/10),
						1,
						p.devs.get(p.devs.size()-1).textcolor});
				i++;
			}
			i+=2;
			this.height=((margin*5)+e.dev.get(0).getHeight(null)+(e.dev.get(0).getWidth(null)/8)*i);
			image.add(e.dev.get(p.devs.get(p.devs.size()-1).index));
			imagePoints.add(new int[] {
					(width/2)-e.dev.get(0).getWidth(null)/2,
					margin*5});
			break;
			
			
		case ROBBER:
			
			string.add("BUILDING COSTS");
			stringPoints.add(new int[]{(width/2),(margin),(40),1});
			
			quitable=false;
			break;
			
			
		case OPTIONS:
			
			string.add("BUILDING COSTS");
			stringPoints.add(new int[]{(width/2),(margin),(40),1});
			
			quitable=false;
			break;
			
			
		default:
			break;
		}
		generateButtons();
	}
	public void generateButtons() {
		switch(type) {
		case MAIN:
			buttons.add(new Button("PLAY NOW",
					width/2-x,
					image.get(0).getHeight(null)+(((x/3)+margin)*1),
					x*2,
					x/3));
			buttons.add(new Button("JOIN MATCH",
					width/2-x,
					image.get(0).getHeight(null)+(((x/3)+margin)*2),
					x*2,
					x/3));
			buttons.add(new Button("OPTIONS",
					width/2-x,
					image.get(0).getHeight(null)+(((x/3)+margin)*3),
					x*2,
					x/3));
			buttons.add(new Button("QUIT",
					width/2-x,
					image.get(0).getHeight(null)+(((x/3)+margin)*4),
					x*2,
					x/3));
			break;
		case BUY:
			buttons.add(new Button("",
					margin,
					((x/2+margin)*2)-(2*x/3),
					width-margin*2,
					x/2+margin));
			buttons.add(new Button("",
					margin,
					((x/2+margin)*3)-(2*x/3),
					width-margin*2,
					x/2+margin));
			buttons.add(new Button("",
					margin,
					((x/2+margin)*4)-(2*x/3),
					width-margin*2,
					x/2+margin));
			buttons.add(new Button("",
					margin,
					((x/2+margin)*5)-(2*x/3),
					width-margin*2,
					x/2+margin));
			break;
		case TRADE:
			buttons.add(new Button("",
					margin*2, 
					margin*6,
					HEX_SIZE/2,
					HEX_SIZE/2));
			int hcount=1;
			for(Harbor h : p.harbors) {
				if(h.type==1) {
					buttons.add(new Button("",
							margin*2, 
							margin*6,
							HEX_SIZE/2,
							HEX_SIZE/2));
				}else {
					buttons.add(new Button("",
							margin*2+(hcount*HEX_SIZE), 
							margin*6,
							HEX_SIZE/2,
							HEX_SIZE/2));
					hcount++;
				}
			}
			break;
		case TRADEPICK:
			int imgx=HEX_SIZE,imgy=height-HEX_SIZE-2*margin;
			for(int i=2;i<7;i++) {
				buttons.add(new Button("",
						(i-2)*imgx+e.icons.get(0).getWidth(null)/4,
						imgy+e.icons.get(0).getHeight(null)/4,
						HEX_SIZE-2*margin,
						HEX_SIZE-2*margin));
			}			
			break;			
		case TRADECUSTOM:
			for(int i = 2; i<e.icons.size(); i++) {
				buttons.add(new Button("+",
						e.icons.get(i).getWidth(null)+2*margin,
						margin+(e.icons.get(i).getHeight(null)*(i-1))+e.icons.get(i).getHeight(null)/3,
						e.icons.get(i).getWidth(null)/2,
						e.icons.get(i).getHeight(null)/3
						
				));
				buttons.add(new Button("-",
						e.icons.get(i).getWidth(null)+2*margin,
						margin+(e.icons.get(i).getHeight(null)*(i-1))+(2*e.icons.get(i).getHeight(null)/3),
						e.icons.get(i).getWidth(null)/2,
						e.icons.get(i).getHeight(null)/3
						
				));
			}
			for(int i = 2; i<e.icons.size(); i++) {
				buttons.add(new Button("+",
						width-margin-e.icons.get(i).getWidth(null)-(2*margin+e.icons.get(i).getWidth(null)/2)+margin,
						margin+(e.icons.get(i).getHeight(null)*(i-1))+e.icons.get(i).getHeight(null)/3,
						e.icons.get(i).getWidth(null)/2,
						e.icons.get(i).getHeight(null)/3
						
				));
				buttons.add(new Button("-",
						width-margin-e.icons.get(i).getWidth(null)-(2*margin+e.icons.get(i).getWidth(null)/2)+margin,
						margin+(e.icons.get(i).getHeight(null)*(i-1))+(2*e.icons.get(i).getHeight(null)/3),
						e.icons.get(i).getWidth(null)/2,
						e.icons.get(i).getHeight(null)/3
						
				));
			}
			buttons.add(new Button("OK",
					width/2-x/2,
					height-margin-20*5-x/4,
					x,
					x/4
					));
			break;
		case CARDS:
			int titlex = (width/2)-(x+margin)-(x/2);
			int titley = (margin*4);
			int index=0;
			for(Development d : p.devs) {
				buttons.add(new Button(d.info,
					titlex-(x/2)-margin/2,
					titley+(3*(x/2)*(index/4)),
					(x)+margin,
					4*x/3));
				index++;
				if(index%4==0) titlex = (width/2)-(x+margin)-(x/2);
				else titlex+=x+margin;
			}
			break;
		case DEV:
			break;
		case ROBBER:
			break;
		case OPTIONS:
			break;
		default:
			break;
		}
	}
	public void tick(MouseAdapter mAdapter){
		if(deltay>0) {
			deltay-=4;
			y-=4;
		}
		for(Button b: buttons) {
			if(b.hover(mAdapter.mx-x,mAdapter.my-y)&&mAdapter.click) {
				b.pressed=true;
			}else {
				b.pressed=false;
			}
		}
	}
	public Button bGet(String str) {
		for(Button b : buttons) {
			if(b.label.equalsIgnoreCase(str))return b;
		}
		return null;
	}
}
