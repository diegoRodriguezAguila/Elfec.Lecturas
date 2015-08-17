package com.elfec.lecturas.controlador.accionesycustomizaciones;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ActivitySwipeDetector implements OnTouchListener {

	static final String logTag = "ActivitySwipeDetector";
	private ISwipeListener activityListener;
	static final int MIN_DISTANCE = 70;
	private float downX, downY, upX, upY;

	public ActivitySwipeDetector(ISwipeListener activityListener) {
		this.activityListener = activityListener;
	}

	public void onRightToLeftSwipe() {
		activityListener.onRightToLeftSwipe();
	}

	public void onLeftToRightSwipe() {
		activityListener.onLeftToRightSwipe();
	}

	public void onTopToBottomSwipe() {
		activityListener.onTopToBottomSwipe();
	}

	public void onBottomToTopSwipe() {
		activityListener.onBottomToTopSwipe();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			downX = event.getX();
			downY = event.getY();
			return true;
		}
		case MotionEvent.ACTION_UP: {
			upX = event.getX();
			upY = event.getY();

			float deltaX = downX - upX;
			float deltaY = downY - upY;

			// swipe horizontal?
			if (Math.abs(deltaX) > Math.abs(deltaY)) {
				if (Math.abs(deltaX) > MIN_DISTANCE) {
					// left or right
					if (deltaX < 0) {
						this.onLeftToRightSwipe();
						return true;
					}
					if (deltaX > 0) {
						this.onRightToLeftSwipe();
						return true;
					}
				} else {
					Log.i(logTag,
							"Horizontal Swipe was only " + Math.abs(deltaX)
									+ " long, need at least " + MIN_DISTANCE);
					return false; // We don't consume the event
				}
			}
			// swipe vertical?
			else {
				if (Math.abs(deltaY) > MIN_DISTANCE) {
					// top or down
					if (deltaY < 0) {
						this.onTopToBottomSwipe();
						return true;
					}
					if (deltaY > 0) {
						this.onBottomToTopSwipe();
						return true;
					}
				} else {
					Log.i(logTag, "Vertical Swipe was only " + Math.abs(deltaX)
							+ " long, need at least " + MIN_DISTANCE);
					return false; // We don't consume the event
				}
			}

			return true;
		}
		}
		return false;
	}

}