package at.ac.uibk.robotwasd;

import jp.ksksue.driver.serial.FTDriver;

/**
 * Created by effi on 3/25/15.
 */
public class Robot
{
    public static Integer xCoord;
    public static Integer yCoord;
    public static Float theta;
    public static MainActivity  j;
    public static FTDriver com;


    public static boolean driveForward;

    public static boolean turnLeft;

    public static boolean turnRight;



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

        if(data[0] == 's')
        {
            turnLeft = false;
            turnRight = false;
            driveForward = false;
        }
        else if(data[0] == 'w')
        {
            driveForward = true;
            turnLeft = false;
            turnRight = false;
        }
        else if(data[0] == 'l')
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

    public static  void Turn(byte degree) {
        float x = (float)degree * 1.13f;
        comReadWrite(
                new byte[]{'l', (byte) x, '\r', '\n'}
        );
    }

}
