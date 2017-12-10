package de.tud.optalgos.controller.neighborhood;

import java.util.Random;

import de.tud.optalgos.model.MOptProblem;
import de.tud.optalgos.model.MSolution;
import de.tud.optalgos.model.OptProblem;
import de.tud.optalgos.model.RuleBasedSolution;
import de.tud.optalgos.model.Solution;

public class RuleBasedNeighborhood extends Neighborhood {

	public RuleBasedNeighborhood(OptProblem instance, Solution currentSolution) {
		super(instance, currentSolution);
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Solution next() {
		RuleBasedSolution newSolution = ((RuleBasedSolution)this.currentSolution).clone(); 
		int range = ((MOptProblem)this.getInstance()).getRechtangles().size()-1;
		Random r = new Random();
		int pos1 = r.nextInt(range);
		int pos2;
		do {
			pos2 = r.nextInt(range);
		} while (pos1 == pos2);
		newSolution.permutate(pos1, pos2);
		newSolution.autoInsert();
		return newSolution;
	}

	@Override
	public void onCurrentSolutionChange(Solution newSolution) {
		this.currentSolution = (MSolution) newSolution;
	}

}
