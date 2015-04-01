package at.ac.uibk.robotwasd;

import jp.ksksue.driver.serial.FTDriver;
import android.app.Activity;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.lang.Thread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends Activity {

	@SuppressWarnings("unused")
	private String TAG = "iRobot";
	private TextView textLog;
    private TextView xText;
    private TextView yText;
    private TextView ThetaText;
	private FTDriver com;


    private CurrentTest MainThread;
    Thread x;
    AvoidanceManager AM;
	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textLog = (TextView) findViewById(R.id.textLog);
        Robot Robot = new Robot();

        AM = new AvoidanceManager();
        MainThread = new CurrentTest(AM);
		com = new FTDriver((UsbManager) getSystemService(USB_SERVICE));

		connect();
        Robot.com = com;

        Log.d("Info","Connection established");


	}

	public void connect()
    {
		// TODO implement permission request

		if (com.begin(9600)) {
			textLog.append("connected\n");
		} else {
			textLog.append("could not connect\n");
		}
	}

	public void disconnect()
    {
		com.end();
		if (!com.isConnected())
        {
			textLog.append("disconnected\n");
		}
	}



	/**
	 * transfers given bytes via the serial connection.
	 * 
	 * @param data
	 */
	public void comWrite(byte[] data)
    {
		if (com.isConnected()) {
			com.write(data);
		} else {
			textLog.append("not connected\n");
		}
	}

	/**
	 * reads from the serial buffer. due to buffering, the read command is
	 * issued 3 times at minimum and continuously as long as there are bytes to
	 * read from the buffer. Note that this function does not block, it might
	 * return an empty string if no bytes have been read at all.
	 * 
	 * @return buffer content as string
	 */





	public void logText(String text) {
		if (text.length() > 0) {
			textLog.append("[" + text.length() + "] " + text + "\n");
		}
	}

    public void logCoord(Integer x, Integer y, Float theta) {

            xText.setText(x.toString());
            yText.setText(y.toString());
            ThetaText.setText(theta.toString());

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
    {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
    {
		switch (item.getItemId()) {
		case R.id.connect:
			connect();
			return true;

		case R.id.disconnect:
			disconnect();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


	// move forward
	public void buttonW_onClick(View v) {
		logText(Robot.comReadWrite(new byte[]{'w', '\r', '\n'}));

	}

	// turn left
	public void buttonA_onClick(View v) {
		logText(Robot.comReadWrite(new byte[] { 'a', '\r', '\n' }));
	}

	// stop
	public void buttonS_onClick(View v) {
        MainThread.youShallNotPass = true;

	}

	// turn right
	public void buttonD_onClick(View v) {
		logText(Robot.comReadWrite(new byte[] { 'd', '\r', '\n' }));
	}

	// move backward
	public void buttonX_onClick(View v) {
		// logText(comReadWrite(new byte[] { 'x', '\r', '\n' }));
        Robot.SetVelocity((byte) -30, (byte) -30);
	}

	// lower bar a few degrees
	public void buttonMinus_onClick(View v) {
		logText(Robot.comReadWrite(new byte[] { '-', '\r', '\n' }));
	}

	// rise bar a few degrees
	public void buttonPlus_onClick(View v) {
		logText(Robot.comReadWrite(new byte[] { '+', '\r', '\n' }));
	}

	// fixed position for bar (low)
	public void buttonDown_onClick(View v) {
        Robot.SetBar((byte) 0);
	}

	// fixed position for bar (high)
	public void buttonUp_onClick(View v) {
        Robot.SetBar((byte) 255);
	}

	public void buttonLedOn_onClick(View v) {
		// logText(comReadWrite(new byte[] { 'r', '\r', '\n' }));
		Robot.SetLeds((byte) 255, (byte) 128);
	}

	public void buttonLedOff_onClick(View v) {
		// logText(comReadWrite(new byte[] { 'e', '\r', '\n' }));
        Robot.SetLeds((byte) 0, (byte) 0);
	}

	public void buttonSensor_onClick(View v) {
		logText(Robot.comReadWrite(new byte[] { 'q', '\r', '\n' }));
	}
	

	
	public void doTheThing_onClick(View v)
	{
         x = new Thread(MainThread);
         x.start();


	}
/*
    public void DriveTo(int x, int y)
    {
        try
        {

            int td = x * 10;
            int tx = x * 20;
            Robot.Drive((byte) -x);
            xCoord += x;
            Thread.sleep(tx);
            //logCoord(xCoord,yCoord,theta);
            if(y > 0)
            {
                Robot.Turn((byte) 90);
                theta += 90;
            }
            else if (y < 0)
            {
                Robot.Turn((byte) -90);
                theta += -90;
            }
            if(y != 0)
            {
                Thread.sleep(td);
            }
            //logCoord(xCoord,yCoord,theta);
            Robot.Drive((byte) -y);
            yCoord += y;
            //logCoord(xCoord,yCoord,theta);
            Thread.sleep(y*20);
            Robot.Turn((byte) -theta);
            theta = 0;
            //logCoord(xCoord,yCoord,theta);
        }
        catch(Exception e)
        {
            //fuck execptions
        }


    }
*/
	public void AvoidEverythingTurnL()
	{


	}
	

}
