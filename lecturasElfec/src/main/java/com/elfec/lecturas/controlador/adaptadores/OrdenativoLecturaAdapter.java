package com.elfec.lecturas.controlador.adaptadores;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.elfec.lecturas.R;
import com.elfec.lecturas.controlador.adaptadores.viewholders.OrdenativoLecturaViewHolder;
import com.elfec.lecturas.helpers.ui.Animador;
import com.elfec.lecturas.modelo.OrdenativoLectura;

public class OrdenativoLecturaAdapter extends ArrayAdapter<OrdenativoLectura> {

	private LayoutInflater inflater = null;
	private boolean[] expandidos;

	public OrdenativoLecturaAdapter(Context context, int textViewResourceId,
			List<OrdenativoLectura> ordenativos) {
		super(context, textViewResourceId, ordenativos);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		expandidos = new boolean[ordenativos.size()];
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		OrdenativoLecturaViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_ord_lec, parent,
					false);
			viewHolder = new OrdenativoLecturaViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (OrdenativoLecturaViewHolder) convertView.getTag();
		final OrdenativoLecturaViewHolder refViewHolder = viewHolder;
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				expandidos[position] = !expandidos[position];
				if (expandidos[position])
					mostrarFecha(refViewHolder.layoutFecha);
				else
					esconderFecha(refViewHolder.layoutFecha);
			}
		});
		viewHolder.bindOrdenativoLec(getItem(position), expandidos[position]);
		return convertView;
	}

	private void mostrarFecha(View vi) {
		Animador.expand(vi);
	}

	private void esconderFecha(View vi) {
		Animador.collapse(vi);
	}
}
