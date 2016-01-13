package com.elfec.lecturas.helpers.ui;

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.elfec.lecturas.R;

/**
 * Helper que sirve para animar los botones flotantes con sus metodos de salida
 * y entrada
 * 
 * @author drodriguez
 *
 */
public class FloatingActionButtonAnimator {

	/**
	 * Indica si alguna animacion de una vista a terminado
	 * 
	 * @param fab
	 * @return
	 */
	public static boolean isAnimating(View view) {
		return view.getAnimation() != null && !view.getAnimation().hasEnded();
	}

	/**
	 * Muestra el FloatingActionButton con la animaciÃ³n por defecto
	 * 
	 * @param fab
	 */
	public static void show(FloatingActionButton fab) {
		show(fab, null, null);
	}

	/**
	 * Muestra el FloatingActionButton con la animaciÃ³n pasada en los
	 * parÃ¡metros. Este mÃ©todo no llama a los callbacks de animaciÃ³n de la
	 * animaciÃ³n para ello utilize el mÃ©todo
	 * {@link #hide(FloatingActionButton, Animation, AnimationListener)}
	 * 
	 * @param fab
	 * @param anim
	 */
	public static void show(final FloatingActionButton fab, Animation anim) {
		show(fab, anim, null);
	}

	/**
	 * Muestra el FloatingActionButton con la animaciÃ³n pasada en los parÃ¡metros
	 * con el callback de animaciones proporcionado
	 * 
	 * @param fab
	 * @param anim
	 * @param listener
	 */
	public static void show(final FloatingActionButton fab, Animation anim,
			final AnimationListener listener) {
		fab.setEnabled(true);
		if (anim == null) {
			anim = AnimationUtils.loadAnimation(fab.getContext(),
					R.anim.design_fab_in);
			anim.setDuration(150);
		}
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				fab.setVisibility(View.VISIBLE);
				if (listener != null)
					listener.onAnimationStart(animation);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				if (listener != null)
					listener.onAnimationRepeat(animation);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (listener != null)
					listener.onAnimationEnd(animation);
			}
		});
		fab.startAnimation(anim);
	}

	/**
	 * Esconde el FloatingActionButton con la animaciÃ³n por defecto
	 * 
	 * @param fab
	 */
	public static void hide(FloatingActionButton fab) {
		hide(fab, null, null);
	}

	/**
	 * Esconde el FloatingActionButton con la animaciÃ³n pasada en los
	 * parÃ¡metros. Este mÃ©todo no llama a los callbacks de animaciÃ³n de la
	 * animaciÃ³n para ello utilize el mÃ©todo
	 * {@link #hide(FloatingActionButton, Animation, AnimationListener)}
	 * 
	 * @param fab
	 * @param anim
	 */
	public static void hide(final FloatingActionButton fab, Animation anim) {
		hide(fab, anim, null);
	}

	/**
	 * Esconde el FloatingActionButton con la animaciÃ³n pasada en los parÃ¡metros
	 * se llama a los callbacks de animaciÃ³n proporcionados
	 * 
	 * @param fab
	 * @param anim
	 * @param listener
	 */
	public static void hide(final FloatingActionButton fab, Animation anim,
			final AnimationListener listener) {
		fab.setEnabled(false);
		if (anim == null) {
			anim = AnimationUtils.loadAnimation(fab.getContext(),
					R.anim.design_fab_out);
			anim.setDuration(150);
		}
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				if (listener != null)
					listener.onAnimationStart(animation);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				if (listener != null)
					listener.onAnimationRepeat(animation);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				fab.setVisibility(View.GONE);
				if (listener != null)
					listener.onAnimationEnd(animation);
			}
		});
		fab.startAnimation(anim);
	}

	/**
	 * Esconde un FloatingActionButton y al finalizar la animaciÃ³n inicia la
	 * animaciÃ³n de mostrar el otro botÃ³n, con las animaciones por defecto
	 * 
	 * @param fabToHide
	 * @param fabToShow
	 */
	public static void hideAndShow(FloatingActionButton fabToHide,
			FloatingActionButton fabToShow) {
		hideAndShow(fabToHide, fabToShow, null, null);
	}

	/**
	 * Esconde un FloatingActionButton y al finalizar la animaciÃ³n inicia la
	 * animaciÃ³n de mostrar el otro botÃ³n, con las animaciones proporcionadas
	 * 
	 * @param fabToHide
	 * @param fabToShow
	 * @param hideAnim
	 * @param showAnim
	 */
	public static void hideAndShow(FloatingActionButton fabToHide,
			final FloatingActionButton fabToShow, Animation hideAnim,
			final Animation showAnim) {
		hide(fabToHide, hideAnim, new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				show(fabToShow, showAnim, null);
			}
		});
	}

}
