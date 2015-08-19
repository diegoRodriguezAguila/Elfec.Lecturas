package com.elfec.lecturas.controlador.dialogos;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.elfec.lecturas.controlador.adaptadores.DispositivoBluetoothAdapter;
import com.elfec.lecturas.helpers.ManejadorImpresora;
import com.lecturas.elfec.R;

public class DialogoSeleccionImpresora {
	private AlertDialog mDialog;
	private View rootView;

	private Context context;

	private ListView listViewSelecImpresora;
	public List<BluetoothDevice> listaDispositivos;
	private DispositivoBluetoothAdapter adapter;
	private BluetoothAdapter bluetoothAdapter;
	private BroadcastReceiver mReceiver;
	private ArrayList<OnItemClickListener> itemClickListeners;

	@SuppressLint("InflateParams")
	public DialogoSeleccionImpresora(Context context) {
		this.context = context;
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		rootView = LayoutInflater.from(context).inflate(
				R.layout.dialogo_seleccionar_impresora, null, false);
		mDialog = new AlertDialog.Builder(context).setView(rootView)
				.setTitle(R.string.titulo_seleccionar_impresora)
				.setIcon(R.drawable.config_impresora_d)
				.setNegativeButton(R.string.btn_cancel, null).create();
		listViewSelecImpresora = (ListView) rootView
				.findViewById(R.id.list_view_dispositivos);
		ponerItemClickListener();
		perpararDispositivos();

	}

	/**
	 * Asigna la lista de dispositivos y segun eso asigna la visibilidad de la
	 * lista o el mensaje
	 * 
	 * @param dispositivos
	 */
	private void setAdapterDispositivosBluetooth(
			List<BluetoothDevice> dispositivos) {
		listaDispositivos = dispositivos;
		adapter = new DispositivoBluetoothAdapter(context,
				R.layout.list_item_dispositivo, listaDispositivos);
		listViewSelecImpresora.setAdapter(adapter);
		if (listaDispositivos == null || listaDispositivos.size() == 0) {
			rootView.findViewById(R.id.lbl_info_no_dispositivos).setVisibility(
					View.VISIBLE);
			listViewSelecImpresora.setVisibility(View.GONE);
		} else {
			rootView.findViewById(R.id.lbl_info_no_dispositivos).setVisibility(
					View.GONE);
			listViewSelecImpresora.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Prepara la lista de dispositivos bluetooth
	 */
	private void perpararDispositivos() {
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
			setBluetoothReceiver();
		}
		setAdapterDispositivosBluetooth(ManejadorImpresora
				.obtenerDispositivosSincronizados(context));
		if (ManejadorImpresora.impresoraPredefinidaFueAsignada()) {
			adapter.setSeleccionado(listaDispositivos
					.indexOf(ManejadorImpresora.obtenerImpresoraPredefinida()));
		}
	}

	/**
	 * Pone en funcionamiento el bluetooth receiver
	 * 
	 * @param context
	 */
	private void setBluetoothReceiver() {
		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();

				if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
					final int state = intent.getIntExtra(
							BluetoothAdapter.EXTRA_STATE,
							BluetoothAdapter.ERROR);
					switch (state) {
					case BluetoothAdapter.STATE_ON:
						perpararDispositivos();
						context.unregisterReceiver(mReceiver);
						break;
					}
				}
			}
		};
		IntentFilter filter = new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED);
		context.registerReceiver(mReceiver, filter);
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
						mDialog.dismiss();
						if (itemClickListeners != null) {
							for (OnItemClickListener listener : itemClickListeners) {
								listener.onItemClick(parent, view, pos, id);
							}
						}
					}
				});
	}

	/**
	 * Pone un item Click listener
	 * 
	 * @param onClickListener
	 * @return la isntancia de este dialogo
	 */
	public DialogoSeleccionImpresora addOnItemClickListener(
			OnItemClickListener onClickListener) {
		if (itemClickListeners == null)
			itemClickListeners = new ArrayList<OnItemClickListener>();
		itemClickListeners.add(onClickListener);
		return this;
	}

	/**
	 * Muestra el dialogo
	 */
	public void show() {
		mDialog.show();
	}
}
