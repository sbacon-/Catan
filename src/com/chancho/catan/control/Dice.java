package com.chancho.catan.control;
import java.util.Random;

public class Dice {
	public int show=7, a=4, b=3,timer=1000;	
	public boolean distr = true;
	public void roll() {
		Random r = new Random();
		a = r.nextInt((6 - 1) + 1) + 1;
		b = r.nextInt((6 - 1) + 1) + 1;
		show = a+b;
		timer = 0;
		distr=false;
	}
	public void tick() {
		timer++;
	}
}
