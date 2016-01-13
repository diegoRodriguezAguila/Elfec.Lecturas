package com.elfec.lecturas.controlador.adaptadores.viewholders;

import android.view.View;
import android.widget.TextView;

import com.elfec.lecturas.R;
import com.elfec.lecturas.modelo.Lectura;

/**
 * Patron ViewHolder para las listas de lecturas
 * 
 * @author drodriguez
 *
 */
public class LecturaViewHolder {

	private TextView lblCuenta;
	private TextView lblNusCliente;
	private TextView lblNumMedidor;
	private TextView lblEstadoLectura;
	private TextView lblRuta;

	public LecturaViewHolder(View convertView) {
		lblCuenta = ((TextView) convertView
				.findViewById(R.id.lbl_num_cuenta_item));
		lblNusCliente = ((TextView) convertView
				.findViewById(R.id.lbl_nus_cliente_item));
		lblNumMedidor = ((TextView) convertView
				.findViewById(R.id.lbl_num_medidor_item));
		lblEstadoLectura = ((TextView) convertView
				.findViewById(R.id.lbl_estado_lectura));
		lblRuta = ((TextView) convertView.findViewById(R.id.lbl_ruta_item));
	}

	/**
	 * Asigna la informaciÃ³n a los campos de la vista
	 * 
	 * @param lectura
	 * @param esDialogo
	 */
	public void bindLectura(Lectura lectura, boolean esDialogo) {
		lblCuenta.setText(lectura.obtenerCuentaConFormato());
		if (esDialogo) {
			lblCuenta.setTextSize(18);
		}
		lblNusCliente.setText("" + lectura.Suministro);
		lblNumMedidor.setText(lectura.NumeroMedidor);
		lblEstadoLectura.setText(lectura.obtenerEstadoLectura());
		lblEstadoLectura.setBackgroundColor(lectura.getEstadoLectura()
				.getColor(lblEstadoLectura.getContext()));
		lblRuta.setText("" + lectura.Ruta);
	}
}
