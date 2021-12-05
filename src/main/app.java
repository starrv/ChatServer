package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class app implements KeyListener
{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//ChatServer server=new ChatServer(Integer.parseInt(args[0]));
		new ChatServer(80);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e.getKeyCode());
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e.getKeyCode());
	}
}
