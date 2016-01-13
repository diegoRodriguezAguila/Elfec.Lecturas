package com.elfec.lecturas;

import com.activeandroid.app.Application;
import com.elfec.lecturas.settings.AppPreferences;

import net.danlew.android.joda.JodaTimeAndroid;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ElfecApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		AppPreferences.initialize(getApplicationContext());
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setDefaultFontPath("fonts/helvetica_neue_roman.otf")
				.setFontAttrId(R.attr.fontPath).build());
		JodaTimeAndroid.init(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		AppPreferences.dispose();
	}
}
