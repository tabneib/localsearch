package de.tud.optalgos.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import de.tud.optalgos.controller.algos.GeometryBasedNeighborhood;
import de.tud.optalgos.controller.algos.LocalSearch;
import de.tud.optalgos.controller.algos.Neighborhood;
import de.tud.optalgos.controller.algos.NeighborhoodBased;
import de.tud.optalgos.model.InstanceFactory;
import de.tud.optalgos.model.MBox;
import de.tud.optalgos.model.MInstance;
import de.tud.optalgos.model.MRectangle;
import de.tud.optalgos.model.MSolution;
import de.tud.optalgos.model.OptProblem;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	// Default parameters for instance generation
	public static final int DEFAULT_AMOUNT = 1000;
	public static final int DEFAULT_MIN_LENGTH = 10;
	public static final int DEFAULT_MAX_LENGTH = 150;
	public static final int DEFAULT_BOX_LENGTH = 150;
	public static final int DEFAULT_INIT_LENGTH = 500;

	// GUI Constants
	public static final int WINDOW_HEIGHT = 650;
	public static final int BOXES_CONTAINER_WIDTH = 800;
	public static final int MENU_CONTAINER_WIDTH = 380;
	public static final int BOXES_PADDING = 10;
	public static final int SCROLL_VIEW_PADDING = 20;
	public static final int DUMMY_PADDING = 20; // hack

	// Other Constants
	private static final String GEN_RANDOM = "Random-Instance-Generator";
	private static final String GEN_SPLIT = "Split-Instance-Generator";
	private static final String NEIGHBORHOOD_GEO = "Geometry-Based-Neighborhood";
	private static final String NEIGHBORHOOD_PERM = "Permutation-Neighborhood";
	private static final String NEIGHBORHOOD_OVERL = "Overlapping-Neighborhood";
	private static final String ALGO_LOCAL = "Local-Search-Algorithm";
	private static final String ALGO_SIM = "Simulated-Annealing-Algorithm";
	private static final String ALGO_TABOO = "Taboo-Search-Algorithm";

	// Data
	private MInstance mInstance;
	private MSolution mSolution;
	private ArrayList<MBox> boxes;

	// Options
	private String generator = GEN_RANDOM;
	private int amount = DEFAULT_AMOUNT;
	private int minLength = DEFAULT_MIN_LENGTH;
	private int maxLength = DEFAULT_MAX_LENGTH;
	private int boxLength = DEFAULT_BOX_LENGTH;
	private int initLength = DEFAULT_INIT_LENGTH;
	private String neighborhood = NEIGHBORHOOD_GEO;
	private String algorithm = ALGO_LOCAL;

	// GUI Components
	private JLabel labelStatusBar;
	private JFrame frame;
	private JLabel labelInsGen;
	private JRadioButton radioRandGen;
	private JRadioButton radioSplitGen;
	private JTextField textFieldArgs;
	private JButton buttonInsGen;
	private JLabel labelParams;
	private JLabel labelNeighbor;
	private JLabel labelAlgo;
	private JRadioButton radioNeighborGeo;
	private JRadioButton radioNeighborPerm;
	private JRadioButton radioNeighborOverl;
	private JRadioButton radioAlgoLocal;
	private JRadioButton radioAlgoSim;
	private JRadioButton radioAlgoTaboo;
	private JButton buttonRun;

	private final Font defaultFont = new JLabel().getFont();

	public static void main(String[] args) {

		new GUI();
	}

	public GUI() {

		// Init data
		makeInstance();

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
		frame.setMinimumSize(new Dimension(BOXES_CONTAINER_WIDTH + SCROLL_VIEW_PADDING
				+ MENU_CONTAINER_WIDTH + DUMMY_PADDING, WINDOW_HEIGHT));
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

		ArrayList<MBoxPanel> boxPanels = new ArrayList<>();

		if (mSolution == null)
			boxes = mInstance.getBoxes();
		else
			boxes = mSolution.getBoxes();

		int gridX = BOXES_CONTAINER_WIDTH / (boxLength + 2 * BOXES_PADDING);
		int gridY = boxes.size() / gridX + (boxes.size() % gridX == 0 ? 0 : 1);

		JPanel boxesContainer = new JPanel();
		boxesContainer.setPreferredSize(new Dimension(BOXES_CONTAINER_WIDTH,
				gridY * (boxLength + 2 * BOXES_PADDING)));

		boxesContainer.setLayout(new GridLayout(0, gridX, BOXES_PADDING, BOXES_PADDING));

		// Fill the grids with boxes
		MBoxPanel boxPanel;
		for (int i = 0; i < boxes.size(); i++) {
			boxPanel = new MBoxPanel(boxes.get(i));
			boxPanel.setPreferredSize(new Dimension(boxes.get(i).getBoxLength(),
					boxes.get(i).getBoxLength()));
			boxesContainer.add(boxPanel);
			boxPanels.add(boxPanel);
		}

		// The whole grid panel is contained inside a scroll pane
		JScrollPane scrollPane = new JScrollPane(boxesContainer);
		scrollPane.setPreferredSize(new Dimension(
				BOXES_CONTAINER_WIDTH + SCROLL_VIEW_PADDING, WINDOW_HEIGHT));

		// Update the status bar
		if (mSolution == null)
			labelStatusBar.setText("  #Box: " + boxes.size() + "   #Rect: "
					+ mInstance.getRechtangles().size() + "   boxLength: " + boxLength);
		else
			labelStatusBar.setText("  #Box: " + boxes.size() + "   #Rect: "
					+ mSolution.getRechtangles().size() + "   boxLength: " + boxLength);

		return scrollPane;
	}

	/**
	 * Create a panel that displays the menu
	 * 
	 * @return
	 */
	private Container makeMenuContainer() {
		JPanel panel = new JPanel();

		panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.ipady = 15;
		c.anchor = GridBagConstraints.NORTHWEST;

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		// Dummy label to keep the "width" beast at bay
		JLabel dummy = new JLabel(" ");
		dummy.setPreferredSize(new Dimension(MENU_CONTAINER_WIDTH, 1));
		panel.add(dummy, c);

		// Instance generators

		labelInsGen = new JLabel("Instance Generator");
		labelInsGen.setFont(new Font(defaultFont.getFontName(), Font.BOLD,
				defaultFont.getSize() + 2));
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

		// Arguments

		textFieldArgs = new JTextField(
				amount + " " + minLength + " " + maxLength + " " + boxLength);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		textFieldArgs.setPreferredSize(new Dimension(300, 30));
		c.fill = GridBagConstraints.VERTICAL;
		panel.add(textFieldArgs, c);
		c.fill = GridBagConstraints.NONE;

		buttonInsGen = new JButton("Generate");
		c.gridx = 2;
		c.gridy = 3;
		c.gridwidth = 1;
		buttonInsGen.setMaximumSize(new Dimension(100, 30));
		panel.add(buttonInsGen, c);

		labelParams = new JLabel("<amount> <minLen> <maxLen> <boxLen>");
		labelParams.setFont(new Font("arial", Font.PLAIN, 11));
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		panel.add(labelParams, c);

		// Neighborhoods

		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setPreferredSize(
				new Dimension(MENU_CONTAINER_WIDTH + DUMMY_PADDING, 1));
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 3;
		panel.add(separator, c);

		labelNeighbor = new JLabel("Neighborhood");
		labelNeighbor.setFont(new Font(defaultFont.getFontName(), Font.BOLD,
				defaultFont.getSize() + 2));
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 3;
		panel.add(labelNeighbor, c);

		radioNeighborGeo = new JRadioButton("Geometry based");
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 1;
		panel.add(radioNeighborGeo, c);

		radioNeighborPerm = new JRadioButton("Permutation");
		c.gridx = 1;
		c.gridy = 7;
		c.gridwidth = 1;
		panel.add(radioNeighborPerm, c);

		radioNeighborOverl = new JRadioButton("Overlap");
		c.gridx = 2;
		c.gridy = 7;
		c.gridwidth = 1;
		panel.add(radioNeighborOverl, c);

		ButtonGroup groupNeighbor = new ButtonGroup();
		groupNeighbor.add(radioNeighborGeo);
		groupNeighbor.add(radioNeighborPerm);
		groupNeighbor.add(radioNeighborOverl);
		radioNeighborGeo.setSelected(true);

		// Algos

		labelAlgo = new JLabel("Algorithm");
		labelAlgo.setFont(new Font(defaultFont.getFontName(), Font.BOLD,
				defaultFont.getSize() + 2));
		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 3;
		panel.add(labelAlgo, c);

		radioAlgoLocal = new JRadioButton("Local Search");
		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 1;
		panel.add(radioAlgoLocal, c);

		radioAlgoSim = new JRadioButton("Simulated Annealing");
		c.gridx = 1;
		c.gridy = 9;
		c.gridwidth = 1;
		panel.add(radioAlgoSim, c);

		radioAlgoTaboo = new JRadioButton("Taboo");
		c.gridx = 2;
		c.gridy = 9;
		c.gridwidth = 1;
		panel.add(radioAlgoTaboo, c);

		ButtonGroup groupAlgo = new ButtonGroup();
		groupAlgo.add(radioAlgoLocal);
		groupAlgo.add(radioAlgoSim);
		groupAlgo.add(radioAlgoTaboo);
		radioAlgoLocal.setSelected(true);

		buttonRun = new JButton("Run");
		c.gridx = 2;
		c.gridy = 10;
		c.gridwidth = 1;
		buttonRun.setPreferredSize(new Dimension(100, 30));
		panel.add(buttonRun, c);

		// TODO: remove this when all features are implemented
		radioNeighborOverl.setEnabled(false);
		radioNeighborPerm.setEnabled(false);
		radioAlgoSim.setEnabled(false);
		radioAlgoTaboo.setEnabled(false);
		
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
		radioNeighborGeo.addActionListener(new NeighborSelectListener());
		radioNeighborPerm.addActionListener(new NeighborSelectListener());
		radioNeighborOverl.addActionListener(new NeighborSelectListener());
		radioAlgoLocal.addActionListener(new AlgoSelectListener());
		radioAlgoSim.addActionListener(new AlgoSelectListener());
		radioAlgoTaboo.addActionListener(new AlgoSelectListener());

		buttonInsGen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Parse arguments
				String[] argStrs = textFieldArgs.getText().split(" ");

				try {
					switch (argStrs.length) {
					case 4:
						if (generator.equals(GEN_SPLIT))
							throw new Exception();
						amount = Integer.parseInt(argStrs[0]);
						minLength = Integer.parseInt(argStrs[1]);
						maxLength = Integer.parseInt(argStrs[2]);
						boxLength = Integer.parseInt(argStrs[3]);
						if (amount <= 1 || minLength < 1 || minLength > maxLength
								|| maxLength > boxLength
								|| boxLength >= BOXES_CONTAINER_WIDTH - 2 * BOXES_PADDING)
							throw new Exception();
						break;
					case 3:
						if (generator.equals(GEN_RANDOM))
							throw new Exception();
						initLength = Integer.parseInt(argStrs[0]);
						boxLength = Integer.parseInt(argStrs[1]);
						minLength = Integer.parseInt(argStrs[2]);
						if (minLength < 1 || minLength > boxLength
								|| initLength < 2 * minLength
								|| boxLength >= BOXES_CONTAINER_WIDTH - 2 * BOXES_PADDING)
							throw new Exception();
						break;
					default:
						throw new Exception();
					}

					javax.swing.SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							makeInstance();
							Component bContainer = frame.getContentPane().getComponent(0);
							GridBagLayout l = (GridBagLayout) frame.getContentPane()
									.getLayout();
							GridBagConstraints c = l.getConstraints(bContainer);

							frame.getContentPane().remove(bContainer);
							frame.getContentPane().add(makeBoxesContainer(), c, 0);
							frame.getContentPane().validate();
						}
					});

				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, "Invalid arguments!", "Error",
							JOptionPane.INFORMATION_MESSAGE);
				}

			}
		});

		buttonRun.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				OptProblem optProblem = new OptProblem(mInstance,
						OptProblem.Direction.MAXIMALMIZING);
				MSolution startSolution = new MSolution(optProblem,
						mInstance.getClonedBoxes());
				Neighborhood neighborhut;
				NeighborhoodBased algorhythm;

				// Setup neighborhood
				switch (neighborhood) {
				case NEIGHBORHOOD_GEO:
					neighborhut = new GeometryBasedNeighborhood(mInstance,
							startSolution.clone());
					break;
				case NEIGHBORHOOD_PERM:
				case NEIGHBORHOOD_OVERL:
					// TODO

				default:
					throw new RuntimeException("Unknown neighborhood");
				}

				// Setup algorithm
				switch (algorithm) {
				case ALGO_LOCAL:
					algorhythm = new LocalSearch(optProblem, neighborhut, startSolution.clone());
					break;
				case ALGO_SIM:
				case ALGO_TABOO:
					// TODO
				default:
					throw new RuntimeException("Unknown algorithm");
				}

				// Run and display result
				algorhythm.run();
				mSolution = (MSolution) algorhythm.getOptimum();

				javax.swing.SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						Component bContainer = frame.getContentPane().getComponent(0);
						GridBagLayout l = (GridBagLayout) frame.getContentPane()
								.getLayout();
						GridBagConstraints c = l.getConstraints(bContainer);

						frame.getContentPane().remove(bContainer);
						frame.getContentPane().add(makeBoxesContainer(), c, 0);
						labelStatusBar.setText(labelStatusBar.getText() + 
								"   Running time: " + algorhythm.getRunningTime()/1000 + "s");
						frame.getContentPane().validate();
					}
				});

			}
		});
	}

	/**
	 * Generate a new instance and replace the current solution of the old
	 * instance with the obvious solution for the new instance.
	 */
	private void makeInstance() {

		// Reset the solution
		this.mSolution = null;

		switch (this.generator) {
		case GEN_RANDOM:
			mInstance = InstanceFactory.getInstanceRandom(amount, minLength, maxLength,
					boxLength);
			break;
		case GEN_SPLIT:
			mInstance = InstanceFactory.getInstanceSplit(initLength, boxLength,
					minLength);
			break;
		}
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
				generator = GEN_RANDOM;
				textFieldArgs.setText(
						amount + " " + minLength + " " + maxLength + " " + boxLength);
				labelParams.setText("<amount> <minLen> <maxLen> <boxLen>");
			} else if (radioSplitGen.isSelected()) {
				generator = GEN_SPLIT;
				textFieldArgs.setText(initLength + " " + boxLength + " " + minLength);
				labelParams.setText("<initLen> <boxLen> <minLen>");
			}
		}
	}

	/**
	 * Listener for selection of neighborhood
	 *
	 */
	private class NeighborSelectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (radioNeighborGeo.isSelected())
				neighborhood = NEIGHBORHOOD_GEO;
			else if (radioNeighborPerm.isSelected())
				neighborhood = NEIGHBORHOOD_PERM;
			else if (radioNeighborOverl.isSelected())
				neighborhood = NEIGHBORHOOD_OVERL;
		}
	}

	/**
	 * Listener for selection of algorithm
	 *
	 */
	private class AlgoSelectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (radioAlgoLocal.isSelected())
				algorithm = ALGO_LOCAL;
			else if (radioAlgoSim.isSelected())
				algorithm = ALGO_SIM;
			else if (radioAlgoTaboo.isSelected())
				algorithm = ALGO_TABOO;
		}
	}
}
