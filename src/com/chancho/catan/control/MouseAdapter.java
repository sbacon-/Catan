package com.chancho.catan.control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseAdapter implements MouseListener, MouseMotionListener, KeyListener{
	
		public int mx = -1, my=-1;
		public boolean click=false,shift=false,enter=false;
		public Chat c;
		public MouseAdapter(Chat c) {
			this.c = c;
		}
		
		//MOUSE
		@Override
		public void mouseClicked(MouseEvent m) {
			click = true;
			mx=m.getX();
			my=m.getY();
		}

		@Override
		public void mouseEntered(MouseEvent m) {
		}

		@Override
		public void mouseExited(MouseEvent m) {
		}

		@Override
		public void mousePressed(MouseEvent m) {
		}

		@Override
		public void mouseReleased(MouseEvent m) {
		}

		//MOUSE MOTION
		@Override
		public void mouseMoved(MouseEvent m) {
			mx=m.getX();
			my=m.getY();
		}
		@Override
		public void mouseDragged(MouseEvent m) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getExtendedKeyCode();
			System.out.println(key);
			if(key==KeyEvent.VK_SHIFT)shift=true;
			if(c.compose=="Compose...")c.compose="";
			switch(key) {
			case 10:
				enter=true;
				break;
			case 8:
				c.compose=c.compose.substring(0,c.compose.length()-2);
				break;
			default:
				if(c.compose=="Compose...")c.compose="";
				c.compose+=shift?String.valueOf((char)key).toUpperCase():String.valueOf((char)key).toLowerCase() ;
				break;
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();
			if(key==KeyEvent.VK_SHIFT)shift=false;
		}

		@Override
		public void keyTyped(KeyEvent e) {
			
		}		
}
