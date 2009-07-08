package pl.blip.divide.ledtester;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class LedTester extends Activity {
    public class LabelUpdatingOnSeekBarChangeListener implements
			OnSeekBarChangeListener {
		private TextView myLabel;
		public LabelUpdatingOnSeekBarChangeListener(TextView label) {
			myLabel = label;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			myLabel.setText(Integer.toString(progress));
			
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
	private TextView redLabel;
	private TextView greenLabel;
	private TextView blueLabel;
	private NotificationManager notificationManager;
	private Notification notification;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        redBar = (SeekBar) findViewById(R.id.red);
        greenBar = (SeekBar) findViewById(R.id.green);
        blueBar = (SeekBar) findViewById(R.id.blue);
        
        redLabel = (TextView) findViewById(R.id.red_label);
        greenLabel = (TextView) findViewById(R.id.green_label);
        blueLabel = (TextView) findViewById(R.id.blue_label);
        
        redBar.setOnSeekBarChangeListener(new LabelUpdatingOnSeekBarChangeListener(redLabel));
        greenBar.setOnSeekBarChangeListener(new LabelUpdatingOnSeekBarChangeListener(greenLabel));
        blueBar.setOnSeekBarChangeListener(new LabelUpdatingOnSeekBarChangeListener(blueLabel));
        
        Toast.makeText(this, R.string.welcome_text, Toast.LENGTH_LONG).show();
        
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.ledOffMS = 0;
        notification.ledOnMS = 1000;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        updateLedColor();
    }

	public void updateLedColor() {
		int red = redBar.getProgress();
		int green = greenBar.getProgress();
		int blue = blueBar.getProgress();
		
		int color = 0xff000000 | (red << 16) | (green << 8) | blue;
		notification.ledARGB = color;
		notificationManager.notify(0, notification);
	}
}
