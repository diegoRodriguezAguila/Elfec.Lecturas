package com.elfec.lecturas.controlador.adaptadores.viewholders;

import org.joda.time.DateTime;

import android.view.View;
import android.widget.TextView;

import com.elfec.lecturas.R;
import com.elfec.lecturas.modelo.OrdenativoLectura;

/**
 * Viewholder para los ordenativos de una lectura
 * 
 * @author drodriguez
 *
 */
public class OrdenativoLecturaViewHolder {
	public View layoutFecha;
	private TextView lblCodigo;
	private TextView lblDescripcion;
	private TextView lblFecha;
	private TextView lblHora;

	public OrdenativoLecturaViewHolder(View convertView) {
		lblCodigo = ((TextView) convertView.findViewById(R.id.lbl_codigo));
		lblDescripcion = ((TextView) convertView
				.findViewById(R.id.lbl_descripcion));
		lblFecha = ((TextView) convertView
				.findViewById(R.id.lbl_fecha_ord_item));
		lblHora = ((TextView) convertView.findViewById(R.id.lbl_hora_ord_item));
		layoutFecha = convertView.findViewById(R.id.seccion_fecha);
	}

	/**
	 * Asigna la información a los campos de la vista
	 * 
	 * @param ordenativo
	 * @param esSeleccionado
	 */
	public void bindOrdenativoLec(OrdenativoLectura ordLec,
			boolean estaExpandido) {
		lblCodigo.setText("" + ordLec.Ordenativo.Codigo);
		lblDescripcion.setText(ordLec.Ordenativo.Descripcion);
		lblFecha.setText(new DateTime(ordLec.Fecha).toString("dd/MMM/yyyy"));
		lblHora.setText(ordLec.Hora);
		layoutFecha.setVisibility(estaExpandido ? View.VISIBLE : View.GONE);
	}
}
