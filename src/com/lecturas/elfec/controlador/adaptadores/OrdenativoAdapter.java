package com.lecturas.elfec.controlador.adaptadores;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lecturas.elfec.R;
import com.lecturas.elfec.modelo.Ordenativo;

/**
 * Sirve para crear la lista de ordenativos, es el adaptador que se encarga de ajustar la vista
 * @author drodriguez
 *
 */
public class OrdenativoAdapter extends ArrayAdapter<Ordenativo>{

	private ArrayList<Ordenativo> lOrdenativos;
    private static LayoutInflater inflater = null;
    private int ordSeleccionado;
	public OrdenativoAdapter(Context activity, int textViewResourceId,
			ArrayList<Ordenativo> ordenativos) {
		super(activity, textViewResourceId, ordenativos);
		try {
            this.lOrdenativos = ordenativos;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ordSeleccionado = -1;
        } catch (Exception e) {

        }
	}
	
	public void setSeleccionado(int seleccionado) {

	    this.ordSeleccionado = seleccionado;
	    this.notifyDataSetChanged();
	}
	
	public int getSeleccionado()
	{
		return this.ordSeleccionado;
	}
	@Override
	public int getCount() {
        return lOrdenativos.size();
    }
	
	@Override
	 public Ordenativo getItem(int position) {
        return lOrdenativos.get(position);
    }

	 @Override
    public long getItemId(int position) {
        return position;
    }
	 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        vi = inflater.inflate(R.layout.list_item_ordenativo, null);
        if(ordSeleccionado==position)
        {
        	vi.setBackgroundColor(Color.parseColor("#EBEBEB"));
        }
        Ordenativo ordenativo= lOrdenativos.get(position);
        ((TextView) vi.findViewById(R.id.img_impresora)).setText(""+ordenativo.Codigo);
        ((TextView) vi.findViewById(R.id.lbl_nombre_dispositivo)).setText(ordenativo.Descripcion);
        return vi;
    }
	 
}
