package sample.Kevin.Try2.NN1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.text.*;
import java.util.*;

import robocode.RobocodeFileOutputStream;

public class NeuralNet{
    
	//Initialize class variables  
    final Random rand = new Random();
    final ArrayList<Neuroncell> inpLayer = new ArrayList<Neuroncell>();
    final ArrayList<Neuroncell> hidLayer = new ArrayList<Neuroncell>();
    final ArrayList<Neuroncell> outLayer = new ArrayList<Neuroncell>();
    final Neuroncell biasNeuron = new Neuroncell();
    final int[] numLayers;
    
    //Initialize Simulation parameters 
    final double weightMult = 1;
    final double maxInit = 2f;
    final double minInit = 0.2f;
    final double learnRate = 0.001f;
    final double moment = 0.9f;
    final boolean isBipolar = false;  // change this to true to implement bipolar 
 
    // XOR training data
    static double[] output;
 
    // Main function 
 public static void main(String[] args) throws IOException {
    	    	
       
 }
 
    //Create all neurons and connections
    public NeuralNet(int input, int hidden, int output, boolean randWeights, String[][] weight) {
        this.numLayers = new int[] { input, hidden, output };
         
        for (int i = 0; i < numLayers.length; i++) {
            if (i == 0) { 
                for (int j = 0; j < numLayers[i]; j++) {
                    Neuroncell neuron = new Neuroncell();
                    inpLayer.add(neuron);
                }
            } else if (i == 1) { 
                for (int j = 0; j < numLayers[i]; j++) {
                    Neuroncell neuron = new Neuroncell();
                    neuron.inputConnectionsAdd(inpLayer);
                    neuron.biasConnectionAdd(biasNeuron);
                    hidLayer.add(neuron);
                }
            } else if (i == 2) { 
                for (int j = 0; j < numLayers[i]; j++) {
                    Neuroncell neuron = new Neuroncell();
                    neuron.inputConnectionsAdd(hidLayer);
                    neuron.biasConnectionAdd(biasNeuron);
                    outLayer.add(neuron);
                }
            } else {
                System.out.println("Error setting up NN, Check inputs again");
            }
        }
 
        if (randWeights) {
        	for (Neuroncell neuron : hidLayer) {
                ArrayList<Connect> connections = neuron.inputConnectionsGet();
                for (Connect conn : connections) {
                    double freshWeight = randomWeightFunc();
                    conn.updateWeightValue(freshWeight);
                }
            }
            for (Neuroncell neuron : outLayer) {
                ArrayList<Connect> connections = neuron.inputConnectionsGet();
                for (Connect conn : connections) {
                    double freshWeight = randomWeightFunc();
                    conn.updateWeightValue(freshWeight);
                }
            }
        }
        
        else { 
        	
            try {
         	   updateAllWeights(weight);
    		   } 
    		   catch (IOException e) {
    			e.printStackTrace();
    		   }
        }
        /*
        for (int k=0;k<weight.length;k++) {
  		  System.out.println(weight[k][0]+" "+weight[k][1]+"    "+weight[k][2]);
  	    }
  	    */
        
        //reinitialize the counters to 0 for next run 
        Neuroncell.neurCounter = 0;
        Connect.connCounter = 0;
        
 
    }
 
    // Set inputs to input layer  
    public void inputSet(double Xtrain[]) {
        for (int i = 0; i < inpLayer.size(); i++) {
            inpLayer.get(i).setOutputValue(Xtrain[i]);
        }
    }
 
    // get outputs from output layer  
    public double[] getOutputValue() {
        double[] outputs = new double[outLayer.size()];
        for (int i = 0; i < outLayer.size(); i++)
            outputs[i] = outLayer.get(i).getOutputValue();
        return outputs;
    }
    
    // random function used to assign initial weights between [-0.5,0.5]
    double randomWeightFunc() {
        return weightMult * (rand.nextDouble() * (maxInit - minInit) - minInit); // 
    }
 
    
    // forward propagation function
    public void forwardRipple() {
        for (Neuroncell n : hidLayer)
            n.findOutputValue(0);
        for (Neuroncell n : outLayer)
            n.findOutputValue(1);
    }
 
 // Error Back-propagation weight update function
 public void errorBackpropagation(double outputExp[]) {
 
        int k = 0;
        for (Neuroncell n : outLayer) {
        	//System.out.println(k);
            ArrayList<Connect> connections = n.inputConnectionsGet();
            for (Connect con : connections) {
                double ak = n.getOutputValue();
                double ai = con.LNeuron.getOutputValue();
                double tempIdealOut = outputExp[k];
                double partialDescent;
                
                partialDescent   = -ai * (tempIdealOut - ak);
                
                double delWeight = -learnRate * partialDescent;
                double freshWeight = con.getWeightValue() + delWeight;
                con.updateDeltaWeight(delWeight);
                con.updateWeightValue(freshWeight + con.getPrevDeltaWeight() * moment);
            }
            k++;
       }
 
        // for the hidden layer
        for (Neuroncell n : hidLayer) {
            ArrayList<Connect> connections = n.inputConnectionsGet();
            for (Connect con : connections) {
                double aj = n.getOutputValue();
                double ai = con.LNeuron.getOutputValue();
                double sumKoutputs = 0;
                int j = 0;
                for (Neuroncell out_neu : outLayer) {
                    double wjk = out_neu.getConnection(n.neurId).getWeightValue(); //new updated weight is used   
                    double tempIdealOut = (double) outputExp[j];
                    double ak = out_neu.getOutputValue();
                    j++;
                    
                    sumKoutputs = sumKoutputs + (-(tempIdealOut - ak) * wjk);
                   
                }
                
                double partialDescent;
               
                partialDescent   = aj * (1 - aj) * ai * sumKoutputs;
                
                double delWeight = -learnRate * partialDescent  ;
                double freshWeight = con.getWeightValue() + delWeight;
                con.updateDeltaWeight(delWeight);
                con.updateWeightValue(freshWeight + moment * con.getPrevDeltaWeight());
            }
        }
    }
 
  // Train NN until minError reached or maxSteps exceeded	
  public static Map<String, Object> start(double [] Xtrain, double [] Ytrain, boolean randWeights, boolean netTrain, String [][] weight) throws IOException {
    	
	  /*
	  for (int k=0;k<weight.length;k++) {
		  System.out.println(weight[k][0]+" "+weight[k][1]+"    "+weight[k][2]);
	  }*/
            NeuralNet nn = new NeuralNet(5, 3, 1,randWeights,weight);
            nn.inputSet(Xtrain);
 
            nn.forwardRipple();
 
            output = nn.getOutputValue();
             
            if (netTrain) {          
            nn.errorBackpropagation(Ytrain);
            }    
            
         
            weight = nn.saveWeights(weight);
            
            Map<String, Object> multiValues = new HashMap<String, Object>();
            multiValues.put("value", output[0]);
            multiValues.put("array", weight);
            
            return multiValues;
            	
 }
  
 // update weights before starting  
  public void updateAllWeights(String [][] weight) throws IOException{
	  /*
	  for (int k=0;k<weight.length;k++) {
		  System.out.println(weight[k][0]+" "+weight[k][1]+"    "+weight[k][2]);
	  }	*/
	  
  int i = 0;	
  for (Neuroncell n : hidLayer) {
      ArrayList<Connect> connections = n.inputConnectionsGet();
      for (Connect con : connections) {
          con.updateWeightValue(Double.valueOf(weight[i][2]).doubleValue());
          i = i+1;
      }
  }
      // update weights for the output layer
      for (Neuroncell n : outLayer) {
          ArrayList<Connect> connections = n.inputConnectionsGet();
          for (Connect con : connections) {
              con.updateWeightValue(Double.valueOf(weight[i][2]).doubleValue());
              i = i+1;
          }
      }
      
 }

// save weights before exit   
public String[][] saveWeights(String [][] weight) {
	  //String [][] weight = new String[3*6 + 3 + 1][3];
	  int j = 0;
      for (Neuroncell n : hidLayer) {
      	ArrayList<Connect> connections = n.inputConnectionsGet();
          for (Connect con : connections) {
          	  weight[j][0] = Double.toString(n.neurId);
              weight[j][1] = Double.toString(con.connId);
              weight[j][2] = Double.toString(con.getWeightValue());
              j = j+1;
          }
      }
      
      for (Neuroncell n : outLayer) {
          ArrayList<Connect> connections = n.inputConnectionsGet();
          for (Connect con : connections) {
              weight[j][0] = Double.toString(n.neurId);
              weight[j][1] = Double.toString(con.connId);
              weight[j][2] = Double.toString(con.getWeightValue());
              j=j+1;
          }
      }
      /*
      for (int k=0;k<weight.length;k++) {
		  System.out.println(weight[k][0]+" "+weight[k][1]+"    "+weight[k][2]);
	  }*/
      return weight;
 
 }    

}

