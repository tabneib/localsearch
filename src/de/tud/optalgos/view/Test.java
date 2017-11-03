package de.tud.optalgos.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashSet;

import javax.swing.JFrame;

import de.tud.optalgos.model.MBox;
import de.tud.optalgos.model.MRectangle;

public class Test extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final int BOX_LENGTH = 500;
	private MBox mBox;
	private HashSet<MRectangle> mRectangles;
	
	public Test(MBox mBox, HashSet<MRectangle> mRectangles) {
		
		this.mBox = mBox;
		this.mRectangles = mRectangles;
		
		setTitle("Test GUI");
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.draw(mBox);
		
		for (MRectangle r : mRectangles) {
			g2.setColor(Color.CYAN);
			g2.fill(r);
			g2.setColor(Color.BLACK);	
			g2.draw(r);
		}
	}
	
	public static void main(String[] args) {
		
		MRectangle r1 = new MRectangle(0, 0, 250, 250, BOX_LENGTH);
		MRectangle r2 = new MRectangle(250, 0, 50, 50, BOX_LENGTH);
		MRectangle r3 = new MRectangle(0, 250, 100, 100, BOX_LENGTH);
		
		HashSet<MRectangle> rectangles = new HashSet<MRectangle>();

		MBox box = new MBox(BOX_LENGTH,rectangles);
		rectangles.add(r1);
		rectangles.add(r2);
		rectangles.add(r3);
		
		new Test(box, rectangles);
		
	}
}
