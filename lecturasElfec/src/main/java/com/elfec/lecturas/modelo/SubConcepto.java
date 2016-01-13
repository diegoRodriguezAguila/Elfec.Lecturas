package com.elfec.lecturas.modelo;

import java.math.BigDecimal;
/**
 * Esta clase se utiliza al momento de generar el aviso de cobro, cada concepto tiene subconceptos que se generan
 * @author drodriguez
 *
 */
public class SubConcepto {
	public int IdConcepto;
	public int IdSubConcepto;
	public String Descripcion;
	public BigDecimal Importe;
	public int LimiteValor;
	public int Unidades;
	public int  TipoPartida;
	public int AreaImpresion;
	
	public SubConcepto(int idConcepto, int idSubConcepto, String descripcion,
			BigDecimal importe, int limiteValor,int unidades, int tipoPartida, int areaImpresion) {
		IdConcepto = idConcepto;
		IdSubConcepto = idSubConcepto;
		Descripcion = descripcion;
		Importe = importe;
		LimiteValor=limiteValor;
		Unidades = unidades;
		TipoPartida = tipoPartida;
		AreaImpresion = areaImpresion;
	}

}
