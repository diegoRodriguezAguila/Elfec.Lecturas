package com.elfec.lecturas.modelo.detallesresumenes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.OrdenativoLectura;

/**
 * Es el resumen diario de Detalle de Lecturas Impedidas, listo para ser impreso
 * 
 * @author drodriguez
 *
 */
public class DetalleImpedidas extends DetalleResumenGenerico {

	public DetalleImpedidas() {
		titulo = "DETALLE LECTURAS IMPEDIDAS - GC-0815-2";
		Date fechaActual = new Date();
		DateFormat df = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
		DateFormat horaFormato = new SimpleDateFormat("HH:mm:ss",
				Locale.getDefault());
		fechaImpresion = df.format(fechaActual);
		horaImpresion = horaFormato.format(fechaActual);
	}

	@Override
	public List<ResumenLectura> obtenerResumenesLecturas() {
		ArrayList<Lectura> lecturas = (ArrayList<Lectura>) Lectura
				.obtenerLecturasPorEstado(2);// impedidas
		List<ResumenLectura> resLecs = new ArrayList<ResumenLectura>();
		for (Lectura lec : lecturas) {
			ResumenLectura resLec = new ResumenLectura(lec);
			List<OrdenativoLectura> ordenativos = lec
					.obtenerOrdenativosLectura();
			int tam = ordenativos.size();
			if (tam > 0)
				resLec.ord1 = "" + ordenativos.get(0).Ordenativo.Codigo;
			if (tam > 1)
				resLec.ord2 = "" + ordenativos.get(1).Ordenativo.Codigo;
			if (tam > 2)
				resLec.ord3 = "" + ordenativos.get(2).Ordenativo.Codigo;
			if (tam > 3)
				resLec.ord4 = "" + ordenativos.get(3).Ordenativo.Codigo;
			if (tam > 4)
				resLec.ord5 = "" + ordenativos.get(4).Ordenativo.Codigo;
			if (tam > 5)
				resLec.ord6 = "" + ordenativos.get(5).Ordenativo.Codigo;
			resLecs.add(resLec);
		}
		return resLecs;
	}

}
