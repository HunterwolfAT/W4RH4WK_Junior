package at.ac.uibk.robotwasd;

import android.util.Log;

/**
 * Created by effi on 3/31/15.
 */
public class CurrentTest implements Runnable {
	// kill thread
	public volatile static Boolean youShallNotPass;
    OdometryManager om;
    Thread OdomThread;


    public Float xGoal;
    public Float yGoal;
    public Float thetaGoal;

    public static boolean isKilled = false;
	public CurrentTest(Float xGoal,Float yGoal, Float thetaGoal) {

        this.xGoal = xGoal;
        this.yGoal = yGoal;
        this.thetaGoal = thetaGoal;

	}

	public void run() {
        om = new OdometryManager(xGoal,yGoal);
        OdomThread = new Thread(om);
        OdomThread.start();
        bugAvoid(xGoal, yGoal, thetaGoal);
	}

    private void turnToGoal(float x, float y, float theta)
    {
        float alpha = (float) Math.toDegrees(Math.atan2((double) ((y - Robot.yCoord)),(x - Robot.xCoord)));
        float toTurn = alpha - Robot.theta;
        //Log.d("info", "Turning: " + Float.toString(toTurn));
        checkAngle(toTurn);
        /*double deltay = y - Robot.yCoord;
        double deltax = x - Robot.xCoord;
        double dist =  Math.sqrt( (deltax * deltax + deltay * deltay));
        double tmpTheta1, tmpTheta2;
        if(dist > 0.2)
        {
            double tmpTheta = Math.asin(deltay / dist);

            if(deltax < 0 && deltay > 0)
            {
                tmpTheta = Math.toRadians(180) - tmpTheta;

            }
            if(deltax < 0 && deltay < 0)
            {
                tmpTheta = -Math.toRadians(180) -tmpTheta;
            }

            tmpTheta1 = Math.toDegrees(tmpTheta) - theta;

        }

        checkAngle(tmpTheta1);*/

    }

    /**
     * implements the bug 0 algorithm from the lecture
     * @param x goal x coordinate
     * @param y goal y coordinate
     * @param theta goal angle
     */
    public void bugAvoid(float x, float y, float theta)
    { //TODO magic numbers

        youShallNotPass = false;

        turnToGoal(x, y, theta);

        while(true) {
            try {

                if(youShallNotPass || isKilled)
                {
                    Robot.comReadWrite(new byte[]{'i', '\r', '\n'});
                    //Robot.xCoord = 0f;
                    //Robot.yCoord = 0f;

                    if(isKilled)
                    {
                        float toTurn = theta - Robot.theta;
                        checkAngle(toTurn);

                    }
                    //Robot.theta = 0f;
                    OdomThread.interrupt();
                    break;
                }
                Robot.comReadWrite(new byte[]{'w', '\r', '\n'});
                int sensors = AvoidanceManager.Avoid(17,17,30);
                if (sensors != 0)
                {
                    Robot.comReadWrite(new byte[]{'i', '\r', '\n'});
                    if(sensors > 1)
                    {
                        checkAngle(90);
                    }
                    else
                    {
                        checkAngle(-90);
                    }
                    Robot.comReadWrite(new byte[]{'w', '\r', '\n'});
                    Thread.sleep(1750);
                    turnToGoal(x, y, theta);

                }



               /* if(Robot.xCoord - 5 > x || Robot.yCoord - 5 > y)
                {
                    turnToGoal(x,y,theta);
                }
                */



            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

	}


	/**
	 * checks if two floats are within a range from each other 
	 * @param a
	 * @param b
	 * @param range
	 * @return
	 */
	private boolean floatApproximation (float a, float b, float range) {
		if ( a < b + range && a > b - range)
			return true;
		else
			return false;
	}
	
	/**
	 * checks if there is a turn to make and executes it if necessary
	 * @param toTurn angle
	 */
	private void checkAngle(float toTurn) {
		if (toTurn != 0) {
			Robot.Turn(toTurn);
            Log.d("info", Float.toString(toTurn));
			try {
				Thread.sleep(calcSleepAngle(toTurn));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * calculates an appropriate thread sleep time
     * @param angle in degrees
     * @return time in milliseconds
     */
    private int calcSleepAngle(float angle)
    {
        return Integer.signum((int) (angle * 10)) * (int) (angle*10);
    }

	/**
	 * turns the robot until it's parallel to an obstacle
	 * 
	 * @throws InterruptedException
	 */
	/*public void turnUntilAlligned() throws InterruptedException {
        float dist_to_side_sensor = 0f;
		dist_to_side_sensor = AM.retSensor(3);

        //angle between Main and side sensor
        float main_to_side_sens = (float) Math.toRadians(17);
        Float TurnTheThing = (dist_to_side_sensor * (float) Math.sin(main_to_side_sens)) / 6; //6cm is the distance between the main and the side sensor
        Log.d("info", Double.toString(Math.asin(TurnTheThing)) +" " + Float.toString(main_to_side_sens) + " " + Float.toString(dist_to_side_sensor) );

        Robot.Turn((byte) Math.toDegrees(Math.asin(TurnTheThing)));
    }
*/
}
