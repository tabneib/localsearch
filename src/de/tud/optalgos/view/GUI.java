package de.tud.optalgos.view;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

import de.tud.optalgos.model.InstanceFactory;
import de.tud.optalgos.model.MBox;
import de.tud.optalgos.model.MInstance;
import de.tud.optalgos.model.MRectangle;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	// Default parameters for instance generation
	public static final int AMOUNT = 20;
	public static final int MIN_LENGTH = 10;
	public static final int MAX_LENGTH = 150;
	public static final int BOX_LENGTH = 150;

	// GUI Constants
	public static final int WINDOW_HEIGHT = 650;
	public static final int BOXES_CONTAINER_WIDTH = 900;
	public static final int MENU_CONTAINER_WIDTH = 200;
	public static final int BOXES_PADDING = 10;
	public static final int SCROLL_VIEW_PADDING = 20;

	public static void main(String[] args) {

		// Job for the event-dispatching thread
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}

	/**
	 * 
	 */
	public static void initGUI() {
		JFrame frame = new JFrame("OptAlgos GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setSize(BOXES_CONTAINER_WIDTH + MENU_CONTAINER_WIDTH, WINDOW_HEIGHT);

		addComponentsToPane(frame.getContentPane());

		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Add components to the main window
	 * 
	 * @param pane
	 */
	private static void addComponentsToPane(Container pane) {

		pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;

		// The container of all the boxes
		c.weightx = 1.0;
		c.gridx = 0;
		pane.add(createBoxesContainer(), c);

		// The container of the menu
		c.weightx = 0.5;
		c.gridx = 1;
		pane.add(createMenuContainer(InstanceFactory.
				getInstanceRandom(AMOUNT, MIN_LENGTH, MAX_LENGTH, BOX_LENGTH)), c);

	}

	/**
	 * Create a panel that display all the boxes
	 * 
	 * @return
	 */
	private static Container createMenuContainer(MInstance mInstance) {

		ArrayList<MBox> boxes = mInstance.getBoxes();

		JPanel panel = new JPanel();
		int gridX = BOXES_CONTAINER_WIDTH / (BOX_LENGTH + 2 * BOXES_PADDING);
		int gridY = boxes.size()/gridX + (boxes.size() % gridX == 0 ? 0 : 1);

		panel.setPreferredSize(new Dimension(BOXES_CONTAINER_WIDTH, 
					gridY * (BOX_LENGTH + 2 * BOXES_PADDING)));
		
		panel.setLayout(new GridLayout(0, gridX, BOXES_PADDING, BOXES_PADDING));

		// Fill the grids with boxes
		MBoxPanel boxPanel;
		for (int i = 0; i < boxes.size(); i++) {
			boxPanel= new MBoxPanel(boxes.get(i));
			boxPanel.setPreferredSize(new 
					Dimension(boxes.get(i).getBoxLength(), boxes.get(i).getBoxLength()));
			panel.add(boxPanel);
		}

		// The whole grid panel is contained inside a scroll pane
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setPreferredSize(new Dimension(
				BOXES_CONTAINER_WIDTH + SCROLL_VIEW_PADDING, WINDOW_HEIGHT));

		return scrollPane;
	}

	/**
	 * 
	 * @return
	 */
	private static Container createBoxesContainer() {
		JPanel panel = new JPanel();
		panel.setSize(MENU_CONTAINER_WIDTH, WINDOW_HEIGHT);

		// TODO : generate the menu

		return panel;
	}

	
	/**
	 * 
	 *
	 */
	private static class MBoxPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private final MBox mBox;

		private MBoxPanel(MBox mBox) {
			this.mBox = mBox;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.draw(mBox);	
			
			for (MRectangle r : mBox.getMRectangles()) {
				g2.setColor(Color.CYAN);
				g2.fill(r);
				g2.setColor(Color.BLACK);	
				g2.draw(r);
			}
		}
	}
}
