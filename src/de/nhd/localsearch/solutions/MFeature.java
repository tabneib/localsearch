package de.nhd.localsearch.solutions;

import de.nhd.localsearch.problem.geometry.MRectangle;

public abstract class MFeature {

	protected MRectangle rect;

	public MFeature(MRectangle rect) {
		super();
		this.rect = rect;
	}
	
	public MRectangle getRect() {
		return rect;
	}
}
