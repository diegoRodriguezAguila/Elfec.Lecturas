package com.elfec.lecturas.helpers.ui;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.elfec.lecturas.R;

/**
 * Se encarga de contraer o expandir un control, es un ayudante para la
 * animaciÃ³n de controles
 * 
 * @author drodriguez
 *
 */
public class Animador {

	public static void expand(final View v) {
		Animation heightAnim = AnimationUtils.loadAnimation(v.getContext(),
				R.anim.scale_fade_in);
		v.setVisibility(View.VISIBLE);
		v.startAnimation(heightAnim);
	}

	public static void collapse(final View v) {
		Animation heightAnim = AnimationUtils.loadAnimation(v.getContext(),
				R.anim.scale_fade_out);
		AnimationListener collapselistener = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.GONE);
			}
		};
		heightAnim.setAnimationListener(collapselistener);
		v.startAnimation(heightAnim);
	}

}
