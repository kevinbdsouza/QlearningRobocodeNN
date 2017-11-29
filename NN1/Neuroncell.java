
package sample.Kevin.Try2.NN1;

import java.util.*;
 
public class Neuroncell {   
	
	//initialize class variables 
    static int neurCounter = 0;
    final public int neurId;  
    Connect biasConnection;
    final double biasValue = -1;
    double outputValue;
    ArrayList<Connect> connectionsLeft = new ArrayList<Connect>();
    HashMap<Integer,Connect> connectHashMap = new HashMap<Integer,Connect>();
     
    public Neuroncell(){        
        neurId = neurCounter;
        neurCounter++;
    }
     
    // function to add bias connection while setting up 
    public void biasConnectionAdd(Neuroncell n){
        Connect con = new Connect(n,this);
        biasConnection = con;
        connectionsLeft.add(con);
    }
    
    // function to add connections while setting up 
    public void inputConnectionsAdd(ArrayList<Neuroncell> inNeurons){
        for(Neuroncell n: inNeurons){
            Connect con = new Connect(n,this);
            connectionsLeft.add(con);
            connectHashMap.put(n.neurId, con);
        }
    }
    
    // function to get connection to implement backpropogation
    public Connect getConnection(int neuronIndex){
        return connectHashMap.get(neuronIndex);
    }
 
       
    //Compute Sj = Wij*Aij + w0j*bias
    public void findOutputValue(int out){
        double s = 0;
        for(Connect con : connectionsLeft){
            Neuroncell leftNeuron = con.getLeftNeuron();
            double wt = con.getWeightValue();
            double ac = leftNeuron.getOutputValue(); 
             
            s = s + (wt*ac);
        }
        s = s + (biasConnection.getWeightValue()*biasValue);
        
        if (out == 1){
        outputValue = s;	
        }
        else {
        outputValue = sigmoid(s);
        }
    }
    
    // function to implement bipolar sigmoid 
    double bipolarSigmoid(double x) {
    	return (1.0 - (Math.exp(-x)))/ (1.0 +  (Math.exp(-x)));
    }
    
    // function to implement regular sigmoid 
    double sigmoid(double x) {
        return 1.0 / (1.0 +  (Math.exp(-x)));
    }
    
    // get the ouput value from a neuron 
    public double getOutputValue() {
        return outputValue;
    }
    
    // set the output value of a neuron (for inputs) 
    public void setOutputValue(double o){
        outputValue = o;
    }
    
    // get bias value 
    public double getBias() {
        return biasValue;
    }
    
    // get all the connections coming from the previous layer  
    public ArrayList<Connect> inputConnectionsGet(){
        return connectionsLeft;
    }
     
       
}