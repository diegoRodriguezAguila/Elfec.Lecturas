package com.elfec.lecturas.controlador.adaptadores;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.elfec.lecturas.controlador.adaptadores.viewholders.OrdenativoViewHolder;
import com.elfec.lecturas.modelo.Ordenativo;
import com.lecturas.elfec.R;

/**
 * Sirve para crear la lista de ordenativos, es el adaptador que se encarga de
 * ajustar la vista
 * 
 * @author drodriguez
 *
 */
public class OrdenativoAdapter extends ArrayAdapter<Ordenativo> {

	private LayoutInflater inflater = null;

	public OrdenativoAdapter(Context context, int textViewResourceId,
			List<Ordenativo> ordenativos) {
		super(context, textViewResourceId, ordenativos);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		OrdenativoViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_ordenativo,
					parent, false);
			viewHolder = new OrdenativoViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (OrdenativoViewHolder) convertView.getTag();
		viewHolder.bindOrdenativo(getItem(position));
		return convertView;
	}

}
