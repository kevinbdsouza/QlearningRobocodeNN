package sample.Kevin.Try2.LUT; //change it into your package name

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
import java.util.Random;

@SuppressWarnings("unused")
public class Rl_lut extends AdvancedRobot {

	// declare variables
	final double epsilon = 0;
	final double gamma = 0.9;
	final double alpha = 0.1;
    
    //LUT table initialization
    int[] action=new int[4];
    int[] actionsAll=new int[4];
    int[] statesActionsCombo=new int[8*6*4*4*action.length];
    String[][] LUT=new String[statesActionsCombo.length][2];
    double[][] doubleLUT=new double[statesActionsCombo.length][2];
    
    //standard robocode parameters 
    double turnGunValue;
	double bearing;
    double absbearing=0;
    double distance=0;
    private double getVelocity;
	private double getBearing;
	    
    //quantized parameters
    int quantX=0;
    int quantY=0;
    int quantDist=0;
    int quantBearingAbs=0;
    
    //initialize reward related variables
    double reward=0;
    String presentQValue=null;
    double doublePresentQValue=0;
    int actionEpsRandom=0;
    int chosenAction = 0;
    String stateActionCombo=null;
    int saComboInLUT=0;
    String stateActionComboNext=null;
	int saComboInLUTNext=0;
	String NextQValue=null;
	double doubleNextQValue=0;
	int actionQGreedy=0;
	int[] matchActions=new int[actionsAll.length];
	double[] optionsQ=new double[actionsAll.length];
    
	//counting variables 
	int count = 0;
	static int [] winsRate = new int[10000];
			
	
public void run(){
	
	if(getRoundNum() == 0){
		
		//initialize LUT for learning 
		initialiseLUT();
		save();
		
		//load LUT for testing 
		/*try {
			load();
			} 
		catch (IOException e) {
			e.printStackTrace();
			}
		save();*/
    }
	
	count+=1;
	try {
		load();
		} 
	catch (IOException e) {
		e.printStackTrace();
		}
	
	//set colour
    setColors(null, new Color(200,0,192), new Color(0,192,192), Color.black, new Color(0, 0, 0));
    setBodyColor(new java.awt.Color(192,100,100,100));
   
    while(true){
    	
    	//draw a random value for E greedy 
    	Random rand = new Random();
    	double epsilonCheck = rand.nextDouble();
    
    // load everytime you enter the while loop
    try {
		load();
		} 
	catch (IOException e) {
		e.printStackTrace();
		}
    
    //turn gun to scan 
	turnGunRight(360);
	
	//random action with prob = epsilon 
	if (epsilonCheck <= epsilon) {
		actionEpsRandom=intRandom(1,actionsAll.length);
		stateActionCombo=quantX+""+quantY+""+quantDist+""+quantBearingAbs+""+actionEpsRandom;
	}
	
	//greedy with prob = 1 - epsilon
	else if (epsilonCheck > epsilon) {
		stateActionCombo = Qpolicy();
	} // back outside 
	
	/*--------------common code--------------*/
	
	//find the match for the state action combo in LUT 
	for(int i=0;i<LUT.length;i++){
		if(LUT[i][0].equals(stateActionCombo))
		{
			saComboInLUT=i;
			break;
		}
	}
	
	 //measure the Q value in the present state before executing action 
	 presentQValue = LUT[saComboInLUT][1];
	 doublePresentQValue=Double.parseDouble(presentQValue);
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
	 stateActionComboNext = Qpolicy();
	 
	 //find the match for the state action combo in LUT
	 for(int i=0;i<LUT.length;i++){
			if(LUT[i][0].equals(stateActionComboNext))
			{
				saComboInLUTNext=i;
				break;
			}
	 }
	
	 //find the next Q value (greedy)
	 NextQValue = LUT[saComboInLUTNext][1];
	 doubleNextQValue=Double.parseDouble(NextQValue);
	 
	 //update the present Q value according to Q learning equation  
	 doublePresentQValue=doublePresentQValue+alpha*(reward+gamma*doubleNextQValue-doublePresentQValue);
	 LUT[saComboInLUT][1]=Double.toString(doublePresentQValue);
	 
	 //save the LUT before exiting the while loop 
	 save();
	
}//while loop ends
    
}//run function ends

//function to support greedy policy 
public String Qpolicy()
{
	      // find action that gives maximum Q value
			for(int j=1;j<=actionsAll.length;j++)
			{
				stateActionCombo=quantX+""+quantY+""+quantDist+""+quantBearingAbs+""+j;
			
				for(int i=0;i<LUT.length;i++){
					if(LUT[i][0].equals(stateActionCombo))
					{
						matchActions[j-1]=i;
						break;
					
					}
				}
			 
			}
			
			//convert LUT to double
			for(int i=0;i<statesActionsCombo.length;i++){
				for(int j=0;j<2;j++){		
			doubleLUT[i][j]= Double.valueOf(LUT[i][j]).doubleValue();
				}
			}
			
			//get options for actions
			for(int k=0;k<actionsAll.length;k++){
				optionsQ[k]=doubleLUT[matchActions[k]][1];
			}
			
			//finding action that produces maximum q
			actionQGreedy=getMax(optionsQ)+1;
			stateActionCombo=quantX+""+quantY+""+quantDist+""+quantBearingAbs+""+actionQGreedy;
			
	return stateActionCombo;
	
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
private int quantDistance (double dist) {
	
	if((dist > 0) && (dist<=250)){
		quantDist=1;
		}
	else if((dist > 250) && (dist<=500)){
		quantDist=2;
		}
	else if((dist > 500) && (dist<=750)){
		quantDist=3;
		}
	else if((dist > 750) && (dist<=1000)){
		quantDist=4;
		}
	
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
	if(quantDist==1){fire(3);}
	if(quantDist==2){fire(2);}
	if(quantDist==3){fire(1);}
		
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
private int quantAngle(double bearAbs) {
	
	if((bearAbs > 0) && (bearAbs<=90)){
		quantBearingAbs=1;
		}
	else if((bearAbs > 90) && (bearAbs<=180)){
		quantBearingAbs=2;
		}
	else if((bearAbs > 180) && (bearAbs<=270)){
		quantBearingAbs=3;
		}
	else if((bearAbs > 270) && (bearAbs<=360)){
		quantBearingAbs=4;
		}
	return quantBearingAbs;
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
private int quantPos(double pos) {
				
	if((pos > 0) && (pos<=100)){
		quantX=1;
		}
	else if((pos > 100) && (pos<=200)){
		quantX=2;
		}
	else if((pos > 200) && (pos<=300)){
		quantX=3;
		}
	else if((pos > 300) && (pos<=400)){
		quantX=4;
		}
	else if((pos > 400) && (pos<=500)){
		quantX=5;
		}
	else if((pos > 500) && (pos<=600)){
		quantX=6;
		}
	else if((pos > 600) && (pos<=700)){
		quantX=7;
		}
	else if((pos > 700) && (pos<=800)){
		quantX=8;
		}
	return quantX;

}

//lut initialization for learning
public void initialiseLUT() {
    int[] statesActionsCombo=new int[8*6*4*4*action.length];
    LUT=new String[statesActionsCombo.length][2];
    int r=0;
    for(int i=1;i<=8;i++){
    	for(int j=1;j<=6;j++){
    		for(int k=1;k<=4;k++){
    			for(int l=1;l<=4;l++){
    				for(int m=1;m<=action.length;m++){
    					LUT[r][0]=i+""+j+""+k+""+l+""+m;
    					LUT[r][1]="0";
    					r=r+1;
    				}
    			}
    		}
    	}
    }
} 

//load LUT from file with IO exception 
public void load() throws IOException {
	BufferedReader fileReader = new BufferedReader(new FileReader(getDataFile("LookUpTable.txt")));
	String rowLine = fileReader.readLine();
	try {
      int u=0;
      while (rowLine != null) {
      	String splitLine[] = rowLine.split("    ");
      	LUT[u][0]=splitLine[0]; 
      	LUT[u][1]=splitLine[1];
      	u=u+1;
      	rowLine= fileReader.readLine();
      }
	} catch (IOException e) {
		e.printStackTrace();
	}finally {
		fileReader.close();
	}
}

//save the LUT to txt file 
public void save() {
	PrintStream S = null;
	try {
		S = new PrintStream(new RobocodeFileOutputStream(getDataFile("LookUpTable.txt")));
		for (int i=0;i<LUT.length;i++) {
			S.println(LUT[i][0]+"    "+LUT[i][1]);
		}
	} catch (IOException e) {
		e.printStackTrace();
	}finally {
		S.flush();
		S.close();
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
	save();	
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
