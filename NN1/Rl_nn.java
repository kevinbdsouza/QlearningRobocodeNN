package sample.Kevin.Try2.NN1; //change it into your package name

import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import com.sun.javafx.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.BattleEndedEvent;
import robocode.BulletHitEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobocodeFileOutputStream;
import robocode.RoundEndedEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("unused")
public class Rl_nn extends AdvancedRobot {

	// declare variables
	final double epsilon = 0.6;
	final double gamma = 0.9;
	final double alpha = 0.1;
    
    //LUT table initialization
    int[] action=new int[4];
    int[] actionsAll=new int[4];
    double[] Xtrain=new double[5];
	double[] Xtrain_next=new double[5];
	double[] Ytrain=new double[1];  
	static String [][] weight = new String[3*6 + 3 + 1][3];
	Map<String, Object> multiValues = new HashMap<String, Object>();
    
    //standard robocode parameters 
    double turnGunValue;
	double bearing;
    double absbearing=0;
    double distance=0;
    private double getVelocity;
	private double getBearing;
	    
    //quantized parameters
    double quantX=0;
    double quantY=0;
    double quantDist=0;
    double quantBearingAbs=0;
    
    //initialize reward related variables
    double reward=0;
    double presentQValue;
    int actionEpsRandom=0;
    int chosenAction = 0;
    double NextQValue;
    double latestQValue;
	int actionQGreedy=0;
	int[] matchActions=new int[actionsAll.length];
	double[] optionsQ=new double[actionsAll.length];
	double[] q_possible=new double[actionsAll.length];
    
	//counting variables 
	int count = 0;
	static int [] winsRate = new int[10000];
			
	
public void run(){
	
	//if(getRoundNum() != 0)
	//{
		try {
	    weightUpdate();}
	    catch(IOException e) {
	    	e.printStackTrace();
	    }
	//}
	
	count+=1;
		
	//set colour
    setColors(null, new Color(200,0,192), new Color(0,192,192), Color.black, new Color(0, 0, 0));
    setBodyColor(new java.awt.Color(192,100,100,100));
   
    while(true){
    	
    	//draw a random value for E greedy 
    	Random rand = new Random();
    	double epsilonCheck = rand.nextDouble();
    
    
    //turn gun to scan 
	turnGunRight(360);
	
	//random action with prob = epsilon 
	if (epsilonCheck <= epsilon) {
		actionEpsRandom=intRandom(1,actionsAll.length);
		
		Xtrain[0]=quantX;
		Xtrain[1]=quantY;
		Xtrain[2]=quantDist;
		Xtrain[3]=quantBearingAbs;
		Xtrain[4]=actionEpsRandom;
		
		System.out.println("Hi");
				
		try {
		if (getRoundNum() == 0)
		multiValues = NeuralNet.start(Xtrain,Ytrain,false,false,weight);
		else 
		multiValues = NeuralNet.start(Xtrain,Ytrain,false,false,weight);
		}
		catch (IOException e) {
			e.printStackTrace();
			}
		
		
		presentQValue = (double) multiValues.get("value");
		weight = (String[][]) multiValues.get("array"); 
		
				
	}
	
	//greedy with prob = 1 - epsilon
	else if (epsilonCheck > epsilon) {
		try {
			presentQValue = Qpolicy();
		}
		catch (IOException e) {
			e.printStackTrace();
			}
	} // back outside 
	
	/*--------------common code--------------*/
	
	 //reset reward to 0
	 reward=0;
	 
	 /*---------common code ends------------*/
	 
	 //take random action
	 if (epsilonCheck <= epsilon) {
	 actionSet(actionEpsRandom);
	 chosenAction  = actionEpsRandom; 
	 }
	 
	 //take greedy action
	 else if (epsilonCheck > epsilon) {
	 actionSet(actionQGreedy);
	 chosenAction = actionQGreedy;
	 }
	 
	 /*----------------common code----------------*/
	 
	 //scan again
	 turnGunRight(360);
	 
	 //off-policy Q learning (update state with greedy policy) (This part changes for Sarsa)
	 try {
	 NextQValue = Qpolicy();
	 }
	 catch (IOException e) {
			e.printStackTrace();
			}
	 	 
	 //update the present Q value according to Q learning equation  
	 presentQValue=presentQValue+alpha*(reward+gamma*NextQValue-presentQValue);
	 Ytrain[0] = presentQValue;
	 //-----update Q value in NN 
	 try {
	 multiValues = NeuralNet.start(Xtrain,Ytrain,false,true,weight);
	 }
	 catch (IOException e) {
			e.printStackTrace();
	 }
	 latestQValue = (double) multiValues.get("value");
	 weight = (String[][]) multiValues.get("array");
	 
	 weightSave();
	 
	 
	 
}//while loop ends
    
}//run function ends

//function to support greedy policy 
public double Qpolicy() throws IOException
{   /*
	for (int k=0;k<weight.length;k++) {
		  System.out.println(weight[k][0]+" "+weight[k][1]+"    "+weight[k][2]);
	}*/
	
	for(int j=1;j<=actionsAll.length;j++)
	{
		Xtrain[0]=quantX;
		Xtrain[1]=quantY;
		Xtrain[2]=quantDist;
		Xtrain[3]=quantBearingAbs;
		Xtrain[4]=j;
			
		multiValues =NeuralNet.start(Xtrain,Ytrain,false,false,weight);
		
		q_possible[j-1] = (double) multiValues.get("value");
		weight = (String[][]) multiValues.get("array");
	}
	/*
	weightSave();
	try {
		weightUpdate();}
		catch (IOException e) {
			e.printStackTrace();
		}
	*/
	
	//finding action that produces maximum q
	actionQGreedy=getMax(q_possible)+1;
	
			
	return q_possible[actionQGreedy-1];
	
}

//Intermediate reward functions: (turn this off for terminal rewards)
public void onHitRobot(HitRobotEvent event){reward-=2;} 
public void onBulletHit(BulletHitEvent event){reward+=3;} 
public void onHitByBullet(HitByBulletEvent event){reward-=3;} 

//function to chose random action
public static int intRandom(int min, int max) {
  Random rand = new Random();
  int randomNum = rand.nextInt((max - min) + 1) + min;
  return randomNum;
}

//function to get the maximum Q value 
public static int getMax(double[] array){ 

	int indc = 0;
	double maxValue = array[0];
	for (int i = 1; i < array.length; i++) {
	  if ( array[i] >= maxValue ) {
	      maxValue = array[i];
	      indc = i;
	   }
	}
	return indc;
}

//function to quantize distance to 4 values 
private double quantDistance (double dist) {
	quantDist = dist/100;	
	return quantDist;
}

//function that return values on scanning the enemy
public void onScannedRobot(ScannedRobotEvent e)
	{
	double getVelocity=e.getVelocity();
	this.getVelocity=getVelocity;
	double getBearing=e.getBearing();
	this.getBearing=getBearing;
	
	//got from trial and error
	this.turnGunValue = normalRelativeAngleDegrees(e.getBearing() + getHeading() - getGunHeading() -15);  
	
	//distance to enemy
	distance = e.getDistance(); //distance to the enemy
	quantDist=quantDistance (distance); 
	
	//fire depending on the quantized distance   
	if(quantDist<=2.50){fire(3);}
	if(quantDist>2.50&&quantDist<5.00){fire(2);}
	if(quantDist>5.00&&quantDist<7.50){fire(1);}
		
	//My robot
	quantX=quantPos(getX()); 
	quantY=quantPos(getY()); 
	
	//Calculate the coordinates of the robot
	double enemyAngle   = e.getBearing();
	double angle = Math.toRadians((getHeading() + enemyAngle   % 360));
	double oppoX = (getX() + Math.sin(angle) * e.getDistance());
	double oppoY = (getY() + Math.cos(angle) * e.getDistance());
	
	//absolute angle to enemy
	absbearing=getAbsBearing((float) getX(),(float) getY(),(float) oppoX,(float) oppoY);
	quantBearingAbs=quantAngle(absbearing); //state number 4
	
}

//find absolute bearing (borrowed from robocode wiki)  
double getAbsBearing(float xPos, float yPos, float oppX, float oppY) {
	double xo = oppX-xPos;
	double yo = oppY-yPos;
	double hyp = Point2D.distance(xPos, yPos, oppX, oppY);
	double arcSin = Math.toDegrees(Math.asin(xo / hyp));
	double bearing = 0;

	if (xo > 0 && yo > 0) { // both pos: lower-Left
		bearing = arcSin;
	} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
		bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang
	} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
		bearing = 180 - arcSin;
	} else if (xo < 0 && yo < 0) { // both neg: upper-right
		bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
	}

	return bearing;
}

//function to quantize bearing to 4 values 
private double quantAngle(double bearAbs) {

	return bearAbs/90;
}

//function to chose from 4 set of actions
public void actionSet(int x)
			{
	switch(x){
		case 1: 
			int moveDirection=+1;  
			setTurnRight(getBearing + 90);
			setAhead(150 * moveDirection);
			break;
		case 2: 
			int moveDirection1=-1;  
			setTurnRight(getBearing + 90);
			setAhead(150 * moveDirection1);
			break;
		case 3: 
			setTurnGunRight(turnGunValue); 
			turnRight(getBearing-25); 
			ahead(150);
			break;
		case 4: 
			setTurnGunRight(turnGunValue); 
			turnRight(getBearing-25); 
			back(150);
			break;
	}
}

//function to quantize position into 8 values (X) or 6 values (Y)
private double quantPos(double pos) {

	return pos/100;
}

public void weightSave()
{   
	PrintStream S = null;
	try {
	S = new PrintStream(new RobocodeFileOutputStream(getDataFile("weight.txt")));
	for (int k=0;k<weight.length;k++) {
	  S.println(weight[k][0]+" "+weight[k][1]+"    "+weight[k][2]);
	}
	}
	catch (IOException e) {
		e.printStackTrace();
	}finally {
		S.flush();
		S.close();
	}
}

public void weightUpdate() throws IOException
{
	BufferedReader fileReader = new BufferedReader(new FileReader(getDataFile("weight.txt")));
	String rowLine = fileReader.readLine();
	try {
	int u=0;
	while (rowLine != null) {
	 String[] splitLine = rowLine.split("    ");
	 //System.out.println(u);
	 String[] splitFurther = splitLine[0].split(" "); 
	 weight[u][0]=splitFurther[0]; 
	 weight[u][1]=splitFurther[1]; 
	 weight[u][2]=splitLine[1];
	 u=u+1;
	 rowLine= fileReader.readLine();
	}
	} catch (IOException e) {
	  e.printStackTrace();
	  }finally {
	fileReader.close();
	}
}

//save win rate 
public void saveWinRate()
{
  PrintStream w = null;
  try
  {
    w = new PrintStream(new RobocodeFileOutputStream(getDataFile("winsRate.txt")));
    for(int i=0; i<winsRate.length; i++)
  	  w.println(winsRate[i]);
  } 
  catch (IOException e) {
		e.printStackTrace();
	}finally {
		w.flush();
		w.close();
	}
  
   
}

   
public void onBattleEnded(BattleEndedEvent e) {
	saveWinRate();
}

public void onDeath(DeathEvent event)
{
	reward += -5;
	winsRate[getRoundNum()] = 0;
}

public void onWin(WinEvent event)
{
	reward += 5;
	winsRate[getRoundNum()] = 1;
}

}//Rl_check class
