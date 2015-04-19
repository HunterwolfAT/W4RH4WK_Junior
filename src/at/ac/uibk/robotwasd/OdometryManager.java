package at.ac.uibk.robotwasd;

import android.content.ContentUris;
import android.util.Log;

import jp.ksksue.driver.serial.FTDriver;

/**
 * Created by effi on 3/25/15.
 */
public class OdometryManager implements Runnable
{
    private Float Goalx;
    private Float Goaly;


    public OdometryManager(float x, float y)
    {
        Goalx = x;
        Goaly = y;
    }

    public void run()
    {
        while(true)
        {
           if(floatApproximation(Robot.yCoord, Goaly, 4f) && floatApproximation(Robot.xCoord, Goalx, 4f))
           {
               CurrentTest.isKilled = true;
               break;
           }
           try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            if(Robot.driveForward)
            {
                Robot.xCoord  +=   (float) (Math.cos(Math.toRadians(Robot.theta))) ;
                Robot.yCoord  +=   (float) (Math.sin(Math.toRadians(Robot.theta))) ;
            }
            //Log.d("Coord","x:" +  Float.toString(Robot.xCoord) + "; y:" + Float.toString(Robot.yCoord) + "th: " + Float.toString(Robot.theta));
            //Log.d("Sensor", "Fr: " + AvoidanceManager.retSensor(6) +" Rg" + AvoidanceManager.retSensor(3) + " L" + AvoidanceManager.retSensor(2));
            if(Thread.interrupted())
            {
                return;
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
    private boolean floatApproximation (float a, float b, float range)
    {
        if ( a < b + range && a > b - range)
            return true;
        else
            return false;
    }

}
