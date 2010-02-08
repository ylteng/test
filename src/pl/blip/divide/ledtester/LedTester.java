/*
    LED tester for Android platform
    Copyright (C) 2009-2010 Rafał Rzepecki

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package pl.blip.divide.ledtester;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView.OnEditorActionListener;

public class LedTester extends Activity {
    public class TextEditListener implements OnEditorActionListener, OnFocusChangeListener {
		private SeekBar seekBar;

		public TextEditListener(SeekBar seekBar) {
			this.seekBar = seekBar;
		}

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			updateFrom(v);
			return true;
		}

		private void updateFrom(TextView v) {
			if (v == null)
				return;
			
			int value;
			try {
				value = Integer.parseInt(v.getText().toString());
			} catch (NumberFormatException e) {
				value = 0;
			}
			seekBar.setProgress(value);
			v.setText(String.valueOf(seekBar.getProgress()));
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus)
				updateFrom((TextView) v);
		}

	}

	public class LabelUpdatingOnSeekBarChangeListener implements
			OnSeekBarChangeListener {
		private TextView myLabel;
		public LabelUpdatingOnSeekBarChangeListener(TextView label) {
			myLabel = label;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			final String text = Integer.toString(progress);
			myLabel.setText(text);
			updateLedColor();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	}

	private SeekBar redBar;
	private SeekBar greenBar;
	private SeekBar blueBar;
	private EditText redText;
	private EditText greenText;
	private EditText blueText;
	private NotificationManager notificationManager;
	private Notification notification;
	private SeekBar onBar;
	private EditText onText;
	private EditText offText;
	private SeekBar offBar;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        redBar = (SeekBar) findViewById(R.id.red);
        greenBar = (SeekBar) findViewById(R.id.green);
        blueBar = (SeekBar) findViewById(R.id.blue);
        onBar = (SeekBar) findViewById(R.id.on_ms);
        offBar = (SeekBar) findViewById(R.id.off_ms);
                
        redText = (EditText) findViewById(R.id.red_text);
        greenText = (EditText) findViewById(R.id.green_text);
        blueText = (EditText) findViewById(R.id.blue_text);
        onText = (EditText) findViewById(R.id.on_ms_text);
        offText = (EditText) findViewById(R.id.off_ms_text);
        
        redBar.setOnSeekBarChangeListener(new LabelUpdatingOnSeekBarChangeListener(redText));
        greenBar.setOnSeekBarChangeListener(new LabelUpdatingOnSeekBarChangeListener(greenText));
        blueBar.setOnSeekBarChangeListener(new LabelUpdatingOnSeekBarChangeListener(blueText));
        onBar.setOnSeekBarChangeListener(new LabelUpdatingOnSeekBarChangeListener(onText));
        offBar.setOnSeekBarChangeListener(new LabelUpdatingOnSeekBarChangeListener(offText));
        
        TextEditListener tel;
        tel = new TextEditListener(redBar);
        redText.setOnEditorActionListener(tel);
        redText.setOnFocusChangeListener(tel);
        tel = new TextEditListener(greenBar);
        greenText.setOnEditorActionListener(tel);
        greenText.setOnFocusChangeListener(tel);
        tel = new TextEditListener(blueBar);
        blueText.setOnEditorActionListener(tel);
        blueText.setOnFocusChangeListener(tel);
        tel = new TextEditListener(onBar);
        onText.setOnEditorActionListener(tel);
        onText.setOnFocusChangeListener(tel);
        tel = new TextEditListener(offBar);
        offText.setOnEditorActionListener(tel);
        offText.setOnFocusChangeListener(tel);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.ledOffMS = 0;
        notification.ledOnMS = 5000;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        
        printSystemInfo();

        if (savedInstanceState == null)
        	showDialog(0);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	return new AlertDialog.Builder(this)
    		.setMessage(R.string.welcome_text)
    		.setPositiveButton(android.R.string.ok, new OnClickListener() {
	    		@Override
	    		public void onClick(DialogInterface dialog, int which) {
	    			dialog.dismiss();
	    		}
    	}).create();
    }

	private void printSystemInfo() {
		Log.i("LedTester", "System info:\n" +
				"\tBoard: " + Build.BOARD +
				"\n\tBrand: " + Build.BRAND +
				"\n\tDevice: " + Build.DEVICE +
				"\n\tDisplay: " + Build.DISPLAY +
				"\n\tFingerprint: " + Build.FINGERPRINT +
				"\n\tHost: " + Build.HOST +
				"\n\tID: " + Build.ID +
				"\n\tModel: " + Build.MODEL +
				"\n\tProduct: " + Build.PRODUCT + 
				"\n\tTags: " + Build.TAGS + 
				"\n\tType: " + Build.TYPE +
				"\n\tUser: " + Build.USER
		);
	}

	public void updateLedColor() {
		int red = redBar.getProgress();
		int green = greenBar.getProgress();
		int blue = blueBar.getProgress();
		
		int color = 0xff000000 | (red << 16) | (green << 8) | blue;

		notification.ledARGB = color;

        notification.ledOffMS = offBar.getProgress();
        notification.ledOnMS = onBar.getProgress();
        
        notificationManager.notify(0, notification);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		notificationManager.cancel(0);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateLedColor();
	}
}
