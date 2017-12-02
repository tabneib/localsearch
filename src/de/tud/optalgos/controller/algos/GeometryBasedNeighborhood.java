package de.tud.optalgos.controller.algos;
import java.util.Random;
import de.tud.optalgos.model.MBox;
import de.tud.optalgos.model.MInstance;
import de.tud.optalgos.model.MRectangle;
import de.tud.optalgos.model.MSolution;
import de.tud.optalgos.model.Solution;

public class GeometryBasedNeighborhood extends Neighborhood{

	public static final int ATTEMPTS =60;
	
	private MSolution nextSolution;
	private boolean findNext;
	
	public GeometryBasedNeighborhood(MInstance instance, MSolution currentSolution) {
		super(instance, currentSolution);
		this.nextSolution = null;
		this.findNext = false;
	}

	@Override
	public boolean hasNext() {
		//findNext process was executed
		this.findNext = true;
		this.nextSolution = null;
		
		
		//attempt to find next solution
		int attempt = 0;
		while(attempt < ATTEMPTS) {
			if(this.attempt()) {
				//new solution was found
				this.findNext = true;
				return true;
			}
			attempt++;
		}
		
		//if no solution was found
		return false;
	}
	
	
	public boolean attempt() {
		
		MSolution newSolution = ((MSolution)this.currentSolution).clone();
		Random r = new Random();
	
		if(newSolution.getBoxes().size()<2) {
			return false;
		}
		
		//pick two random boxes
		int sourceBoxIndex = newSolution.getRandomBoxIndexForEmpty();
		int destinationBoxIndex = newSolution.getRandomBoxIndexForEmpty();
		 
		//pick random rectangle
		MBox sourceBox = newSolution.getBoxes().get(sourceBoxIndex); 
		int sourceBoxSize = sourceBox.getMRectangles().size(); 
		MRectangle m = null;
		if(sourceBoxSize > 0) {
			int item = 0;
			if(sourceBoxSize > 1) {
				item = r.nextInt(sourceBoxSize-1);
			}
			int i = 0;
			for(MRectangle tempRectangle : sourceBox.getMRectangles())
			{
			    if (i == item)
			        m = tempRectangle;
			    i++;
			}
		}else {
			return false;
		}
		
		//insert this rectangle into new box
		MBox destinationBox = newSolution.getBoxes().get(destinationBoxIndex);
		if(destinationBox.insert(m.clone())) {
			sourceBox.getMRectangles().remove(m);
			if(sourceBox.getMRectangles().isEmpty()) {
				newSolution.removeEmptyBox(sourceBoxIndex);
			}else {
				sourceBox.optimalSort();
			}
		}else {
			return false;
		}
		
		this.nextSolution = newSolution;
		return true;
	}

	@Override
	public Solution next() {
		if(this.findNext) {
			//the searching process for new solution was executed
			this.findNext = false;
			

			return this.nextSolution;
		}else {
			if(this.hasNext()) {
				//execute a new searching process for new solution
				this.findNext = false;
				
				return this.nextSolution;
			}else {
				return null;
			}
		}
	}

	@Override
	public void onCurrentSolutionChange(Solution newSolution) {
		this.currentSolution = (MSolution)newSolution;
		this.nextSolution = null;
	}
}
