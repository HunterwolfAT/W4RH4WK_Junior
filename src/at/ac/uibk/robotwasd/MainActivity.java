package at.ac.uibk.robotwasd;

import jp.ksksue.driver.serial.FTDriver;
import android.app.Activity;
import android.content.Intent;
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




/*
    General Information/Bugs/ToDo things

    Never ever ever ever change the velocity of the Robot.... you will kill it
    Don't play with the LEDs. App will crash, no idea why (didn't change a thing)
    The app is multithreaded but the implementation isn't really clean so don't do anything funky
    Be careful when writing sth to the com interface (it writes things back and can kill our avoidanceManager)
    Do not start a second mainthread!!!! it is only there so the interface isn't stuck

    Odometry isn't working right now, do not try to use it
    if you have spare time you can try to make the avoidance manager callable again (multithreaded), but I had problems with the Future<Boolean> isDone() method, it only returned true( so the robot was doing some crazy shit)





 */

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
        logText(Robot.comReadWrite(new byte[] {'i' ,'\r', '\n' }));


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
        //check if x is null then execute or thread was killed
        if(x == null || !x.isAlive())
        {
            x = new Thread(MainThread);
            x.start();
        }
	}
	
	public void buttoncam_onClick(View v)
	{
		Intent i = new Intent(this, BallManager.class);
		startActivity(i);
	}
	

}
