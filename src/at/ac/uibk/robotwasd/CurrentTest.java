package at.ac.uibk.robotwasd;

import java.util.concurrent.RunnableFuture;

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void BugAvoid(float x, float y, float theta) {
		float alpha = (float) Math.atan((double)((y - Robot.yCoord))/(x - Robot.xCoord));
		float toTurn = alpha - Robot.theta;
		Robot.Turn((byte) toTurn);
		Robot.com.write(new byte[] { 'w', '\r', '\n' });
		if (!AM.Avoid(30, 6)) {
			try {
				turnUntilAlligned();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Robot.com.write(new byte[] { 'w', '\r', '\n' });
		while(AM.Avoid(15, 3))
		{
			Robot.com.write(new byte[] { 's', '\r', '\n' });
		}
		
		if (Robot.xCoord == x && Robot.yCoord == y) { //TODO add approximation 
			toTurn = theta - Robot.theta;
			Robot.Turn((byte) toTurn);
			return;
		}
		BugAvoid(x, y, theta);
		
		
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
