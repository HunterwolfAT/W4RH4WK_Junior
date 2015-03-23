package com.example.w4rh4wk;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import jp.ksksue.driver.serial.FTDriver;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDeviceConnection;



public class MainActivity extends Activity {
	
	private FTDriver com;
	
	static Button button1;
	static Button button2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	
	button1 = (Button) findViewById(R.id.button1);
	button2 = (Button) findViewById(R.id.button2);
	
	//textLog = (TextView) findViewById(R.id.textLog);
	com = new FTDriver((UsbManager) getSystemService(USB_SERVICE));
	
	connect();
	
	//comWrite(new byte[]{'w', '\r', '\n'});
	//robotDrive((byte)5);
		
	// Create button
	/*
	button1.setOnClickListener( new View.OnClickListener() {
		public void onClick(View v) {
			comWrite(new byte[]{'w', '\r', '\n'});
			Log.w("myApp", "func called");
			
		}
	});
	
	
	
	button2.setOnClickListener( new View.OnClickListener() {
		public void onClick(View v) {
			com.begin(9600);
			if (com.isConnected())
				button2.setActivated(false);
		}
	});
	*/
	}
	
	public void connect() {
        if (com.begin(9600)) {
          // textLog.append("connected\n");
        	Toast.makeText(this, "Happy face", Toast.LENGTH_SHORT).show();  
        } else {
          // textLog.append("could not connect\n");
        	Toast.makeText(this, "Sad Face", Toast.LENGTH_SHORT).show(); 
        }
	}
	 
	public void disconnect() {
	       com.end();
	       if (!com.isConnected()) {
	          // textLog.append("disconnected\n");
	       }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void comWrite(byte[] data) {
		if (com.isConnected()) {
			com.write(data);
		} else {
			// textLog.append("not connected\n");
		}
	}
	 
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
	 
	public String comReadWrite(byte[] data) {
		com.write(data);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// ignore
		}
		return comRead();
	}
	
	public void robotDrive(byte distance_cm) {
		comReadWrite(
			new byte[] { 'k', distance_cm, '\r', '\n' }
		);
	}
	
	public void robotTurn(byte degree) {
		comReadWrite(
			new byte[] { 'l', degree, '\r', '\n' }
		);
	}
	
	public void robotSetVelocity(byte left, byte right) {
		comReadWrite(
			new byte[] { 'i', left, right, '\r', '\n' }
		);
	}
	
	public void robotSetBar(byte value) {
		comReadWrite(
			new byte[] { 'o', value, '\r', '\n' }
		);
	}
}
