package at.ac.uibk.robotwasd;

import android.util.Log;

import java.util.concurrent.Callable;

/**
 * Created by effi on 3/25/15.
 * Boolean Threads
 * return if Robot is on collison with sth
 */
public class AvoidanceManager //implements Callable<Boolean>
{

   // private Integer dist;
    //private Integer sensor;
    /* This function checks if sth is in front of the robo
        returns false if obstacle is spotted

        sensor 6 == front
        sensor 2 == left
        sensor 3 == right

     */
  /*  public AvoidanceManager(Integer dist, Integer sensor)
    {
        this.dist = dist;
        this.sensor = sensor;
    }


    public Boolean call()
    {
        while(true)
        {
            if (!Avoid())
            {
                return true;
            }
        }
    }*/
    /*
        Avoidance Func
        Return false if sth is too close (dist) to the sensor

     */
    public synchronized boolean Avoid(Integer dist, Integer sensor) {
        String s = Robot.comReadWrite(new byte[]{'q', '\r', '\n'});
        //Log.d("Info", s);
        String arr[] = s.split(" ");

        Boolean flag = false;
        Integer[] n = new Integer[8];
        int i = 0;
        for (String t : arr) {
            if (t.contains("sensor:")) {
                flag = true;
                continue;
            }
            if (flag)
            {
                //so that other commands can't influence this function....
                if(t.startsWith("0x"))
                {
                    Integer base16 = (16 * ReallyStupidFunction(t.charAt(2))) + ReallyStupidFunction(t.charAt(3));
                    n[i++] = base16;
                }
            }
        }
        if (n[sensor] <= dist)
        {
            return false;
        }

       /* for(Integer x : n)
        {
            logText(x.toString());
        }*/

        return true;
    }


    /*because java std lib isn't working...
        converts a char into int (char must be hex)
     */
    private Integer ReallyStupidFunction(char c)
    {
        switch(c)
        {
            case '0': return 0;
            case '1': return 1;
            case '2': return 2;
            case '3': return 3;
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 7;
            case '8': return 8;
            case '9': return 9;
            case 'a': return 10;
            case 'b': return 11;
            case 'c': return 12;
            case 'd': return 13;
            case 'e': return 14;
            case 'f': return 15;
        }
        return 0;
    }
}
