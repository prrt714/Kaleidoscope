package vnd.blueararat.kaleidoscope6;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;

public class Prefs extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private SeekbarPref mSeekbarPrefM;
	private SeekbarPref mSeekbarPrefJ;
	private ListPreference mSaveFormat;
	private SeekbarPref mSeekbarPrefB;
	private CheckBoxPreference mCheckBoxPreference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		mSeekbarPrefM = (SeekbarPref) getPreferenceScreen().findPreference(
				"number_of_mirrors");
		mSeekbarPrefJ = (SeekbarPref) getPreferenceScreen().findPreference(
				"jpeg_quality");
		mSeekbarPrefB = (SeekbarPref) getPreferenceScreen().findPreference(
				"blur_value");
		mCheckBoxPreference = (CheckBoxPreference) getPreferenceScreen()
				.findPreference("blur");
		mSaveFormat = (ListPreference) getPreferenceScreen().findPreference(
				"format");
		mSaveFormat.setSummary(getString(R.string.pictures_will_be_saved) + " "
				+ mSaveFormat.getValue());
		mSeekbarPrefJ.setEnabled(mSaveFormat.getValue().equals("JPEG"));
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// if (arg1.equals("reset_settings")) {
		//
		// } else if (arg1.equals(KView.KEY_NUMBER_OF_MIRRORS)) {
		//
		// } else if (arg1.equals(Kaleidoscope.KEY_IMAGE_URI)) {
		//
		// } else
		if (arg1.equals("format")) {
			mSeekbarPrefJ.setEnabled(mSaveFormat.getValue().equals("JPEG"));
			mSaveFormat.setSummary(getString(R.string.pictures_will_be_saved)
					+ " " + mSaveFormat.getValue());
		}
	}

	public void onButtonClicked(View v) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor edit = preferences.edit();
		// edit.putInt(Kaleidoscope.KEY_NUMBER_OF_MIRRORS, 4);
		edit.putString(Kaleidoscope.KEY_IMAGE_URI, "");
		edit.commit();
		mSeekbarPrefM.setProgressValue(4);
		mSeekbarPrefJ.setProgressValue(40);
		mSaveFormat.setValue(getString(R.string.default_save_format));
		mSeekbarPrefB.setProgressValue(49);
		mCheckBoxPreference.setChecked(true);
	}

	// @Override
	// public void onBackPressed() {
	// finish();
	// }

	// @Override
	// public void onProgressChanged(SeekBar seekBar, int progress,
	// boolean fromUser) {
	// // TODO Auto-generated method stub
	// mTextView.setText(progress);
	// //Toast.makeText(this, "reset_settings", Toast.LENGTH_LONG).show();
	//
	// }
	//
	// @Override
	// public void onStartTrackingTouch(SeekBar seekBar) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onStopTrackingTouch(SeekBar seekBar) {
	// // TODO Auto-generated method stub
	// }
}
