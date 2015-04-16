package at.ac.uibk.robotwasd;

import android.util.Log;

/**
 * Created by effi on 3/31/15.
 */
public class CurrentTest implements Runnable {
	// kill thread
	public volatile Boolean youShallNotPass;
	private AvoidanceManager AM;

	public CurrentTest(AvoidanceManager AM) {
		this.AM = AM;
	}

	public void run() {
		// Robot.SetVelocity((byte) 60, (byte) 60);
		//youShallNotPass = false;
		/*while (true) {
			try {
				if (youShallNotPass) {
					// Robot.comReadWrite(new byte[] { 's', '\r', '\n' });
					Robot.com.write(new byte[] { 's', '\r', '\n' });
					return;
				}

				// drive
				Robot.Drive((byte) 100);
				if (!AM.Avoid(25, 6)) {
					System.out.println(Robot.comReadWrite(new byte[] { 's',
							'\r', '\n' }));
					Robot.Turn((byte) 90);
					Thread.sleep(900);

				} else if (!AM.Avoid(15, 2)) {
					Robot.Turn((byte) -50);
					Thread.sleep(900);
				} else if (!AM.Avoid(15, 3)) {
					Robot.Turn((byte) 50);
					Thread.sleep(900);
				}

				Thread.sleep(200);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
        */

        bugAvoid(100,100,0);
	}
	/**
	 * implements the bug 0 algorithm from the lecture 
	 * @param x goal x coordinate
	 * @param y goal y coordinate
	 * @param theta goal angle
	 */
	public void bugAvoid(float x, float y, float theta) { //TODO magic numbers 
		float alpha = (float) Math.toDegrees(Math.atan((double) ((y - Robot.yCoord)) / (x - Robot.xCoord)));
		float toTurn = alpha - Robot.theta;
		checkAngle(toTurn);

		Robot.com.write(new byte[] { 'w', '\r', '\n' });

        while(true) {
            if (!AM.Avoid(30, 6)) {
                try {
                    Log.d("info", "Hey there is sth out there");
                    turnUntilAlligned();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        /*
		if(AM.Avoid(15, 3)) { 
			Robot.com.write(new byte[] { 'i', '\r', '\n' });
		}
		
		if (floatApproximation(Robot.xCoord, x, (float) 0.1) && floatApproximation(Robot.yCoord, y, 0.1)) { //range depends on odometry...
			Robot.com.write(new byte[] { 'i', '\r', '\n' });
			toTurn = theta - Robot.theta;
			checkAngle(toTurn);
			return;
			
		}
        bugAvoid(x, y, theta);
		*/

	}
	
	/**
	 * checks if two floats are within a range from each other 
	 * @param a
	 * @param b
	 * @param range
	 * @return
	 */
	private boolean floatApproximation (float a, float b, double range) {
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
			Robot.Turn((byte) toTurn);
			try {
				Thread.sleep(calcSleepAngle(toTurn));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * calculates the amount to sleep based on a angle
	 * @param angle
	 * @return time to sleep in milliseconds
	 */
	private int calcSleepAngle (float angle) {
		return (int) (angle * 900 / 45); 
	}
	
	

	/**
	 * turns the robot until it's parallel to an obstacle
	 * 
	 * @throws InterruptedException
	 */
	public void turnUntilAlligned() throws InterruptedException {
        float distToSideSensor = 0f;
        boolean leftOrRight;
		distToSideSensor = (AM.retSensor(3) < AM.retSensor(2)) ? (AM.retSensor(3) ) : AM.retSensor(2);
		if (AM.retSensor(3) < AM.retSensor(2)) {
			distToSideSensor = AM.retSensor(3);
			leftOrRight = false;
		} else {
			distToSideSensor = AM.retSensor(2);
			leftOrRight = true;
		}

        //angle between Main and side sensor
        float mainToSideSens = (float) Math.toRadians(17);
        Float TurnTheThing = (distToSideSensor * (float) Math.sin(mainToSideSens)) / 6; //6cm is the distance between the main and the side sensor
        double angle = Math.toDegrees(Math.asin(TurnTheThing));
        Log.d("info", Double.toString(angle) +" " + Float.toString(mainToSideSens) + " " + Float.toString(distToSideSensor) );
        
        if(leftOrRight) {
        	Robot.Turn((byte) (360 - angle));
        	Thread.sleep(calcSleepAngle((float) (360 -angle)));
        } else {
        	Robot.Turn((byte) angle);
            Thread.sleep(calcSleepAngle((float) angle));	
        }
        
    }

}
