package com.chancho.catan.control;

public class Button {
	public String label;
	public int x,y,width,height,ratio;
	public boolean pressed = false, show = true;
	public Button(String label,int x,int y,int width, int height) {
		this.label = label;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.show = true;
		this.ratio = 10;
	}public Button(String label,int x,int y,int width, int height,boolean show) {
		this.label = label;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.show = show;
		this.ratio = 6;
	}
	public boolean hover(int mx,int my) {
		return (mx>x && mx<x+width)&&(my>y && my<y+height);
	}
}
