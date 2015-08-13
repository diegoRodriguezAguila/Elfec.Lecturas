package com.elfec.lecturas.controlador.accionesycustomizaciones;

import com.lecturas.elfec.R;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomDialog extends AlertDialog {

	private Context context;
	private LinearLayout DialogContent;
	private ImageView icon;
	private View CustomContent;
	private TextView txtTitle;
	private TextView txtMessage;
	private Drawable iconLeft;
	private boolean showProgressbar = false;
	private boolean cancelable = true;
	private boolean hideAllButtons = true;
	private View customTitleView;
	private Button btnPositive;
	private View.OnClickListener btnPositiveOnClickListener;
	private Button btnNegative;
	private View.OnClickListener btnNegativeOnClickListener;
	private Button btnNeutral;
	private View.OnClickListener btnNeutralOnClickListener;
	private View.OnClickListener genericOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};
	private int titleId;
	private CharSequence titleLabel;
	private int messageId;
	private CharSequence messageLabel;
	
	public CustomDialog(Context context) {
		super(context, R.style.DialogElfecTheme);
		this.context = context;
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public CustomDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.cancelable = cancelable;
		this.context = context;
	}
	
	/**
	 * Si se sobreescribe debe de llamarse al metodo del padre, es decir super.onCreate(savedInstanceState); para que funcione la aplicación
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		int cantBotonesHabilitados = 0;
		super.setContentView(R.layout.custom_dialog);
		if(btnPositive!=null)
		{
			cantBotonesHabilitados++;
			Button btnPos = btnPositive;
			btnPositive = (Button) super.findViewById(R.id.btn_ok);
			btnPositive.setVisibility(btnPos.getVisibility());
			btnPositive.setText(btnPos.getText());
			btnPositive.setOnClickListener(btnPositiveOnClickListener!=null?btnPositiveOnClickListener:genericOnClickListener);
		}
		else super.findViewById(R.id.btn_ok).setVisibility(View.GONE);
		if(btnNegative!=null)
		{
			cantBotonesHabilitados++;
			Button btnNeg = btnNegative;
			btnNegative = (Button) super.findViewById(R.id.btn_cancel);
			btnNegative.setVisibility(btnNeg.getVisibility());
			btnNegative.setText(btnNeg.getText());
			btnNegative.setOnClickListener(btnNegativeOnClickListener!=null?btnNegativeOnClickListener:genericOnClickListener);
		}
		else super.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
		if(btnNeutral!=null)
		{
			cantBotonesHabilitados++;
			Button btnNeu = btnNeutral;
			btnNeutral = (Button) super.findViewById(R.id.btn_neutral);
			btnNeutral.setVisibility(btnNeu.getVisibility());
			btnNeutral.setText(btnNeu.getText());
			btnNeutral.setOnClickListener(btnNeutralOnClickListener!=null?btnNeutralOnClickListener:genericOnClickListener);
		}
		else super.findViewById(R.id.btn_neutral).setVisibility(View.GONE);
		txtTitle=((TextView) super.findViewById(R.id.txt_title));
		txtMessage=((TextView) super.findViewById(R.id.txt_message));
		if(titleLabel!=null)
		{
			txtTitle.setText(titleLabel);
		}
		else if(titleId!=0)
		{
			txtTitle.setText(titleId);
		}
		if(txtTitle.getText().length()>20)
		{
			txtTitle.setTextAppearance(context, android.R.style.TextAppearance_Medium);
			txtTitle.setTextColor(context.getResources().getColor(R.color.white));
		}
		if(messageLabel!=null)
		{
			txtMessage.setText(messageLabel);
		}
		else if(titleId!=0)
		{
			txtMessage.setText(messageId);
		}
		if(hideAllButtons)
		{
			super.findViewById(R.id.dialog_buttons).setVisibility(View.GONE);
			super.findViewById(R.id.separator_buttons).setVisibility(View.GONE);
		}
		if(cantBotonesHabilitados==1)
		{
			if(btnPositive!=null)
			{
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
				params.weight = 0;
				params.width = 180;
				btnPositive.setLayoutParams(params);
			}
			if(btnNegative!=null)
			{
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnNegative.getLayoutParams();
				params.weight = 0;
				params.width = 180;
				btnNegative.setLayoutParams(params);
			}
			if(btnNeutral!=null)
			{
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnNeutral.getLayoutParams();
				params.weight = 0;
				params.width = 180;
				btnNeutral.setLayoutParams(params);
			}
		}
		if(customTitleView!=null)
		{
			LinearLayout titleLayout =(LinearLayout)super.findViewById(R.id.dialog_title);
			titleLayout.removeAllViewsInLayout();
			titleLayout.addView(customTitleView);
		}
		icon = (ImageView)super.findViewById(R.id.icon_left);
		if(iconLeft!=null)
		{
			icon.setImageDrawable(iconLeft);
			icon.setVisibility(View.VISIBLE);
		}
		if(showProgressbar)
		{
			((ProgressBar)super.findViewById(R.id.custom_progressbar)).setVisibility(View.VISIBLE);
		}
		DialogContent = (LinearLayout)super.findViewById(R.id.dialog_message);
		if(CustomContent!=null)
		{
			DialogContent.setPadding(0, 0, 0, 0);
			DialogContent.removeAllViews();
			DialogContent.addView(CustomContent);
		}
		
	}
	
	@Override
	public void setIcon(Drawable icon)
	{
		iconLeft = icon;
		if(this.icon!=null)
		{
			this.icon.setImageDrawable(iconLeft);
			this.icon.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void setIcon(int iconId)
	{
		iconLeft = context.getResources().getDrawable( iconId);
		if(icon!=null)
		{
			icon.setImageDrawable(iconLeft);
			icon.setVisibility(View.VISIBLE);
		}
	}
	
	public void setPositiveButton(View.OnClickListener onClickListener)
	{
		btnPositive = new Button(context);
		hideAllButtons=false;
		btnPositive.setVisibility(View.VISIBLE);
		btnPositiveOnClickListener = onClickListener;
		btnPositive.setText(R.string.btn_ok);
	}
	
	public void setPositiveButton(int labelId, View.OnClickListener onClickListener)
	{
		btnPositive = new Button(context);
		hideAllButtons=false;
		btnPositive.setVisibility(View.VISIBLE);
		btnPositiveOnClickListener = onClickListener;
		btnPositive.setText(labelId);
	}
	public void setPositiveButton(CharSequence btnLabel, View.OnClickListener onClickListener)
	{
		btnPositive = new Button(context);
		hideAllButtons=false;
		btnPositive.setVisibility(View.VISIBLE);
		btnPositiveOnClickListener = onClickListener;
		btnPositive.setText(btnLabel);
	}
	
	public void setNegativeButton(View.OnClickListener onClickListener)
	{
		btnNegative = new Button(context);
		hideAllButtons=false;
		btnNegative.setVisibility(View.VISIBLE);
		btnNegativeOnClickListener = onClickListener;
		btnNegative.setText(R.string.btn_cancel);
	}
	
	public void setNegativeButton(int labelId, View.OnClickListener onClickListener)
	{
		btnNegative = new Button(context);
		hideAllButtons=false;
		btnNegative.setVisibility(View.VISIBLE);
		btnNegativeOnClickListener = onClickListener;
		btnNegative.setText(labelId);
	}
	
	public void setNegativeButton(CharSequence btnLabel, View.OnClickListener onClickListener)
	{
		btnNegative = new Button(context);
		hideAllButtons=false;
		btnNegative.setVisibility(View.VISIBLE);
		btnNegativeOnClickListener = onClickListener;
		btnNegative.setText(btnLabel);
	}
	
	public void setNeutralButton(View.OnClickListener onClickListener)
	{
		btnNeutral = new Button(context);
		hideAllButtons=false;
		btnNeutral.setVisibility(View.VISIBLE);
		btnNeutralOnClickListener = onClickListener;
		btnNeutral.setText(R.string.btn_neutral);
	}
	
	public void setNeutralButton(int labelId, View.OnClickListener onClickListener)
	{
		btnNeutral = new Button(context);
		hideAllButtons=false;
		btnNeutral.setVisibility(View.VISIBLE);
		btnNeutralOnClickListener = onClickListener;
		btnNeutral.setText(labelId);
	}
	
	public void setNeutralButton(CharSequence btnLabel, View.OnClickListener onClickListener)
	{
		btnNeutral = new Button(context);
		hideAllButtons=false;
		btnNeutral.setVisibility(View.VISIBLE);
		btnNeutralOnClickListener = onClickListener;
		btnNeutral.setText(btnLabel);
	}
	
	@Override
	public void setCustomTitle(View customTitleView)
	{
		this.customTitleView = customTitleView;
	}
	@Override
	public void setTitle(int titleId)
	{
		this.titleId = titleId;
		if(txtTitle!=null)
		{
			txtTitle.setText(titleId);
			if(txtTitle.getText().length()>20)
			{
				txtTitle.setTextAppearance(context, android.R.style.TextAppearance_Medium);
				txtTitle.setTextColor(context.getResources().getColor(R.color.white));
			}
		}
	}
	
	@Override
	public void setTitle(CharSequence titleLabel)
	{
		this.titleLabel = titleLabel;
		if(txtTitle!=null)
		{
			txtTitle.setText(titleLabel);
			if(txtTitle.getText().length()>20)
			{
				txtTitle.setTextAppearance(context, android.R.style.TextAppearance_Medium);
				txtTitle.setTextColor(context.getResources().getColor(R.color.white));
			}
		}
	}
	
	public void setMessage(int messageId)
	{
		this.messageId = messageId;
		if(txtMessage!=null)
		{
			txtMessage.setText(messageId);
		}
	}
	
	@Override
	public void setMessage(CharSequence messageLabel)
	{
		this.messageLabel = messageLabel;
		if(txtMessage!=null)
		{
			txtMessage.setText(messageLabel);
		}
	}
	
	@Override
	public void setCancelable(boolean cancelable)
	{
		this.cancelable = cancelable;
	}
	
	@Override
	public void onBackPressed()
	{
		if(cancelable)
			this.dismiss();
	}
	
	public void showProgressbar(boolean show)
	{
		showProgressbar = show;
	}
	
	@Override
	public void setContentView(View view)
	{
		CustomContent = view;
		if(DialogContent!=null)
		{
			DialogContent.setPadding(0, 0, 0, 0);
			DialogContent.removeAllViews();
			DialogContent.addView(CustomContent);
		}
	}
	
	@Override
	public void setContentView(int viewId)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(viewId, null);
		CustomContent =view;
		if(DialogContent!=null)
		{
			DialogContent.setPadding(0, 0, 0, 0);
			DialogContent.removeAllViews();
			DialogContent.addView(CustomContent);
		}
	}
	
	@Override
	public View findViewById(int viewId)
	{
		if(CustomContent!=null)
		{
			return CustomContent.findViewById(viewId);
		}
		return super.findViewById(viewId);
	}
}
