package com.chancho.catan;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.ImageIcon;


public class Etch {
	//IMAGE ICON may not be necessary to convert to Buffered Image
	private ImageIcon hexTilesII =new ImageIcon(Etch.class.getResource("res/HexTiles.png"));
	private ImageIcon chitII =new ImageIcon(Etch.class.getResource("res/Chit.png"));
	private ImageIcon outlineII = new ImageIcon(Etch.class.getResource("res/Outline.png"));
	private ImageIcon iconsII = new ImageIcon(Etch.class.getResource("res/Icons.png"));
	private ImageIcon diceII = new ImageIcon(Etch.class.getResource("res/Dice.png"));
	private ImageIcon mainII = new ImageIcon(Etch.class.getResource("res/Main.png"));
	private ImageIcon buildII = new ImageIcon(Etch.class.getResource("res/Build.png"));
	private ImageIcon devII = new ImageIcon(Etch.class.getResource("res/Dev.png"));
	//Simplify
	public Image main = toBufferedImage(mainII.getImage());
	public Image hexTiles = toBufferedImage(hexTilesII.getImage());
	public Image iconImage = toBufferedImage(iconsII.getImage());
	public Image chit = getTile(0,toBufferedImage(chitII.getImage()));/*occupied*/
	public Image nchit = getTile(1,toBufferedImage(chitII.getImage()));/*neutral*/
	public Image nchitflash = flash();
	public Image outline = getTile(1,toBufferedImage(outlineII.getImage()));
	public Image outline_sea = getTile(0,toBufferedImage(outlineII.getImage()));
	public Image diceImage = toBufferedImage(diceII.getImage());
	
	public Color roadPrimary = Color.decode("#bcaaa4");
	public Color roadSecondary = Color.decode("#8c7b75");
	public Color guiPrimary = Color.decode("#dddddd");
	public Color guiSecondary = Color.decode("#ababab");
	
	//SETTLEMENTS + CITIES
	/*
	 * 0 - White
	 * 1 - Red
	 * 2 - Blue
	 * 3 - Orange
	 */
	//ICONS
	/*
	 * 0 - Port
	 * 1 - Robber
	 * 2 - Grain
	 * 3 - Lumber
	 * 4 - Ore
	 * 5 - Brick
	 * 6 - Sheep
	 */
	
	public ArrayList<Image> build = new ArrayList<Image>();
	public ArrayList<Image> dice = new ArrayList<Image>();
	public ArrayList<Image> icons = new ArrayList<Image>();
	public ArrayList<Image> iconsQ = new ArrayList<Image>();
	public ArrayList<Image> nchit_Settlement = new ArrayList<Image>();
	public ArrayList<Image> nchit_City = new ArrayList<Image>();
	public ArrayList<Image> dev = new ArrayList<Image>();
	public ArrayList<Image> hex = new ArrayList<Image>();
	
	String fName = "res/Oxygen-Regular.ttf";
    InputStream is = Etch.class.getResourceAsStream(fName);
	public Font chFont; 
	public int HEX_SIZE;
	
	public Etch(int HEX_SIZE) {
		this.HEX_SIZE=HEX_SIZE;
		outline = outline.getScaledInstance(HEX_SIZE, HEX_SIZE, Image.SCALE_DEFAULT);
		outline_sea = outline_sea.getScaledInstance(HEX_SIZE, HEX_SIZE, Image.SCALE_DEFAULT);
		chit = chit.getScaledInstance(HEX_SIZE/3, HEX_SIZE/3, Image.SCALE_DEFAULT);
		nchit = nchit.getScaledInstance(HEX_SIZE/6, HEX_SIZE/6, Image.SCALE_DEFAULT);
		nchitflash = nchitflash.getScaledInstance(HEX_SIZE/6, HEX_SIZE/6, Image.SCALE_DEFAULT);
		for(int i = 2;i<=9;i+=2)nchit_Settlement.add(getTile(i,toBufferedImage(chitII.getImage())).getScaledInstance(HEX_SIZE/4, HEX_SIZE/4, Image.SCALE_DEFAULT));
		for(int i = 3;i<=9;i+=2)nchit_City.add(getTile(i,toBufferedImage(chitII.getImage())).getScaledInstance(HEX_SIZE/4, HEX_SIZE/4, Image.SCALE_DEFAULT));
		for(int i = 0;i<7;i++)hex.add(getTile(i,hexTiles).getScaledInstance(HEX_SIZE, HEX_SIZE,Image.SCALE_DEFAULT));
		for(int i = 0;i<7;i++)icons.add(getTile(i,iconImage).getScaledInstance(HEX_SIZE/2, HEX_SIZE/2,Image.SCALE_DEFAULT));
		for(int i = 0;i<7;i++)iconsQ.add(getTile(i,iconImage).getScaledInstance(HEX_SIZE/4, HEX_SIZE/4,Image.SCALE_DEFAULT));
		for(int i = 0;i<6;i++)dice.add(getTile(i,diceImage).getScaledInstance(HEX_SIZE/2, HEX_SIZE/2,Image.SCALE_DEFAULT));
		for(int i = 0;i<9;i++)dev.add(getTile(i,devII.getImage()).getScaledInstance(HEX_SIZE, HEX_SIZE,Image.SCALE_DEFAULT));
		for(int i = 0;i<4;i++)build.add(getTile(i,buildII.getImage()).getScaledInstance(HEX_SIZE/4, HEX_SIZE/4,Image.SCALE_DEFAULT));
		try {
			chFont= Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
	}
	//From Java Game Engine	
	public BufferedImage toBufferedImage(Image img)	{
	    if (img instanceof BufferedImage)return (BufferedImage) img;
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
	    return bimage;
	}	
	public BufferedImage getTile(int x,Image img) {
		BufferedImage ts = toBufferedImage(img);
		return ts.getSubimage(x*200,0,200,200);/*Why did you do this? :skull_emoji:*/
	}
	public BufferedImage imgRotate(Image hex, int rad) {
		BufferedImage image = toBufferedImage(hex);
		double rotationRequired = Math.toRadians (90);
		double locationX = image.getWidth() / 2;
		double locationY = image.getHeight() / 2;
		AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		return op.filter(image, null);
	}
	public void updateBuild(Player p) {
		ArrayList<Image>bUpdate=new ArrayList<Image>();
		bUpdate.clear();
		build.clear();
		for(int i = 0;i<4;i++)build.add(getTile(i,buildII.getImage()).getScaledInstance(HEX_SIZE/4, HEX_SIZE/4,Image.SCALE_DEFAULT));
		for(Image b : build) {
			BufferedImage bi = toBufferedImage(b);
			for(int x = 0; x<bi.getWidth();x++)for(int y=0;y<bi.getHeight();y++) {
				if(bi.getRGB(x, y)==Color.decode("#000000").getRGB())bi.setRGB(x, y, p.secondaryColor().getRGB());
				if(bi.getRGB(x, y)==Color.decode("#ffffff").getRGB())bi.setRGB(x, y, p.primaryColor().getRGB());
			}
			bUpdate.add(bi);
		}
		build=bUpdate;
	}
	public Image flash() {
		/*
			 Neutral replace for bcaaa4 & 8c7b75
		*/
		BufferedImage bi = getTile(1,toBufferedImage(chitII.getImage()));
		for(int x = 0; x<bi.getWidth();x++)for(int y=0;y<bi.getHeight();y++) {
			if(bi.getRGB(x, y)==Color.decode("#bcaaa4").getRGB())bi.setRGB(x, y, Color.decode("#8c7b75").getRGB());
			else if(bi.getRGB(x, y)==Color.decode("#8c7b75").getRGB())bi.setRGB(x, y, Color.decode("#bcaaa4").getRGB());
		}
		return bi;
	}
}
