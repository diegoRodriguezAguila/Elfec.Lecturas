package com.elfec.lecturas.controlador.adaptadores;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
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
	private int ordSeleccionado;
	private int selectableBgId;

	public OrdenativoAdapter(Context context, int textViewResourceId,
			List<Ordenativo> ordenativos) {
		super(context, textViewResourceId, ordenativos);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ordSeleccionado = -1;
		TypedValue outValue = new TypedValue();
		context.getTheme().resolveAttribute(R.attr.selectableItemBackground,
				outValue, true);
		selectableBgId = outValue.resourceId;
	}

	public void setSeleccionado(int seleccionado) {
		this.ordSeleccionado = seleccionado;
		this.notifyDataSetChanged();
	}

	public int getSeleccionado() {
		return this.ordSeleccionado;
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
		if (ordSeleccionado == position)
			convertView.setBackgroundColor(Color.parseColor("#95A5A6"));
		else
			convertView.setBackgroundResource(selectableBgId);
		convertView.setPadding(10, 10, 10, 10);
		viewHolder.bindOrdenativo(getItem(position));
		return convertView;
	}

}
