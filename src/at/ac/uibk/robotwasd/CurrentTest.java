package at.ac.uibk.robotwasd;

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
		while (true) {
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
		if (!AM.Avoid(30, 6)) {
			try {
				turnUntilAlligned();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
		bugAvoid(x, y, theta);
		
		
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
		while (true) {
			if (AM.Avoid(12, 3)) {
				Robot.Turn((byte) 1);
				Thread.sleep(10);
			} else
				break;
		}
	}

}
