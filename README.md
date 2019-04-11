# Local Search Optimization Algorithms
Implementation of some classic local search optimization algorithms solving the optimal rectangle packing problem:
* *Classical local search*
* *Simulated annealing*
* *Taboo search*

The algorithms are visualized for an randomly-generated set of rectangles of any given
size. User can choose between running the whole algorithm at once with a given delay between the 
intermediate steps and running one single step of the algorithm. Changes between two adjacent steps are
annotated accordingly.

###### The classical local search algorithm packing 40 rectangles. Rectangles are moved from black-colored boxes to yellow-colored ones. The being-moved rectangles are annoted with red color:
<img src="https://raw.githubusercontent.com/tabneib/localsearch/master/images/localsearch.gif" 
data-canonical-src="https://raw.githubusercontent.com/tabneib/localsearch/master/images/localsearch.gif" width="600"/>

###### The simulated annealing algorithm packing 40 rectangles. Red background indicates the algorithm is temporally stepping to a worse packing solution:
<img src="https://raw.githubusercontent.com/tabneib/localsearch/master/images/simulatedannealing.gif" 
data-canonical-src="https://raw.githubusercontent.com/tabneib/localsearch/master/images/simulatedannealing.gif" width="600"/>

###### The classical local search algorithm packing 526 rectangles into 31 boxes:
<img src="https://raw.githubusercontent.com/tabneib/localsearch/master/images/localsearch-526.gif" 
data-canonical-src="https://raw.githubusercontent.com/tabneib/localsearch/master/images/localsearch-526.gif" width="600"/>
