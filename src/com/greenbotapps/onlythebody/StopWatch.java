package com.greenbotapps.onlythebody;

import android.os.Handler;
import android.widget.TextView;

public class StopWatch {
	private TextView tempTextView;
	private Handler mHandler = new Handler();
	private long startTime;
	private long elapsedTime;
	private final int REFRESH_RATE = 100;
	private String hours, minutes, seconds;
	private long secs, mins, hrs;
	private boolean stopped = false;

	public StopWatch(TextView tv) {
		tempTextView = tv;
		tempTextView.setText("00:00:00");
	}

	public void start() {
		if (stopped) {
			startTime = System.currentTimeMillis() - elapsedTime;
		} else {
			startTime = System.currentTimeMillis();
		}
		mHandler.removeCallbacks(startTimer);
		mHandler.postDelayed(startTimer, 0);
	}

	public void stop() {
		mHandler.removeCallbacks(startTimer);
		stopped = true;
	}

	public void reset() {
		stopped = false;
		tempTextView.setText("00:00:00");
	}

	private Runnable startTimer = new Runnable() {
		public void run() {
			elapsedTime = System.currentTimeMillis() - startTime;
			updateTimer(elapsedTime);
			mHandler.postDelayed(this, REFRESH_RATE);
		}
	};

	private void updateTimer(float time) {
		secs = (long) (time / 1000);
		mins = (long) ((time / 1000) / 60);
		hrs = (long) (((time / 1000) / 60) / 60);

		/*
		 * Convert the seconds to String and format to ensure it has a leading
		 * zero when required
		 */
		secs = secs % 60;
		seconds = String.valueOf(secs);
		if (secs == 0) {
			seconds = "00";
		}
		if (secs < 10 && secs > 0) {
			seconds = "0" + seconds;
		}

		/* Convert the minutes to String and format the String */

		mins = mins % 60;
		minutes = String.valueOf(mins);
		if (mins == 0) {
			minutes = "00";
		}
		if (mins < 10 && mins > 0) {
			minutes = "0" + minutes;
		}

		/* Convert the hours to String and format the String */

		hours = String.valueOf(hrs);
		if (hrs == 0) {
			hours = "00";
		}
		if (hrs < 10 && hrs > 0) {
			hours = "0" + hours;
		}

		/* Setting the timer text to the elapsed time */
		tempTextView.setText(hours + ":" + minutes + ":" + seconds);
	}
}
