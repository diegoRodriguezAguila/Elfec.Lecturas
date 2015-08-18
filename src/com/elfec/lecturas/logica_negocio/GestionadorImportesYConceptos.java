package com.elfec.lecturas.logica_negocio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;

import com.elfec.lecturas.modelo.BaseCalculo;
import com.elfec.lecturas.modelo.BaseCalculoConcepto;
import com.elfec.lecturas.modelo.ConceptoCategoria;
import com.elfec.lecturas.modelo.ConceptoLectura;
import com.elfec.lecturas.modelo.ConceptoTarifa;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.SubConcepto;
import com.elfec.lecturas.settings.VariablesDeEntorno;

public class GestionadorImportesYConceptos {
	
	private static ArrayList<SubConcepto> subConceptos;
	private static SparseArray<ConceptoLectura> conceptosLectura;
	private static String categoriaLectura;
	public static void agregarConceptos(Lectura lectura)
	{
		//se asegura que se tenga la categoria correcta
		categoriaLectura = lectura.Categoria;
		if(lectura.recategorizarSiEsNecesario())
		{
			categoriaLectura = lectura.CategoriaRecategorizada;
		}
		subConceptos = new ArrayList<SubConcepto>();
		conceptosLectura =  new SparseArray<ConceptoLectura>();
		subConceptos.addAll(obtenerConceptosEnergiaDemandaYBDignidad(lectura));
		subConceptos.addAll(obtenerConceptosParticularesDeSuministro(lectura));
		subConceptos.addAll(obtenerConceptosGenerales(lectura));
		for(SubConcepto subCon : subConceptos)
		{
			BaseCalculo baseCalculo = BaseCalculoConcepto
					.obtenerBaseCalculoImpresion(subCon.IdConcepto, subCon.IdSubConcepto);
			ConceptoLectura conceptoLectura = conceptosLectura.get(baseCalculo.IdBaseCalculo);
			if(conceptoLectura==null)
			{
				conceptoLectura = new ConceptoLectura(lectura ,baseCalculo.IdBaseCalculo, 
						baseCalculo.Descripcion, subCon.Importe, baseCalculo.OrdenImpresion,subCon.AreaImpresion);
				conceptosLectura.put(baseCalculo.IdBaseCalculo, conceptoLectura);
			}
			else
			{
				conceptoLectura.Importe=conceptoLectura.Importe.add(subCon.Importe);
			}
		}
		for (int i = 0; i < conceptosLectura.size(); i++) 
		{
			ConceptoLectura concepLectura = conceptosLectura.get(conceptosLectura.keyAt(i));
			concepLectura.Lectura = lectura;
			concepLectura.save();
		}
		lectura.ImporteCargoFijo = conceptosLectura.get(VariablesDeEntorno.idBaseCalculoCargoFijo)==null?
				new BigDecimal(0.0):conceptosLectura.get(VariablesDeEntorno.idBaseCalculoCargoFijo).
				Importe.setScale(2,RoundingMode.HALF_UP).setScale(1,RoundingMode.HALF_UP);
		lectura.ImportePorEnergia = conceptosLectura.get(VariablesDeEntorno.idBaseCalculoImporteEnergia)==null?
				new BigDecimal(0.0):conceptosLectura.get(VariablesDeEntorno.idBaseCalculoImporteEnergia).
				Importe.setScale(2,RoundingMode.HALF_UP).setScale(1,RoundingMode.HALF_UP);
		lectura.ImportePorPotencia = conceptosLectura.get(VariablesDeEntorno.idBaseCalculoImportePotencia)==null?
				new BigDecimal(0.0):conceptosLectura.get(VariablesDeEntorno.idBaseCalculoImportePotencia).
				Importe.setScale(2,RoundingMode.HALF_UP).setScale(1,RoundingMode.HALF_UP);
		lectura.ImporteTotal = obtenerImporteTotal(lectura).setScale(2,RoundingMode.HALF_UP).
				setScale(1,RoundingMode.HALF_UP);
	}
	
	private static BigDecimal obtenerImporteTotal(Lectura lectura) {
		List<ConceptoLectura> conceptosLectura = lectura.obtenerConceptosLectura();
		BigDecimal importeTotal = new BigDecimal(0.0);
		for(ConceptoLectura cptoLectura : conceptosLectura)
		{
			importeTotal = importeTotal.add((cptoLectura.Importe.setScale(2,RoundingMode.HALF_UP)).setScale(1,RoundingMode.HALF_UP));
		}
		return importeTotal;
	}

	private static ArrayList<SubConcepto> obtenerConceptosEnergiaDemandaYBDignidad(Lectura lectura)
	{
		ArrayList<SubConcepto> subConceptos = new ArrayList<SubConcepto>();
		ArrayList<ConceptoCategoria> conceptosCat = (ArrayList<ConceptoCategoria>) ConceptoCategoria
		.obtenerConceptoCategoriasPorCategoriaYAplicabilidad(categoriaLectura,"PC");
		
		int valLimiteAnterior=0;
		if(lectura.ConsumoFacturado==0)
			valLimiteAnterior=-1;
		for(ConceptoCategoria conceptoCat : conceptosCat)
		{
			//CARGO FIJO
			if (conceptoCat.TipoCalculo.equals("F") && conceptoCat.TipoTarifa.equals("I"))
			{
				BigDecimal importe = (conceptoCat.Importe.setScale(2, RoundingMode.HALF_UP))
						.setScale(1, RoundingMode.HALF_UP);
				subConceptos.add(new SubConcepto(conceptoCat.IdConcepto, conceptoCat.IdSubConcepto, 
						conceptoCat.Concepto.Descripcion, importe, conceptoCat.LimiteValor, 
						0, conceptoCat.Concepto.TipoPartida, conceptoCat.Concepto.AreaImpresion));
				lectura.ImporteCargoFijo = importe;
			}
			//CARGO VARIABLE
			if (conceptoCat.TipoCalculo.equals("VEs") && 
					conceptoCat.TipoTarifa.equals("I") && conceptoCat.Concepto.IdMetodo==5)
			{
				if(lectura.ConsumoFacturado > conceptoCat.LimiteValor)
				{
					BigDecimal importe = ((new BigDecimal(conceptoCat.LimiteValor-valLimiteAnterior).multiply(conceptoCat.Importe))
							.setScale(2, RoundingMode.HALF_UP))
							.setScale(1, RoundingMode.HALF_UP);
					subConceptos.add(new SubConcepto(conceptoCat.IdConcepto, conceptoCat.IdSubConcepto, 
							conceptoCat.Concepto.Descripcion, importe, conceptoCat.LimiteValor,
							(conceptoCat.LimiteValor-valLimiteAnterior), conceptoCat.Concepto.TipoPartida, conceptoCat.Concepto.AreaImpresion));
					valLimiteAnterior = conceptoCat.LimiteValor;
				}
				else if (lectura.ConsumoFacturado > valLimiteAnterior)
				{
					if(lectura.ConsumoFacturado==0)
						valLimiteAnterior=0;
					BigDecimal importe = ((new BigDecimal(lectura.ConsumoFacturado-valLimiteAnterior).multiply(conceptoCat.Importe))
							.setScale(2, RoundingMode.HALF_UP))
							.setScale(1, RoundingMode.HALF_UP);
					subConceptos.add(new SubConcepto(conceptoCat.IdConcepto, conceptoCat.IdSubConcepto, 
							conceptoCat.Concepto.Descripcion, importe, conceptoCat.LimiteValor,
							(lectura.ConsumoFacturado-valLimiteAnterior), conceptoCat.Concepto.TipoPartida, conceptoCat.Concepto.AreaImpresion));
					valLimiteAnterior = conceptoCat.LimiteValor;
				}
			}
			//CARGO POTENCIA
			if(conceptoCat.TipoCalculo.equals("V") && 
					conceptoCat.TipoTarifa.equals("I") && conceptoCat.Concepto.IdMetodo==3 
					&& lectura.TagCalculaPotencia==1)
			{
				BigDecimal importe = ((new BigDecimal(lectura.PotenciaLectura.ConsumoFacturado).multiply(conceptoCat.Importe))
						.setScale(2, RoundingMode.HALF_UP))
						.setScale(1, RoundingMode.HALF_UP);
				subConceptos.add(new SubConcepto(conceptoCat.IdConcepto, conceptoCat.IdSubConcepto, 
						conceptoCat.Concepto.Descripcion, importe, conceptoCat.LimiteValor,
						lectura.PotenciaLectura.ConsumoFacturado, conceptoCat.Concepto.TipoPartida, conceptoCat.Concepto.AreaImpresion));
				lectura.ImportePorPotencia = importe;
			}
			//CALCULO ESPECIFICO CUANDO EL TIPO DE CALCULO ES UN SCRIPT ESPECIAL  
			if(conceptoCat.TipoCalculo.equals("S") && lectura.TagTarifaDignidad==1)
			{
				//TARIFA DIGNIDAD
				if(conceptoCat.IdConcepto==VariablesDeEntorno.idConceptoBeneficioDignidad 
						&& lectura.ConsumoFacturado<=VariablesDeEntorno.limiteConsumoAplicaDignidad)
				{
					BigDecimal sumatoria = sumatoriaSubconceptosBaseCalculo(conceptoCat.IdBaseCalculo, subConceptos);
					BigDecimal importe = sumatoria.multiply(conceptoCat.Tasa.divide(new BigDecimal(100),10, RoundingMode.HALF_UP))
							.multiply(new BigDecimal(-1));
					subConceptos.add(new SubConcepto(conceptoCat.IdConcepto, conceptoCat.IdSubConcepto, 
							conceptoCat.Concepto.Descripcion, importe, conceptoCat.LimiteValor,
							0, conceptoCat.Concepto.TipoPartida, conceptoCat.Concepto.AreaImpresion));
					lectura.ImporteDescuentoTarifaDignidad = importe;
				}
			}
		}
		return subConceptos;
	}
	
	private static ArrayList<SubConcepto> obtenerConceptosParticularesDeSuministro(Lectura lectura)
	{
		ArrayList<SubConcepto> subConceptos = new ArrayList<SubConcepto>();
		ArrayList<ConceptoCategoria> conceptosCat = (ArrayList<ConceptoCategoria>) ConceptoCategoria
		.obtenerConceptoCategoriasPorCategoriaYAplicabilidad(categoriaLectura,"PS");
		
		int valLimiteAnterior=-1;
		for(ConceptoCategoria conceptoCat : conceptosCat)
		{
			//CASO DE USO ASEO
			if (lectura.CotizaAseo!=0 && conceptoCat.TipoCalculo.equals("FV") && conceptoCat.TipoTarifa.equals("I"))
			{
				if((lectura.CotizaAseo==1 && conceptoCat.IdConcepto==VariablesDeEntorno.idConceptoAseoUrbano)
						|| (lectura.CotizaAseo==2 && conceptoCat.IdConcepto==VariablesDeEntorno.idConceptoAseoSacaba))
				{
					if(lectura.ConsumoFacturado>valLimiteAnterior && lectura.ConsumoFacturado<=conceptoCat.LimiteValor)
					{
						BigDecimal importe = (conceptoCat.Importe.setScale(2, RoundingMode.HALF_UP))
								.setScale(1, RoundingMode.HALF_UP);
						subConceptos.add(new SubConcepto(conceptoCat.IdConcepto, conceptoCat.IdSubConcepto, 
								conceptoCat.Concepto.Descripcion, importe, conceptoCat.LimiteValor, 
								0, conceptoCat.Concepto.TipoPartida, conceptoCat.Concepto.AreaImpresion));
						valLimiteAnterior = conceptoCat.LimiteValor;
						lectura.ImporteAseo = importe;
					}
				}
			}
			//CASO CALCULO SCRIPT ESPECIAL
			if(conceptoCat.TipoCalculo.equals("S") && lectura.TagLey1886==1)
			{
				//APLICA LEY1886
				if(conceptoCat.IdConcepto==VariablesDeEntorno.idConceptoLey1886)
				{		
					BigDecimal importe;
					if(lectura.ConsumoFacturado<=VariablesDeEntorno.limiteConsumoAplicaLey)
					{
						BigDecimal sumatoria = sumatoriaSubconceptosBaseCalculo(VariablesDeEntorno.idBaseCalculoAplicaLey, 
								GestionadorImportesYConceptos.subConceptos);
						importe = sumatoria.multiply(VariablesDeEntorno.descuentoLey1886);
					}
					else
					{
						BigDecimal sumatoriaInfLim = sumatoriaSubconceptosBaseCalculo(VariablesDeEntorno.idBaseCalculoAplicaLey, 
								GestionadorImportesYConceptos.subConceptos,VariablesDeEntorno.limiteConsumoAplicaLey);
						valLimiteAnterior = maximoLimiteValorCargoVariable(GestionadorImportesYConceptos.subConceptos, VariablesDeEntorno.limiteConsumoAplicaLey);
						importe = (sumatoriaInfLim.add((new BigDecimal(VariablesDeEntorno.limiteConsumoAplicaLey-valLimiteAnterior)
						.multiply(conceptoCat.Importe))
								.setScale(2, RoundingMode.HALF_UP)
								.setScale(1, RoundingMode.HALF_UP))).multiply(VariablesDeEntorno.descuentoLey1886);
					}
					importe = (importe.setScale(2, RoundingMode.HALF_UP))
					.setScale(1, RoundingMode.HALF_UP).multiply(new BigDecimal(-1));
					subConceptos.add(new SubConcepto(conceptoCat.IdConcepto, conceptoCat.IdSubConcepto, 
							conceptoCat.Concepto.Descripcion, importe, conceptoCat.LimiteValor, 
							0, conceptoCat.Concepto.TipoPartida, conceptoCat.Concepto.AreaImpresion));
					lectura.ImporteLey1886 = importe;
				}
			}
			
		}
		return subConceptos;
	}
	
	private static ArrayList<SubConcepto> obtenerConceptosGenerales(Lectura lectura)
	{
		ArrayList<SubConcepto> subConceptos = new ArrayList<SubConcepto>();
		ArrayList<ConceptoTarifa> conceptosTarifas = (ArrayList<ConceptoTarifa>) ConceptoTarifa
		.obtenerConceptoTarifasPorAplicabilidad("PS");		
		for(ConceptoTarifa conceptoTar : conceptosTarifas)
		{
			//CASO DE ALUMBRADO PUBLICO
			if (conceptoTar.IdConcepto>=VariablesDeEntorno.idConceptoAPMin && 
					conceptoTar.IdConcepto<=VariablesDeEntorno.idConceptoAPMax )
			{
				if(lectura.TipoTAP>0)//alumbrado publico 1, 2
				{
					BigDecimal sumatoria = sumatoriaSubconceptosBaseCalculo( conceptoTar.IdBaseCalculo, GestionadorImportesYConceptos.subConceptos);
					BigDecimal importe = ((sumatoria.multiply(lectura.PorcentajeTAP.divide(new BigDecimal(100),10, RoundingMode.HALF_UP)))
						.setScale(2, RoundingMode.HALF_UP))
						.setScale(1, RoundingMode.HALF_UP);
					if((lectura.TipoTAP==1 && conceptoTar.IdConcepto!=VariablesDeEntorno.idConceptoAPMax)
						||(lectura.TipoTAP==2 && conceptoTar.IdConcepto==VariablesDeEntorno.idConceptoAPMax))
					{
						if(lectura.PorcentajeTAP.setScale(2, RoundingMode.HALF_UP).equals((conceptoTar.Tasa.multiply(new BigDecimal(0.87)).setScale(2, RoundingMode.HALF_UP))))
						subConceptos.add(new SubConcepto(conceptoTar.IdConcepto, conceptoTar.IdSubConcepto, 
								conceptoTar.Concepto.Descripcion, importe, 0, 0, conceptoTar.Concepto.TipoPartida, conceptoTar.Concepto.AreaImpresion));
						lectura.ImporteTap = importe;
					}
				}
			}
			
		}
		return subConceptos;
	}
	
	/**
	 * Realiza la sumatoria de los importes de los subconceptos que pertenecen a la base de calculo del parametro (idBaseCalculo)
	 * @param idBaseCalculo, id de la base de calculo
	 * @param subConceptos, lista de subconceptos de la que se realizará la sumatoria
	 * @return Sumatoria de los importes de los subconceptos que pertenecen a una misma base de calculo
	 */
	private static BigDecimal sumatoriaSubconceptosBaseCalculo(int idBaseCalculo, ArrayList<SubConcepto> subConceptos )
	{
		BigDecimal sumatoria = new BigDecimal(0);
		for(SubConcepto subconcepto : subConceptos)
		{
			if(BaseCalculoConcepto.subConceptoPerteneceABaseCalculo(idBaseCalculo, subconcepto.IdConcepto, subconcepto.IdSubConcepto))
			{
				sumatoria = sumatoria.add(subconcepto.Importe);
			}
		}
		return sumatoria;
	}
	
	/**
	 * Realiza la sumatoria de los importes de los subconceptos que pertenecen a la base de calculo del parametro (idBaseCalculo)
	 * y que sean menores (o iguales) al limite de valor proporcionado.
	 * @param limiteValor, valorLimite
	 * @param idBaseCalculo, id de la base de calculo
	 * @param subConceptos, lista de subconceptos de la que se realizará la sumatoria
	 * @return Sumatoria de los importes de los subconceptos que pertenecen a una misma base de calculo
	 */
	private static BigDecimal sumatoriaSubconceptosBaseCalculo(int idBaseCalculo, ArrayList<SubConcepto> subConceptos, int limiteValor)
	{
		BigDecimal sumatoria = new BigDecimal(0);
		for(SubConcepto subconcepto : subConceptos)
		{
			if(BaseCalculoConcepto.subConceptoPerteneceABaseCalculo(idBaseCalculo, subconcepto.IdConcepto, subconcepto.IdSubConcepto)
					&& subconcepto.LimiteValor<=limiteValor)
			{
				sumatoria = sumatoria.add(subconcepto.Importe);
			}
		}
		return sumatoria;
	}
	
	/**
	 * Retorna el maximo limite de valor de un subconcepto de cargo variable, menor al limite solicitado
	 * @param subConceptos, lista de subconceptos en la que se buscaran los conceptos de cargo variable
	 * @param limiteValor, es el limite de valor que define que el maximo que se encuentre es menor a el
	 * @return Sumatoria de los importes de los subconceptos que pertenecen a una misma base de calculo
	 */
	private static int maximoLimiteValorCargoVariable(ArrayList<SubConcepto> subConceptos, int limiteValor)
	{
		int max = 0;
		for(SubConcepto subconcepto : subConceptos)
		{
			if(subconcepto.IdConcepto==VariablesDeEntorno.idConceptoCargoVariable && subconcepto.LimiteValor<=limiteValor)
			{
				max =subconcepto.LimiteValor;
			}
		}
		return max;
	}

}
