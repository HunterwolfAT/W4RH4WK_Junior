package at.ac.uibk.robotwasd;

import android.util.Log;

import jp.ksksue.driver.serial.FTDriver;

/**
 * Created by effi on 3/25/15.
 */
public class Robot
{
    public static volatile Float xCoord = 0f;
    public static volatile Float yCoord = 0f;
    public static volatile Float theta = 0f;
    public static MainActivity  j;
    public static FTDriver com;


    public static boolean driveForward;




    public static String SetLeds(byte red, byte blue)
    {
        return(comReadWrite(new byte[] { 'u', red, blue, '\r', '\n' }));
    }

    public static String SetVelocity(byte left, byte right)
    {
        return (comReadWrite(new byte[] { 'i', left, right, '\r', '\n' }));
    }

    public static String SetBar(byte value)
    {
        return (comReadWrite(new byte[] { 'o', value, '\r', '\n' }));
    }

    public static String comRead() {
        String s = "";
        int i = 0;
        int n = 0;
        while (i < 3 || n > 0) {
            byte[] buffer = new byte[256];
            n = com.read(buffer);
            s += new String(buffer, 0, n);
            i++;
        }
        return s;
    }

    public static String comReadWrite(byte[] data)
    {

        if(data[0] == 'i')
        {

            driveForward = false;
        }
        else if(data[0] == 'w')
        {

            //Log.d("Info", "I'm driving Forward");
            driveForward = true;


        }
        else if(data[0] == 'l')
        {
            driveForward = false;
            //Log.d("info", "In comReadWrite " + Float.toString(((float) (data[1])) / 1.2f));
            theta += (((float) (data[1])) / 1.16f);

            xCoord += (float) Math.cos(Math.toRadians(data[1] / 1.2)) * 2.22f;
            yCoord += (float) Math.sin(Math.toRadians(data[1] / 1.2)) * 2.22f;


        }
        com.write(data);


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }
        return comRead();

    }



    public static void Drive(byte distance_cm) {
        comReadWrite(
                new byte[] { 'k', distance_cm, '\r', '\n' }
        );
    }

    public static  void Turn(float degree) {
        //Log.d("info", "In RobotTurn " + Float.toString(degree));
        float x = degree * 1.16f;
        if(x > 127 || x < -127)
        {
            float newTurn = x/2;
            comReadWrite(
                    new byte[]{'l', (byte) newTurn, '\r', '\n'}
            );
            try { Thread.sleep(Integer.signum((int) (newTurn * 10)) * (int) (newTurn*10)); } catch(Exception e){}
            comReadWrite(
                    new byte[]{'l', (byte) newTurn, '\r', '\n'}
            );
            try { Thread.sleep(Integer.signum((int) (newTurn * 10)) * (int) (newTurn*10)); } catch(Exception e){}
        }
        else {
            comReadWrite(
                    new byte[]{'l', (byte) x, '\r', '\n'}
            );
        }
    }

}
