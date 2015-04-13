package at.ac.uibk.robotwasd;

/**
 * Created by effi on 3/25/15.
 */
public class OdometryManager implements Runnable
{


<<<<<<< HEAD
=======
    public float getPos()
    {

    	return 0.0f;
    }

>>>>>>> origin/master
    public void run()
    {
        while(true)
        {

<<<<<<< HEAD
            try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            if(Robot.driveForward)
            {
                Robot.xCoord = Robot.xCoord + (float) Math.cos(Robot.theta.doubleValue());
                Robot.yCoord = Robot.yCoord + (float) Math.sin(Robot.theta.doubleValue());
            }
=======
            try
            {
            	Thread.sleep(100);
            	if(Robot.driveForward)
                {
                    Robot.xCoord = Robot.xCoord + (float)Math.cos(Robot.theta.doubleValue());
                    Robot.yCoord = Robot.yCoord + (float)Math.sin(Robot.theta.doubleValue());
                }
            } catch (Exception e) { } // TODO EFFFFIIIIII
            
>>>>>>> origin/master

        }
    }

}
