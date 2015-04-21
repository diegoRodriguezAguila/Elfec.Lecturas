package com.lecturas.elfec.modelo.backuptexto;

/**
 * Define una interfaz que se usa para convertir cualquier objeto
 * a una linea de texto que será utilizada para guardar en el archivo de backup que corresponda
 * @author drodriguez
 *
 */
public interface IModeloBackupableTexto {

	/**
	 * Se encarga de obtener una cadena que representa el modelo, los datos deben estar separados por
	 * el caracter <b>'|'</b>  por ejemplo: Nombre|Apellido|Suministro
	 * @return
	 */
	public String obtenerLineaTextoBackup();
	
	/**
	 * Debe retornar el nombre del archivo de backup destinado para la clase que implemente la interfaz
	 * @return
	 */
	public String obtenerNombreArchivoBackup();
	
	/**
	 * Retorna la estructura de las lineas de backup por ejemplo: Nombre|Apellido|Suministro
	 * @return
	 */
	public String obtenerCabeceraBackup();
}
