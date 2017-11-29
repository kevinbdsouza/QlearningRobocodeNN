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
public class Rl_check_q extends AdvancedRobot {

	// declare variables 
	final double alpha = 0.1;
    final double gamma = 0.9;
    final double epsilon = 0;
          
    //LUT table initialization
    int[] action=new int[4];
    int[] total_states_actions=new int[8*6*4*4*action.length];
    int[] total_actions=new int[4];
    String[][] LUT=new String[total_states_actions.length][2];
    String[][] CUM=new String[10][2];
    double[][] LUT_double=new double[total_states_actions.length][2];
    
    //standard robocode parameters 
    double absbearing=0;
    double distance=0;
    double gunTurnAmt;
	double bearing;
	int rlaction;
	private double getVelocity;
	private double getBearing;
	    
    //quantized parameters
    int qrl_x=0;
    int qrl_y=0;
    int qenemy_x=0;
    int qenemy_y=0;
    int qdistancetoenemy=0;
    int q_absbearing=0;
    
    //initialize reward related variables
    double reward=0;
    String state_action_combi=null;
    int sa_combi_inLUT=0;
    String q_present=null;
    double q_present_double=0;
    int random_action=0;
    int chosenAction = 0;
    String state_action_combi_next=null;
	int sa_combi_inLUT_next=0;
	String q_next=null;
	double q_next_double=0;
	int Qmax_action=0;
	int[] actions_indices=new int[total_actions.length];
	double[] q_possible=new double[total_actions.length];
    
	//counting variables 
	int count = 0;
	int count_battles;
	int winsCount = 0;
	static int [] winsRate = new int[10000];
	static int index1=0;
		
	
public void run(){
	
	if(getRoundNum() == 0){
		//initialize LUT
		/*initialiseLUT();
		save();*/
		try {
			load();
			} 
		catch (IOException e) {
			e.printStackTrace();
			}
		save();
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
    	
    	Random rand = new Random();
    	double epsilonCheck = rand.nextDouble();
    
    //save();	
    try {
		load();
		} 
	catch (IOException e) {
		e.printStackTrace();
		}
    
    //predict current state:
	turnGunRight(360);
	
	if (epsilonCheck <= epsilon) {
		
		random_action=randInt(1,total_actions.length);
		state_action_combi=qrl_x+""+qrl_y+""+qdistancetoenemy+""+q_absbearing+""+random_action;
	}
	
	else if (epsilonCheck > epsilon) {
		
		state_action_combi = Qpolicy();
	} // back outside 
	
	/*--------------common code--------------*/
	for(int i=0;i<LUT.length;i++){
		if(LUT[i][0].equals(state_action_combi))
		{
			sa_combi_inLUT=i;
			break;
		}
	}
	
	 q_present = LUT[sa_combi_inLUT][1];
	 q_present_double=Double.parseDouble(q_present);
	 reward=0;
	 /*---------common code ends------------*/
	 
	 if (epsilonCheck <= epsilon) {
	 rl_action(random_action);
	 chosenAction  = random_action; 
	 }
	 
	 else if (epsilonCheck > epsilon) {
	 rl_action(Qmax_action);
	 chosenAction = Qmax_action;
	 }
	 
	 /*----------------common code----------------*/
	 turnGunRight(360);
	 	 
	 //state_action_combi_next =qrl_x+""+qrl_y+""+qdistancetoenemy+""+q_absbearing+""+chosenAction;
	 state_action_combi_next = Qpolicy();
	 
	 for(int i=0;i<LUT.length;i++){
			if(LUT[i][0].equals(state_action_combi_next))
			{
				sa_combi_inLUT_next=i;
				break;
			}
	 }
	
	 q_next = LUT[sa_combi_inLUT_next][1];
	 q_next_double=Double.parseDouble(q_next);
	 
	 //performing update
	 q_present_double=q_present_double+alpha*(reward+gamma*q_next_double-q_present_double);
	 //System.out.println(sa_combi_inLUT);
	 //System.out.println(q_present_double);
	 LUT[sa_combi_inLUT][1]=Double.toString(q_present_double);
	 
	 
	 save();
	
}//while loop ends
    
}//run function ends

public String Qpolicy()
{
	// finding action that produces maximum Q value
			for(int j=1;j<=total_actions.length;j++)
			{
				state_action_combi=qrl_x+""+qrl_y+""+qdistancetoenemy+""+q_absbearing+""+j;
			
				for(int i=0;i<LUT.length;i++){
					if(LUT[i][0].equals(state_action_combi))
					{
						actions_indices[j-1]=i;
						break;
					
					}
				}
			 
			}
			//converting table to double
			for(int i=0;i<total_states_actions.length;i++){
				for(int j=0;j<2;j++){		
			LUT_double[i][j]= Double.valueOf(LUT[i][j]).doubleValue();
				}
			}
			//converting table to double
			for(int k=0;k<total_actions.length;k++){
				q_possible[k]=LUT_double[actions_indices[k]][1];
			}
			
			//finding action that produces maximum q
			Qmax_action=getMax(q_possible)+1;
				
			state_action_combi=qrl_x+""+qrl_y+""+qdistancetoenemy+""+q_absbearing+""+Qmax_action;
			
	return state_action_combi;
	
}
   
//function definitions for RL robot:
public void onScannedRobot(ScannedRobotEvent e)
	{
	double getVelocity=e.getVelocity();
	this.getVelocity=getVelocity;
	double getBearing=e.getBearing();
	this.getBearing=getBearing;
	this.gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + getHeading() - getGunHeading() -15);
	
	//distance to enemy
	distance = e.getDistance(); //distance to the enemy
	qdistancetoenemy=quantize_distance(distance); //distance to enemy state number 3
	
	//fire
	if(qdistancetoenemy==1){fire(3);}
	if(qdistancetoenemy==2){fire(2);}
	if(qdistancetoenemy==3){fire(1);}
		
	//your robot
	qrl_x=quantize_position(getX()); //your x position -state number 1
	qrl_y=quantize_position(getY()); //your y position -state number 2
	
	//Calculate the coordinates of the robot
	double angleToEnemy = e.getBearing();
	double angle = Math.toRadians((getHeading() + angleToEnemy % 360));
	double enemyX = (getX() + Math.sin(angle) * e.getDistance());
	double enemyY = (getY() + Math.cos(angle) * e.getDistance());
	qenemy_x=quantize_position(enemyX); //enemy x-position 
	qenemy_y=quantize_position(enemyY); //enemy y-position

	//absolute angle to enemy
	absbearing=absoluteBearing((float) getX(),(float) getY(),(float) enemyX,(float) enemyY);
	q_absbearing=quantize_angle(absbearing); //state number 4
	
	}

public double normalizeBearing(double angle) {
	while (angle >  180) angle -= 360;
	while (angle < -180) angle += 360;
	return angle;
	
}

//reward functions:
public void onHitRobot(HitRobotEvent event){reward-=2;} //our robot hit by enemy robot
public void onBulletHit(BulletHitEvent event){reward+=3;} //one of our bullet hits enemy robot
public void onHitByBullet(HitByBulletEvent event){reward-=3;} //when our robot is hit by a bullet
//public void BulletMissedEvent(Bullet bullet){reward-=3;} 

private int quantize_angle(double absbearing2) {
	
	if((absbearing2 > 0) && (absbearing2<=90)){
		q_absbearing=1;
		}
	else if((absbearing2 > 90) && (absbearing2<=180)){
		q_absbearing=2;
		}
	else if((absbearing2 > 180) && (absbearing2<=270)){
		q_absbearing=3;
		}
	else if((absbearing2 > 270) && (absbearing2<=360)){
		q_absbearing=4;
		}
	return q_absbearing;
}

private int quantize_distance(double distance2) {
	
	if((distance2 > 0) && (distance2<=250)){
		qdistancetoenemy=1;
		}
	else if((distance2 > 250) && (distance2<=500)){
		qdistancetoenemy=2;
		}
	else if((distance2 > 500) && (distance2<=750)){
		qdistancetoenemy=3;
		}
	else if((distance2 > 750) && (distance2<=1000)){
		qdistancetoenemy=4;
		}
	
	return qdistancetoenemy;
}

//absolute bearing
double absoluteBearing(float x1, float y1, float x2, float y2) {
	double xo = x2-x1;
	double yo = y2-y1;
	double hyp = Point2D.distance(x1, y1, x2, y2);
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

private int quantize_position(double rl_x2) {
				
	if((rl_x2 > 0) && (rl_x2<=100)){
		qrl_x=1;
		}
	else if((rl_x2 > 100) && (rl_x2<=200)){
		qrl_x=2;
		}
	else if((rl_x2 > 200) && (rl_x2<=300)){
		qrl_x=3;
		}
	else if((rl_x2 > 300) && (rl_x2<=400)){
		qrl_x=4;
		}
	else if((rl_x2 > 400) && (rl_x2<=500)){
		qrl_x=5;
		}
	else if((rl_x2 > 500) && (rl_x2<=600)){
		qrl_x=6;
		}
	else if((rl_x2 > 600) && (rl_x2<=700)){
		qrl_x=7;
		}
	else if((rl_x2 > 700) && (rl_x2<=800)){
		qrl_x=8;
		}
	return qrl_x;

	}

public void rl_action(int x)
			{
	switch(x){
		case 1: //action 1 of the RL robot
			int moveDirection=+1;  //moves in anticlockwise direction
			// circle our enemy
			setTurnRight(getBearing + 90);
			setAhead(150 * moveDirection);
			break;
		case 2: //action 2 of the RL robot
			int moveDirection1=-1;  //moves in clockwise direction
			// circle our enemy
			setTurnRight(getBearing + 90);
			setAhead(150 * moveDirection1);
			break;
		case 3: //action 3 of the RL robot

			setTurnGunRight(gunTurnAmt); 
			turnRight(getBearing-25); 
			ahead(150);
			break;
		case 4: //action 4 of the RL robot
			setTurnGunRight(gunTurnAmt); 
			turnRight(getBearing-25); 
			back(150);
			break;
			
	}
}//rl_action()

//randomint
public static int randInt(int min, int max) {

    Random rand = new Random();
    // nextInt is normally exclusive of the top value, so add 1 to make it inclusive
    int randomNum = rand.nextInt((max - min) + 1) + min;
    return randomNum;
}

//Look up table (lut) initialization:

public void initialiseLUT() {
    int[] total_states_actions=new int[8*6*4*4*action.length];
    LUT=new String[total_states_actions.length][2];
    int z=0;
    for(int i=1;i<=8;i++){
    	for(int j=1;j<=6;j++){
    		for(int k=1;k<=4;k++){
    			for(int l=1;l<=4;l++){
    				for(int m=1;m<=action.length;m++){
    					LUT[z][0]=i+""+j+""+k+""+l+""+m;
    					LUT[z][1]="0";
    					z=z+1;
    				}
    			}
    		}
    	}
    }

   } //Initialize LUT

public void save() {

	PrintStream w = null;
	try {
		w = new PrintStream(new RobocodeFileOutputStream(getDataFile("LookUpTable.txt")));
		for (int i=0;i<LUT.length;i++) {
			w.println(LUT[i][0]+"    "+LUT[i][1]);
		}
	} catch (IOException e) {
		e.printStackTrace();
	}finally {
		w.flush();
		w.close();
	}

}//save

public void load() throws IOException {
	BufferedReader reader = new BufferedReader(new FileReader(getDataFile("LookUpTable.txt")));
	String line = reader.readLine();
	try {
        int zz=0;
        while (line != null) {
        	String splitLine[] = line.split("    ");
        	LUT[zz][0]=splitLine[0]; 
        	LUT[zz][1]=splitLine[1];
        	zz=zz+1;
        	line= reader.readLine();
        }
	} catch (IOException e) {
		e.printStackTrace();
	}finally {
		reader.close();
	}
}//load

//get max (Max function)
public static int getMax(double[] array){ 

	double largest = array[0];int index = 0;
	for (int i = 1; i < array.length; i++) {
	  if ( array[i] >= largest ) {
	      largest = array[i];
	      index = i;
	   }
	}
	return index;
  }//end of getMax

public void onRoundEnded(RoundEndedEvent e) {
	//cum_reward_array[getRoundNum()]=cum_reward_while;
	//index1=index1+1;
	
}

// save win rate 
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
