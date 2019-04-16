package de.nhd.localsearch.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

import de.nhd.localsearch.algorithms.LocalSearch;
import de.nhd.localsearch.algorithms.NeighborhoodBasedAlgo;
import de.nhd.localsearch.algorithms.SimulatedAnnealing;
import de.nhd.localsearch.algorithms.TabooSearch;
import de.nhd.localsearch.problem.MInstanceFactory;
import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;
import de.nhd.localsearch.solutions.MSolution;

/**
 * GUI starter
 *
 */
public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	// Default parameters for instance generation
	public static final int DEFAULT_AMOUNT = 130;
	public static final int DEFAULT_MIN_LENGTH = 2;
	public static final int DEFAULT_MAX_LENGTH = 20;
	public static final int DEFAULT_BOX_LENGTH = 40;
	public static final int DEFAULT_INIT_LENGTH = 90;
//	public static final int DEFAULT_AMOUNT = 6;
//	public static final int DEFAULT_MIN_LENGTH = 10;
//	public static final int DEFAULT_MAX_LENGTH = 60;
//	public static final int DEFAULT_BOX_LENGTH = 200;
//	public static final int DEFAULT_INIT_LENGTH = 90;

	// GUI States
	private static final String STATE_INIT = "STATE_INIT";
	private static final String STATE_RUNNING = "STATE_RUNNING";
	private static final String STATE_PAUSED = "STATE_PAUSED";
	private static final String STATE_FINISHED = "STATE_FINISHED";

	// GUI Constants
	public static final int WINDOW_HEIGHT = 650;
	public static final int BOXES_CONTAINER_WIDTH = 800;
	public static final int MENU_CONTAINER_WIDTH = 380;
	public static final int BOXES_PADDING = 10;
	public static final int SCROLL_VIEW_PADDING = 20;
	public static final int DUMMY_PADDING = 20; // hack

	public static final Color COLOR_REPOSITIONING_SOURCE = Color.black;
	public static final Color COLOR_REPOSITIONING_DESTINATION = Color.yellow;
	public static final Color COLOR_RECT = new Color(47, 255, 228);
	public static final Color COLOR_TABOO_RECT = new Color(57, 185, 211);
	public static final Color COLOR_REPOSITIONED_RECT = Color.red;
	public static final Color COLOR_REMOVED_RECT = Color.lightGray;
	public static final Color COLOR_WORSE_THAN_PREVIOUS_SOLUTION = new Color(247, 185,
			185);
	public static final Color COLOR_TERMINAL_BG = Color.black;
	public static final Color COLOR_TERMINAL_FONT = Color.white;

	// Other Constants
	private static final int DEFAULT_DELAY = 50;
	private static final String GEN_RANDOM = "RANDOM";
	private static final String GEN_SPLIT = "SPLIT";
	private static final String ALGO_LOCAL_SEARCH = "LOCAL_SEARCH";
	private static final String ALGO_SIM_ANNEALING = "SIM_ANNEALING";
	private static final String ALGO_TABOO = "TABOO_SEARCH";

	// Options
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
	private JTextField textFieldArgs;
	private JButton buttonInsGen;
	private JLabel labelParams;
	private JLabel labelNeighbor;
	private JLabel labelAlgo;
	private JRadioButton radioNeighborGeo;
	private JRadioButton radioNeighborPerm;
	private JCheckBox checkboxNeighborOverl;
	private JCheckBox checkboxShowWorse;
	private JRadioButton radioAlgoLocal;
	private JRadioButton radioAlgoSim;
	private JRadioButton radioAlgoTaboo;
	private JLabel labelDelay;
	private JTextField textFieldDelay;
	private JButton buttonAnimateRun;
	private JButton buttonRunStep;
	private JButton buttonRun;
	private JButton buttonReset;
	JScrollPane terminalScrollPane;
	private JTextArea textAreaTerminal;

	private ButtonGroup groupNeighbor;
	private ButtonGroup groupAlgo;

	private final Font defaultFont = new JLabel().getFont();

	private String generatorName = GEN_RANDOM;
	private String neighborhood = NeighborhoodBasedAlgo.NEIGHBORHOOD_GEO;
	private boolean overlap = false;
	private String algorithmName = ALGO_LOCAL_SEARCH;
	private boolean showWorseSolutions = true;
	private NeighborhoodBasedAlgo algorithm;
	private MOptProblem problem;
	private MSolution solution;

	private Timer timer = new Timer();
	private TimerTask algoRunTask = null;

	private String state = STATE_INIT;

	public static void main(String[] args) {
		new GUI();
	}

	public GUI() {
		// Initialize logic stuff
		genInstance();
		this.algorithm = new LocalSearch(problem, this.neighborhood);
		this.solution = (MSolution) this.algorithm.getCurrentSolution();
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
		frame = new JFrame("Local Search");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(BOXES_CONTAINER_WIDTH + SCROLL_VIEW_PADDING
				+ MENU_CONTAINER_WIDTH + DUMMY_PADDING, WINDOW_HEIGHT));
		addComponentsToPane(frame.getContentPane());

		frame.pack();
		frame.setVisible(true);
		this.updateState(this.state);
		this.terminalOut("[+] Generator: " + this.generatorName);
		this.terminalOut("[+] Neighborhoos: " + this.neighborhood);
		this.terminalOut("[+] Algorithm: " + this.algorithmName);
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
		JScrollPane scrollPane = new JScrollPane(makeBoxesContainer());
		scrollPane.setPreferredSize(new Dimension(
				BOXES_CONTAINER_WIDTH + SCROLL_VIEW_PADDING, WINDOW_HEIGHT));
		pane.add(scrollPane, c);

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
		ArrayList<MBox> boxes;

		boxes = solution.getBoxes();

		int gridX = BOXES_CONTAINER_WIDTH / (boxLength + 2 * BOXES_PADDING);
		int gridY = boxes.size() / gridX + (boxes.size() % gridX == 0 ? 0 : 1);

		JPanel boxesContainer = new JPanel();
		if (this.solution.isWorseThanPrevious())
			boxesContainer.setBackground(COLOR_WORSE_THAN_PREVIOUS_SOLUTION);
		boxesContainer.setPreferredSize(new Dimension(BOXES_CONTAINER_WIDTH,
				gridY * (boxLength + 2 * BOXES_PADDING)));

		boxesContainer.setLayout(new GridLayout(0, gridX, BOXES_PADDING, BOXES_PADDING));

		// Fill the grids with boxes
		MBoxPanel boxPanel;
		for (int i = 0; i < boxes.size(); i++) {
			boxPanel = new MBoxPanel(boxes.get(i));
			if (this.solution.isWorseThanPrevious())
				boxPanel.setBackground(COLOR_WORSE_THAN_PREVIOUS_SOLUTION);
			boxPanel.setPreferredSize(new Dimension(boxes.get(i).getBoxLength(),
					boxes.get(i).getBoxLength()));
			boxesContainer.add(boxPanel);
			boxPanels.add(boxPanel);
		}

		// Update the status bar
		if (solution == null)
			labelStatusBar.setText("  #Box: " + boxes.size() + "   #Rect: "
					+ problem.getRechtangles().size() + "   boxLength: " + boxLength);
		else
			labelStatusBar.setText("  #Box: " + boxes.size() + "   #Rect: "
					+ solution.getRectangles().size() + "   boxLength: " + boxLength);

		return boxesContainer;
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
		separator
				.setPreferredSize(new Dimension(MENU_CONTAINER_WIDTH + DUMMY_PADDING, 1));
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

		checkboxNeighborOverl = new JCheckBox("Overlap");
		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 1;
		panel.add(checkboxNeighborOverl, c);

		this.groupNeighbor = new ButtonGroup();
		this.groupNeighbor.add(radioNeighborGeo);
		this.groupNeighbor.add(radioNeighborPerm);
		// groupNeighbor.add(radioNeighborOverl);
		radioNeighborGeo.setSelected(true);

		// Algos

		labelAlgo = new JLabel("Algorithm");
		labelAlgo.setFont(new Font(defaultFont.getFontName(), Font.BOLD,
				defaultFont.getSize() + 2));
		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 3;
		panel.add(labelAlgo, c);

		radioAlgoLocal = new JRadioButton("Local Search");
		c.gridx = 0;
		c.gridy = 10;
		c.gridwidth = 1;
		panel.add(radioAlgoLocal, c);

		radioAlgoSim = new JRadioButton("Simulated Annealing");
		c.gridx = 1;
		c.gridy = 10;
		c.gridwidth = 1;
		panel.add(radioAlgoSim, c);

		radioAlgoTaboo = new JRadioButton("Taboo");
		c.gridx = 2;
		c.gridy = 10;
		c.gridwidth = 1;
		panel.add(radioAlgoTaboo, c);

		this.groupAlgo = new ButtonGroup();
		this.groupAlgo.add(radioAlgoLocal);
		this.groupAlgo.add(radioAlgoSim);
		this.groupAlgo.add(radioAlgoTaboo);
		radioAlgoLocal.setSelected(true);

		checkboxShowWorse = new JCheckBox("Show worse solutions");
		c.gridx = 0;
		c.gridy = 11;
		c.gridwidth = 1;
		panel.add(checkboxShowWorse, c);
		checkboxShowWorse.setSelected(this.showWorseSolutions);

		buttonAnimateRun = new JButton("Animate");
		c.gridx = 0;
		c.gridy = 12;
		c.gridwidth = 1;
		buttonAnimateRun.setPreferredSize(new Dimension(130, 20));
		panel.add(buttonAnimateRun, c);

		labelDelay = new JLabel("Delay:   ", JLabel.RIGHT);
		labelDelay.setFont(new Font(defaultFont.getFontName(), Font.BOLD,
				defaultFont.getSize() + 2));
		labelDelay.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		c.gridx = 1;
		c.gridy = 12;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(labelDelay, c);
		c.fill = GridBagConstraints.NONE;

		textFieldDelay = new JTextField("" + DEFAULT_DELAY);
		c.gridx = 2;
		c.gridy = 12;
		c.gridwidth = 1;
		textFieldDelay.setPreferredSize(new Dimension(100, 20));
		panel.add(textFieldDelay, c);

		buttonRunStep = new JButton("1 Step");
		c.gridx = 0;
		c.gridy = 13;
		c.gridwidth = 1;
		buttonRunStep.setPreferredSize(new Dimension(100, 20));
		panel.add(buttonRunStep, c);

		buttonRun = new JButton("Run");
		c.gridx = 1;
		c.gridy = 13;
		c.gridwidth = 1;
		buttonRun.setPreferredSize(new Dimension(100, 20));
		panel.add(buttonRun, c);

		buttonReset = new JButton("Reset");
		c.gridx = 2;
		c.gridy = 13;
		c.gridwidth = 1;
		buttonReset.setPreferredSize(new Dimension(100, 20));
		panel.add(buttonReset, c);

		// The container of all the boxes
		c.gridx = 0;
		c.gridy = 14;
		c.gridwidth = 3;
		textAreaTerminal = new JTextArea();
		textAreaTerminal.setEditable(false);
		textAreaTerminal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		textAreaTerminal.setForeground(COLOR_TERMINAL_FONT);
		textAreaTerminal.setBackground(COLOR_TERMINAL_BG);
		terminalScrollPane = new JScrollPane(textAreaTerminal);
		terminalScrollPane
				.setPreferredSize(new Dimension(MENU_CONTAINER_WIDTH + 75, 160));
		terminalScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 5, 5, 5));
		panel.add(terminalScrollPane, c);

		setListeners();
		return panel;
	}

	/**
	 * Setup listeners for the GUI components
	 */
	private void setListeners() {

		// Radio buttons
		radioRandGen.addActionListener(new GeneratorSelectListener());
		radioSplitGen.addActionListener(new GeneratorSelectListener());
		radioNeighborGeo.addActionListener(new NeighborSelectListener());
		radioNeighborPerm.addActionListener(new NeighborSelectListener());
		checkboxNeighborOverl.addActionListener(new OverlapSelectListener());
		radioAlgoLocal.addActionListener(new AlgoSelectListener());
		radioAlgoSim.addActionListener(new AlgoSelectListener());
		radioAlgoTaboo.addActionListener(new AlgoSelectListener());
		checkboxShowWorse.addActionListener(new ShowWorseSelectListener());
		buttonInsGen.addActionListener(new InsGenerateListener());
		textAreaTerminal.addMouseListener(new TerminalRightClickListener());

		// Run the whole algorithm without delay between the steps
		buttonRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				terminalOut("[+] Running...");
				algorithm.run();
				solution = (MSolution) algorithm.getCurrentSolution();
				repaintBoxes(
						"   Running time: " + algorithm.getRunningTime() / 1000 + "s");
				updateState(STATE_FINISHED);
				reportDone();
			}
		});

		// Run 1 step of the algorithm
		buttonRunStep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!runStep())
					updateState(STATE_PAUSED);
				if (algorithmName.equals(ALGO_LOCAL_SEARCH)
						|| !solution.isWorseThanPrevious() || showWorseSolutions) {
					repaintBoxes(null);
					reportCurrentSolution();
					if (algorithm.isFinished())
						reportDone();
				}
			}
		});

		// Run the whole algorithm stepwise with given delay
		buttonAnimateRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if (state.equals(STATE_RUNNING)) {
							removeAlgoRunTask();
							updateState(STATE_PAUSED);
						} else {
							algoRunTask = genAlgoRunTask();

							try {
								final int delay = Integer
										.parseInt(textFieldDelay.getText());
								timer.schedule(algoRunTask, 0, delay);
								updateState(STATE_RUNNING);
							} catch (Exception e) {
								JOptionPane.showMessageDialog(null, "Invalid delay",
										"Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				});
			}
		});

		buttonReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						revalidateAlgo();
					}
				});
			}
		});
	}

	/**
	 * Generate a new instance and replace the current solution of the old
	 * instance with the obvious solution for the new instance.
	 */
	private void genInstance() {
		switch (this.generatorName) {
			case GEN_RANDOM :
				problem = MInstanceFactory.getInstanceRandom(amount, minLength, maxLength,
						boxLength);
				break;
			case GEN_SPLIT :
				problem = MInstanceFactory.getInstanceSplit(initLength, boxLength, -1);
				break;
		}
		this.terminalOut("[+] Problem instance generated");
	}

	/**
	 * Re-generate the algorithm instance. This must be called after the
	 * algorithm, neighborhood or problem is changed.
	 */
	private void revalidateAlgo() {
		switch (this.algorithmName) {
			case ALGO_LOCAL_SEARCH :
				this.algorithm = new LocalSearch(problem, this.neighborhood);
				break;
			case ALGO_SIM_ANNEALING :
				algorithm = new SimulatedAnnealing(problem, neighborhood);
				break;
			case ALGO_TABOO :
				algorithm = new TabooSearch(problem, neighborhood);
				break;
			default :
				throw new RuntimeException("Invalid algorithm name: " + algorithmName);
		}
		this.solution = (MSolution) this.algorithm.getCurrentSolution();
		repaintBoxes(null);
		updateState(STATE_INIT);
	}

	/**
	 * Repaint the boxes
	 * 
	 * @param statusMsg
	 *            The message to append to the status bar
	 */
	private void repaintBoxes(String statusMsg) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JScrollPane boxesPane = (JScrollPane) frame.getContentPane()
						.getComponent(0);
				boxesPane.setViewportView(makeBoxesContainer());

				if (statusMsg != null)
					labelStatusBar.setText(labelStatusBar.getText() + statusMsg);
			}
		});
	}

	/**
	 * Print to GUI terminal
	 * 
	 * @param message
	 */
	private void terminalOut(String message) {
		try {
			textAreaTerminal.append(message + "\n");
			terminalScrollPane.getVerticalScrollBar()
					.setValue(textAreaTerminal.getHeight());
			terminalScrollPane.validate();
		} catch (NullPointerException e) {
		}
	}

	private void reportDone() {
		terminalOut("[+] Done");
		terminalOut("[+] Final solution:");
		terminalOut("          #boxes:    " + solution.getBoxes().size());
		terminalOut("          objective: " + String.format("%.10f", solution.getObjective()));
	}

	private void reportCurrentSolution() {
		terminalOut("[+] Solution " + this.solution.getIndex() + ":");
		terminalOut("          #boxes:    " + solution.getBoxes().size());
		terminalOut("          objective: " + String.format("%.10f", solution.getObjective()));
	}
	/**
	 * Run one single step of the current chosen algorithm
	 */
	private boolean runStep() {
		if (!algorithm.isFinished()) {
			algorithm.runStep();
			this.solution = (MSolution) algorithm.getCurrentSolution();
		}
		if (algorithm.isFinished()) {
			removeAlgoRunTask();
			updateState(STATE_FINISHED);
			return true;
		} else
			return false;
	}

	/**
	 * Generate the asynchronous task to run the algorithm periodically
	 * 
	 * @return
	 */
	private TimerTask genAlgoRunTask() {
		return new TimerTask() {

			@Override
			public void run() {
				runStep();
				if (algorithmName.equals(ALGO_LOCAL_SEARCH)
						|| !solution.isWorseThanPrevious() || showWorseSolutions) {
					repaintBoxes(null);
					reportCurrentSolution();

				}
				if (algorithm.isFinished())
					reportDone();
			}
		};
	}

	/**
	 * Cancel and remove any asynchronous algorithm running task
	 */
	private void removeAlgoRunTask() {
		if (algoRunTask != null) {
			algoRunTask.cancel();
			algoRunTask = null;
		}
	}

	/**
	 * Update the GUI to the given state. The state transition is validated
	 * first.
	 * 
	 * @param newState
	 */
	private void updateState(String newState) {
		switch (newState) {
			case STATE_INIT :
				if (this.state.equals(STATE_RUNNING))
					throw new RuntimeException(
							"Invalid state change: " + this.state + " -> " + newState);
				this.state = newState;
				labelInsGen.setEnabled(true);
				radioRandGen.setEnabled(true);
				radioSplitGen.setEnabled(true);
				textFieldArgs.setEnabled(true);
				buttonInsGen.setEnabled(true);
				labelParams.setEnabled(true);
				labelNeighbor.setEnabled(true);
				radioNeighborGeo.setEnabled(true);
				radioNeighborPerm.setEnabled(true);
				checkboxNeighborOverl.setEnabled(true);
				labelAlgo.setEnabled(true);
				radioAlgoLocal.setEnabled(true);
				radioAlgoSim.setEnabled(true);
				radioAlgoTaboo.setEnabled(true);
				checkboxShowWorse.setEnabled(this.algorithmName.equals(ALGO_SIM_ANNEALING)
						|| this.algorithmName.equals(ALGO_TABOO));

				buttonRun.setEnabled(true);
				buttonReset.setEnabled(false);
				buttonRunStep.setEnabled(true);
				buttonAnimateRun.setEnabled(true);
				labelDelay.setEnabled(true);
				textFieldDelay.setEnabled(true);

				buttonAnimateRun.setText("Animate Run");
				break;
			case STATE_RUNNING :
				if (this.state.equals(STATE_FINISHED))
					throw new RuntimeException(
							"Invalid state change: " + this.state + " -> " + newState);
				this.state = newState;
				labelInsGen.setEnabled(false);
				radioRandGen.setEnabled(false);
				radioSplitGen.setEnabled(false);
				textFieldArgs.setEnabled(false);
				buttonInsGen.setEnabled(false);
				labelParams.setEnabled(false);
				labelNeighbor.setEnabled(false);
				radioNeighborGeo.setEnabled(false);
				radioNeighborPerm.setEnabled(false);
				checkboxNeighborOverl.setEnabled(false);
				labelAlgo.setEnabled(false);
				radioAlgoLocal.setEnabled(false);
				radioAlgoSim.setEnabled(false);
				radioAlgoTaboo.setEnabled(false);
				checkboxShowWorse.setEnabled(false);

				buttonRun.setEnabled(false);
				buttonReset.setEnabled(false);
				buttonRunStep.setEnabled(false);
				buttonAnimateRun.setEnabled(true);
				labelDelay.setEnabled(false);
				textFieldDelay.setEnabled(false);

				buttonAnimateRun.setText("Pause");
				break;
			case STATE_PAUSED :
				if (this.state.equals(STATE_FINISHED))
					throw new RuntimeException(
							"Invalid state change: " + this.state + " -> " + newState);
				this.state = newState;
				labelInsGen.setEnabled(false);
				radioRandGen.setEnabled(false);
				radioSplitGen.setEnabled(false);
				textFieldArgs.setEnabled(false);
				buttonInsGen.setEnabled(false);
				labelParams.setEnabled(false);
				labelNeighbor.setEnabled(false);
				radioNeighborGeo.setEnabled(false);
				radioNeighborPerm.setEnabled(false);
				checkboxNeighborOverl.setEnabled(false);
				labelAlgo.setEnabled(false);
				radioAlgoLocal.setEnabled(false);
				radioAlgoSim.setEnabled(false);
				radioAlgoTaboo.setEnabled(false);
				checkboxShowWorse.setEnabled(false);

				buttonRun.setEnabled(true);
				buttonReset.setEnabled(true);
				buttonRunStep.setEnabled(true);
				buttonAnimateRun.setEnabled(true);
				labelDelay.setEnabled(true);
				textFieldDelay.setEnabled(true);

				buttonAnimateRun.setText("Animate Run");
				break;
			case STATE_FINISHED :
				this.state = newState;
				labelInsGen.setEnabled(false);
				radioRandGen.setEnabled(false);
				radioSplitGen.setEnabled(false);
				textFieldArgs.setEnabled(false);
				buttonInsGen.setEnabled(false);
				labelParams.setEnabled(false);
				labelNeighbor.setEnabled(false);
				radioNeighborGeo.setEnabled(false);
				radioNeighborPerm.setEnabled(false);
				checkboxNeighborOverl.setEnabled(false);
				labelAlgo.setEnabled(false);
				radioAlgoLocal.setEnabled(false);
				radioAlgoSim.setEnabled(false);
				radioAlgoTaboo.setEnabled(false);
				checkboxShowWorse.setEnabled(false);

				buttonRun.setEnabled(false);
				buttonReset.setEnabled(true);
				buttonRunStep.setEnabled(false);
				buttonAnimateRun.setEnabled(false);
				labelDelay.setEnabled(false);
				textFieldDelay.setEnabled(false);

				buttonAnimateRun.setText("Animate Run");
				break;
		}
	}

	/**
	 * Class that represents a panel that displays a single box
	 *
	 */
	private class MBoxPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		private final MBox box;

		private MBoxPanel(MBox box) {
			this.box = box;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(this.box.isRepositionDest()
					? COLOR_REPOSITIONING_DESTINATION
					: this.box.isRepositionSrc()
							? COLOR_REPOSITIONING_SOURCE
							: new Color(227, 227, 227));
			g2.fill(box);
			g2.setColor(Color.BLACK);
			g2.draw(box);

			for (MRectangle r : box.getMRectangles()) {
				g2.setColor(r.isRepositioned()
						? COLOR_REPOSITIONED_RECT
						: solution.checkTaboo(r)
						? COLOR_TABOO_RECT
						: COLOR_RECT);
				g2.fill(r);
				g2.setColor(Color.BLACK);
				g2.draw(r);
			}

			if (this.box.isRepositionSrc()) {
				MRectangle removedRect = this.box.getRemovedRect();
				g2.setColor(COLOR_REMOVED_RECT);
				g2.fill(removedRect);
				g2.setColor(Color.BLACK);
				g2.draw(removedRect);
			}
		}
	}

	private class GeneratorSelectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (radioRandGen.isSelected()) {
				generatorName = GEN_RANDOM;
				textFieldArgs.setText(
						amount + " " + minLength + " " + maxLength + " " + boxLength);
				labelParams.setText("<amount> <minLen> <maxLen> <boxLen>");
			} else if (radioSplitGen.isSelected()) {
				generatorName = GEN_SPLIT;
				textFieldArgs.setText(initLength + " " + boxLength);
				labelParams.setText("<initLen> <boxLen>");
			} else
				throw new RuntimeException("No generator type selected");
			updateState(STATE_INIT);
			terminalOut("[+] Generator: " + generatorName);
		}
	}

	private class InsGenerateListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// Parse arguments
			String[] argStrs = textFieldArgs.getText().trim().split("\\s+");
			textFieldArgs.setText(String.join(" ", argStrs));

			try {
				switch (argStrs.length) {
					case 4 :
						if (generatorName.equals(GEN_SPLIT))
							throw new Exception(
									"Invalid number of arguments for random generator");
						amount = Integer.parseInt(argStrs[0]);
						minLength = Integer.parseInt(argStrs[1]);
						maxLength = Integer.parseInt(argStrs[2]);
						boxLength = Integer.parseInt(argStrs[3]);
						if (amount <= 1 || minLength < 1 || minLength > maxLength
								|| maxLength > boxLength
								|| boxLength >= BOXES_CONTAINER_WIDTH - 2 * BOXES_PADDING)
							throw new RuntimeException("Invalis arguments");
						break;
					case 2 :
						if (generatorName.equals(GEN_RANDOM))
							throw new Exception(
									"Invalid number of arguments for split generator");
						initLength = Integer.parseInt(argStrs[0]);
						boxLength = Integer.parseInt(argStrs[1]);
						/*
						 * minLength = Integer.parseInt(argStrs[2]); if
						 * (minLength < 1 || minLength > boxLength || initLength
						 * < 2 * minLength || boxLength >= BOXES_CONTAINER_WIDTH
						 * - 2 * BOXES_PADDING)
						 */
						if (boxLength >= BOXES_CONTAINER_WIDTH - 2 * BOXES_PADDING)
							throw new Exception("Invalis arguments");
						break;
					default :
						throw new RuntimeException("Invalid number of arguments");
				}
				// Generating instance might throw exceptions if the given
				// arguments are still invalid
				genInstance();
				revalidateAlgo();
				updateState(STATE_INIT);

			} catch (Exception exception) {
				System.err.println(exception);
				JOptionPane.showMessageDialog(null, "Invalid arguments!", "Error",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private class NeighborSelectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (radioNeighborGeo.isSelected()) {
				neighborhood = NeighborhoodBasedAlgo.NEIGHBORHOOD_GEO;
				revalidateAlgo();
			} else if (radioNeighborPerm.isSelected()) {
				neighborhood = NeighborhoodBasedAlgo.NEIGHBORHOOD_PERM;
				revalidateAlgo();
			}
			// else if (checkboxNeighborOverl.isSelected())
			// neighborhood = NEIGHBORHOOD_OVERL;
			else
				throw new RuntimeException("No neighborhood is selected");
			updateState(STATE_INIT);
			terminalOut("[+] Neighborhoos: " + neighborhood);
		}
	}

	private class OverlapSelectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			if (checkboxNeighborOverl.isSelected()) {
				MRectangle.setOverlapMode(true);
				terminalOut("[+] Overlapping rectangles: ON");
			} else {
				MRectangle.setOverlapMode(false);
				terminalOut("[+] Overlapping rectangles: OFF");
			}
		}
	}

	private class ShowWorseSelectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			if (checkboxShowWorse.isSelected()) {
				showWorseSolutions = true;
				terminalOut("[+] Show worse solutions: ON");
			} else {
				showWorseSolutions = false;
				terminalOut("[+] Show worse solutions: OFF");
			}
		}
	}

	private class AlgoSelectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (radioAlgoLocal.isSelected()) {
				algorithmName = ALGO_LOCAL_SEARCH;
			} else if (radioAlgoSim.isSelected()) {
				algorithmName = ALGO_SIM_ANNEALING;
			} else if (radioAlgoTaboo.isSelected()) {
				algorithmName = ALGO_TABOO;
			} else
				throw new RuntimeException("No algorithm selected");
			revalidateAlgo();
			terminalOut("[+] Algorithm: " + algorithmName);
		}
	}

	class TerminalRightClickListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		private void doPop(MouseEvent e) {
			TerminalPopupMenu menu = new TerminalPopupMenu();
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	private class TerminalPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		JMenuItem clearItem;
		public TerminalPopupMenu() {
			clearItem = new JMenuItem("Clear");
			add(clearItem);
			clearItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					textAreaTerminal.setText("");
				}
			});
		}
	}
}
