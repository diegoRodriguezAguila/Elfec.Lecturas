package com.elfec.lecturas.controlador.adaptadores.viewholders;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.elfec.lecturas.R;

/**
 * Viewholder dispositivos bluetooth
 * 
 * @author drodriguez
 *
 */
public class DispositivoBluetoothViewHolder {
	private TextView lblNombre;
	private TextView lblMAC;

	public DispositivoBluetoothViewHolder(View convertView) {
		lblNombre = ((TextView) convertView
				.findViewById(R.id.lbl_nombre_dispositivo));
		lblMAC = ((TextView) convertView.findViewById(R.id.lbl_mac_dispositivo));
	}

	/**
	 * Asigna la información a los campos de la vista
	 * 
	 * @param dispositivoBluetooth
	 * @param esSeleccionado
	 */
	public void bindDispositivo(BluetoothDevice dispositivoBluetooth,
			boolean esSeleccionado) {
		if (esSeleccionado) {
			lblNombre.setTextColor(Color.parseColor("#FFFFFF"));
			lblMAC.setTextColor(Color.parseColor("#FFFFFF"));
		} else {
			lblNombre.setTextColor(Color.parseColor("#000000"));
			lblMAC.setTextColor(Color.parseColor("#000000"));
		}
		lblNombre.setText(dispositivoBluetooth.getName());
		lblMAC.setText(dispositivoBluetooth.getAddress());
	}
}
