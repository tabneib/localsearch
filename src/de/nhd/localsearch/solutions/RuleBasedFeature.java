package de.nhd.localsearch.solutions;

import de.nhd.localsearch.problem.geometry.MRectangle;

public class RuleBasedFeature extends MFeature {

	private int index;

	public RuleBasedFeature(MRectangle rect, int index) {
		super(rect);
		this.index= index;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MFeature other = (MFeature) obj;
		if (this.index == ((RuleBasedFeature) other).getIndex()
				&& this.rect.equals(other.getRect()))
			return true;
		else{
			return false;
		}
	}

	public int getIndex() {
		return this.index;
	}

	@Override
	public String toString() {
		return "MFeature(rect: " + this.rect.getId() + "; index: "
				+ this.getIndex() + ")";
	}
}
