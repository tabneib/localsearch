package de.nhd.localsearch.solutions;

import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;

public class GeometryBasedFeature extends MFeature {

	private MBox residentialBox;

	public GeometryBasedFeature(MRectangle rect, MBox residentialBox) {
		super(rect);
		this.residentialBox = residentialBox;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MFeature other = (MFeature) obj;
		if (this.residentialBox.equals(((GeometryBasedFeature) other).getBox())
				&& this.rect.equals(other.getRect()))
			return true;
		else{
			return false;
		}
	}

	public MBox getBox() {
		return residentialBox;
	}

	@Override
	public String toString() {
		return "MFeature(rect: " + this.rect.getId() + "; box: "
				+ this.residentialBox.getId() + ")";
	}
}
