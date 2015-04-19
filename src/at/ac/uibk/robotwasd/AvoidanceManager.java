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

    /**
     * checks if something is within range
     * @param ldist range for left sensor
     * @param rdist range for right sensor
     * @param mdist range for middle sensor
     * @return 1 left, 2 right, 3 middle, 0 nothing
     */

    public static synchronized Integer Avoid(Integer ldist, Integer rdist, Integer mdist)
    {
        String s = Robot.comReadWrite(new byte[]{'q', '\r', '\n'});
        //Log.d("Info", s);
        String arr[] = s.split(" ");
        Integer[] n = new Integer[8];
        int i = 0;
        for (String t : arr)
        {
            //so that other commands can't influence this function.... (other commands write to the std output of the robot, really bad for multithreading
            //maybe we don't need the sensor flag anymore, too lazy to try....
            if(t.startsWith("0x"))
            {
                Integer base16 = (16 * ReallyStupidFunction(t.charAt(2))) + ReallyStupidFunction(t.charAt(3));
                n[i++] = base16;
            }

        }

        if(n[6] <= mdist)
        {
            Robot.comReadWrite(new byte[] { 'u', (byte) 255, 0, '\r', '\n' });
            return 3;
        }

        if(n[3] <= rdist)
        {
            Robot.comReadWrite(new byte[] { 'u', (byte) 255, 0, '\r', '\n' });
            return 2;
        }

        if(n[2] <= ldist)
        {
            Robot.comReadWrite(new byte[] { 'u', (byte) 255, 0, '\r', '\n' });
            return 1;
        }

        Robot.comReadWrite(new byte[] { 'u',  0, (byte) 255, '\r', '\n' });
        return 0;

    }


    public static synchronized Integer retSensor(Integer sensor)
    {
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
                //so that other commands can't influence this function.... (other commands write to the std output of the robot, really bad for multithreading
                //maybe we don't need the sensor flag anymore, too lazy to try....
                if(t.startsWith("0x"))
                {
                    Integer base16 = (16 * ReallyStupidFunction(t.charAt(2))) + ReallyStupidFunction(t.charAt(3));
                    n[i++] = base16;
                }
            }
        }
        if(n[sensor] != null)
        {

           return n[sensor];
        }

        return null;



    }


    /*because java std lib isn't working...
        converts a char into int (char must be hex)
     */
    private  static Integer ReallyStupidFunction(char c)
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
