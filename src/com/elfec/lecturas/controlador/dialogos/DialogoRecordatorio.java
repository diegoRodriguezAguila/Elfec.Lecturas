package com.elfec.lecturas.controlador.dialogos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elfec.lecturas.R;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.eventos.OnRecordatorioGuardadoListener;

public class DialogoRecordatorio {

	private AlertDialog mDialog;
	private View rootView;

	private OnRecordatorioGuardadoListener mListener;

	private Lectura lecturaActual;
	private TextView lblRecordatorioActual;
	private EditText txtRecordatorioNuevo;
	private Activity context;

	@SuppressLint("InflateParams")
	public DialogoRecordatorio(Activity cont, Lectura lectura,
			OnRecordatorioGuardadoListener listener) {
		lecturaActual = lectura;
		context = cont;
		mListener = listener;
		rootView = LayoutInflater.from(context).inflate(
				R.layout.dialogo_recordatorio_lector, null, false);
		mDialog = new AlertDialog.Builder(context).setView(rootView)
				.setTitle(R.string.titulo_recordatorio)
				.setIcon(R.drawable.recordatorio_lector_d)
				.setNegativeButton(R.string.salida_btn, null)
				.setPositiveButton(R.string.guardar_btn, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						guardarRecordatorio();
						Toast.makeText(context,
								R.string.recordatorio_guardado_msg,
								Toast.LENGTH_LONG).show();
						if (mListener != null)
							mListener.onRecordatorioGuardado(lecturaActual);
					}
				}).create();

		lblRecordatorioActual = (TextView) rootView
				.findViewById(R.id.lbl_recordatorio_actual_msg);
		if (lecturaActual.Recordatorio != null)
			lblRecordatorioActual.setText(lecturaActual.Recordatorio);
		if (!lecturaActual.tieneRecordatorio()) {
			lblRecordatorioActual.setText(R.string.recordatorio_actual_lbl);
		}
		txtRecordatorioNuevo = (EditText) rootView
				.findViewById(R.id.txt_recordatorio_nuevo);
	}

	/**
	 * Muestra el dialogo de recordatorio
	 */
	public void show() {
		mDialog.show();
	}

	private void guardarRecordatorio() {
		String recNuevo = txtRecordatorioNuevo.getText().toString();
		lecturaActual.Recordatorio = recNuevo;
		lecturaActual.save();
	}

}
