package com.elfec.lecturas.modelo.avisocobranza;

import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 * Es un concepto con su importe, solo se utiliza para imprimir
 * @author drodriguez
 *
 */
public class ImportePorConcepto {

	public String concepto;
	public String importe;
	public int idBaseCalculo;
	
	public ImportePorConcepto()
	{
		
	}
	
	public ImportePorConcepto(String concepto, BigDecimal monto, int idBaseCalculo)
	{
		this.idBaseCalculo = idBaseCalculo;
		this.concepto = concepto;
		this.importe = (monto.setScale(2,RoundingMode.HALF_UP)).setScale(1,RoundingMode.HALF_UP).toString()+"0";
	}
}
