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
import java.util.Timer;
import java.util.TimerTask;
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
	private String TAG = "SwagBot";
	private TextView textLog;
    private TextView xText;
    private TextView yText;
    private TextView ThetaText;
    private TextView xEdit;
    private TextView yEdit;
    private TextView thetaEdit;
	private FTDriver com;


    private CurrentTest MainThread;
    private Timer measurementTimer;
    Thread thr;

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textLog = (TextView) findViewById(R.id.textLog);
        xText = (TextView) findViewById(R.id.xText);
        yText = (TextView) findViewById(R.id.yText);
        ThetaText = (TextView) findViewById(R.id.ThetaText);
        xEdit = (TextView) findViewById(R.id.xEdit);
        yEdit = (TextView) findViewById(R.id.yEdit);
        thetaEdit = (TextView) findViewById(R.id.thetaEdit);

        Robot Robot = new Robot();



		com = new FTDriver((UsbManager) getSystemService(USB_SERVICE));

		connect();
        Robot.com = com;

        Log.d("Info","Connection established");

        measurementTimer = new Timer();
        measurementTimer.scheduleAtFixedRate(new MeasurementTimerTask(this), 0, 500);


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



    private class MeasurementTimerTask extends TimerTask
    {
        MainActivity ax;

        public MeasurementTimerTask(MainActivity av)
        {
            ax = av;
        }
        @Override
        public void run()
        {
            ax.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    xText.setText(Robot.xCoord.toString());
                    yText.setText(Robot.yCoord.toString());
                    ThetaText.setText(Robot.theta.toString());
                }
            });

        }
     }

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

    public void button100_onClick(View v) {
        Robot.comReadWrite(new byte[]{'w', '\r', '\n'});
        try{Thread.sleep(100);}catch(Exception e){}
        Robot.comReadWrite(new byte[] {'i' ,'\r', '\n' });
    }

	// stop
	public void buttonS_onClick(View v)
    {
        logText(Robot.comReadWrite(new byte[] {'i' ,'\r', '\n' }));

        CurrentTest.youShallNotPass = true;

        //Robot.xCoord = 0f;
        //Robot.yCoord = 0f;
        //Robot.theta = 0f;
       // thr.stop();



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

        //37/100 is calibration to convert in cm
        Float x = (Float.parseFloat(xEdit.getText().toString())); //* 37) / 100;
        Float y = (Float.parseFloat(yEdit.getText().toString())); //* 37) / 100;
        Float theta = Float.parseFloat(thetaEdit.getText().toString());

        MainThread = new CurrentTest(x,y,theta);
        //check if x is null then execute or thread was killed
        if(thr == null || !thr.isAlive())
        {
            thr = new Thread(MainThread);
            thr.start();
        }
	}
	
	public void buttoncam_onClick(View v)
	{
		Intent i = new Intent(this, BallManager.class);
		startActivity(i);
	}
	

}
