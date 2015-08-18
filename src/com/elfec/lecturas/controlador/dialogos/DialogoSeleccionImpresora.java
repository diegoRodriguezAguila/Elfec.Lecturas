package com.elfec.lecturas.controlador.dialogos;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.elfec.lecturas.controlador.adaptadores.DispositivoBluetoothAdapter;
import com.elfec.lecturas.helpers.ManejadorImpresora;
import com.lecturas.elfec.R;

public class DialogoSeleccionImpresora extends AlertDialog {
	private ListView listViewSelecImpresora;
	public ArrayList<BluetoothDevice> listaDispositivos;
	private DispositivoBluetoothAdapter adapter;
	private Button btnSalir;
	private ArrayList<OnItemClickListener> itemClickListeners;

	public DialogoSeleccionImpresora(Activity actividad) {
		super(actividad);
		listaDispositivos = ManejadorImpresora
				.obtenerDispositivosSincronizados(actividad);
		adapter = new DispositivoBluetoothAdapter(actividad,
				R.layout.list_item_dispositivo, listaDispositivos);
		if (ManejadorImpresora.impresoraPredefinidaFueAsignada()) {
			adapter.setSeleccionado(listaDispositivos
					.indexOf(ManejadorImpresora.obtenerImpresoraPredefinida()));
		}
	}

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_seleccionar_impresora);
		setTitle(R.string.titulo_seleccionar_impresora);
		setIcon(R.drawable.impresora);
		listViewSelecImpresora = (ListView) findViewById(R.id.list_view_dispositivos);
		btnSalir = (Button) findViewById(R.id.btn_salir_dialogo_selec_impr);
		listViewSelecImpresora.setAdapter(adapter);
		ponerItemClickListener();
		if (listaDispositivos.size() == 0)// no hay dispositivos
		{
			findViewById(R.id.lbl_info_no_dispositivos).setVisibility(
					View.VISIBLE);
			listViewSelecImpresora.setVisibility(View.GONE);
			setCancelable(true);
		} else {
			findViewById(R.id.lbl_info_no_dispositivos)
					.setVisibility(View.GONE);
			listViewSelecImpresora.setVisibility(View.VISIBLE);
		}
		setBotonSalirListener();
	}

	public void esconderBotonSalir() {
		btnSalir.setVisibility(View.GONE);
	}

	private void ponerItemClickListener() {
		listViewSelecImpresora
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int pos, long id) {
						view.setBackgroundColor(Color.parseColor("#215b92"));
						((TextView) view
								.findViewById(R.id.lbl_nombre_dispositivo))
								.setTextColor(Color.parseColor("#FFFFFF"));
						((TextView) view.findViewById(R.id.lbl_mac_dispositivo))
								.setTextColor(Color.parseColor("#FFFFFF"));
						ManejadorImpresora
								.asignarImpresoraPredefinida(listaDispositivos
										.get(pos));
						adapter.setSeleccionado(pos);
						dismiss();
						if (itemClickListeners != null) {
							for (OnItemClickListener listener : itemClickListeners) {
								listener.onItemClick(parent, view, pos, id);
							}
						}
					}
				});
	}

	public void addOnItemClickListener(OnItemClickListener onClickListener) {
		if (itemClickListeners == null)
			itemClickListeners = new ArrayList<OnItemClickListener>();
		itemClickListeners.add(onClickListener);
	}

	private void setBotonSalirListener() {
		btnSalir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
	}
}
