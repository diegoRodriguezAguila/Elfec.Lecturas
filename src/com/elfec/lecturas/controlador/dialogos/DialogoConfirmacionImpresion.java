package com.elfec.lecturas.controlador.dialogos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.elfec.lecturas.R;
import com.elfec.lecturas.modelo.eventos.OnImpresionConfirmadaListener;
import com.elfec.lecturas.settings.VariablesDeSesion;
import com.elfec.lecturas.settings.ui.AdminUI;
import com.elfec.lecturas.settings.ui.OpcionesPreferenciasUI;

public class DialogoConfirmacionImpresion {

	private AlertDialog mDialog;
	private TextView txtAclaracionDecision;
	private CheckBox chkDecision;
	private OnImpresionConfirmadaListener mListener;

	@SuppressLint("InflateParams")
	public DialogoConfirmacionImpresion(Context context,
			OnImpresionConfirmadaListener listener) {
		mListener = listener;
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.dialogo_confirmar_impresion, null, false);
		mDialog = new AlertDialog.Builder(context).setView(rootView)
				.setTitle(R.string.titulo_confirmar_impresion)
				.setIcon(R.drawable.impresora_d)
				.setNegativeButton(R.string.btn_no, null)
				.setPositiveButton(R.string.btn_si, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						guardarPreferenciaMostrarDialogo();
						if (mListener != null)
							mListener.onImpresionConfirmada();
					}
				}).create();
		txtAclaracionDecision = (TextView) rootView
				.findViewById(R.id.txt_aclaracion_no_mostrar);
		chkDecision = (CheckBox) rootView
				.findViewById(R.id.chk_no_mostrar_mensaje);
		chkDecision.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton btn, boolean checked) {
				txtAclaracionDecision.setVisibility((checked ? View.VISIBLE
						: View.GONE));
			}
		});
	}

	/**
	 * Muestra el dialogo
	 */
	public void show() {
		mDialog.show();
	}

	/**
	 * Guarda la preferencia de mostrar dialogo de confirmacion de impresion del
	 * usuario.
	 */
	public void guardarPreferenciaMostrarDialogo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String preferenciaMostrarDialogo = "1";
				if (chkDecision.isChecked())
					preferenciaMostrarDialogo = "0";
				AdminUI adminUI = AdminUI.instanciar(VariablesDeSesion
						.getUsuarioLogeado());
				adminUI.guardarPreferencia(
						OpcionesPreferenciasUI.MOSTRAR_CONFIRMACION_IMPRESION,
						preferenciaMostrarDialogo);
			}
		}).start();
	}
}
