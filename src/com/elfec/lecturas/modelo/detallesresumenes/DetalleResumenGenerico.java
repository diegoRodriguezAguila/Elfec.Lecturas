package com.elfec.lecturas.modelo.detallesresumenes;

import java.util.List;

/**
 * Clase generica de resumen diario, contiene el metodo para obtener el imprimible ya implementado,
 * y se debe heredar de ella si en el futuro se tiene mas resumenes diarios, que cumplan con el formato:
 * Cuenta, NUS, N. Medidor, Lectura A. , Demanda, Reactiva, Or.1, Or.2, Or.3, Or.4, Or.5, Or.6, Estado
 * @author drodriguez
 *
 */
public abstract class DetalleResumenGenerico 
{
	/**
	 * Titulo de la papeleta de resumen
	 */
	public String titulo;
	/**
	 * Fecha de impresión de la papeleta de resumen
	 */
	public String fechaImpresion;
	/**
	 * Hora de impresión de la papeleta de resumen
	 */
	public String horaImpresion;
	/**
	 * El numero de lecturas que entra en una hoja
	 */
	private final int LECTURAS_POR_PAGINA = 25;
	
	/**
	 * Obtiene una cadena en formato CPCL listo para ser enviada a la impresora para imprimir,
	 * Para impresoras RW 420
	 * @return
	 */
	public final String obtenerImprimible()
	{
		List<ResumenLectura> resumenesLecturas = obtenerResumenesLecturas();
		int cantLecs = resumenesLecturas.size();
		int numPags = cantLecs/LECTURAS_POR_PAGINA;
		int maxPags = (cantLecs/LECTURAS_POR_PAGINA)+(cantLecs%LECTURAS_POR_PAGINA!=0?1:0);
		int pagActual;
		String imprimible = "";
		for (pagActual=0;pagActual < numPags ;pagActual++)
		{
			imprimible+=imprimirPagina(resumenesLecturas.subList((pagActual*LECTURAS_POR_PAGINA), ((pagActual+1)*LECTURAS_POR_PAGINA)),(pagActual+1), maxPags);
		}
		if(cantLecs%LECTURAS_POR_PAGINA!=0)
		{
			imprimible+=imprimirPagina(resumenesLecturas.subList((pagActual*LECTURAS_POR_PAGINA), cantLecs),(pagActual+1), maxPags);
		}
		return imprimible;
	}
	/**
	 * Obtiene una cadena en formato CPCL listo de una hoja de 25 lecturas para imprimir,
	 * Para impresoras RW 420
	 * @return
	 */
	private final String imprimirPagina(List<ResumenLectura> resumenesLecturas, int pagActual, int numPags)
	{
		int cantLecturas = resumenesLecturas.size();
		double margenIzqCuenta = 0;
		double margenCache = margenIzqCuenta;
		double separacionEntreLecturas = 0.35;
		double separacionTitulos = 0.5;
		double margenIzqTitulo = 2.5;
		double margenInferior = 10.3;
		String imprimible =
		 "! 0 1200 1723 1223 1\r\n" + "ON-FEED IGNORE\r\n"+"IN-CENTIMETERS\r\n" +
				 "T270 SEGOEUI7.CPF 0 "+margenInferior+" "+(margenIzqTitulo-2)+" "+pagActual+"/"+numPags+"\r\n" +
				 "T270 SEGOEUI7.CPF 0 "+margenInferior+" "+(margenIzqTitulo-1)+" ("+cantLecturas+")\r\n" +
				 "T270 SEGOEUIB.CPF 0 "+margenInferior+" "+margenIzqTitulo+" "+titulo+"\r\n" +
				 "T270 SEGOEUI7.CPF 0 "+margenInferior+" "+(margenIzqTitulo+=7)+" "+fechaImpresion+"\r\n" +
				 "T270 SEGOEUI7.CPF 0 "+margenInferior+" "+(margenIzqTitulo+=1.5)+" "+horaImpresion+"\r\n" +
				 "T270 SEGOEUI8.CPF 0 "+(margenInferior-=separacionTitulos)+" "+margenIzqCuenta+" Cuenta\r\n" ;
		imprimible+=
				 "T270 SEGOEUI8.CPF 0 "+margenInferior+" "+(margenCache+=2.1)+" N.U.S\r\n" +
				 "T270 SEGOEUI8.CPF 0 "+margenInferior+" "+(margenCache+=1.05)+" N. Medidor\r\n" +
				 "T270 SEGOEUI8.CPF 0 "+margenInferior+" "+(margenCache+=1.9)+" Lectura A.\r\n" +
				 "T270 SEGOEUI8.CPF 0 "+margenInferior+" "+(margenCache+=1.5)+" Demanda\r\n" +
				 "T270 SEGOEUI8.CPF 0 "+margenInferior+" "+(margenCache+=1.5)+" Reactiva\r\n" +
				 "T270 SEGOEUI8.CPF 0 "+margenInferior+" "+(margenCache+=1.6)+" Or.1\r\n" +
				 "T270 SEGOEUI8.CPF 0 "+margenInferior+" "+(margenCache+=0.7)+" Or.2\r\n" +
				 "T270 SEGOEUI8.CPF 0 "+margenInferior+" "+(margenCache+=0.7)+" Or.3\r\n" +
				 "T270 SEGOEUI8.CPF 0 "+margenInferior+" "+(margenCache+=0.7)+" Or.4\r\n" +
				 "T270 SEGOEUI8.CPF 0 "+margenInferior+" "+(margenCache+=0.7)+" Or.5\r\n" +
				 "T270 SEGOEUI8.CPF 0 "+margenInferior+" "+(margenCache+=0.7)+" Or.6\r\n" +
				 "T270 SEGOEUI8.CPF 0 "+margenInferior+" "+(margenCache+=0.8)+" Est.\r\n" ;
		margenInferior-=separacionTitulos;
		for (ResumenLectura resLec : resumenesLecturas) 
		{
			margenCache = margenIzqCuenta;
			imprimible+=
					"T270 SEGOEUI7.CPF 0 "+margenInferior+" "+margenCache+" "+resLec.cuenta+"\r\n" +
					"RIGHT "+((margenCache+=2)+0.8)+"\r\n" +
					"T270 SEGOEUI7.CPF 0 "+margenInferior+" "+(margenCache)+" "+resLec.nus+"\r\n" +
					"RIGHT "+((margenCache+=1.05)+1.4)+"\r\n" +
					"T270 SEGOEUI7.CPF 0 "+margenInferior+" "+(margenCache)+" "+resLec.numMedidor+"\r\n" +
					"RIGHT "+((margenCache+=2)+1.1)+"\r\n" +
					"T270 SEGOEUI7.CPF 0 "+margenInferior+" "+(margenCache)+" "+resLec.lecturaActiva+"\r\n" +
					"LEFT\r\n";
			margenCache+=1.5;
			if(resLec.leePotencia)
			{
				imprimible+="RIGHT "+((margenCache)+0.8)+"\r\n" +
				"T270 SEGOEUI7.CPF 0 "+margenInferior+" "+(margenCache)+" "+resLec.demanda+"\r\n" +
				"LEFT\r\n";
			}
			margenCache+=1.5;
			if(resLec.leeReactiva)
			{
				imprimible+="RIGHT "+((margenCache)+0.8)+"\r\n" +
				"T270 SEGOEUI7.CPF 0 "+margenInferior+" "+(margenCache)+" "+resLec.reactiva+"\r\n" +
				"LEFT\r\n";
			}
			margenCache+=1.6;
					 if(resLec.ord1!=null)
					 {
						 imprimible+="CENTER "+((margenCache)+0.5)+"\r\n" +
						 "T270 SEGOEUI7.CPF 0 "+margenInferior+" "+margenCache+" "+resLec.ord1+"\r\n" +
						 "LEFT\r\n";
					 }
		    margenCache+=0.7;
					 if(resLec.ord2!=null)
					 {
						 imprimible+="CENTER "+((margenCache)+0.5)+"\r\n" +
						 "T270 SEGOEUI7.CPF 0 "+margenInferior+" "+margenCache+" "+resLec.ord2+"\r\n" +
						 "LEFT\r\n";
					 }
			margenCache+=0.7;
					 if(resLec.ord3!=null)
					 {
						 imprimible+="CENTER "+((margenCache)+0.5)+"\r\n" +
						"T270 SEGOEUI7.CPF 0 "+margenInferior+" "+margenCache+" "+resLec.ord3+"\r\n" +
						"LEFT\r\n";		 
					 }
			margenCache+=0.7;
					 if(resLec.ord4!=null)
					 {
						 imprimible+="CENTER "+((margenCache)+0.5)+"\r\n" +
						 "T270 SEGOEUI7.CPF 0 "+margenInferior+" "+margenCache+" "+resLec.ord4+"\r\n"  +
						 "LEFT\r\n";
					 }
			margenCache+=0.7;		 
					 if(resLec.ord5!=null)
					 {
						 imprimible+="CENTER "+((margenCache)+0.5)+"\r\n" +
						 "T270 SEGOEUI7.CPF 0 "+margenInferior+" "+margenCache+" "+resLec.ord5+"\r\n" +
						 "LEFT\r\n";
					 }
			margenCache+=0.7;
					 if(resLec.ord6!=null)
					 {
						 imprimible+="CENTER "+((margenCache)+0.5)+"\r\n" +
						 "T270 SEGOEUI7.CPF 0 "+margenInferior+" "+margenCache+" "+resLec.ord6+"\r\n" +
						 "LEFT\r\n";
					 }
		    margenCache+=0.8;
		    imprimible+="CENTER "+(margenCache+0.5)+"\r\n";
		    imprimible+="T270 SEGOEUI7.CPF 0 "+margenInferior+" "+(margenCache)+" "+resLec.estado+"\r\n" ;
		    imprimible+="LEFT\r\n";
			margenInferior-=separacionEntreLecturas;
		}
		
		imprimible+=
		"PRINT\r\n";
		return imprimible;
	}
	
	/**
	 * Las clases que hereden el resumen generico deben implementar un metodo para obtener las lecturas del resumen en orden de cuentas
	 * @return
	 */
	public abstract List<ResumenLectura> obtenerResumenesLecturas();
}
