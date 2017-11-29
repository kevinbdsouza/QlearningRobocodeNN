package sample.Kevin.Try2.NN1;

public class Connect {
	
	// initialize class variables 
    double weightValue = 0;
    double delPrevWeight = 0; 
    double delWeight = 0;
    final Neuroncell LNeuron;
    static int connCounter = 0;
    final public int connId; 
 
    public Connect(Neuroncell fromN, Neuroncell toN) {
        LNeuron = fromN;
        connId = connCounter;
        connCounter++;
    }
  
    // update deltaweight and prevdelta weight during backprop 
    public void updateDeltaWeight(double w) {
        delPrevWeight = delWeight;
        delWeight = w;
    }
 
    // get previous value of delta weight for momentum 
    public double getPrevDeltaWeight() {
        return delPrevWeight;
    }
    
    // get the left side of the connection to find activation
    public Neuroncell getLeftNeuron() {
        return LNeuron;
    }
 
    // update weight
    public void updateWeightValue(double w) {
        weightValue = w;
    }
    
    // get weight to implement backprop and find activation  
    public double getWeightValue() {
        return weightValue;
    }
 

 
}