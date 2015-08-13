package com.elfec.lecturas.controlador.adaptadores;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.elfec.lecturas.controlador.adaptadores.viewholders.LecturaViewHolder;
import com.elfec.lecturas.modelo.Lectura;
import com.lecturas.elfec.R;

/**
 * Sirve para crear la lista de lecturas, es el adaptador que se encarga de
 * ajustar la vista
 * 
 * @author drodriguez
 *
 */
public class LecturaAdapter extends ArrayAdapter<Lectura> {

	private boolean esDialogo;
	private List<Lectura> lLecturas;
	private static LayoutInflater inflater = null;

	public LecturaAdapter(Context context, int textViewResourceId,
			List<Lectura> lecturas) {
		this(context, textViewResourceId, lecturas, false);
	}

	public LecturaAdapter(Context context, int textViewResourceId,
			List<Lectura> lecturas, boolean esDialogo) {
		super(context, textViewResourceId, lecturas);
		this.esDialogo = esDialogo;
		try {
			this.lLecturas = lecturas;
			inflater = LayoutInflater.from(context);
		} catch (Exception e) {

		}
	}

	@Override
	public int getCount() {
		return lLecturas.size();
	}

	@Override
	public Lectura getItem(int position) {
		return lLecturas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LecturaViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_lectura, parent,
					false);
			viewHolder = new LecturaViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (LecturaViewHolder) convertView.getTag();
		viewHolder.bindLectura(lLecturas.get(position), esDialogo);
		return convertView;
	}

}
