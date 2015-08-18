package com.elfec.lecturas.modelo.avisocobranza;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.elfec.lecturas.helpers.ManejadorImpresora;
import com.elfec.lecturas.modelo.ConceptoLectura;
import com.elfec.lecturas.modelo.EvolucionConsumo;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.excepciones.ImpresoraPredefinidaNoAsignadaExcepcion;
import com.elfec.lecturas.settings.VariablesDeEntorno;

/**
 * El aviso de cobranza listo para ser impreso
 * @author drodriguez
 *
 */
public class AvisoCobranza {

	//--------------------DATOS LECTURA-----------------------------------
	private int consumo;
	private int lecturaActual;
	private int lecturaAnterior;
	private String multLectura;
	private int potenciaContratada;
	private int potenciaFacturada;
	private String periodo;
	private String fechaLecturaActual;
	private String fechaLecturaAnterior;
	
	//--------------------DATOS CLIENTE-----------------------------------
	private String nus;
	private String cuenta;
	private String nombreCliente;
	private String nitOCi;
	private String direccion;
	private String numMedidor;
	private String fechaEmision;
	private String categoria;
	private String pasibleDeCorteDesde;
	
	//--------------------DATOS DEUDAS-----------------------------------
	private String fechaDeudasAcumuladas;
	private String mesesAdeudadosAl;
	private String facturaActual;
	private String facturasAdeudadas;
	private String totalDeuda;
	
	//--------------------DATOS GENERALES AVISO DE COBRANZA--------------
	private ArrayList<MesConsumo> evolucionConsumo;
	private ArrayList<ImportePorConcepto> importesPorConcepto;
	private String totalSujetoCreditoFiscal;
	private String totalFacturaActual;
	private String montoLiteral;
	private String fechaVencimiento;
	private String fechaProximaEmision;
	
	public AvisoCobranza(Lectura lectura)
	{
		asignarEvolucionConsumo(lectura.obtenerEvolucionConsumoLectura(),lectura.Anio,lectura.Mes,lectura.ConsumoFacturado);
		String categoriaLectura = lectura.CategoriaRecategorizada==null?lectura.Categoria:lectura.CategoriaRecategorizada;
		asignarConceptos((ArrayList<ConceptoLectura>)lectura.obtenerConceptosLecturaOrdenImpresion(),categoriaLectura);
		agruparConceptosSiExcedenMaximo();
		setDatosLectura(lectura);
		setDatosCliente(lectura);
		setDatosDeudas(lectura);
		DateFormat df = new SimpleDateFormat("dd/MM/yy",Locale.getDefault());
		fechaVencimiento = df.format(lectura.FechaVencimiento);
		fechaProximaEmision = df.format(lectura.FechaProximaEmision);
		totalFacturaActual = lectura.ImporteTotal.toString()+"0";
		montoLiteral = getMontoLiteral(lectura.ImporteTotal);
	}

	/**
	 * Obtiene todos los parametros del aviso de cobranza y crea una cadena en lenguaje CPCL para poder ser impresa.
	 * Analisa las variables de entorno para saber que tipo de impresora se seleccionó. Si no se seleccionó impresora
	 * lanza la exepcion 
	 * las iMZ320 y RW420deberian tener el prefijo asignado en las VariablesDeEntorno en el nombre de dispositivo bluetooth
	 * @throws ImpresoraPredefinidaNoAsignadaExcepcion
	 * @return aviso de cobranza en lenguaje CPCL
	 */
	public String obtenerImprimible() throws ImpresoraPredefinidaNoAsignadaExcepcion
	{
		if(ManejadorImpresora.impresoraPredefinidaFueAsignada())
		{
			if(ManejadorImpresora.obtenerImpresoraPredefinida().getName().contains(VariablesDeEntorno.prefijoImpresorasiMZ320))
			{
				return obtenerImprimibleVertical();
			}
			else
			{
				return obtenerImprimibleHorizontal();
			}
		}
		else
		{
			throw new ImpresoraPredefinidaNoAsignadaExcepcion();
		}
	}

	/**
	 * Obtiene todos los parametros del aviso de cobranza y crea una cadena en lenguaje CPCL para poder ser impresa.
	 * Para impresoras RW 420
	 * @return aviso de cobranza en lenguaje CPCL
	 */
	public String obtenerImprimibleHorizontal()
	{
		double margenIzq = 2.2;
		double dif1 = 0.35;
		double separacionLectura = 2.1;
		double margenIzqDatosCliente = 7.1;
		double margenInferior =8.85;
		String imprimible =
		 "! 0 1200 1723 1223 1\r\n" + "ON-FEED IGNORE\r\n"+"IN-CENTIMETERS\r\n" +
				"T270 7 0 "+margenInferior+" "+margenIzq+" "+consumo+"\r\n" +
				"T270 7 0 "+(margenInferior-dif1)+" "+margenIzq+" "+lecturaActual+"\r\n" +
				"T270 7 0 "+(margenInferior-(dif1*2))+" "+margenIzq+" "+lecturaAnterior+"\r\n" +
			    "T270 7 0 "+(margenInferior-(dif1*3))+" "+margenIzq+" "+multLectura+"\r\n" +
				"T270 7 0 "+(margenInferior-(dif1*4))+" "+margenIzq+" "+potenciaContratada+"\r\n" +
				"T270 7 0 "+margenInferior+" "+(margenIzq+separacionLectura)+" "+periodo+"\r\n" +
				"T270 7 0 "+(margenInferior-dif1)+" "+(margenIzq+separacionLectura-0.3)+" "+fechaLecturaActual+"\r\n" +
				"T270 7 0 "+(margenInferior-(dif1*2))+" "+(margenIzq+separacionLectura-0.25)+" "+fechaLecturaAnterior+"\r\n" +
				"T270 7 0 "+(margenInferior-(dif1*4))+" "+(margenIzq+separacionLectura+0.25)+" "+potenciaFacturada+"\r\n" +
				"T270 7 0 "+margenInferior+" "+margenIzqDatosCliente+" "+nombreCliente+"\r\n" +
				"T270 7 0 "+(margenInferior-dif1)+" "+margenIzqDatosCliente+" "+nitOCi+"\r\n" +
				"T270 7 0 "+(margenInferior-(dif1*2))+" "+margenIzqDatosCliente+" "+direccion+"\r\n" +
				"T270 7 0 "+(margenInferior-(dif1*3))+" "+(margenIzqDatosCliente+0.2)+" "+numMedidor+"\r\n" +
				"T270 7 0 "+(margenInferior-(dif1*4))+" "+(margenIzqDatosCliente+0.7)+" "+fechaEmision+"\r\n" +
				"T270 7 0 "+(margenInferior-(dif1*3))+" 10.9 "+categoria+"\r\n" +
				"T270 7 0 "+(margenInferior-(dif1*4))+" 12.2 "+pasibleDeCorteDesde+"\r\n";
		
		imprimible+="SETMAG 2 2\r\n";
		imprimible+="T270 7 0 "+(margenInferior+0.7)+" "+(margenIzqDatosCliente-0.5)+" "+nus+"\r\n";
		imprimible+="SETMAG 0 0\r\n";
		imprimible+="T270 SEGOEUIB.CPF 0 "+(margenInferior+0.6)+" "+(margenIzqDatosCliente+3.6)+" "+cuenta+"\r\n";
		double margenIzqEvConsumo = 0.3;
		double difEvConsAsteriscos = 1.2;
		double difEvConsConsumoMes = 4.5;
		double margenInferiorEvConsumo=6.8;
		double i =0;
		for(MesConsumo mesConsumo : evolucionConsumo)
		{
			imprimible+="T270 SEGOEUI7.CPF 0 "+(margenInferiorEvConsumo+i)+" "+margenIzqEvConsumo+" "+mesConsumo.fechaMes+"\r\n";
			imprimible+="T270 SEGOEUI7.CPF 0 "+(margenInferiorEvConsumo+i)+" "+(margenIzqEvConsumo+difEvConsAsteriscos)+" "+mesConsumo.asteriscosConsumo+"\r\n";
			imprimible+="T270 SEGOEUI7.CPF 0 "+(margenInferiorEvConsumo+i)+" "+(margenIzqEvConsumo+difEvConsConsumoMes)+" "+mesConsumo.consumo+"\r\n";
			i-=0.28;
		}
		double margenIzqConceptos = 6.5;
		double difImporteConcepto = 12.5;
		double margenInferiorConceptos = 7;
		i = 0;
		for(ImportePorConcepto impConcepto : importesPorConcepto)
		{
			imprimible+="T270 7 0 "+(margenInferiorConceptos+i)+" "+margenIzqConceptos+" "+impConcepto.concepto+"\r\n";
			imprimible+="RIGHT "+difImporteConcepto+"\r\n";
			imprimible+="T270 7 0 "+(margenInferiorConceptos+i)+" "+margenIzqConceptos+" "+impConcepto.importe+"\r\n";
			imprimible+="LEFT\r\n";
			i-=0.4;
		}
		double margenIzqTotalesFactura = 13;
		double margenInfTotalesFactura = 1.95;
		imprimible+="RIGHT "+margenIzqTotalesFactura+"\r\n";
		imprimible+="T270 7 0 "+margenInfTotalesFactura+" "+margenIzqTotalesFactura+" "+totalSujetoCreditoFiscal+"\r\n";
		imprimible+="T270 7 0 "+(margenInfTotalesFactura-0.3)+" "+margenIzqTotalesFactura+" "+totalFacturaActual+"\r\n";
		imprimible+="LEFT\r\n";
		double margenIzqMontoLiteral = 6.5;
		double margenInfMontoLiteral = 1.25;
		imprimible+="T270 7 0 "+margenInfMontoLiteral+" "+margenIzqMontoLiteral+" "+getMontoLiteralConGuiones(montoLiteral,45)+"\r\n";
		imprimible+="T270 7 0 "+(margenInfMontoLiteral-0.3)+" "+margenIzqMontoLiteral+" Bolivianos.\r\n";
		
		double margenIzqFechaVencimiento = 8.3;
		double difProxEmision = 3.9;
		double margenInfFechaVencimiento = 0.5;
		imprimible+="T270 7 0 "+margenInfFechaVencimiento+" "+margenIzqFechaVencimiento+" "+fechaVencimiento+"\r\n";
		imprimible+="T270 7 0 "+margenInfFechaVencimiento+" "+(margenIzqFechaVencimiento+difProxEmision)+" "+fechaProximaEmision+"\r\n";
		
		double margenIzqFechaDeuda = 3;
		double margenInfFechaDeuda = 3;
		imprimible+="T270 7 0 "+margenInfFechaDeuda+" "+margenIzqFechaDeuda+" "+fechaDeudasAcumuladas+"\r\n";
		
		double margenIzqMesesAdeudados = 0.1;
		double margenInfMesesAdeudados = 2.7;
		imprimible+="T270 7 0 "+margenInfMesesAdeudados+" "+margenIzqMesesAdeudados+" "+mesesAdeudadosAl+"\r\n";
		
		double margenIzqFacturaActual=4.5;
		double margenInfFacturaActual = 2.5;
		double difInf = 0.35;
		imprimible+="RIGHT "+margenIzqFacturaActual+"\r\n";
		imprimible+="T270 7 0 "+margenInfFacturaActual+" 0 "+facturaActual+"\r\n";
		imprimible+="T270 7 0 "+(margenInfFacturaActual-difInf) +" 0 "+facturasAdeudadas+"\r\n";
		imprimible+="T270 7 0 "+(margenInfFacturaActual-(difInf*2))+" 0 "+totalDeuda+"\r\n";
		imprimible+="LEFT\r\n";
		imprimible+="CENTER 5.5\r\n";
		imprimible+="ML 0.27\r\n";
		imprimible+="T270 7 0 1.15 "+margenIzqMesesAdeudados+" "+ponerSaltosDeLineaDeSerNecesario(VariablesDeEntorno.observacionesORecomendaciones, 35)+"\r\n";
		imprimible+="ENDML\r\n";
		imprimible+="LEFT\r\n";
		imprimible+="PRINT\r\n";
		return imprimible;
	}	
	
	/**
	 * Obtiene todos los parametros del aviso de cobranza y crea una cadena en lenguaje CPCL para poder ser impresa en una impresora
	 * verticalmente con ancho de hoja de 3 pulgadas.
	 * Para impresoras iMZ320 o similares.
	 * @return aviso de cobranza en lenguaje CPCL
	 */
	public String obtenerImprimibleVertical()
	{
		double margenIzq = 0.5;
		double margenSuperior = 0.5;
		double dif = 0.35;
		double margenSeparador = 0.4;
		int colImportesCptos = 7;
		double dobleColumna = 2.9;
		double margenAsteriscos = 1.5;
		double margenConsumos = 6.5;
		String imprimible =
				"PCX "+margenIzq+" "+margenSuperior+" !<ELFEC_LO.PCX\r\n" +
				"T 0 0 "+(margenIzq+0.4)+" "+(margenSuperior+=1.33)+" Empresa de Luz y Fuerza Electrica Cochabamba S.A.\r\n" +
				"T SEGOEUI1.CPF 0 "+(margenIzq+0.4)+" "+(margenSuperior+=0.2)+" AVISO DE COBRANZA\r\n"+
				"T SEGOEUI8.CPF 0 "+margenIzq+" "+(margenSuperior+=1.2)+" NUS:\r\n"+
				"T SEGOEUIB.CPF 0 "+(margenIzq+0.7)+" "+margenSuperior+" "+nus+"\r\n"+
				"T SEGOEUI8.CPF 0 "+(margenIzq+dobleColumna)+" "+(margenSuperior)+" CUENTA:\r\n"+
				"T SEGOEUIB.CPF 0 "+(margenIzq+dobleColumna+1.25)+" "+margenSuperior+" "+cuenta+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=0.8)+" CLIENTE:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+0.95)+" "+(margenSuperior)+" "+nombreCliente+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" N.I.T./C.I.:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+1)+" "+(margenSuperior)+" "+nitOCi+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" DIRECCION:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+1.25)+" "+(margenSuperior)+" "+direccion+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" Nro. MEDIDOR:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+1.5)+" "+(margenSuperior)+" "+numMedidor+"\r\n"+
				"T SEGOEUI6.CPF 0 "+(margenIzq+dobleColumna)+" "+(margenSuperior)+" CATEGORIA:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+dobleColumna+1.3)+" "+(margenSuperior)+" "+categoria+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" FECHA EMISION:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+1.7)+" "+(margenSuperior)+" "+fechaEmision+"\r\n"+
				"T SEGOEUI6.CPF 0 "+(margenIzq+dobleColumna)+" "+(margenSuperior)+" PASIBLE A CORTE DESDE:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+dobleColumna+2.5)+" "+(margenSuperior)+" "+pasibleDeCorteDesde+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=margenSeparador)+" __________________________________________________________________________\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=(margenSeparador+0.1))+" CONSUMO:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+1.25)+" "+margenSuperior+" "+consumo+"\r\n"+
				"T SEGOEUI6.CPF 0 "+(margenIzq+dobleColumna)+" "+margenSuperior+" PERIODO:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+dobleColumna+1.05)+" "+margenSuperior+" "+periodo+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" LECTURA ACTUAL:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+1.85)+" "+margenSuperior+" "+lecturaActual+"\r\n"+
				"T SEGOEUI6.CPF 0 "+(margenIzq+dobleColumna)+" "+margenSuperior+" FECHA:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+dobleColumna+0.8)+" "+margenSuperior+" "+fechaLecturaActual+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" LECTURA ANTERIOR:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+2.1)+" "+margenSuperior+" "+lecturaAnterior+"\r\n"+
				"T SEGOEUI6.CPF 0 "+(margenIzq+dobleColumna)+" "+margenSuperior+" FECHA:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+dobleColumna+0.8)+" "+margenSuperior+" "+fechaLecturaAnterior+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" MULT. LECTURA:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+1.65)+" "+margenSuperior+" "+multLectura+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" POT.CONTRATADA:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+2)+" "+margenSuperior+" "+potenciaContratada+"\r\n"+
				"T SEGOEUI6.CPF 0 "+(margenIzq+dobleColumna)+" "+margenSuperior+" FACTURADA:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+dobleColumna+1.35)+" "+margenSuperior+" "+potenciaFacturada+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=margenSeparador)+" __________________________________________________________________________\r\n";
				margenSuperior+=0.15;		
				for(ImportePorConcepto impConcepto : importesPorConcepto)
				{
					imprimible+="T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" "+impConcepto.concepto+"\r\n"+
					"RIGHT "+colImportesCptos+"\r\n"+
					"T SEGOEUI6.CPF 0 0 "+margenSuperior+" "+impConcepto.importe+"\r\n"+
					"LEFT\r\n";
				}
				imprimible+=
				"T SEGOEUIS.CPF 0 "+margenIzq+" "+(margenSuperior+=margenSeparador+0.25)+" TOTAL SUJETO CREDITO FISCAL:\r\n"+
				"RIGHT "+colImportesCptos+"\r\n"+
				"T SEGOEUIB.CPF 0 0 "+(margenSuperior-0.1)+" "+totalSujetoCreditoFiscal+"\r\n"+
				"LEFT\r\n"+
				"T SEGOEUIS.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" TOTAL FACTURA ACTUAL:\r\n"+
				"RIGHT "+colImportesCptos+"\r\n"+
				"T SEGOEUIB.CPF 0 0 "+(margenSuperior-0.1)+" "+totalFacturaActual+"\r\n"+
				"LEFT\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" SON:\r\n"+
				"ML "+(dif-(0.1))+"\r\n";
		        String montoLiteralConGuiones = getMontoLiteralConGuiones(montoLiteral,50);
				imprimible+="T SEGOEUI6.CPF 0 "+(margenIzq+0.65)+" "+(margenSuperior)+" "+ponerSaltosDeLineaDeSerNecesario(montoLiteralConGuiones,50)+"\r\n"+
				"ENDML\r\n";
				int numFilasLiteral=((montoLiteralConGuiones.length()/50)+(montoLiteralConGuiones.length()%50==0?0:1));
				imprimible+="T SEGOEUI6.CPF 0 "+(margenIzq+0.65)+" "+(margenSuperior+=((dif-0.1)*numFilasLiteral))+" Bolivianos.\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" FECHA VENCIMIENTO:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+2.25)+" "+(margenSuperior)+" "+fechaVencimiento+"\r\n"+
				"T SEGOEUI6.CPF 0 "+(margenIzq+(dobleColumna+0.6))+" "+(margenSuperior)+" PROXIMA EMISION:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+dobleColumna+2.6)+" "+(margenSuperior)+" "+fechaProximaEmision+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=margenSeparador)+" __________________________________________________________________________\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=(margenSeparador+0.1))+" MESES ADEUDADOS AL:\r\n"+
				"T SEGOEUIS.CPF 0 "+(margenIzq+2.35)+" "+margenSuperior+" "+fechaDeudasAcumuladas+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" "+mesesAdeudadosAl+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=margenSeparador+0.15)+" FACTURA ACTUAL:\r\n"+
				"RIGHT "+colImportesCptos+"\r\n"+
				"T SEGOEUIS.CPF 0 0 "+margenSuperior+" "+facturaActual+"\r\n"+
				"LEFT\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" FACTURAS ADEUDADAS:\r\n"+
				"RIGHT "+colImportesCptos+"\r\n"+
				"T SEGOEUIS.CPF 0 0 "+margenSuperior+" "+facturasAdeudadas+"\r\n"+
				"LEFT\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" TOTAL DEUDA:\r\n"+
				"RIGHT "+colImportesCptos+"\r\n"+
				"T SEGOEUIS.CPF 0 0 "+margenSuperior+" "+totalDeuda+"\r\n"+
				"LEFT\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=margenSeparador)+" __________________________________________________________________________\r\n"+
				"CENTER\r\n"+
				"T SEGOEUIS.CPF 0 "+margenIzq+" "+(margenSuperior+=(margenSeparador+0.1))+" EVOLUCION DEL CONSUMO\r\n"+
				"LEFT\r\n";
				margenSuperior+=0.1;
				for(MesConsumo mesConsumo : evolucionConsumo)
				{
					imprimible+=
							"T SEGOEUI6.CPF 0 "+(margenIzq+0.3)+" "+(margenSuperior+=(dif-0.1))+" "+mesConsumo.fechaMes+"\r\n"+
							"T SEGOEUI6.CPF 0 "+(margenIzq+0.3+margenAsteriscos)+" "+(margenSuperior)+" "+mesConsumo.obtenerAsteriscosProporcionNueva(40)+"\r\n"+
							"RIGHT "+margenConsumos+"\r\n"+		
							"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior)+" "+mesConsumo.consumo+"\r\n"+
							"LEFT\r\n";
				}
				imprimible+=
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=margenSeparador)+" __________________________________________________________________________\r\n"+
				"CENTER\r\n"+
				"T SEGOEUIS.CPF 0 "+margenIzq+" "+(margenSuperior+=(margenSeparador+0.1))+" OBSERVACIONES O RECOMENDACIONES\r\n"+
				"ML "+(dif-(0.1))+"\r\n"+
				"T SEGOEUI6.CPF 0 "+margenIzq+" "+(margenSuperior+=dif)+" "+ponerSaltosDeLineaDeSerNecesario(VariablesDeEntorno.observacionesORecomendaciones, 36)+
				"ENDML\r\n"+
				"LEFT\r\n"+
		"PRINT\r\n";
		int tamEnDots = ((int)((margenSuperior+2)*80.0));
		String tamanioAviso = "! 0 650 "+tamEnDots+" "+tamEnDots+" 1\r\n" + "ON-FEED IGNORE\r\n"+"IN-CENTIMETERS\r\n";
		return (tamanioAviso+imprimible);
	}	
	/**
	 * Pone saltos de linea \r\n a las cadenas
	 * @param cadena , la cadena a la que se pondrán los saltos de linea
	 * @param maximo , maximo numero de caracteres por linea
	 * @return
	 */
	private String ponerSaltosDeLineaDeSerNecesario(String cadena, int maximo)
	{
		if(cadena.length()>maximo)
		{
			for (int i = maximo; i > 0; i--) 
			{
				if(cadena.charAt(i)==' ')
				{
					return cadena.substring(0, i)+"\r\n"+ponerSaltosDeLineaDeSerNecesario(cadena.substring(i+1,cadena.length()),maximo);
				}
			}
		}
		return cadena;
	}
	
	/**
	 * Asigna los datos correspondientes de la lectura, los datos monetarios los redondea y convierte a cadena en 
	 * el formato establecido por la empresa, convierte las fechas en cadenas listas para ser imprimidas
	 * con el formato correcto.
	 * @param lectura
	 */
	private void setDatosLectura(Lectura lectura)
	{
		consumo = lectura.ConsumoFacturado;
		lecturaActual = lectura.LecturaNueva;
		lecturaAnterior = lectura.LecturaAnterior;
		multLectura = lectura.FactorMultiplicador.setScale(2,RoundingMode.HALF_UP).toString();
		potenciaContratada = lectura.PotenciaContratada;
		potenciaFacturada = lectura.PotenciaLectura!=null?lectura.PotenciaLectura.ConsumoFacturado:lectura.PotenciaFacturada;
		DateFormat df = new SimpleDateFormat("dd/MM/yy",Locale.getDefault());
		fechaLecturaActual = df.format(lectura.FechaLecturaActual);
		fechaLecturaAnterior = df.format(lectura.FechaLecturaAnterior);
		periodo = getPeriodo(lectura.Mes,lectura.Anio);
	}
	
	private String[] meses = {"ENE","FEB","MAR","ABR","MAY", "JUN", "JUl", "AGO", "SEP", "OCT", "NOV", "DIC"};
	private String getPeriodo(int mes, int anio)
	{
		return meses[mes-1]+"/"+anio;
	}
	
	/**
	 * Asigna los datos correspondientes del cliente, convierte las fechas en cadenas listas para ser imprimidas
	 * con el formato correcto.
	 * @param lectura
	 */
	private void setDatosCliente(Lectura lectura)
	{
		nus = ""+lectura.NusCliente;
		cuenta = lectura.obtenerCuentaConFormato();
		nombreCliente = lectura.NombreCliente;
		nitOCi = ""+lectura.NitCliente;
		direccion = lectura.DireccionSuministro;
		numMedidor = lectura.NumeroMedidor;
		DateFormat df = new SimpleDateFormat("dd/MM/yy",Locale.getDefault());
		fechaEmision = df.format(new Date());
		categoria = lectura.SiglaCategoria;
		pasibleDeCorteDesde = lectura.FechaPosibleCorte==null?"No aplica.": df.format(lectura.FechaPosibleCorte);
	}
	
	/**
	 * Asigna los datos correspondientes a las deudas que tiene el cliente. Meses adeudados, etc. y se les
	 * da el formato adecuado para mostrarse
	 * @param lectura
	 */
	private void setDatosDeudas(Lectura lectura)
	{
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy  HH:mm",Locale.getDefault());
		fechaDeudasAcumuladas = df.format(new Date());
		mesesAdeudadosAl = lectura.MesesAdeudados;
		facturaActual = ((lectura.ImporteTotal.setScale(2,RoundingMode.HALF_UP)).setScale(1,RoundingMode.HALF_UP)).toString()+"0";
		facturasAdeudadas = ((lectura.ImporteFacturasAdeudadas.setScale(2,RoundingMode.HALF_UP)).setScale(1,RoundingMode.HALF_UP)).toString()+"0";
		totalDeuda =((lectura.ImporteTotal.add(lectura.ImporteFacturasAdeudadas)).setScale(2,RoundingMode.HALF_UP).setScale(1,RoundingMode.HALF_UP)).toString()+"0";
	}
	/**
	 * Asigna los datos correspondientes a los conceptos del aviso de cobranza y les
	 * da el formato adecuado para mostrarse
	 * @param lectura
	 */
	private void asignarConceptos(ArrayList<ConceptoLectura> conceptosLec, String categoria) 
	{
		importesPorConcepto = new ArrayList<ImportePorConcepto>();
		int areaImpresionAnterior=1;
		BigDecimal importeTotalConsumo = new BigDecimal(0);
		BigDecimal importeTotalSuministro = new BigDecimal(0);
		BigDecimal cargoFijo =null;
		boolean imprimeCargoFijo = imprimeCargoFijo(categoria);
		for(ConceptoLectura concepto : conceptosLec)
		{
			if(concepto.AreaImpresion==VariablesDeEntorno.areaImpresionTotalPorConsumo)
			{
				importeTotalConsumo = importeTotalConsumo.add((concepto.Importe.setScale(2,RoundingMode.HALF_UP))
						.setScale(1,RoundingMode.HALF_UP));
				importeTotalSuministro = importeTotalSuministro.add((concepto.Importe.setScale(2,RoundingMode.HALF_UP))
						.setScale(1,RoundingMode.HALF_UP));
			}
			if(concepto.AreaImpresion==VariablesDeEntorno.areaImpresionTotalPorSuministro)
			{
				importeTotalSuministro = importeTotalSuministro.add((concepto.Importe.setScale(2,RoundingMode.HALF_UP))
						.setScale(1,RoundingMode.HALF_UP));
			}
			if(areaImpresionAnterior==VariablesDeEntorno.areaImpresionTotalPorConsumo && concepto.AreaImpresion>areaImpresionAnterior)
			{
				importesPorConcepto.add(new ImportePorConcepto(VariablesDeEntorno.descripcionTotalPorConsumo, 
						importeTotalConsumo, VariablesDeEntorno.areaImpresionTotalPorConsumo));
			}
			if((areaImpresionAnterior==VariablesDeEntorno.areaImpresionTotalPorSuministro 
					&& concepto.AreaImpresion>areaImpresionAnterior)
					||(areaImpresionAnterior==VariablesDeEntorno.areaImpresionTotalPorConsumo 
					&& concepto.AreaImpresion>areaImpresionAnterior
					&& concepto.AreaImpresion!=VariablesDeEntorno.areaImpresionTotalPorSuministro))
			{
				importesPorConcepto.add(new ImportePorConcepto(VariablesDeEntorno.descripcionTotalPorSuministro, 
						importeTotalSuministro, VariablesDeEntorno.areaImpresionTotalPorSuministro));
				totalSujetoCreditoFiscal = ((importeTotalSuministro.setScale(2,RoundingMode.HALF_UP)).setScale(1,RoundingMode.HALF_UP)).toString()+"0";
			}
			if(imprimeCargoFijo)
			{
				importesPorConcepto.add(new ImportePorConcepto(concepto.Descripcion, concepto.Importe, concepto.ConceptoCodigo));
			}
			else
			{
				if(concepto.ConceptoCodigo!=VariablesDeEntorno.idBaseCalculoCargoFijo)//NO se imprime cargo fijo
				{
					if(cargoFijo!=null)
					{
						concepto.Importe = concepto.Importe.add(cargoFijo);
						cargoFijo = null;
					}
					importesPorConcepto.add(new ImportePorConcepto(concepto.Descripcion, concepto.Importe, concepto.ConceptoCodigo));
				}
				else
				{
					cargoFijo = concepto.Importe;
				}
			}
			areaImpresionAnterior=concepto.AreaImpresion;
		}
	}
	/**
	 * Agrupa conceptos iguales en caso de que excedan el numero proporcionado por las variables de entorno
	 * es decir, de la tabla de parametros
	 * @param lectura
	 */
	private void agruparConceptosSiExcedenMaximo() {
		if(importesPorConcepto.size()>VariablesDeEntorno.maxConceptosAviso)
		{
			BigDecimal importeConceptos = new BigDecimal(0);
			int idBaseCalculoAnterior = -1;
			for (int i = 0; i < importesPorConcepto.size(); i++) 
			{
				ImportePorConcepto impPorConc = importesPorConcepto.get(i);
				if(idBaseCalculoAnterior!=-1)
				{
					if(idBaseCalculoAnterior==impPorConc.idBaseCalculo)
					{
						importeConceptos = importeConceptos.add(new BigDecimal(impPorConc.importe));
						importesPorConcepto.remove(i);
						i--;
					}
					else
					{
						if(!importeConceptos.equals(new BigDecimal(0)))
						{
							importesPorConcepto.get(i-1).importe = ((importeConceptos.setScale(2,RoundingMode.HALF_UP)).setScale(1,RoundingMode.HALF_UP)).toString()+"0";
							importeConceptos = new BigDecimal(0);
						}
					}
				}
				idBaseCalculoAnterior = impPorConc.idBaseCalculo;
			}
		}
	}
	/**
	 * Crea la lista de evoluciones de consumo, agrega elementos de tipo MesConsumo a la lista evolucionConsumo
	 * @param evConsumo
	 * @param anio
	 * @param mes
	 * @param consumo
	 */
	private void asignarEvolucionConsumo(EvolucionConsumo evConsumo, int anio, int mes, int consumo)
	{
		evolucionConsumo = new ArrayList<MesConsumo>();
		int maxConsumo = Math.max(evConsumo.obtenerMaximoConsumo(),consumo);
		String mesOO = ""+anio+"/"+(mes>9?mes:("0"+mes));
		evolucionConsumo.add(new MesConsumo(mesOO,consumo,maxConsumo));
		if(evConsumo.Mes01!=null)
			evolucionConsumo.add(new MesConsumo(evConsumo.Mes01,evConsumo.ConsumoKWH01,maxConsumo));
		if(evConsumo.Mes02!=null)
			evolucionConsumo.add(new MesConsumo(evConsumo.Mes02,evConsumo.ConsumoKWH02,maxConsumo));
		if(evConsumo.Mes03!=null)
			evolucionConsumo.add(new MesConsumo(evConsumo.Mes03,evConsumo.ConsumoKWH03,maxConsumo));
		if(evConsumo.Mes04!=null)
			evolucionConsumo.add(new MesConsumo(evConsumo.Mes04,evConsumo.ConsumoKWH04,maxConsumo));
		if(evConsumo.Mes05!=null)
			evolucionConsumo.add(new MesConsumo(evConsumo.Mes05,evConsumo.ConsumoKWH05,maxConsumo));
		if(evConsumo.Mes06!=null)
			evolucionConsumo.add(new MesConsumo(evConsumo.Mes06,evConsumo.ConsumoKWH06,maxConsumo));
		if(evConsumo.Mes07!=null)
			evolucionConsumo.add(new MesConsumo(evConsumo.Mes07,evConsumo.ConsumoKWH07,maxConsumo));
		if(evConsumo.Mes08!=null)
			evolucionConsumo.add(new MesConsumo(evConsumo.Mes08,evConsumo.ConsumoKWH08,maxConsumo));
		if(evConsumo.Mes09!=null)
			evolucionConsumo.add(new MesConsumo(evConsumo.Mes09,evConsumo.ConsumoKWH09,maxConsumo));
		if(evConsumo.Mes10!=null)
			evolucionConsumo.add(new MesConsumo(evConsumo.Mes10,evConsumo.ConsumoKWH10,maxConsumo));
		if(evConsumo.Mes11!=null)
			evolucionConsumo.add(new MesConsumo(evConsumo.Mes11,evConsumo.ConsumoKWH11,maxConsumo));
		if(evConsumo.Mes12!=null)
			evolucionConsumo.add(new MesConsumo(evConsumo.Mes12,evConsumo.ConsumoKWH12,maxConsumo));
	}
	/**
	 * Verifica si se deben juntar los importes de cargo fijo y cargo variable y mostrarlos como importe por energia ,
	 * verifica segun los parametros proporcionados en las variables de entorno.
	 * @param categoria
	 * @return
	 */
	private boolean imprimeCargoFijo(String categoria)
	{
		int tam = VariablesDeEntorno.categoriasNoCargoFijo.size();
		for (int i = 0; i < tam; i++) 
		{
			if(VariablesDeEntorno.categoriasNoCargoFijo.get(i).equals(categoria))
			{
				return false;
			}
		}
		return true;
	}
	/**
	 * Convierte el monto total en un literal con sus centavos mas.
	 * @param monto
	 * @return
	 */
	private String getMontoLiteral(BigDecimal monto)
	{
		String montoLiteral = convertirNumero(""+monto.setScale(0,RoundingMode.DOWN).intValue())
				+" "+((monto.setScale(2,RoundingMode.HALF_UP)
				.subtract(monto.setScale(0,RoundingMode.DOWN)).
				multiply(new BigDecimal(100))).intValue())
				+"/100.";		
		return montoLiteral;
	}
	/**
	 * Le agrega giones al monto literal en caso de ser mas pequeño que el <b>maxEstablecido</b> de largo
	 * @param montoLiteral
	 * @param maxEstablecido
	 * @return
	 */
	private String getMontoLiteralConGuiones(String montoLiteral, int maxEstablecido)
	{
		int tamCad = montoLiteral.length();
		StringBuilder guiones =new StringBuilder("");
		for (int i = tamCad; i < maxEstablecido; i++) 
		{
			guiones.append("-");
		}
		return montoLiteral+guiones.toString();
	}
	
	/**
	 * Convierte una cadena que representa un numero a su literal
	 * @param numeroCad
	 * @return
	 */
	public String convertirNumero(String numeroCad)
	{
		String numCad="";
		for (int i = 0; i < numeroCad.length(); i++) 
		{
			if(numeroCad.charAt(i)!='0')
			{
				numCad = numeroCad.substring(i,numeroCad.length());
				break;
			}
		}
		numeroCad = numCad;
		int tam = numeroCad.length();
		int indTam = tam-1;
		int numero = 0;
		if(numeroCad.length()>0)
			numero = Integer.parseInt(numeroCad);
		switch(tam)
		{
			case(0):
				return "";
			case(1):
			{
				return unidades[numero];
			}
			case(2):
			{
				if(numero<16)
					return unidades[numero];
				else if(numeroCad.charAt(indTam)=='0')
					return decenas[Integer.parseInt(""+numeroCad.charAt(indTam-1))];
				else 
					return decenasConj[Integer.parseInt(""+numeroCad.charAt(indTam-1))]
							+ unidades[Integer.parseInt(""+numeroCad.charAt(indTam))];
			}
			case(3):
			{
				if(numeroCad.charAt(indTam)=='0' && numeroCad.charAt(indTam-1)=='0')
				{
					return centenas[Integer.parseInt(""+numeroCad.charAt(indTam-2))];
				}
				else
				{
					if(numeroCad.charAt(indTam-2)=='1')
						return "CIENTO "+convertirNumero(numeroCad.substring(1,tam));
					return centenas[Integer.parseInt(""+numeroCad.charAt(indTam-2))]+" "+
					convertirNumero(numeroCad.substring(1,tam));		
				}
			}
			case(4):
			{
				if(numeroCad.charAt(indTam-3)=='1')
				{
					return "MIL "+convertirNumero(numeroCad.substring(1,tam));
				}
				else
				{
					return unidades[Integer.parseInt(""+numeroCad.charAt(indTam-3))]+" MIL "+
					convertirNumero(numeroCad.substring(1,tam));		
				}
			}
			case(5):
			{
				return convertirNumero(numeroCad.substring(0,2))+" MIL "+
				convertirNumero(numeroCad.substring(2,tam));		
			}
			case(6):
			{
				return convertirNumero(numeroCad.substring(0,3))+" MIL "+
				convertirNumero(numeroCad.substring(3,tam));		
			}
			default:
				return numeroCad;
				
		}
	}
	
	private String[] unidades = {"CERO", "UNO","DOS","TRES","CUATRO","CINCO", "SEIS",
			"SIETE", "OCHO", "NUEVE","DIEZ","ONCE","DOCE","TRECE","CATORCE","QUINCE"};
	private String[] decenas = {"","DIEZ", "VEINTE","TREINTA","CUARENTA","CINCUENTA","SESENTA",
			"SETENTA", "OCHENTA", "NOVEINTA"};
	private String[] decenasConj = {"","DIECI", "VEINTI","TREINTA Y ","CUARENTA Y ","CINCUENTA Y ","SESENTA Y ",
			"SETENTA Y ", "OCHENTA Y ", "NOVENTA Y "};
	private String[] centenas = {"","CIEN", "DOSCIENTOS","TRESCIENTOS","CUATROCIENTOS","QUINIENTOS","SEISCIENTOS",
			"SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS"};
}
