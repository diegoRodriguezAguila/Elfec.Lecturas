package com.lecturas.elfec.modelo.detallesresumenes;

import com.lecturas.elfec.modelo.Lectura;
import com.lecturas.elfec.modelo.MedidorEntreLineas;

/**
 * Es uno de los elementos de los resumenes de Detalles
 * @author drodriguez
 *
 */
public class ResumenLectura {
	public String cuenta;
	public String nus;
	public String numMedidor;
	public String lecturaActiva;
	public String demanda;
	public String reactiva;
	public String ord1;
	public String ord2;
	public String ord3;
	public String ord4;
	public String ord5;
	public String ord6;
	public String estado;
	public boolean leePotencia;
	public boolean leeReactiva;
	public ResumenLectura()
	{
	}
	
	public ResumenLectura(Lectura lec) {
		cuenta = lec.obtenerCuentaConFormato();
		nus = ""+lec.NusCliente;
		numMedidor = lec.NumeroMedidor;
		lecturaActiva = ""+lec.LecturaNueva;
		if(lec.PotenciaLectura!=null)
		{
			demanda = lec.PotenciaLectura.LecturaNuevaPotencia!=null?lec.PotenciaLectura.LecturaNuevaPotencia.toString():" ";
			reactiva = ""+(lec.PotenciaLectura.Reactiva!=null?lec.PotenciaLectura.Reactiva:" ");
		}
		estado = ""+lec.getEstadoLectura().getEstadoEntero();
		leePotencia = lec.LeePotencia==1;
		leeReactiva = lec.LeeReactiva==1;
	}
	
	public ResumenLectura(MedidorEntreLineas lecEL)
	{
		cuenta = " ";
		nus = " ";
		numMedidor = lecEL.NumeroMedidor;
		lecturaActiva = ""+lecEL.LecturaNueva;
		demanda = lecEL.LecturaPotencia!=null?lecEL.LecturaPotencia.toString():" ";
		reactiva = lecEL.Reactiva!=null?lecEL.Reactiva.toString():" ";
		estado = "1";
		leePotencia = lecEL.LecturaPotencia!=null;
		leeReactiva = lecEL.Reactiva!=null;
	}
	
	@Override
	public boolean equals(Object rl)
	{
		boolean resp = false;
		if(rl != null && rl instanceof ResumenLectura)
		{
			ResumenLectura resLec = (ResumenLectura)rl;
			resp = this.cuenta.equals(resLec.cuenta);
		}
		return resp;
	}
	
	

}
