package com.elfec.lecturas;

import com.activeandroid.app.Application;
import com.elfec.lecturas.settings.AppPreferences;

public class ElfecApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		AppPreferences.initialize(getApplicationContext());
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		AppPreferences.dispose();
	}
}
