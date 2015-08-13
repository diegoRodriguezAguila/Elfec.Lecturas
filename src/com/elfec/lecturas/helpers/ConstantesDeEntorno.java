package com.elfec.lecturas.helpers;

/**
 * Guarda constantes que se utilizan a lo largo de toda la aplicaci�n por lo que es una clase est�tica
 * @author drodriguez
 *
 */
public class ConstantesDeEntorno {

	/**
	 * Es el directorio raiz para la aplicaci�n en almacenamiento externo, se usa para guardar fotos y los archivos json
	 * de par�metros.
	 */
	public static final String directorioAplicacion = "com.lecturas.elfec";
	/**
	 * Es el nombre del archivo json de parametros generales, en el se guarda la informaci�n de la tabla 
	 * ERP_ELFEC.SGC_MOVIL_PARAM
	 */
	public static final String archivoParametrosGrales = "gral_params";
	/**
	 * Es el nombre del archivo json de parametros de las categorias en las que no se muestra el cargo
	 * fijo en el aviso de cobro, en el se guarda la informaci�n de la tabla 
	 * ERP_ELFEC.SGC_MOVIL_PARAM_CATEG_NO_CFIJO
	 */
	public static final String archivoCategsNoMostrarCargoFijo = "cargofijo_params";
	/**
	 * Es el nombre del archivo json de parametros de los codigos de los ordenativos que se toman en cuenta para la opci�n del menu
	 * de Detalle Ordenativos, en el se guarda la informaci�n de la tabla 
	 * ERP_ELFEC.SGC_MOVIL_PARAM_COD_ORD_RES
	 */
	public static final String archivoCodsResumenOrdenativos = "resordenativos_params";
}
