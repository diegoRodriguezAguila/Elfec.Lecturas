package com.lecturas.elfec.modelo.detallesresumenes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.lecturas.elfec.modelo.OrdenativoLectura;

/**
 * Es el resumen diario de Detalle de Ordenativos, listo para ser impreso
 * 
 * @author drodriguez
 *
 */
public class DetalleOrdenativos extends DetalleResumenGenerico {

	public DetalleOrdenativos() {
		titulo = "RESUMEN DETALLE LECTURAS POR ORDENATIVO - GC-702-13";
		Date fechaActual = new Date();
		DateFormat df = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
		DateFormat horaFormato = new SimpleDateFormat("HH:mm:ss",
				Locale.getDefault());
		fechaImpresion = df.format(fechaActual);
		horaImpresion = horaFormato.format(fechaActual);
	}

	/**
	 * Obtiene los resumenes de lecturas que tengan los ordenativos de los
	 * parametros en <b>VariablesDeEntorno</b> para este Resumen
	 * 
	 * @return Lista de tipo <b>ResumenLectura</b> con resumenes de las lecturas
	 *         que cumplen las condiciones
	 */
	@Override
	public List<ResumenLectura> obtenerResumenesLecturas() {
		List<ResumenLectura> resLecs = new ArrayList<ResumenLectura>();
		List<OrdenativoLectura> ordLecs = OrdenativoLectura
				.obtenerLecturasConOrdenativosParametros();
		for (OrdenativoLectura ordLectura : ordLecs) {
			ResumenLectura resLec = new ResumenLectura(ordLectura.Lectura);
			if (!resLecs.contains(resLec)) {
				List<OrdenativoLectura> ordenativos = ordLectura.Lectura
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

		}
		Collections.sort(resLecs, new Comparator<ResumenLectura>() {
			@Override
			public int compare(ResumenLectura resL1, ResumenLectura resL2) {
				return resL1.cuenta.compareTo(resL2.cuenta);
			}
		});
		return resLecs;
	}

}
