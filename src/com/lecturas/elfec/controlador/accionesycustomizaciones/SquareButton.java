package com.lecturas.elfec.controlador.accionesycustomizaciones;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.lecturas.elfec.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SquareButton extends RelativeLayout{

	private Button baseButton;
	private TextView principalText;
	private TextView subText;
	private ImageView bottomIcon;
	
	@SuppressWarnings("deprecation")
	public SquareButton(final Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
		        R.styleable.SquareButtonOptions, 0, 0);
		    String textPrincipal = a.getString(R.styleable.SquareButtonOptions_principalText);
		    String textSub = a.getString(R.styleable.SquareButtonOptions_subText);
		    Drawable iconBottom = a.getDrawable(R.styleable.SquareButtonOptions_bottomIcon);
		    Drawable background = a.getDrawable(R.styleable.SquareButtonOptions_buttonBackground);
		    a.recycle();
		    
		    int[] onClickAttr = new int[] { android.R.attr.onClick };
		    TypedArray ta = context.obtainStyledAttributes(attrs, onClickAttr,0,0);
		    final String onClickHandler = ta.getString(0);
		    ta.recycle();

		    setGravity(Gravity.CENTER_VERTICAL);
		    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    RelativeLayout layoutButton = (RelativeLayout) inflater.inflate(R.layout.square_button, this, true);
		    principalText = (TextView) layoutButton.findViewById(R.id.principal_text);
		    principalText.setText(textPrincipal);
		    subText = (TextView) layoutButton.findViewById(R.id.sub_text);
		    subText.setText(textSub);
		    bottomIcon = (ImageView) layoutButton.findViewById(R.id.bottom_icon);
		    bottomIcon.setImageDrawable(iconBottom);    
		    baseButton = (Button) layoutButton.findViewById(R.id.principal_button);
		    if(background!=null)
		    {
		    	baseButton.setBackgroundDrawable(background);
		    }
		    
		    baseButton.setOnClickListener(new OnClickListener() {
				private Method mHandler;
				@Override
				public void onClick(View v) {
					if(mHandler == null)
					{
						try{
							mHandler = getContext().getClass().getMethod(onClickHandler, View.class);
						}
						catch(NoSuchMethodException e){
							throw new IllegalStateException();
						}
					}
						
						try{
							mHandler.invoke(getContext(), baseButton);
						} catch (IllegalAccessException e) {
		                    throw new IllegalStateException();
		                } catch (InvocationTargetException e) {
		                    throw new IllegalStateException();
		                }
				}
			});
	}

}
