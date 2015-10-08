package com.elfec.lecturas.controlador.adaptadores;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.elfec.lecturas.R;
import com.elfec.lecturas.controlador.adaptadores.viewholders.DispositivoBluetoothViewHolder;

/**
 * Sirve para crear la lista de dispositivos bluetooth, es el adaptador que se
 * encarga de ajustar la vista
 * 
 * @author drodriguez
 *
 */
public class DispositivoBluetoothAdapter extends ArrayAdapter<BluetoothDevice> {
	private LayoutInflater inflater = null;
	private int dispSeleccionado;
	private int selectableBgId;

	public DispositivoBluetoothAdapter(Context context, int textViewResourceId,
			List<BluetoothDevice> dispositivos) {
		super(context, textViewResourceId, dispositivos);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dispSeleccionado = -1;
		TypedValue outValue = new TypedValue();
		context.getTheme().resolveAttribute(R.attr.selectableItemBackground,
				outValue, true);
		selectableBgId = outValue.resourceId;
	}

	public void setSeleccionado(int seleccionado) {
		this.dispSeleccionado = seleccionado;
		this.notifyDataSetChanged();
	}

	public int getSeleccionado() {
		return this.dispSeleccionado;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DispositivoBluetoothViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_dispositivo,
					parent, false);
			viewHolder = new DispositivoBluetoothViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (DispositivoBluetoothViewHolder) convertView.getTag();
		boolean dispEsSelec = dispSeleccionado == position;
		if (dispEsSelec)
			convertView.setBackgroundColor(Color.parseColor("#215b92"));
		else
			convertView.setBackgroundResource(selectableBgId);
		convertView.setPadding(10, 10, 10, 10);
		viewHolder.bindDispositivo(getItem(position), dispEsSelec);
		return convertView;
	}
}
