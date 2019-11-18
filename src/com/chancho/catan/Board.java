package com.chancho.catan;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.chancho.catan.control.Button;
import com.chancho.catan.control.Chat;
import com.chancho.catan.control.Dice;
import com.chancho.catan.control.MouseAdapter;
import com.chancho.catan.control.Toast;
import com.chancho.catan.control.Toast.ToastType;
import com.chancho.catan.map.Development;
import com.chancho.catan.map.Development.DevType;
import com.chancho.catan.map.Harbor;
import com.chancho.catan.map.Resource.ResType;
import com.chancho.catan.map.Road;
import com.chancho.catan.map.SetNode;
import com.chancho.catan.map.Tile;
import com.chancho.catan.map.Tile.TileType;


public class Board extends JPanel implements Runnable{
	private static final long serialVersionUID = 2195379760914381351L;
	public int WIDTH = 600, HEIGHT = 600, DELAY = 100, HEX_SIZE=120;
	public int SIDEBAR_WIDTH = 3*HEX_SIZE;
	public int hex_x = (WIDTH/2)-((HEX_SIZE*4)/2), hex_y=(HEIGHT)-((HEIGHT-(HEX_SIZE*4)/2))/2;
	public int activeplayer, turn, ticks, roadQueue, setQueue, cityQueue;
	public StringTokenizer startTokens;
	public boolean mainMenu = true;
	
	private Dice dice;
	private MouseAdapter mAdapter;
	private Etch e;
	private Thread timer;
	private Toast toast;
	private Chat chat;

	public Stack<Development> devs = new Stack<Development>();
	private ArrayList<Button> buttons = new ArrayList<Button>();
	private ArrayList<Road> roads = new ArrayList<Road>();
	private ArrayList<SetNode> setNodes = new ArrayList<SetNode>();
	private ArrayList<Harbor> harbors = new ArrayList<Harbor>();
	private ArrayList<Player> players = new ArrayList<Player>();
	public ArrayList<Tile> hexes = new ArrayList<Tile>(),ocean_hexes = new ArrayList<Tile>();
	
	public Board() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.decode("#016cba"));
		mAdapter = new MouseAdapter(chat=new Chat());
		addKeyListener(mAdapter);
		addMouseListener(mAdapter);
		addMouseMotionListener(mAdapter);
		setFocusable(true);
		requestFocus();
		splash();
		initGame();
	}
	public void initGame() {
		//CREATE BUFFERED IMAGES
		e = new Etch(HEX_SIZE);
		dice = new Dice();
		hex_x = (WIDTH/2)-((HEX_SIZE*4)/2); hex_y=(HEIGHT)-((HEIGHT-(HEX_SIZE*4)/2))/2;
		hexes.clear();ocean_hexes.clear();buttons.clear();roads.clear();setNodes.clear();harbors.clear();players.clear();devs.clear();
		//ADD TILES AND SHUFFLE
		hexes.add(new Tile(Tile.TileType.DESERT,HEX_SIZE));
		hexes.get(0).robber=true;
		for(int i=0; i<4; i++)hexes.add(new Tile(Tile.TileType.FIELD,HEX_SIZE));
		for(int i=0; i<4; i++)hexes.add(new Tile(Tile.TileType.FOREST,HEX_SIZE));
		for(int i=0; i<4; i++)hexes.add(new Tile(Tile.TileType.PASTURE,HEX_SIZE));
		for(int i=0; i<3; i++)hexes.add(new Tile(Tile.TileType.MOUNTAIN,HEX_SIZE));
		for(int i=0; i<3; i++)hexes.add(new Tile(Tile.TileType.HILL,HEX_SIZE));
		Collections.shuffle(hexes);
		
		//ASSIGN TOKENS AND ARRANGE TILES
		StringTokenizer token = new StringTokenizer("5 2 6 3 8 10 9 12 11 4 8 10 9 4 5 6 3 11");
		StringTokenizer arranger = new StringTokenizer("FN FN FN HF HF SF SF BN BN SB SB HB FN FN HF SF BN SB FN");
		for(Tile h: hexes) {
			String arrange = arranger.nextToken();
			switch(arrange.substring(0, 1)) {
			case "F":
				hex_x+=HEX_SIZE;
				break;
			case "B":
				hex_x-=HEX_SIZE;
				break;
			case "H":
				hex_x+=HEX_SIZE/2;
				break;
			case "S":
				hex_x-=HEX_SIZE/2;
				break;
			}
			switch(arrange.substring(1, 2)) {
			case "F":
				hex_y-=(HEX_SIZE/4)*3;
				break;
			case "B":
				hex_y+=(HEX_SIZE/4)*3;
				break;
			}
			h.setCoords(hex_x, hex_y);
			if(h.getType()!=Tile.TileType.DESERT)h.setVal(Integer.parseInt(token.nextToken()));
			//System.out.println(h.getType()+" - "+h.getVal());
		}
		
		//CREATE SETTLEMENT NODES
		for(int c = 0; c<3; c++) {
			hex_x = (WIDTH/2)-((HEX_SIZE*4)/2)+(HEX_SIZE)-(HEX_SIZE/2*c);
			hex_y=(HEIGHT)-((HEIGHT-(HEX_SIZE*4)/2))/2+((HEX_SIZE/4)*3)-((HEX_SIZE/4)*3*c);
			boolean d = true;
			for(int h=0-c; h<=6+c;h++) {
				setNodes.add(new SetNode(hex_x,hex_y,e.nchit));
				setNodes.add(new SetNode(hex_x , (HEIGHT/2)-(Math.abs((HEIGHT/2)-hex_y)),e.nchit));
				hex_x+=HEX_SIZE/2;
				hex_y+=(d?1:-1)*(HEX_SIZE/4);
				d=!d;
			}			
		}
		for(SetNode s : setNodes) {
			for(Tile t : hexes) {
				if(t.getX()==s.getX() && t.getY()==s.getY()-(HEX_SIZE/4))s.tiles.add(t);
				if(t.getX()==s.getX() && t.getY()==s.getY()-(3*(HEX_SIZE/4)))s.tiles.add(t);
				if(t.getX()==s.getX()-(HEX_SIZE/2) && t.getY()==s.getY())s.tiles.add(t);
				if(t.getX()==s.getX()-(HEX_SIZE/2) && t.getY()==s.getY()-HEX_SIZE)s.tiles.add(t);
				if(t.getX()==s.getX()-HEX_SIZE && t.getY()==s.getY()-(HEX_SIZE/4))s.tiles.add(t);
				if(t.getX()==s.getX()-HEX_SIZE && t.getY()==s.getY()-(3*(HEX_SIZE/4)))s.tiles.add(t);
			}
		}
		
		//CREATE ROADS LIST
		for(int i = 0;i<=12;i+=2) {
			if(i!=12) {
				roads.add(new Road(setNodes.get(i),setNodes.get(i+2)));
				roads.add(new Road(setNodes.get(i+1),setNodes.get(i+3)));
			}
			if(i%4==0) {
				roads.add(new Road(setNodes.get(i),setNodes.get(i+16)));
				roads.add(new Road(setNodes.get(i+1),setNodes.get(i+17)));
			}
		}
		for(int i = 14;i<=30;i+=2) {
			if(i!=30) {
				roads.add(new Road(setNodes.get(i),setNodes.get(i+2)));
				roads.add(new Road(setNodes.get(i+1),setNodes.get(i+3)));
			}
			if(i%4!=0) {
				roads.add(new Road(setNodes.get(i),setNodes.get(i+20)));
				roads.add(new Road(setNodes.get(i+1),setNodes.get(i+21)));
			}
		}
		for(int i = 32;i<=52;i+=2) {
			if(i!=52) {
				roads.add(new Road(setNodes.get(i),setNodes.get(i+2)));
				roads.add(new Road(setNodes.get(i+1),setNodes.get(i+3)));
			}
			if(i%4==0)roads.add(new Road(setNodes.get(i),setNodes.get(i+1)));
		}
		
		//ASSIGN ROADS TO SETNODES
		for(SetNode s: setNodes) {for(Road r: roads) {
			if(r.connects(s))s.roads.add(r);
		}}
		
		//ARRANGE OCEAN TILES
		hex_x = (WIDTH/2)-((HEX_SIZE*4)/2);
		hex_y=(HEIGHT)-((HEIGHT-(HEX_SIZE*4)/2))/2;
		StringTokenizer oceanTiles = new StringTokenizer("NN SF SF HF HF HF FN FN FN HB HB HB SB SB SB BN BN BN");
		while(oceanTiles.hasMoreTokens()) {
			String arrange = oceanTiles.nextToken();
			switch(arrange.substring(0, 1)) {
			case "F":
				hex_x+=HEX_SIZE;
				break;
			case "B":
				hex_x-=HEX_SIZE;
				break;
			case "H":
				hex_x+=HEX_SIZE/2;
				break;
			case "S":
				hex_x-=HEX_SIZE/2;
				break;
			}
			switch(arrange.substring(1, 2)) {
			case "F":
				hex_y-=(HEX_SIZE/4)*3;
				break;
			case "B":
				hex_y+=(HEX_SIZE/4)*3;
				break;
			}
			ocean_hexes.add(new Tile(Tile.TileType.OCEAN,HEX_SIZE));
			ocean_hexes.get(ocean_hexes.size()-1).setCoords(hex_x, hex_y);
			
		}
		
		//CREATE HARBORS
		StringTokenizer ports = new StringTokenizer("17 0 2 1 15 6 8 3 1 14 34 2 13 28 30 5 11 52 53 1 5 1 3 1 7 7 9 6 3 15 35 4 9 29 31 1");
		while(ports.hasMoreTokens()) {
			harbors.add(new Harbor(
					ocean_hexes.get(Integer.valueOf(ports.nextToken())),
					setNodes.get(Integer.valueOf(ports.nextToken())),
					setNodes.get(Integer.valueOf(ports.nextToken())),
					Integer.valueOf(ports.nextToken())
			));
		}
		//SHUFFLE DEVELOPMENT CARDS
		for(int i=0;i<14;i++)devs.add(new Development(DevType.KNIGHT,"K"));
		StringTokenizer devC = new StringTokenizer("MON MON RB RB YOP YOP");
		while(devC.hasMoreTokens())devs.add(new Development(DevType.PROG,devC.nextToken()));
		devC = new StringTokenizer("C L M P U");
		while(devC.hasMoreTokens())devs.add(new Development(DevType.VP,devC.nextToken()));
		Collections.shuffle(devs);
				
		//INITIALIZE PLAYERS
		//players.add(new Player(Player.PlayerColor.WHITE));
		players.add(new Player(Player.PlayerColor.RED));
		players.add(new Player(Player.PlayerColor.BLUE));
		//players.add(new Player(Player.PlayerColor.ORANGE));
		Collections.shuffle(players);
		activeplayer = 0;
		e.updateBuild(players.get(activeplayer));
		String startQueue=""; 
		for(int p = 1;p<players.size();p++)startQueue+=String.valueOf(p)+" ";
		for(int p = players.size()-1;p>=0;p--)startQueue+=String.valueOf(p)+" ";
		startTokens = new StringTokenizer(startQueue);		
		setQueue=1;	roadQueue=1;
		players.get(activeplayer).endTurn=false;
		for(int i = 100; i>0; i--) {
			players.get(activeplayer).addRes(TileType.values()[i%6]);
		}
		//INITIALIZE ALL BUTTONS
		buttons.add(new Button("ROLL",WIDTH+HEX_SIZE, HEIGHT-4*(HEX_SIZE/2)-HEX_SIZE/2,SIDEBAR_WIDTH-HEX_SIZE,HEX_SIZE/2,false));
		buttons.add(new Button("BUY",WIDTH+HEX_SIZE, HEIGHT-4*(HEX_SIZE/2)-(2*HEX_SIZE/2),(SIDEBAR_WIDTH-HEX_SIZE)/3,HEX_SIZE/2,false));
		buttons.add(new Button("TRADE",WIDTH+HEX_SIZE+(SIDEBAR_WIDTH-HEX_SIZE)/3, HEIGHT-4*(HEX_SIZE/2)-(2*HEX_SIZE/2),(SIDEBAR_WIDTH-HEX_SIZE)/3,HEX_SIZE/2,false));
		buttons.add(new Button("CARDS",WIDTH+HEX_SIZE+(2*(SIDEBAR_WIDTH-HEX_SIZE)/3), HEIGHT-4*(HEX_SIZE/2)-(2*HEX_SIZE/2),(SIDEBAR_WIDTH-HEX_SIZE)/3,HEX_SIZE/2,false));
		buttons.add(new Button("CANCEL",WIDTH+HEX_SIZE, HEIGHT-4*(HEX_SIZE/2)-(2*HEX_SIZE/2),(SIDEBAR_WIDTH-HEX_SIZE),HEX_SIZE,false));
		buttons.add(new Button("Send",WIDTH+HEX_SIZE+(SIDEBAR_WIDTH-HEX_SIZE)-(HEX_SIZE/3), HEIGHT-3*(HEX_SIZE/2) + 3*(HEX_SIZE/2)-(HEX_SIZE/4), HEX_SIZE/3, (HEX_SIZE/4)));
		
		//toast=mainMenu?new Toast(HEX_SIZE,WIDTH,HEIGHT,ToastType.MAIN,e):null;mainMenu=false;
		toast=new Toast(HEX_SIZE,WIDTH,HEX_SIZE*4,ToastType.TRADEPICK,e,players.get(activeplayer),true);mainMenu=false;
	}
	public void splash() {
		WIDTH = ((int) 6.25*HEX_SIZE);
		HEIGHT = (int) 6.25*HEX_SIZE;
		hex_x = (WIDTH/2)-((HEX_SIZE*4)/2);
		hex_y=(HEIGHT)-((HEIGHT-(HEX_SIZE*4)/2))/2;
		setPreferredSize(new Dimension(WIDTH+SIDEBAR_WIDTH, HEIGHT));
		
		//RetrieveOptions
		//PreLoading
	}
	
	/*
	 * GAME LOOP
	 */
	
	public void tick() {
		ticks++;
		dice.tick();
		//TOAST ACTION HANDLER
		if(toast!=null) {
			boolean out = false;
			toast.tick(mAdapter);
			switch(toast.type) {
			case MAIN:
				if(toast.bGet("Play Now").pressed)out=true;
				else if(toast.bGet("Join Match").pressed)out=true;
				else if(toast.bGet("Options").pressed)toast=new Toast(HEX_SIZE,WIDTH,HEIGHT,ToastType.OPTIONS,e);
				else if(toast.bGet("Quit").pressed)System.exit(0);
				break;
				
			case BUY:
				if(toast.buttons.get(0).pressed && players.get(activeplayer).canBuild(0)) {roadQueue++;out=true;buttons.get(4).show=true;for(Player p : players)p.updateValid();}
				else if(toast.buttons.get(1).pressed&& players.get(activeplayer).canBuild(1)) {setQueue++;out=true;buttons.get(4).show=true;}
				else if(toast.buttons.get(2).pressed&& players.get(activeplayer).canBuild(2)) {cityQueue++;out=true;buttons.get(4).show=true;}
				else if(toast.buttons.get(3).pressed&& players.get(activeplayer).canBuild(3)) {
					players.get(activeplayer).devs.add(devs.pop());
					players.get(activeplayer).devs.get(players.get(activeplayer).devs.size()-1).turn=turn;
					players.get(activeplayer).build(3);
					toast=new Toast(HEX_SIZE,WIDTH/2,HEIGHT,ToastType.DEV,e,players.get(activeplayer));}
				break;
				
			case TRADE:
				for(Button b : toast.buttons)if(b.pressed) {
					if(toast.buttons.indexOf(b)==0);
					else if(b==toast.bGet("Custom"))toast=new Toast(HEX_SIZE,WIDTH/2,HEIGHT,ToastType.TRADECUSTOM,e,players.get(activeplayer));
					else if(players.get(activeplayer).canTrade(players.get(activeplayer).harbors.get(toast.buttons.indexOf(b)-1).type,null)) {
						toast=new Toast(HEX_SIZE,WIDTH,HEX_SIZE*4,ToastType.TRADEPICK,e,players.get(activeplayer),true);
					}
				}
				break;
			case TRADEPICK:
				for(Button b : toast.buttons)if(b.pressed) {
					players.get(activeplayer).tradeB=ResType.values()[toast.buttons.indexOf(b)]
				}
				break;
				
			case TRADECUSTOM:
				for(Button b : toast.buttons)if(b.pressed) {
					if(b.label=="OK");//players.get(activeplayer).trade(toast.string);
					else{
						toast.string.set(toast.buttons.indexOf(b)/2,String.valueOf(Integer.valueOf(toast.string.get(toast.buttons.indexOf(b)/2))+(toast.buttons.indexOf(b)%2==0?1:-1)));
						if(Integer.valueOf(toast.string.get(toast.buttons.indexOf(b)/2))<0)toast.string.set(toast.buttons.indexOf(b)/2,"0");
					}
				}
				break;
				
			case CARDS:
				for(Button b : toast.buttons)if(b.pressed && players.get(activeplayer).devs.get(toast.buttons.indexOf(b)).turn!=turn) {
					switch(players.get(activeplayer).devs.get(toast.buttons.indexOf(b)).type) {
					case KNIGHT:
						players.get(activeplayer).moveRobber = true;
						buttons.get(0).label="MOVE ROBBER";
						break;
					case PROG:
						switch(players.get(activeplayer).devs.get(toast.buttons.indexOf(b)).index) {
						case 1://MONOPOLY
							break;
						case 2://ROAD BUILDING
							roadQueue=2;
							break;
						case 3://YEAR OF PLENTY
							break;
						}
					case VP:
						break;
					default:
						break;
					
					}
				}
				break;
				
			case DEV:
				if(mAdapter.click)out=true;
				break;
			case DEVPICK:
				break;
			case NULL:
				break;
			case OPTIONS:
				break;
			case ROBBER:
				break;
			default:
				break;
			}
			if((mAdapter.click && toast.quitable && toast.y<HEIGHT/2 &&(
				mAdapter.mx<toast.x || 
				mAdapter.mx>toast.width+toast.x || 
				mAdapter.my<toast.y || 
				mAdapter.my>toast.height+toast.y))||out) {
				toast=null;
				players.get(activeplayer).resetTrades();
			}
		}
		//GAME LOOP
		else {
			if(roadQueue>0) {
				for(Road r : roads) {
					if(players.get(activeplayer).validRoads.contains(r) && r.hover(mAdapter.mx,mAdapter.my) &&r.p==null) {
							r.primary=players.get(activeplayer).primaryColor();
							r.secondary=players.get(activeplayer).secondaryColor();
							if(mAdapter.click) {
								r.claim(players.get(activeplayer));
								players.get(activeplayer).roads.add(r);
								players.get(activeplayer).build(0);
								mAdapter.click=false;
								roadQueue--;
								if(roadQueue==0 && turn!=0) {for(int i=0;i<4;i++) {buttons.get(i).show=true;}buttons.get(4).show=false;}
								for(Road rr : roads)if(rr.p==null)rr.resetColors();
								for(Player p : players)p.updateValid();
								int oroads=0;
								for(Road rr : roads) {
									if(rr.p==null)rr.resetColors();
									else oroads++;
								}
								if(oroads<players.size()*2) {players.get(activeplayer).fin();setQueue=1;roadQueue=1;}
								else if(turn==0){buttons.get(0).show=true;turn=1;}
								
							}
					}else if(players.get(activeplayer).validRoads.contains(r) && r.p==null) {
						r.resetColors(ticks);
					}
				}
			}
			if(setQueue>0) {
				for(SetNode s : setNodes) {
					if(s.hover(mAdapter.mx, mAdapter.my)&&s.p==null&&s.isValid(players.get(activeplayer))) {
						s.nchit=e.nchit_Settlement.get(players.get(activeplayer).chitIndex());
						if(mAdapter.click){
							s.claim(players.get(activeplayer), e.nchit_Settlement.get(players.get(activeplayer).chitIndex()));
							players.get(activeplayer).nodes.add(s);
							players.get(activeplayer).build(1);
							mAdapter.click=false;
							setQueue--;
							if(setQueue==0&& turn!=0) {for(int i=0;i<4;i++) {buttons.get(i).show=true;}buttons.get(4).show=false;}
							for(Player p : players)p.updateValid();
							for(Harbor h : harbors)if(h.connects(s))players.get(activeplayer).harbors.add(h);
						}
					}else if(s.p == null && s.isValid(players.get(activeplayer))) {
						int tt = ticks;
						while(tt>240)tt-=240;
						if(tt<120) {
							s.nchit=e.nchit;
						}else if(tt<240) {
							s.nchit=e.nchitflash;
						}
					}
				}
				if(setQueue==0) {
					for(SetNode ss : setNodes) {if(ss.p == null) {ss.nchit=e.nchit;}}
				}
			}
			if(cityQueue>0) {
				for(SetNode s : setNodes) {
					if(s.hover(mAdapter.mx, mAdapter.my)&&s.p==players.get(activeplayer)&&!s.upgraded) {
						s.nchit=e.nchit_City.get(players.get(activeplayer).chitIndex());
						if(mAdapter.click){
							s.claim(players.get(activeplayer), e.nchit_City.get(players.get(activeplayer).chitIndex()));
							s.upgraded=true;
							players.get(activeplayer).build(2);
							mAdapter.click=false;
							cityQueue--;
							if(cityQueue==0) {for(int i=0;i<4;i++) {buttons.get(i).show=true;}buttons.get(4).show=false;}
						}
					}else if(s.p==players.get(activeplayer)&&!s.upgraded) {
						s.nchit=e.nchit_Settlement.get(players.get(activeplayer).chitIndex());
					}
				}
			}
			//BUTTON HANDLER
			for(Button b : buttons) {if(b.show) {
				if(buttons.indexOf(b)==4)for(int i=0;i<4;i++) {buttons.get(i).show=false;}
				b.pressed=(b.hover(mAdapter.mx, mAdapter.my)&&mAdapter.click);
				if(b.label=="CANCEL"&&b.pressed) {setQueue=0;roadQueue=0;cityQueue=0;for(int i=0;i<4;i++) {buttons.get(i).show=true;}}
				if(b.label=="BUY"&&b.pressed)toast=new Toast(HEX_SIZE,WIDTH,HEIGHT,ToastType.BUY,e);
				if(b.label=="TRADE"&&b.pressed)toast=new Toast(HEX_SIZE,WIDTH,HEIGHT,ToastType.TRADE,e,players.get(activeplayer));
				if(b.label=="CARDS"&&b.pressed)toast=new Toast(HEX_SIZE,WIDTH,HEIGHT,ToastType.CARDS,e,players.get(activeplayer));
				if(b.label=="ROLL" && players.get(activeplayer).hasRolled && dice.timer>500) {
					if(!dice.distr && dice.show!=7) {for(Player p : players) {for(SetNode n : p.nodes) {for(Tile t : n.tiles) {
						//PRODUCTION ROLL HANDLER
						if(dice.show==t.getVal() && !t.robber) {
							p.addRes(t.getType());
							if(n.upgraded)p.addRes(t.getType());
						}
						dice.distr=true;
						buttons.get(0).label="END TURN";
						buttons.get(1).show=true;
						buttons.get(2).show=true;
						buttons.get(3).show=true;
					}}}}else if(!dice.distr && dice.show==7) {
						//ROBBER ROLL HANDLER
						players.get(activeplayer).moveRobber = true;
						buttons.get(0).label="MOVE ROBBER";
					}
				}
				if(b.label=="ROLL"&& b.pressed && !players.get(activeplayer).hasRolled) {
					players.get(activeplayer).hasRolled=true;
					dice.roll();
				}
				if((b.label=="END TURN"&&b.pressed)) {
					players.get(activeplayer).fin();
				}
				if((b.label=="Send"&&b.pressed)||mAdapter.enter) {
					chat.send(activeplayer);
					mAdapter.enter=false;
				}
			}}
			if(roadQueue==0 && setQueue==0 && cityQueue==0) {buttons.get(4).show=false; for(Road r : roads)r.resetColors();}
			
			
			if(players.get(activeplayer).moveRobber) {
				for(Tile h : hexes) {
					if(!h.robber && h.hover(mAdapter.mx, mAdapter.my) && mAdapter.click) {
						for(Tile hr : hexes) {
							hr.robber=false;
						}
						players.get(activeplayer).moveRobber=false;
						h.robber=true;
						dice.distr=true;
						buttons.get(0).label="END TURN";
						buttons.get(1).show=true;
						buttons.get(2).show=true;
						buttons.get(3).show=true;
					};
				}
			}	
			if(players.get(activeplayer).endTurn) {
				for(Player p : players)p.updateValid();
				for(Road r: roads)r.resetColors(); 
				if(startTokens.hasMoreTokens()) {
					activeplayer = Integer.valueOf(startTokens.nextToken());
					players.get(activeplayer).endTurn=false;
				}else {
					activeplayer++;
					if(activeplayer==players.size()) {activeplayer=0;turn++;}
					e.updateBuild(players.get(activeplayer));
					players.get(activeplayer).endTurn=false;
					buttons.get(0).label="ROLL";buttons.get(0).show=true;
					buttons.get(1).show=false;
					buttons.get(2).show=false;
					buttons.get(3).show=false;
				
				}
			}
		}
		mAdapter.click=false;
		repaint();
	}
	
	
	/*
	 * GRAPHICS 
	 */
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		//OCEANTILES
		for(Tile oh: ocean_hexes) {
			g.drawImage(e.hex.get(oh.getTypeID()),oh.getX(),oh.getY(),this);
			g.drawImage(e.outline_sea,oh.getX(),oh.getY(),this);
			//g.drawString(String.valueOf(ocean_hexes.indexOf(oh)), oh.getX()+HEX_SIZE/2,oh.getY()+HEX_SIZE/2);
		}
		//HEXTILES
		for(Tile h: hexes) {
			g.drawImage(e.hex.get(h.getTypeID()),h.getX(),h.getY(),this);
			g.drawImage(e.outline,h.getX(),h.getY(),this);
			//TOKEN PLACEMENT
			if(h.getType()!=Tile.TileType.DESERT && !h.robber) {
				g.drawImage(e.chit, h.getX()+(HEX_SIZE/2)-(HEX_SIZE/6), h.getY()+(HEX_SIZE/2)-(HEX_SIZE/6),null);
				
				g.setColor((h.getVal()==8||h.getVal()==6)?Color.RED:Color.BLACK);
				//Probability Stars
				String prob = "******";
				prob = prob.substring(Math.abs(7-h.getVal()));
					
				g.setFont(e.chFont.deriveFont(12.0f));
				g.drawString(prob, h.getX()+(HEX_SIZE/2)-(g.getFontMetrics().stringWidth(prob)/2), h.getY()+(HEX_SIZE/2)-(HEX_SIZE/6)+15);
				
				String val = String.valueOf(h.getVal());
				g.setFont(e.chFont.deriveFont(24.0f));
				g.drawString(val, h.getX()+(HEX_SIZE/2)-(g.getFontMetrics().stringWidth(val)/2), h.getY()+(HEX_SIZE/2)-(HEX_SIZE/12)+21);
			}
			if((players.get(activeplayer).moveRobber && h.hover(mAdapter.mx, mAdapter.my) )||h.robber) {
				g.drawImage(e.chit, h.getX()+(HEX_SIZE/2)-(HEX_SIZE/6), h.getY()+(HEX_SIZE/2)-(HEX_SIZE/6),null);
				g.drawImage(e.iconsQ.get(1), h.getX()+(HEX_SIZE/2)-(HEX_SIZE/8), h.getY()+(HEX_SIZE/2)-(HEX_SIZE/8),null);
			}
		}
		//HARBORS
		for(Harbor mp : harbors) {
			g2d.setColor(e.roadSecondary);
			g2d.setStroke(new BasicStroke(14));
			g2d.drawLine(mp.nA.getX(), mp.nA.getY(), mp.getX()+mp.deltaX(mp.nA),mp.getY()+mp.deltaY(mp.nA));
			g2d.drawLine(mp.nB.getX(), mp.nB.getY(), mp.getX()+mp.deltaX(mp.nB),mp.getY()+mp.deltaY(mp.nB));
			g2d.setColor(e.roadPrimary);
			g2d.setStroke(new BasicStroke(12));
			g2d.drawLine(mp.nA.getX(), mp.nA.getY(), mp.getX()+mp.deltaX(mp.nA),mp.getY()+mp.deltaY(mp.nA));
			g2d.drawLine(mp.nB.getX(), mp.nB.getY(), mp.getX()+mp.deltaX(mp.nB),mp.getY()+mp.deltaY(mp.nB));
			
			g.setColor(Color.black);
			int mpX = mp.getX()-(HEX_SIZE/4);
			int mpY = mp.getY()-(HEX_SIZE/4);
			g.drawImage(e.icons.get(0), mpX,mpY,this);
			if(mp.type>1)g.drawImage(e.iconsQ.get(mp.type), mpX+(HEX_SIZE/7), mpY+(HEX_SIZE/16),this);
			else g.drawString("3:1", mpX+(HEX_SIZE/16)+g.getFontMetrics().stringWidth("3:1")/2, mpY+(HEX_SIZE/32)+g.getFontMetrics().getHeight());
		}
		
		//ROADS
		for(Road r : roads) {
			g2d.setColor(r.secondary);
			g2d.setStroke(new BasicStroke(14));
			g2d.drawLine(r.nA.getX(), r.nA.getY(), r.nB.getX(), r.nB.getY());
			g2d.setColor(r.primary);
			g2d.setStroke(new BasicStroke(12));
			g2d.drawLine(r.nA.getX(), r.nA.getY(), r.nB.getX(), r.nB.getY());
			
		}
		//SETTLEMENT NODES
		for(SetNode s : setNodes) {
			g.drawImage(s.nchit, s.getX()-s.nchit.getWidth(this)/2,s.getY()-s.nchit.getWidth(this)/2,this);
			/*NODES TESTING
			g.setColor(Color.black);
			for(Tile t : s.tiles) {
				if(t.robber)g.setColor(Color.red);
			}
			//g.drawString(String.valueOf(s.tiles.size()),s.getX()-s.nchit.getWidth(this)/2,s.getY()-s.nchit.getWidth(this)/2);
			g.drawString(String.valueOf(setNodes.indexOf(s)+""+s.roads.size()),s.getX()-s.nchit.getWidth(this)/2,s.getY()-s.nchit.getWidth(this)/2);
			*/
		}
		
		//DICE
		if(dice.timer<500 && turn>0) {
			g.drawImage(e.dice.get(dice.timer%6), WIDTH+HEX_SIZE/2-HEX_SIZE/4, HEX_SIZE/4,this);
			g.drawImage(e.dice.get(dice.timer%6), WIDTH-HEX_SIZE/4, HEX_SIZE/4,this);
		}else if(turn>0) {
			g.drawImage(e.dice.get(dice.a-1), WIDTH+HEX_SIZE/2-HEX_SIZE/4, HEX_SIZE/4,this);
			g.drawImage(e.dice.get(dice.b-1), WIDTH-HEX_SIZE/4, HEX_SIZE/4,this);
		}
		//SIDEBAR GUI
		g.setColor(e.roadSecondary);
		g.fillRect(WIDTH+HEX_SIZE, 0,SIDEBAR_WIDTH-HEX_SIZE,HEIGHT);
		
		//Turn Indicator
		int playersX = WIDTH+HEX_SIZE;
		int margin = HEX_SIZE/40;
		for(Player p : players) {
			
			g2d.setStroke(new BasicStroke(margin));
			g.setColor(p.primaryColor());
			g.fillRect(playersX,0,(SIDEBAR_WIDTH-HEX_SIZE)/players.size()-margin,(!p.endTurn?HEX_SIZE/2:HEX_SIZE/4));
			g.setColor(p.secondaryColor());
			g.drawRect(playersX,0,(SIDEBAR_WIDTH-HEX_SIZE)/players.size()-margin,(!p.endTurn?HEX_SIZE/2:HEX_SIZE/4));
			playersX+=(SIDEBAR_WIDTH-HEX_SIZE)/players.size();
		}
		g.setColor(Color.black);
		g.drawString(String.valueOf(turn), WIDTH+HEX_SIZE,2*HEX_SIZE/3);
		
		//Player Resources
		g.setColor(e.guiPrimary);
		g.fillRect(WIDTH+SIDEBAR_WIDTH-HEX_SIZE,2*HEX_SIZE/3,HEX_SIZE,(5*(HEX_SIZE/3)));
		g.fillRect(WIDTH+SIDEBAR_WIDTH-HEX_SIZE/2,2*HEX_SIZE/3,HEX_SIZE/2,(6*(HEX_SIZE/3)));
		g.setColor(e.guiSecondary);
		for(int i = 2;i<7;i++) {
			g.drawImage(e.icons.get(i), WIDTH+SIDEBAR_WIDTH-HEX_SIZE,2*HEX_SIZE/3+((i-3)*(HEX_SIZE/3))+(HEX_SIZE/4), this);
			g.drawRect(WIDTH+SIDEBAR_WIDTH-HEX_SIZE,2*HEX_SIZE/3,HEX_SIZE,((i-1)*(HEX_SIZE/3)));
			g.setColor(Color.black);
			g.drawString(String.valueOf(players.get(activeplayer).getRes(i-2)), WIDTH+SIDEBAR_WIDTH-HEX_SIZE/2+(HEX_SIZE/4),2*HEX_SIZE/3+((i-2)*(HEX_SIZE/3))+(HEX_SIZE/4));
			g.setColor(e.guiSecondary);
		}
		g.setColor(Color.black);
		g.drawString(String.valueOf(players.get(activeplayer).getRes(5)), WIDTH+SIDEBAR_WIDTH-HEX_SIZE/2+(HEX_SIZE/4),2*HEX_SIZE/3+((5)*(HEX_SIZE/3))+(HEX_SIZE/4));
		g.setColor(e.guiSecondary);
		g.drawRect(WIDTH+SIDEBAR_WIDTH-HEX_SIZE,2*HEX_SIZE/3,HEX_SIZE,(5*(HEX_SIZE/3)));
		g.drawRect(WIDTH+SIDEBAR_WIDTH-HEX_SIZE/2,2*HEX_SIZE/3,HEX_SIZE/2,(6*(HEX_SIZE/3)));

		//ChatBox
		g.setColor(e.roadPrimary);
		g.fillRect(WIDTH+HEX_SIZE, HEIGHT-4*(HEX_SIZE/2), SIDEBAR_WIDTH-HEX_SIZE, 4*(HEX_SIZE/2));
		g.setColor(e.roadSecondary);
		g.drawRect(WIDTH+HEX_SIZE, HEIGHT-4*(HEX_SIZE/2), SIDEBAR_WIDTH-HEX_SIZE, 4*(HEX_SIZE/2));
		g.drawRect(WIDTH+HEX_SIZE, HEIGHT-3*(HEX_SIZE/2) + 3*(HEX_SIZE/2)-(HEX_SIZE/4), SIDEBAR_WIDTH-HEX_SIZE, (HEX_SIZE/4));
		g.drawRect((WIDTH+HEX_SIZE)+(SIDEBAR_WIDTH-HEX_SIZE)-(HEX_SIZE/3), HEIGHT-3*(HEX_SIZE/2) + 3*(HEX_SIZE/2)-(HEX_SIZE/4), HEX_SIZE/3, (HEX_SIZE/4));
		g.setColor(Color.black);
		g.setFont(e.chFont.deriveFont((float)HEX_SIZE/8));
		g.drawString((chat.compose.length()>25?"..."+chat.compose.substring(chat.compose.length()-22):chat.compose)+(ticks%120<10?"|":""), 
				WIDTH+HEX_SIZE+margin,
				HEIGHT-3*(HEX_SIZE/2) + 3*(HEX_SIZE/2)-(HEX_SIZE/4)+3*g.getFontMetrics().getHeight()/2-2*margin);
		for(int i=3; i>0;i--) {if(chat.chatlog.size()-i>=0){
				g.setFont(e.chFont.deriveFont((float)HEX_SIZE/6));
				int pid = chat.chatlogpid.get(chat.chatlogpid.size()-i);
				if(pid==-1)g.setColor(e.guiSecondary);
				else g.setColor(players.get(pid).primaryColor());
				g.fillRect(WIDTH+HEX_SIZE+margin, HEIGHT-((i+1)*(HEX_SIZE/2))+margin, SIDEBAR_WIDTH-2*margin, 4*(HEX_SIZE/2)/3-margin*7);
				g.setColor(Color.black);
				if(chat.chatlog.get(chat.chatlog.size()-i).length()>40) {
					g.drawString(chat.chatlog.get(chat.chatlog.size()-i).substring(0, 19),WIDTH+HEX_SIZE+3*margin, HEIGHT-(i)*(HEX_SIZE/2)-HEX_SIZE/3+margin);
					g.drawString(chat.chatlog.get(chat.chatlog.size()-i).substring(20),WIDTH+HEX_SIZE+3*margin, HEIGHT-(i)*(HEX_SIZE/2)-HEX_SIZE/6+margin);
				}else g.drawString(chat.chatlog.get(chat.chatlog.size()-i),WIDTH+HEX_SIZE+3*margin, HEIGHT-(i)*(HEX_SIZE/2)-HEX_SIZE/3+margin);
			}
		}

		//Actions
		for(Button b : buttons) {if(b.show) {
			g.setColor(!b.hover(mAdapter.mx, mAdapter.my)?e.guiPrimary:e.guiSecondary);
			g.fillRect(b.x,b.y, b.width-margin,b.height-margin);
			g.setColor(b.hover(mAdapter.mx, mAdapter.my)?e.guiPrimary:e.guiSecondary);
			g.drawRect(b.x,b.y, b.width-margin,b.height-margin);
			g.setFont(e.chFont.deriveFont((float)HEX_SIZE/b.ratio));
			g.setColor(Color.black);
			g.drawString(b.label,b.x+b.width/2-g.getFontMetrics().stringWidth(b.label)/2,b.y+b.height/2+10);
		}}
		//TOAST
		if(toast!=null) {
			g.setColor(e.guiPrimary);
			if(toast.type==ToastType.DEV)g.setColor(players.get(activeplayer).devs.get(players.get(activeplayer).devs.size()-1).color2);
			g.fillRect(toast.x,toast.y, toast.width, toast.height);
			g.setColor(players.get(activeplayer).primaryColor());
			g2d.setStroke(new BasicStroke(16));
			g.drawRect(toast.x,toast.y, toast.width, toast.height);
			g2d.setStroke(new BasicStroke(margin));
			
			for(Button b : toast.buttons) {
				if(toast.type==ToastType.DEV||toast.type==ToastType.CARDS) {
					Development d = players.get(activeplayer).devs.get(toast.buttons.indexOf(b));
					g.setColor(!b.hover(mAdapter.mx-toast.x, mAdapter.my-toast.y)?d.color:d.color2);
					g.fillRect(toast.x+b.x,toast.y+b.y, b.width-margin,b.height-margin);
					g.setColor(b.hover(mAdapter.mx-toast.x, mAdapter.my-toast.y)?d.color:d.color2);
					g.drawRect(toast.x+b.x,toast.y+b.y, b.width-margin,b.height-margin);
					if(b.hover(mAdapter.mx-toast.x, mAdapter.my-toast.y)) {
						g.setFont(e.chFont.deriveFont(20.0f));
						g.setColor(!b.hover(mAdapter.mx-toast.x, mAdapter.my-toast.y)?d.color:d.color2);
						g.fillRect(toast.x-g.getFontMetrics().stringWidth(b.label+1)/2+toast.width/2,toast.y-30, g.getFontMetrics().stringWidth(b.label+1),30);
						g.setColor(b.hover(mAdapter.mx-toast.x, mAdapter.my-toast.y)?d.color:d.color2);
						g.drawRect(toast.x-g.getFontMetrics().stringWidth(b.label+1)/2+toast.width/2,toast.y-30, g.getFontMetrics().stringWidth(b.label+1),30);
						g.setColor(toast.stringPoints.get(toast.buttons.indexOf(b))[4]==1?Color.white:Color.black);
						g.drawString(b.label,toast.x+toast.width/2-g.getFontMetrics().stringWidth(b.label+1)/2,toast.y-5);
					}
				}else {
					g.setColor(!b.hover(mAdapter.mx-toast.x, mAdapter.my-toast.y)?e.roadPrimary:e.roadSecondary);
					g.fillRect(toast.x+b.x,toast.y+b.y, b.width-margin,b.height-margin);
					g.setColor(b.hover(mAdapter.mx-toast.x, mAdapter.my-toast.y)?e.roadPrimary:e.roadSecondary);
					g.drawRect(toast.x+b.x,toast.y+b.y, b.width-margin,b.height-margin);
					g.setFont(e.chFont.deriveFont(25.0f));
					g.setColor(Color.black);
					g.drawString(b.label,toast.x+b.x+b.width/2-g.getFontMetrics().stringWidth(b.label+1)/2,toast.y+b.y+b.height/2+5);
				
				}
			}
			for(int i = 0;i < toast.imagePoints.size(); i++)g.drawImage(toast.image.get(i),toast.x+toast.imagePoints.get(i)[0],toast.y+toast.imagePoints.get(i)[1],this);
			for(int i = 0;i < toast.stringPoints.size(); i++) {
				if(toast.stringPoints.get(i).length==5)g.setColor(toast.stringPoints.get(i)[4]==1?Color.white:Color.black);
				else g.setColor(Color.black);
				g.setFont(e.chFont.deriveFont(Float.valueOf(toast.stringPoints.get(i)[2])));
				g.drawString(toast.string.get(i),toast.x+toast.stringPoints.get(i)[0]-(toast.stringPoints.get(i)[3]==1?g.getFontMetrics().stringWidth(toast.string.get(i))/2:0),toast.y+toast.stringPoints.get(i)[1]+toast.stringPoints.get(i)[2]);
			}
		}		
		g2d.dispose();
		g.dispose();
		Toolkit.getDefaultToolkit().sync();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		render(g);
	}

	@Override
	public void addNotify(){
		super.addNotify();
		timer = new Thread(this);
		timer.start();
	}
	@Override
	public void run() {
		long current,delta,sleep;
		current = System.currentTimeMillis();
		while(true) {
			tick();
			repaint();
			delta=System.currentTimeMillis()-current;
			sleep=DELAY-delta;
			if(sleep<0)sleep=2;
			try{
				Thread.sleep(sleep);
			}catch(InterruptedException e){
				String msg = String.format("Thread interrupted: %s", e.getMessage());
				JOptionPane.showMessageDialog(this, msg,"ERROR",JOptionPane.ERROR_MESSAGE);
			}
		}
	}	
}
