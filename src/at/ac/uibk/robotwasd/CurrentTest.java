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
		youShallNotPass = false;
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
		Robot.com.write(new byte[] { 'w', '\r', '\n' });
		while(AM.Avoid(15, 3)) { 
			Robot.com.write(new byte[] { 's', '\r', '\n' });
		}
		
		if (floatApproximation(Robot.xCoord, x, (float) 0.1) && floatApproximation(Robot.yCoord, y, (float) 0.1)) { //range depends on odometry...
			Robot.com.write(new byte[] { 's', '\r', '\n' });
			toTurn = theta - Robot.theta;
			checkAngle(toTurn);
			return;
			
		}
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
			Robot.Turn((byte) toTurn);
			try {
				Thread.sleep(900);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	

	/**
	 * turns the robot until it's parallel to an obstacle
	 * 
	 * @throws InterruptedException
	 */
	public void turnUntilAlligned() throws InterruptedException {
        float dist_to_side_sensor = 0f;
		dist_to_side_sensor = AM.retSensor(3);

        //angle between Main and side sensor
        float main_to_side_sens = (float) Math.toRadians(17);
        Float TurnTheThing = (dist_to_side_sensor * (float) Math.sin(main_to_side_sens)) / 6; //6cm is the distance between the main and the side sensor
        Log.d("info", Double.toString(Math.asin(TurnTheThing)) +" " + Float.toString(main_to_side_sens) + " " + Float.toString(dist_to_side_sensor) );

        Robot.Turn((byte) Math.toDegrees(Math.asin(TurnTheThing)));
    }

}
