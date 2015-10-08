package com.elfec.lecturas.controlador.adaptadores.viewholders;

import android.view.View;
import android.widget.TextView;

import com.elfec.lecturas.R;
import com.elfec.lecturas.modelo.Ordenativo;

/**
 * Viewholder para ordenativos
 * 
 * @author drodriguez
 *
 */
public class OrdenativoViewHolder {
	private TextView lblCodigo;
	private TextView lblDescripcion;

	public OrdenativoViewHolder(View convertView) {
		lblCodigo = ((TextView) convertView.findViewById(R.id.lbl_codigo));
		lblDescripcion = ((TextView) convertView
				.findViewById(R.id.lbl_descripcion));
	}

	/**
	 * Asigna la información a los campos de la vista
	 * 
	 * @param ordenativo
	 * @param esSeleccionado
	 */
	public void bindOrdenativo(Ordenativo ordenativo) {
		lblCodigo.setText("" + ordenativo.Codigo);
		lblDescripcion.setText(ordenativo.Descripcion);
	}
}
