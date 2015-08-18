package com.elfec.lecturas.controlador.adaptadores;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.elfec.lecturas.helpers.ui.Animador;
import com.elfec.lecturas.modelo.OrdenativoLectura;
import com.lecturas.elfec.R;

public class OrdenativoLecturaAdapter  extends ArrayAdapter<OrdenativoLectura>{

	private ArrayList<OrdenativoLectura> lOrdenativos;
    private static LayoutInflater inflater = null;
    private int ordSeleccionado;
	public OrdenativoLecturaAdapter(Context activity, int textViewResourceId,
			ArrayList<OrdenativoLectura> ordenativos) {
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
	 public OrdenativoLectura getItem(int position) {
        return lOrdenativos.get(position);
    }

	 @Override
    public long getItemId(int position) {
        return position;
    }
	 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        vi = inflater.inflate(R.layout.list_item_ord_lec, null);
        if(ordSeleccionado==position)
        {
        	mostrarFecha(vi);
        }
        else
        {
        	esconderFecha(vi);
        }
        OrdenativoLectura ordLec= lOrdenativos.get(position);
        ((TextView) vi.findViewById(R.id.img_impresora)).setText(""+ordLec.Ordenativo.Codigo);
        ((TextView) vi.findViewById(R.id.lbl_nombre_dispositivo)).setText(ordLec.Ordenativo.Descripcion);
        String fechaOrd=new SimpleDateFormat("dd/MMM/yyyy",Locale.getDefault()).format(ordLec.Fecha);
        ((TextView) vi.findViewById(R.id.lbl_fecha_ord_item)).setText(fechaOrd);
        ((TextView) vi.findViewById(R.id.lbl_hora_ord_item)).setText(ordLec.Hora);
        return vi;
    }
	
	private void mostrarFecha(View vi)
	{
		Animador.expand(vi.findViewById(R.id.seccion_fecha));
	}
	
	private void esconderFecha(View vi)
	{
		vi.findViewById(R.id.seccion_fecha).setVisibility(View.GONE);
	}
}
