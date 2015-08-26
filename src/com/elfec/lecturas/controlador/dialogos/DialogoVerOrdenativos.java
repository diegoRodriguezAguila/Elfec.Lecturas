package com.elfec.lecturas.controlador.dialogos;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.elfec.lecturas.controlador.adaptadores.OrdenativoLecturaAdapter;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.OrdenativoLectura;
import com.lecturas.elfec.R;

public class DialogoVerOrdenativos {

	private AlertDialog mDialog;
	private View rootView;

	private Context context;
	private ListView listViewOrdLect;
	private OrdenativoLecturaAdapter ordAdapter;
	private Handler mHandler;

	@SuppressLint("InflateParams")
	public DialogoVerOrdenativos(Context cont, Lectura lectura) {
		this.context = cont;
		this.mHandler = new Handler();
		rootView = LayoutInflater.from(context).inflate(
				R.layout.dialogo_ver_ordenativos, null, false);
		mDialog = new AlertDialog.Builder(context).setView(rootView)
				.setTitle(R.string.titulo_ver_ordenativos)
				.setNegativeButton(R.string.salida_btn, null).create();
		obtenerOrdenativosLectura(lectura);
	}

	/**
	 * Obtiene los ordenativos de la lectura
	 */
	private void obtenerOrdenativosLectura(final Lectura lecturaActual) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				listViewOrdLect = (ListView) rootView
						.findViewById(R.id.list_view_ord_lect);
				asignarOrdenativos(lecturaActual.obtenerOrdenativosLectura());

			}
		}).start();

	}

	private void asignarOrdenativos(final List<OrdenativoLectura> ordenativos) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				boolean noHayOrdenativos = ordenativos.size() == 0;
				listViewOrdLect.setVisibility(noHayOrdenativos ? View.GONE
						: View.VISIBLE);
				if (!noHayOrdenativos) {
					ordAdapter = new OrdenativoLecturaAdapter(context,
							R.layout.list_item_ordenativo, ordenativos);
					listViewOrdLect.setAdapter(ordAdapter);
				} else {
					rootView.findViewById(R.id.lbl_info_lectura_no_ordenativos)
							.setVisibility(View.VISIBLE);
				}

			}
		});
	}

	/**
	 * Asigna un dismiss listener
	 * 
	 * @param listener
	 */
	public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
		mDialog.setOnDismissListener(listener);
	}

	/**
	 * Muestra el dialogo
	 */
	public void show() {
		mDialog.show();
	}

}
