package com.lecturas.elfec.controlador.adaptadores;

import java.util.ArrayList;

import com.lecturas.elfec.R;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Sirve para crear la lista de dispositivos bluetooth, es el adaptador que se encarga de ajustar la vista
 * @author drodriguez
 *
 */
public class DispositivoBluetoothAdapter extends ArrayAdapter<BluetoothDevice> {

	private ArrayList<BluetoothDevice> lDispositivos;
	private static LayoutInflater inflater = null;
	private int dispSeleccionado;

	public DispositivoBluetoothAdapter(Context activity,
			int textViewResourceId, ArrayList<BluetoothDevice> dispositivos) {
		super(activity, textViewResourceId, dispositivos);
		try {
			this.lDispositivos = dispositivos;
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dispSeleccionado = -1;
		} catch (Exception e) {

		}
	}

	public void setSeleccionado(int seleccionado) {

		this.dispSeleccionado = seleccionado;
		this.notifyDataSetChanged();
	}

	public int getSeleccionado() {
		return this.dispSeleccionado;
	}

	@Override
	public int getCount() {
		return lDispositivos.size();
	}

	@Override
	public BluetoothDevice getItem(int position) {
		return lDispositivos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		vi = inflater.inflate(R.layout.list_item_dispositivo, null);
		if (dispSeleccionado == position) {
			vi.setBackgroundColor(Color.parseColor("#215b92"));
			((TextView) vi.findViewById(R.id.lbl_nombre_dispositivo)).setTextColor(Color.parseColor("#FFFFFF"));
			((TextView) vi.findViewById(R.id.lbl_mac_dispositivo)).setTextColor(Color.parseColor("#FFFFFF"));
		}
		BluetoothDevice dispositivoBluetooth = lDispositivos.get(position);
		((TextView) vi.findViewById(R.id.lbl_nombre_dispositivo)).setText(""
				+ dispositivoBluetooth.getName());
		((TextView) vi.findViewById(R.id.lbl_mac_dispositivo))
				.setText(dispositivoBluetooth.getAddress());
		return vi;
	}
}
