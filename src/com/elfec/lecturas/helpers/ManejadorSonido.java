package com.elfec.lecturas.helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;

public class ManejadorSonido {

	public static void reproducirBeep(Context context)
	{
		try {
			final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
		     tg.startTone(ToneGenerator.TONE_PROP_BEEP);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
}
