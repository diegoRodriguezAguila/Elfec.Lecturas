package com.elfec.lecturas.controlador.accionesycustomizaciones;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lecturas.elfec.R;

public class SquareButton extends LinearLayout {

	private CharSequence textPrincipal;
	private CharSequence textSub;
	private Drawable iconBottom;
	private Drawable background;
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
		background = a
				.getDrawable(R.styleable.SquareButtonOptions_buttonBackground);
		principalTextSize = a.getDimension(
				R.styleable.SquareButtonOptions_principalTextSize, -1);
		subTextSize = a.getDimension(
				R.styleable.SquareButtonOptions_subTextSize, -1);
		a.recycle();

		int[] onClickAttr = new int[] { android.R.attr.onClick };
		TypedArray ta = context
				.obtainStyledAttributes(attrs, onClickAttr, 0, 0);
		onClickHandler = ta.getString(0);
		ta.recycle();

	}

	@SuppressWarnings("deprecation")
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
		if (background != null) {
			backgroundLayout.setBackgroundDrawable(background);
		}
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
