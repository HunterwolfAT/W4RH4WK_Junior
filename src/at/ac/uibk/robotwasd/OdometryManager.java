package at.ac.uibk.robotwasd;

/**
 * Created by effi on 3/25/15.
 */
public class OdometryManager implements Runnable
{


    public Float[] getPos()
    {


    }

    public void run()
    {
        while(true)
        {

            Thread.sleep(100);
            if(Robot.driveForward)
            {
                Robot.xCoord = Robot.xCoord + Math.cos(Robot.theta.doubleValue());
                Robot.yCoord = Robot.yCoord + Math.sin(Robot.theta.doubleValue());
            }

        }
    }

}
