package de.tud.optalgos.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import de.tud.optalgos.controller.algos.GeometryBasedNeighborhood;
import de.tud.optalgos.controller.algos.LocalSearch;
import de.tud.optalgos.model.InstanceFactory;
import de.tud.optalgos.model.MBox;
import de.tud.optalgos.model.MInstance;
import de.tud.optalgos.model.MRectangle;
import de.tud.optalgos.model.MSolution;
import de.tud.optalgos.model.OptProblem;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	// Default parameters for instance generation
	public static final int DEFAULT_AMOUNT = 200;
	public static final int DEFAULT_MIN_LENGTH = 10;
	public static final int DEFAULT_MAX_LENGTH = 150;
	public static final int DEFAULT_BOX_LENGTH = 150;
	public static final int DEFAULT_INIT_LENGTH = 500;
	
	
	// GUI Constants
	public static final int WINDOW_HEIGHT = 650;
	public static final int BOXES_CONTAINER_WIDTH = 900;
	public static final int MENU_CONTAINER_WIDTH = 380;
	public static final int BOXES_PADDING = 10;
	public static final int SCROLL_VIEW_PADDING = 20;
	
	// Other Constants
	private static final String RANDOM_GEN = "Random-Instance-Generator";
	private static final String SPLIT_GEN = "Split-Instance-Generator";
	
	// Data
	private MInstance mInstance;
	private ArrayList<MBox> boxes;
	
	// Options
	private String generator = RANDOM_GEN;
	private int amount = DEFAULT_AMOUNT;
	private int minLength = DEFAULT_MIN_LENGTH;
	private int maxLength = DEFAULT_MAX_LENGTH;
	private int boxLength = DEFAULT_BOX_LENGTH;
	private int initLength = DEFAULT_INIT_LENGTH;

	
	// GUI Components
	private JLabel labelStatusBar;
	private JFrame frame;
	private JLabel labelInsGen;
	private JRadioButton radioRandGen;
	private JRadioButton radioSplitGen;
	private JTextField textFieldParams;
	private JButton buttonInsGen;
	private JLabel labelParams;

	private final Font defaultFont = new JLabel().getFont();
	
	public static void main(String[] args) {
		new GUI();
	}
	
	public GUI() {
		// Job for the event-dispatching thread
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGUI();
			}
		});
	}

	/**
	 * create GUI window and call method to add stuff onto it
	 */
	public void initGUI() {
		frame = new JFrame("OptAlgos GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(
				BOXES_CONTAINER_WIDTH + SCROLL_VIEW_PADDING + MENU_CONTAINER_WIDTH,
				WINDOW_HEIGHT));
		addComponentsToPane(frame.getContentPane());

		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Add components to the GUI window
	 * 
	 * @param pane
	 */
	private void addComponentsToPane(Container pane) {
 
		// setup status bar
		labelStatusBar = new JLabel("");
		labelStatusBar.setFont(new Font("arial", Font.PLAIN, defaultFont.getSize()));
		
		pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;

		// The container of all the boxes
		c.weightx = 1.0;
		c.gridx = 0;
		pane.add(makeBoxesContainer(), c);

		// The container of the menu
		c.weightx = 0.5;
		c.gridx = 1;
		pane.add(makeMenuContainer(), c);
		
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 2;
		c.ipady = 10;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		pane.add(labelStatusBar, c);
	}

	/**
	 * Create a panel that displays all the boxes
	 * 
	 * @return
	 */
	private Container makeBoxesContainer() {
		
		// Retrieve data
		switch (this.generator) {
			case RANDOM_GEN: 
				mInstance = InstanceFactory.
					getInstanceRandom(amount, minLength, maxLength, boxLength);
				break;
			case SPLIT_GEN:
				mInstance = InstanceFactory.
					getInstanceSplit(initLength, boxLength, minLength);
				break;
		}

		ArrayList<MBoxPanel> boxPanels = new ArrayList<>();
		//TODO
		OptProblem optProblem = new OptProblem(this.mInstance, OptProblem.Direction.MAXIMALMIZING);
		MSolution startSolution = new MSolution(optProblem,this.mInstance.getClonedBoxes());
		GeometryBasedNeighborhood neighborhood = new GeometryBasedNeighborhood(this.mInstance, startSolution.clone());
		LocalSearch localSearch = new LocalSearch(optProblem, neighborhood, startSolution.clone()); 
		System.out.println("tryout");
		localSearch.run();
		//boxes = mInstance.getBoxes();
		MSolution finalSolution = ((MSolution) localSearch.getOptimum());
		boxes = finalSolution.getBoxes();
		System.out.println("The final solution has "+boxes.size()+" boxes");
		int gridX = BOXES_CONTAINER_WIDTH / (boxLength + 2 * BOXES_PADDING);
		int gridY = boxes.size()/gridX + (boxes.size() % gridX == 0 ? 0 : 1);

		JPanel boxesContainer = new JPanel();
		boxesContainer.setPreferredSize(new Dimension(BOXES_CONTAINER_WIDTH, 
					gridY * (boxLength + 2 * BOXES_PADDING)));
		
		boxesContainer.setLayout(new GridLayout(0, gridX, BOXES_PADDING, BOXES_PADDING));

		// Fill the grids with boxes
		MBoxPanel boxPanel;
		for (int i = 0; i < boxes.size(); i++) {
			boxPanel= new MBoxPanel(boxes.get(i));
			boxPanel.setPreferredSize(new 
					Dimension(boxes.get(i).getBoxLength(), boxes.get(i).getBoxLength()));
			boxesContainer.add(boxPanel);
			boxPanels.add(boxPanel);
		}

		// The whole grid panel is contained inside a scroll pane
		JScrollPane scrollPane = new JScrollPane(boxesContainer);
		scrollPane.setPreferredSize(new Dimension(
				BOXES_CONTAINER_WIDTH + SCROLL_VIEW_PADDING, WINDOW_HEIGHT));

		// Update the status bar
		labelStatusBar.setText("  #Box: " + boxes.size() + 
				"   #Rect: " + mInstance.getRechtangles().size() +
				"   boxLength: " + boxLength);
		
		return scrollPane;
	}

	/**
	 * Create a panel that displays the menu
	 * @return
	 */
	private Container makeMenuContainer() {
		JPanel panel = new JPanel();

		panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.ipady = 10;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		// Dummy label to keep the "width" beast at bay
		JLabel dummy = new JLabel(" ");
		dummy.setPreferredSize(new Dimension(MENU_CONTAINER_WIDTH, 1));
		panel.add(dummy, c);
		
		// Instance generation
		
		labelInsGen = new JLabel("Instance Generator");
		labelInsGen.setFont(
				new Font(defaultFont.getFontName(), Font.BOLD, defaultFont.getSize() + 2));
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		panel.add(labelInsGen, c);

		radioRandGen = new JRadioButton("Interval");
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		panel.add(radioRandGen, c);
		
		radioSplitGen = new JRadioButton("Split");
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		panel.add(radioSplitGen, c);
		
		
		ButtonGroup groupGen = new ButtonGroup();
		groupGen.add(radioSplitGen);
		groupGen.add(radioRandGen);
		radioRandGen.setSelected(true);
		
		textFieldParams = new JTextField(
				amount + " " + minLength + " " + maxLength + " " + boxLength);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		textFieldParams.setPreferredSize(new Dimension(260,30));
		c.fill = GridBagConstraints.VERTICAL;
		panel.add(textFieldParams, c);
		c.fill = GridBagConstraints.NONE;
		
		buttonInsGen = new JButton("Generate");
		c.gridx = 2;
		c.gridy = 3;
		c.gridwidth = 1;
		buttonInsGen.setMaximumSize(new Dimension(100,30));
		panel.add(buttonInsGen, c);
		
		labelParams = new JLabel("<amount> <minLen> <maxLen> <boxLen>");
		labelParams.setFont(new Font("arial", Font.PLAIN, 11));
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		panel.add(labelParams, c);

		setListeners();
		return panel;
	}

	
	/**
	 * Setup listeners for the GUI components
	 */
	private void setListeners() {
		
		// Radio buttons
		radioRandGen.addActionListener(new GenSelectListener());
		radioSplitGen.addActionListener(new GenSelectListener());
		buttonInsGen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// Parse arguments
				String[] argStrs = textFieldParams.getText().split(" ");
				
				try {
					switch (argStrs.length) {
						case 4:
							if (generator.equals(SPLIT_GEN))
								throw new Exception();
							amount = Integer.parseInt(argStrs[0]);
							minLength = Integer.parseInt(argStrs[1]);
							maxLength = Integer.parseInt(argStrs[2]);
							boxLength = Integer.parseInt(argStrs[3]);
							if (amount <= 1 || minLength < 1 ||
									minLength > maxLength || maxLength > boxLength ||
									boxLength >= BOXES_CONTAINER_WIDTH - 2*BOXES_PADDING)
								throw new Exception();
							break;
						case 3:
							if (generator.equals(RANDOM_GEN))
								throw new Exception();
							initLength = Integer.parseInt(argStrs[0]);
							boxLength = Integer.parseInt(argStrs[1]);
							minLength = Integer.parseInt(argStrs[2]);
							if (minLength < 1 || minLength > boxLength ||
									initLength < 2 * minLength ||
									boxLength >= BOXES_CONTAINER_WIDTH - 2*BOXES_PADDING)
								throw new Exception();
							break;
						default:
							throw new Exception();
					}
					
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							Component bContainer = frame.getContentPane().getComponent(0);
							GridBagLayout l = (GridBagLayout) 
									frame.getContentPane().getLayout();
							GridBagConstraints c = l.getConstraints(bContainer);
							
							frame.getContentPane().remove(bContainer);
							frame.getContentPane().add(makeBoxesContainer(), c, 0);
							frame.getContentPane().validate();
						}
					});
					
				}
				catch (Exception exception) {
					JOptionPane.showMessageDialog(
							null, "Invalid arguments!", "Error",
							JOptionPane.INFORMATION_MESSAGE);
				}
				
			}
		});
	}
	
	
	/**
	 * Class that represents a panel that displays a single box
	 *
	 */
	private class MBoxPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private final MBox mBox;

		private MBoxPanel(MBox mBox) {
			this.mBox = mBox;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(227, 227, 227));
			g2.fill(mBox);
			g2.setColor(Color.BLACK);
			g2.draw(mBox);	
			
			for (MRectangle r : mBox.getMRectangles()) {
				g2.setColor(new Color(47, 255, 228));
				g2.fill(r);
				g2.setColor(Color.BLACK);	
				g2.draw(r);
			}
		}
	}
	
	/**
	 * Listener for selection of instance generator 
	 *
	 */
	private class GenSelectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (radioRandGen.isSelected()) {
				generator = RANDOM_GEN;
				textFieldParams.setText(
						amount + " " + minLength + " " + maxLength + " " + boxLength);
				labelParams.setText("<amount> <minLen> <maxLen> <boxLen>");
			}
			else if (radioSplitGen.isSelected()) {
				generator = SPLIT_GEN;
				textFieldParams.setText(
						initLength + " " + boxLength + " " + minLength);
				labelParams.setText("<initLen> <boxLen> <minLen>");
			}
		}
	}
	
}
