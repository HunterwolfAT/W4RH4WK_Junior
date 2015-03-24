package at.ac.uibk.robotwasd;

import jp.ksksue.driver.serial.FTDriver;
import android.app.Activity;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.lang.Thread;

public class MainActivity extends Activity {

	@SuppressWarnings("unused")
	private String TAG = "iRobot";
	private TextView textLog;
	private FTDriver com;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textLog = (TextView) findViewById(R.id.textLog);

		com = new FTDriver((UsbManager) getSystemService(USB_SERVICE));

		connect();
	}

	public void connect() {
		// TODO implement permission request

		if (com.begin(9600)) {
			textLog.append("connected\n");
		} else {
			textLog.append("could not connect\n");
		}
	}

	public void disconnect() {
		com.end();
		if (!com.isConnected()) {
			textLog.append("disconnected\n");
		}
	}
	
	/**
	 * transfers given bytes via the serial connection.
	 * 
	 * @param data
	 */
	public void comWrite(byte[] data) {
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
	public String comRead() {
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

	/**
	 * write data to serial interface, wait 100 ms and read answer.
	 * 
	 * @param data
	 *            to write
	 * @return answer from serial interface
	 */
	public String comReadWrite(byte[] data) {
		com.write(data);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// ignore
		}
		return comRead();
	}

	public void logText(String text) {
		if (text.length() > 0) {
			textLog.append("[" + text.length() + "] " + text + "\n");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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

	public void robotSetLeds(byte red, byte blue) {
		logText(comReadWrite(new byte[] { 'u', red, blue, '\r', '\n' }));
	}

	public void robotSetVelocity(byte left, byte right) {
		logText(comReadWrite(new byte[] { 'i', left, right, '\r', '\n' }));
	}

	public void robotSetBar(byte value) {
		logText(comReadWrite(new byte[] { 'o', value, '\r', '\n' }));
	}

	// move forward
	public void buttonW_onClick(View v) {
		logText(comReadWrite(new byte[] { 'w', '\r', '\n' }));
		while(true)
		{
			if(!AvoidFront())
			{
				break;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// turn left
	public void buttonA_onClick(View v) {
		logText(comReadWrite(new byte[] { 'a', '\r', '\n' }));
	}

	// stop
	public void buttonS_onClick(View v) {
		logText(comReadWrite(new byte[] { 's', '\r', '\n' }));
	}

	// turn right
	public void buttonD_onClick(View v) {
		logText(comReadWrite(new byte[] { 'd', '\r', '\n' }));
	}

	// move backward
	public void buttonX_onClick(View v) {
		// logText(comReadWrite(new byte[] { 'x', '\r', '\n' }));
		robotSetVelocity((byte) -30, (byte) -30);
	}

	// lower bar a few degrees
	public void buttonMinus_onClick(View v) {
		logText(comReadWrite(new byte[] { '-', '\r', '\n' }));
	}

	// rise bar a few degrees
	public void buttonPlus_onClick(View v) {
		logText(comReadWrite(new byte[] { '+', '\r', '\n' }));
	}

	// fixed position for bar (low)
	public void buttonDown_onClick(View v) {
		robotSetBar((byte) 0);
	}

	// fixed position for bar (high)
	public void buttonUp_onClick(View v) {
		robotSetBar((byte) 255);
	}

	public void buttonLedOn_onClick(View v) {
		// logText(comReadWrite(new byte[] { 'r', '\r', '\n' }));
		robotSetLeds((byte) 255, (byte) 128);
	}

	public void buttonLedOff_onClick(View v) {
		// logText(comReadWrite(new byte[] { 'e', '\r', '\n' }));
		robotSetLeds((byte) 0, (byte) 0);
	}

	public void buttonSensor_onClick(View v) {
		logText(comReadWrite(new byte[] { 'q', '\r', '\n' }));
	}
	
	public void robotDrive(byte distance_cm) {
		comReadWrite(
			new byte[] { 'k', distance_cm, '\r', '\n' }
		);
	}
	
	public void robotTurn(byte degree) {
		float x = (float)degree * 0.86f;
		comReadWrite(
			new byte[] { 'l', (byte)x, '\r', '\n' }
		);
	}
	
	public void driveSquare_onClick(View v)
	{
		/*try
		{
			int x = 200;
			int td = x * 10;
			int tx = x* 20;
			robotDrive((byte)-x);
			Thread.sleep(tx);
			robotTurn((byte)90);
			Thread.sleep(td);
			robotDrive((byte)-x);
			Thread.sleep(tx);
			robotTurn((byte)90);
			Thread.sleep(td);
			robotDrive((byte)-x);
			Thread.sleep(tx);
			robotTurn((byte)90);
			Thread.sleep(td);
			robotDrive((byte)-x);
			Thread.sleep(tx);
			robotTurn((byte)90);
			x
		}
		catch(Exception e)
		{
			//fuck Exceptions
		}
		*/
		//robotDrive((byte)-800);
		logText(comReadWrite(new byte[] { 'w', '\r', '\n' }));
		while(true)
		{
			if(!AvoidFront())
			{
				break;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void AvoidEverything(View v)
	{
		
	}
	
	public boolean AvoidFront()
	{
		String s = comReadWrite(new byte[] { 'q', '\r', '\n' });
		String arr[] = s.split(" ");
			if(arr[arr.length -2].equals("0x1d") || arr[arr.length -2].equals("0x1c") || arr[arr.length -2].equals("0x1e"))
			{
				logText(comReadWrite(new byte[] { 's', '\r', '\n' }));
				logText("stop");
				return false;
			}
		return true;
		/*Boolean flag = false;
		Integer[] n = new Integer[8];
		int i = 0;
		for(String t : arr)
		{
			if(t.contains("sensor:"))
			{
				flag = true;
				continue;
			}
			if(flag)
			{
				//n[i++] = Integer.valueOf(t.substring(2), 16);
				//Integer.parseInt("10");
				//logText(Integer.decode(t).toString());
				
				
			}
		}
		flag = false;
		/*
		for(Integer x : n)
		{
			logText(x.toString());
		}
			*/
	}
}
