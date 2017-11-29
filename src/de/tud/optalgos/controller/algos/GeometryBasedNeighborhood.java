iewpackage de.tud.optalgos.controller.algos;
import java.util.Random;
import de.tud.optalgos.model.MBox;
import de.tud.optalgos.model.MInstance;
import de.tud.optalgos.model.MRectangle;
import de.tud.optalgos.model.MSolution;
import de.tud.optalgos.model.Solution;

public class GeometryBasedNeighborhood extends Neighborhood{

	public static final int ATTEMPTS =1000;
	
	private MSolution nextSolution;
	private boolean findNext;
	
	public GeometryBasedNeighborhood(MInstance instance, MSolution currentSolution) {
		super(instance, currentSolution);
		System.out.println("init GeometryBasedNeighborhood");
		this.nextSolution = null;
		this.findNext = false;
	}

	@Override
	public boolean hasNext() {
		//findNext process was executed
		System.out.println("init hasNext");
		this.findNext = true;
		
		this.nextSolution = this.getCurrentSolution().clone();
		//attempt to find next solution
		int attempt = 0;
		while(attempt < ATTEMPTS) {
			if(this.attempt()) {
				return true;
			}
			attempt++;
		}
		
		//if no solution was found
		return false;
	}
	
	
	public boolean attempt() {
		System.out.println("attempt to find next solution");
		Random r = new Random();
		MInstance instance = (MInstance) this.getInstance();
		if(instance.getBoxes().size()<2) {
			return false;
		}
		
		//pick two random boxes
		int sourceBoxIndex = r.nextInt(instance.getBoxes().size()-1);
		int destinationBoxIndex = -1;
		do {
			destinationBoxIndex = r.nextInt(instance.getBoxes().size()-1);
			}while(sourceBoxIndex == destinationBoxIndex);
		 
		//pick random rectangle
		System.out.println("take element from box" + sourceBoxIndex);
		MBox sourceBox = instance.getBoxes().get(sourceBoxIndex); 
		int sourceBoxInclude = sourceBox.getMRectangles().size(); 
		MRectangle m = null;
		if(sourceBoxInclude > 0) {
			int item = 0;
			if(sourceBoxInclude > 1) {
				item = r.nextInt(sourceBoxInclude-1);
			}
			
			int i = 0;
			for(MRectangle temp : sourceBox.getMRectangles())
			{
			    if (i == item)
			        m = temp;
			    i++;
			}
		
		}else {
			return false;
		}
		
		//insert this rectangle into new box
		MBox destinationBox = instance.getBoxes().get(destinationBoxIndex);
		System.out.println("take element into box" + destinationBoxIndex);
		if(destinationBox.insert(m)) {
			sourceBox.getMRectangles().remove(m);
		}else {
			return false;
		}
		
		return true;
	}

	@Override
	public Solution next() {
		if(this.findNext) {
			this.findNext = false;
			return nextSolution;
		}else {
			if(this.hasNext()) {
				this.findNext = false;
				return nextSolution;
			}else {
				return null;
			}
		}
	}
}
