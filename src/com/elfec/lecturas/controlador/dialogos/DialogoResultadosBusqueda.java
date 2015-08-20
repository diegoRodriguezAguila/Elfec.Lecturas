package com.elfec.lecturas.controlador.dialogos;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.elfec.lecturas.controlador.adaptadores.LecturaAdapter;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.eventos.OnLecturaSeleccionadaListener;
import com.lecturas.elfec.R;

public class DialogoResultadosBusqueda {

	private AlertDialog mDialog;

	@SuppressLint("InflateParams")
	public DialogoResultadosBusqueda(Context context,
			List<Lectura> listaLecturas,
			final OnLecturaSeleccionadaListener listener) {
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.dialogo_resultados_busqueda, null, false);
		mDialog = new AlertDialog.Builder(context).setView(rootView)
				.setTitle(R.string.titulo_resultados_busqueda)
				.setIcon(R.drawable.resultados_busqueda)
				.setNegativeButton(R.string.salida_btn, null).create();
		TextView lblNumLecturas = (TextView) rootView
				.findViewById(R.id.lbl_num_lecturas_lista);
		ListView listViewLecturas = (ListView) rootView
				.findViewById(R.id.list_lecturas);
		listViewLecturas.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				mDialog.dismiss();
				if (listener != null)
					listener.onLecturaSeleccionada((Lectura) adapter
							.getItemAtPosition(pos));
			}
		});
		listViewLecturas.setAdapter(new LecturaAdapter(context,
				R.layout.list_item_lectura, listaLecturas, true));
		lblNumLecturas.setText("" + listaLecturas.size());
	}

	/**
	 * Muestra el dialogo
	 */
	public void show() {
		mDialog.show();
	}

}
