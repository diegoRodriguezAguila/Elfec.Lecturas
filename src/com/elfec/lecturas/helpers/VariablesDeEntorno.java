package com.elfec.lecturas.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.activeandroid.util.Log;
import com.elfec.lecturas.helpers.excepciones.ArchivosDeParametrizablesNoDisponiblesExcepcion;

/**
 * Guarda todos los parametrizables de las tablas de parametros ERP_ELFEC.SGC_MOVIL_PARAM de la base de datos Oracle
 * @author drodriguez
 *
 */
public class VariablesDeEntorno {
	public static int numMaxOrdenativosPorLectura;
	public static int idConceptoBeneficioDignidad;
	public static int limiteConsumoAplicaDignidad;
	public static int idConceptoLey1886;
	public static int limiteConsumoAplicaLey;
	public static int idBaseCalculoAplicaLey;
	public static BigDecimal descuentoLey1886;
	public static int idConceptoCargoVariable;
	public static int idConceptoAseoUrbano;
	public static int idConceptoAseoSacaba;
	public static int idConceptoAPMin;
	public static int idConceptoAPMax;
	public static int idBasesCalculoImpresion;
	public static int areaImpresionTotalPorConsumo;
	public static String descripcionTotalPorConsumo;
	public static int areaImpresionTotalPorSuministro;
	public static String descripcionTotalPorSuministro;
	public static int idBaseCalculoCargoFijo;
	public static int idBaseCalculoImporteEnergia;
	public static int idBaseCalculoImportePotencia;
	public static List<String> categoriasNoCargoFijo;
	public static int maxConceptosAviso;
	//Parametros de Servicio Web
	/**
	 * El protocolo que se usara para conectar con
	 * el servicio web (ej. http | https)
	 */
	public static String protocoloServicioWeb;
	/**
	 * La ip o dominio del servidor del servicio web 
	 * (ej. 192.168.30.44 | webservice.elfec.com)
	 */
	public static String ipServidorServicioWeb;
	/**
	 * El puerto para la conexion al servicio web (ej. 8088)
	 */
	public static int puertoServicioWeb;
	/**
	 * La acción soap del servicio web, dejar en blanco por defecto
	 */
	public static String accionSoap;
	/**
	 * El namespace del servicio web (ej. http://DefaultNamespace)
	 */
	public static String nombreEspacio;
	
	
	/**
	 * Indica el nivel de bateria considerado como crítico, 
	 * cuando el nivel de bateria es igual o por debajo del crítico no se obliga
	 * al usuario a tener encendidos los servicios de 3G o GPS para tomar lecturas
	 */
	public static float nivelBateriaCritico;
	
	// Parametros para el guardado de ubicación 3G GPS etc
	/**
	 * Indica que tipo de guardado de ubicación se utilizará, para mas información vea los estados de ubicacion
	 * <b>0=no guarda, 1=guarda 3g y gps (google services) , 2=guarda gps</b>
	 * @see com.elfec.lecturas.helpers.estadosmanejadorubicacion.IEstadoManejadorUbicacion
	 */
	public static int tipoGuardadoUbicacion;//0=no guarda, 1=guarda 3g , 2=guarda gps
	/**
	 * Es el tiempo maximo de espera para recibir una ubicación
	 */
	public static int timeoutGuardadoUbicacion;
	
	public static String prefijoImpresorasRW420;
	public static String prefijoImpresorasiMZ320;
	public static String observacionesORecomendaciones;
	
	public static int numMaxFotosPorLectura;
	
	public static List<Integer> codigosOrdenativosResumen;
	
	
	/**
	 * Es un acceso rapido a la restriccion de limite de impresiones en el Administrador de seguridad, notese que
	 * este valor debe mantenerse actualizado para reflejar la sesion actual
	 */
	public static int limiteImpresiones;
	/**
	 * Es un acceso rapido a la restriccion de limite de modificaciones de una lectura en el Administrador de seguridad, notese que
	 * este valor debe mantenerse actualizado para reflejar la sesion actual
	 */
	public static int limiteModificacionesLectura;
	
	
	static
	{
		asignarParametrosGenerales();
		asignarParametrosCodigosOrdenativosParaResumen();
		asignarParametrosCategoriasNoMostrarCargoFijo();
	}

	private static void asignarParametrosGenerales() 
	{
		try 
		{
			JSONObject params = new JSONObject(ManejadorJSON.LeerArchivoJSON(ConstantesDeEntorno.archivoParametrosGrales));
			numMaxOrdenativosPorLectura = params.getInt("NUMMAXORDXLEC");
			idConceptoBeneficioDignidad = params.getInt("IDCONCEPTOBENEFICIODIGNIDAD");
			limiteConsumoAplicaDignidad = params.getInt("LIMITECONSUMOAPLICADIGNIDAD");
			idConceptoLey1886 = params.getInt("IDCONCEPTOLEY1886");
			limiteConsumoAplicaLey = params.getInt("LIMITECONSUMOAPLICALEY");
			idBaseCalculoAplicaLey = params.getInt("IDBASECALCAPLICALEY");
			descuentoLey1886 = new BigDecimal(params.getString("DESCUENTOLEY1886"));
			idConceptoCargoVariable = params.getInt("IDCONCEPTOCARGOVARIABLE");
			idConceptoAseoUrbano = params.getInt("IDCONCEPTOASEOURBANO");
			idConceptoAseoSacaba = params.getInt("IDCONCEPTOASEOSACABA");
			idConceptoAPMin = params.getInt("IDCONCEPTOALUMBRADOPUBLICOMIN");
			idConceptoAPMax = params.getInt("IDCONCEPTOALUMBRADOPUBLICOMAX");
			idBasesCalculoImpresion = params.getInt("IDBASESCALCULOIMPRESION");
			areaImpresionTotalPorConsumo = params.getInt("AREAIMPTOTALPORCONSUMO");
			descripcionTotalPorConsumo = params.getString("DESCRIPCIONTOTALPORCONSUMO");
			areaImpresionTotalPorSuministro = params.getInt("AREAIMPTOTALPORSUMINISTRO");
			descripcionTotalPorSuministro = params.getString("DESCRIPCIONTOTALPORSUMINISTRO");
			idBaseCalculoCargoFijo = params.getInt("IDBASECALCULOCARGOFIJO");
			idBaseCalculoImporteEnergia = params.getInt("IDBASECALCULOIMPORTEENERGIA");
			idBaseCalculoImportePotencia = params.getInt("IDBASECALCULOIMPORTEPOTENCIA");
			maxConceptosAviso = params.getInt("MAXCONCEPTOSQUESEMUSTRANAVISO");
			tipoGuardadoUbicacion = params.getInt("TIPOGUARDADOUBICACION");
			timeoutGuardadoUbicacion = params.getInt("TIMEOUT")*1000;//a milisegundos
			prefijoImpresorasRW420  = params.getString("PREFIJOIMPRESORASRW420");
			prefijoImpresorasiMZ320  = params.getString("PREFIJOIMPRESORASIMZ320");
			observacionesORecomendaciones  = params.getString("OBSERVACIONESORECOMENDACIONES")+"\r\n";// se le debe añadir salto de linea
			numMaxFotosPorLectura = params.getInt("NUMMAXFOTOSPORLECTURA");
			protocoloServicioWeb = params.getString("PROTOCOLO_WEB_SERVICE");
			ipServidorServicioWeb = params.getString("IP_WS");
			puertoServicioWeb = params.getInt("PUERTO_WEB_SERVICE");
			accionSoap = params.optString("SOAP_ACTION");
			nombreEspacio = params.getString("NOMBRE_ESPACIO");
			limiteImpresiones = params.getInt("LIMITEIMPRESIONES");
			nivelBateriaCritico = (float)params.getInt("BATERIA_CRITICA");
		} 
		catch (Exception e) 
		{
			Log.d(e.getClass().getName(),e.getMessage());
			throw new ArchivosDeParametrizablesNoDisponiblesExcepcion(ConstantesDeEntorno.archivoParametrosGrales);
		}
	}
		
	private static void asignarParametrosCodigosOrdenativosParaResumen() 
	{
		try 
		{
			codigosOrdenativosResumen = new ArrayList<Integer>();
			JSONArray params = new JSONArray(ManejadorJSON.LeerArchivoJSON(ConstantesDeEntorno.archivoCodsResumenOrdenativos));
			int num = params.length();
			for (int i = 0; i < num; i++)
			{
				codigosOrdenativosResumen.add(params.getJSONObject(i).getInt("CODIGO"));
			}
		} 
		catch (Exception e) 
		{
			Log.d(e.getClass().getName(),e.getMessage());
			throw new ArchivosDeParametrizablesNoDisponiblesExcepcion(ConstantesDeEntorno.archivoCodsResumenOrdenativos);
		}
	}
	
	private static void asignarParametrosCategoriasNoMostrarCargoFijo() 
	{
		try 
		{
			categoriasNoCargoFijo = new ArrayList<String>();
			JSONArray params = new JSONArray(ManejadorJSON.LeerArchivoJSON(ConstantesDeEntorno.archivoCategsNoMostrarCargoFijo));
			int num = params.length();
			for (int i = 0; i < num; i++)
			{
				categoriasNoCargoFijo.add(params.getJSONObject(i).getString("CATEGORIA"));
			}
		} 
		catch (Exception e) 
		{
			Log.d(e.getClass().getName(),e.getMessage());
			throw new ArchivosDeParametrizablesNoDisponiblesExcepcion(ConstantesDeEntorno.archivoCategsNoMostrarCargoFijo);
		}
	}

}
