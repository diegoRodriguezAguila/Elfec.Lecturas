package com.elfec.lecturas.controlador.accionesycustomizaciones;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elfec.lecturas.R;
import com.elfec.lecturas.helpers.ui.ViewBackgroundCompat;

public class SquareButton extends LinearLayout {

	private CharSequence textPrincipal;
	private CharSequence textSub;
	private Drawable iconBottom;
	private Drawable background;
	private Drawable backgroundPressed;
	private Drawable backgroundDisabled;
	private String onClickHandler;
	private LinearLayout backgroundLayout;
	private TextView principalText;
	private float principalTextSize;
	private TextView subText;
	private float subTextSize;
	private ImageView bottomIcon;

	public SquareButton(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.square_button, this);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SquareButtonOptions, 0, 0);
		textPrincipal = a
				.getText(R.styleable.SquareButtonOptions_principalText);
		textSub = a.getText(R.styleable.SquareButtonOptions_subText);
		iconBottom = a.getDrawable(R.styleable.SquareButtonOptions_bottomIcon);
		principalTextSize = a.getDimension(
				R.styleable.SquareButtonOptions_principalTextSize, -1);
		subTextSize = a.getDimension(
				R.styleable.SquareButtonOptions_subTextSize, -1);
		background = ContextCompat.getDrawable(getContext(),
				R.drawable.abc_btn_default_mtrl_shape);
		background.setColorFilter(a.getColor(
				R.styleable.SquareButtonOptions_colorSquareButton, getContext()
						.getResources().getColor(R.color.elfectheme_color)),
				PorterDuff.Mode.SRC_ATOP);
		backgroundPressed = ContextCompat.getDrawable(getContext(),
				R.drawable.abc_btn_default_mtrl_shape);
		backgroundPressed.setColorFilter(a.getColor(
				R.styleable.SquareButtonOptions_colorSquareButtonPressed,
				getContext().getResources().getColor(
						R.color.highlight_elfectheme_color)),
				PorterDuff.Mode.SRC_ATOP);
		backgroundDisabled = ContextCompat.getDrawable(getContext(),
				R.drawable.abc_btn_default_mtrl_shape);
		backgroundDisabled.setColorFilter(a.getColor(
				R.styleable.SquareButtonOptions_colorSquareButton, getContext()
						.getResources().getColor(R.color.elfectheme_color)),
				PorterDuff.Mode.SRC_ATOP);
		backgroundDisabled.setAlpha(50);
		a.recycle();

		int[] onClickAttr = new int[] { android.R.attr.onClick,
				R.attr.colorButtonNormal, R.attr.colorControlHighlight };
		TypedArray ta = context
				.obtainStyledAttributes(attrs, onClickAttr, 0, 0);
		onClickHandler = ta.getString(0);
		ta.recycle();

	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		backgroundLayout.setEnabled(enabled);
		ViewBackgroundCompat.setBackground(backgroundLayout,
				enabled ? background : backgroundDisabled);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		principalText = (TextView) findViewById(R.id.principal_text);
		subText = (TextView) findViewById(R.id.sub_text);
		bottomIcon = (ImageView) findViewById(R.id.bottom_icon);
		principalText.setText(textPrincipal);
		if (principalTextSize != -1)
			principalText.setTextSize(principalTextSize);
		subText.setText(textSub);
		if (subTextSize != -1)
			subText.setTextSize(subTextSize);
		bottomIcon.setImageDrawable(iconBottom);
		backgroundLayout = (LinearLayout) findViewById(R.id.square_button_root_layout);
		ViewBackgroundCompat.setBackground(backgroundLayout, background);
		backgroundLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setPressed(true);
					ViewBackgroundCompat.setBackground(backgroundLayout,
							backgroundPressed);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					ViewBackgroundCompat.setBackground(backgroundLayout,
							background);
					if (v.isPressed())
						v.performClick();

				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					Rect rect = new Rect(v.getLeft(), v.getTop(), v.getRight(),
							v.getBottom());
					if (!rect.contains(v.getLeft() + (int) event.getX(),
							v.getTop() + (int) event.getY())) {
						v.setPressed(false);
					}
				}
				return true;
			}
		});
		backgroundLayout.setOnClickListener(new OnClickListener() {
			private Method mHandler;

			@Override
			public void onClick(View v) {
				if (mHandler == null) {
					try {
						mHandler = getContext().getClass().getMethod(
								onClickHandler, View.class);
					} catch (NoSuchMethodException e) {
						throw new IllegalStateException();
					}
				}

				try {
					mHandler.invoke(getContext(), (View) SquareButton.this);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException();
				} catch (InvocationTargetException e) {
					throw new IllegalStateException();
				}
			}
		});
	}
}
