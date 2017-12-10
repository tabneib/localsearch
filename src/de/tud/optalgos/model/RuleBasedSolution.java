package de.tud.optalgos.model;

import java.util.ArrayList;

import de.tud.optalgos.model.geometry.MBox;
import de.tud.optalgos.model.geometry.MRectangle;

public class RuleBasedSolution extends MSolution {
	private int[] permutation;

	public RuleBasedSolution(OptProblem optProblem,  int[] permutation) {
		super(optProblem, null);
		
		if(permutation == null) {
			this.permutation = new int[((MOptProblem)this.optProblem).getRechtangles().size()];
			for (int i = 0; i < this.permutation.length; i++) {
				this.permutation[i]=i;
			}
		}else {
			this.permutation = permutation;
		}
		
		
		
	}
	public boolean autoInsert() {
		MOptProblem instance = ((MOptProblem)this.getOptProblem());
		ArrayList<MRectangle> rechtangles = instance.getRechtangles();
		int boxLength = instance.getBoxLength();
		if(this.boxes == null) {
			this.boxes = new ArrayList<MBox>();
			for (int i = 0; i < permutation.length; i++) {
				MRectangle m = rechtangles.get(permutation[i]);
				if(!arrange(m)) {
					//add new empty box;
					MBox mbox = new MBox(boxLength);
					mbox.insert(m.clone());
					this.boxes.add(mbox);
				}
			}
			return true;
		}
		return false;
		
	}
	
	private boolean arrange(MRectangle m ) {
		if(this.boxes.size()!=0) {
			for (MBox mBox : this.boxes) {
				if (mBox.insert(m.clone())) {
					return true;
				}
			}
			return false;
		}else {
			return false;
		}
		
	}
	
	public boolean permutate(int x, int y) {
		MOptProblem instance = (MOptProblem)this.getOptProblem();
		if(x == y || 0 > x || 0 > y || x >= instance.getRechtangles().size() || y >= instance.getRechtangles().size()) {
			return false;
		}else {
			int temp = this.permutation[x];
			this.permutation[x] = this.permutation[y];
			this.permutation[y] = temp;
			return true;
		}
		
	}
	
	@Override
	public RuleBasedSolution clone() {
		int[] clonedPermutation = new int[this.permutation.length];
		for (int i = 0; i < this.permutation.length; i++) {
			clonedPermutation[i]=this.permutation[i];
		}
		RuleBasedSolution newSolution = new RuleBasedSolution(this.optProblem, clonedPermutation);
		return newSolution;
	}

}
