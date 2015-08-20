package com.elfec.lecturas;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import com.activeandroid.app.Application;
import com.elfec.lecturas.settings.AppPreferences;
import com.lecturas.elfec.R;

public class ElfecApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		AppPreferences.initialize(getApplicationContext());
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setDefaultFontPath("fonts/helvetica_neue_roman.ttf")
				.setFontAttrId(R.attr.fontPath).build());
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		AppPreferences.dispose();
	}
}
