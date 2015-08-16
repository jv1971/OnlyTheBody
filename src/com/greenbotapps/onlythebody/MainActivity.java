package com.greenbotapps.onlythebody;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	public int LID = 0;
	protected int POS = 0;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;
	// private int animation = R.style.dialog_animation;
	private int animation = R.style.views_animation;

	protected void SavePosition() {
		SharedPreferences settings = getSharedPreferences("SharedPrefPos", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("DrawerPos", POS);
		editor.commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		// show Whats new message box only first time
		SharedPreferences sp = getSharedPreferences("FirstStart", 0);
		boolean fs = sp.getBoolean("FIRST_START", true);
		if (fs) {
			Library.ShowMessage(this, R.string.new_title, R.string.new_message, R.string.about_ok);
			SharedPreferences.Editor ed = sp.edit();
			ed.putBoolean("FIRST_START", false);
			ed.commit();
		}
	}

	@Override
	public void onDestroy() {
		SavePosition();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		SavePosition();
		super.onPause();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		POS = position;
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
		mTitle = getResources().getStringArray(R.array.workouts)[number - 1];
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final ListView lv = (ListView) findViewById(R.id.navigation_drawer);
		switch (item.getItemId()) {
		case R.id.action_about:
			int v = 0;
			try {
				v = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			} catch (NameNotFoundException e) {
				// Huh? Really?
				v = 0;
			}
			String text = String.format(getResources().getString(R.string.about_message), Calendar.getInstance().get(Calendar.YEAR), getResources().getStringArray(R.array.workouts).length, v);
			Library.ShowMessageStr(this, R.string.about_title, text, R.string.about_ok);
			return true;
		case R.id.action_clear:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.a1)).setMessage(getString(R.string.a2)).setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(getString(R.string.a3), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Yes button clicked
							final Manager mng = new Manager("default", MainActivity.this);
							mng.clear(MainActivity.this);
							if (lv != null) {
								lv.invalidateViews();
							}
						}
					}).setNegativeButton(getString(R.string.a4), null);
			AlertDialog dialogClear = builder.create();
			dialogClear.getWindow().getAttributes().windowAnimations = animation;
			dialogClear.show();
			return true;
		case R.id.action_manage:
			final Manager mng = new Manager(mTitle.toString(), this);
			AlertDialog.Builder bld = new AlertDialog.Builder(this);
			String[] tmpArray = getResources().getStringArray(R.array.level);
			Spanned[] lColors = new Spanned[tmpArray.length];
			for (int index = 0; index < tmpArray.length; index++) {
				lColors[index] = Html.fromHtml(tmpArray[index]);
			}
			bld.setTitle(R.string.mark_lavel).setSingleChoiceItems(lColors, mng.getLevel(), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					LID = which;
				}
			}).setPositiveButton(R.string.about_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK, so save the mSelectedItems results
					mng.setLevel(LID);
					if (lv != null) {
						lv.invalidateViews();
					}
				}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// cancel
				}
			});
			AlertDialog dialogLevels = bld.create();
			dialogLevels.getWindow().getAttributes().windowAnimations = animation;
			dialogLevels.show();
			return true;
		case R.id.action_timer:
			// start timer
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.timer_dialog);
			dialog.setTitle(getString(R.string.mnu_timer));
			dialog.getWindow().getAttributes().windowAnimations = animation;
			TextView txt = (TextView) dialog.findViewById(R.id.txtTimer);
			final StopWatch sw = new StopWatch(txt);
			// dismiss button of timer
			Button cmdDismiss = (Button) dialog.findViewById(R.id.cmdDismiss);
			cmdDismiss.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sw.stop();
					sw.reset();
					// dialog.dismiss();
				}
			});
			// start button of timer
			Button cmdStart = (Button) dialog.findViewById(R.id.cmdStart);
			cmdStart.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sw.start();
				}
			});
			// stop button of timer
			Button cmdStop = (Button) dialog.findViewById(R.id.cmdStop);
			cmdStop.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sw.stop();
				}
			});
			dialog.show();
			return true;
		case R.id.action_counter:
			final Dialog dlgCounter = new Dialog(this);
			dlgCounter.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dlgCounter.getWindow().setGravity(Gravity.TOP);
			dlgCounter.setContentView(R.layout.counter_dialog);
			dlgCounter.getWindow().getAttributes().windowAnimations = animation;
			final NumberPicker np = (NumberPicker) dlgCounter.findViewById(R.id.counter);
			np.setMinValue(0); // min value 0
			np.setMaxValue(100); // max value 100
			dlgCounter.show();
			return true;
		case R.id.action_save:
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// We can read and write the media
				File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
				File file = new File(path, mTitle + ".jpg");
				try {
					if (!file.exists()) {
						// Make sure the Pictures directory exists.
						path.mkdirs();
						Bitmap image = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("w" + POS, "drawable", getPackageName()));
						FileOutputStream out = new FileOutputStream(file);
						image.compress(Bitmap.CompressFormat.PNG, 100, out);
						// Tell the media scanner about the new file so that it
						// is immediately available to the user.
						MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null, new MediaScannerConnection.OnScanCompletedListener() {
							public void onScanCompleted(String path, Uri uri) {
							}
						});
						Toast.makeText(getBaseContext(), getString(R.string.file_save) + " " + file.getName(), Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// Unable to create file, likely because external storage is
					// not currently mounted.
					Toast.makeText(getBaseContext(), getString(R.string.file_save_error), Toast.LENGTH_SHORT).show();
				}
			} else {
				// Cannot read and write media
				Toast.makeText(getBaseContext(), getString(R.string.file_save_error), Toast.LENGTH_SHORT).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements AnimationListener {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		private static Animation animBounce = null;

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			TouchImageView imageWorkout = (TouchImageView) rootView.findViewById(R.id.imgWorkout);
			Drawable d;
			int i = 0;
			Bundle bn = getArguments();
			if (bn != null) {
				i = bn.getInt(ARG_SECTION_NUMBER) - 1;
			}
			try {
				d = getResources().getDrawable(getResources().getIdentifier("drawable/w" + i, "drawable", container.getContext().getPackageName()));
			} catch (Exception ex) {
				d = getResources().getDrawable(getResources().getIdentifier("drawable/placeholder", "drawable", container.getContext().getPackageName()));
			}
			imageWorkout.setImageDrawable(d);
			animBounce = AnimationUtils.loadAnimation(container.getContext(), R.anim.bounce);
			animBounce.setAnimationListener(this);
			imageWorkout.setVisibility(View.VISIBLE);
			imageWorkout.startAnimation(animBounce);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (animation == animBounce) {

			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			if (animation == animBounce) {

			}
		}

		@Override
		public void onAnimationStart(Animation animation) {
			if (animation == animBounce) {

			}
		}
	}
}
