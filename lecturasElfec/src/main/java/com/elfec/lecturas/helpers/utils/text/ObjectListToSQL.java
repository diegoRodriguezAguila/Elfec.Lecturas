package com.elfec.lecturas.helpers.utils.text;

import java.util.Arrays;
import java.util.List;

/**
 * Convierte una lista de objetos a una lista aplicable a la consulta IN de SQL
 * con el formato (1234,125534,1234), se utiliza un atributo del objeto definido
 * por la clase
 * 
 * @author drodriguez
 *
 */
public class ObjectListToSQL {

	private static final int IN_LIMIT = 1000;

	/**
	 * Convierte la lista de objetos a la cadena utilizable en una consulta IN
	 * 
	 * @param objectList
	 * @param picker
	 *            se encarga de elegir con que atributo de la clase se llenara
	 *            la lista IN
	 * @return lista en formato (123,5423,7645)
	 */
	public static <T> String convertToSQL(List<T> objectList,
			AttributePicker<String, T> picker) {
		StringBuilder query = new StringBuilder("(");
		for (T obj : objectList) {
			query.append(picker.pickAttribute(obj)).append(",");
		}
		query.setCharAt(query.length() - 1, ')');
		return query.toString();
	}

	/**
	 * Convierte la lista de objetos a la cadena utilizable en una consulta IN
	 * tomando en cuenta el limite de <b>IN_LIMIT</b> valores en cada IN, debe
	 * utilizarse para cantidades de objetos superiores a <b>IN_LIMIT</b>
	 * 
	 * @param objectList
	 * @param columnName
	 *            el nombre de la columna en la base de datos
	 * @param picker
	 * @return lista con el formato (123,425,...) OR COLUMNNAME IN (423,645,...)
	 */
	public static <T> String convertToSQL(List<T> objectList,
			String columnName, AttributePicker<String, T> picker) {
		StringBuilder query = new StringBuilder();
		int totalSize = objectList.size();
		int count = totalSize / IN_LIMIT;
		int startLimit, endLimit;
		for (int i = 0; i <= count; i++) {
			startLimit = (i * IN_LIMIT);
			endLimit = ((i + 1) * IN_LIMIT);
			endLimit = Math.min(endLimit, totalSize);
			query.append(convertToSQL(objectList.subList(startLimit, endLimit),
					picker));
			if (i < count)// no es el ultimo
			{
				query.append(" OR ").append(columnName).append(" IN ");
			}
		}
		return query.toString();
	}

	/**
	 * Convierte la lista de objetos a la cadena utilizable en una consulta IN,
	 * utiliza toString para obtener la cadena
	 * 
	 * @param objectList
	 * @return
	 */
	public static <T> String convertToSQL(List<T> objectList) {
		return convertToSQL(objectList, new AttributePicker<String, T>() {
			@Override
			public String pickAttribute(T object) {
				return object.toString();
			}
		});
	}

	/**
	 * Convierte la lista de objetos a la cadena utilizable en una consulta IN,
	 * utiliza toString para obtener la cadena
	 * 
	 * @param objectList
	 * @return
	 */
	public static <T> String convertToSQL(T[] objectList) {
		return convertToSQL(Arrays.asList(objectList),
				new AttributePicker<String, T>() {
					@Override
					public String pickAttribute(T object) {
						return object.toString();
					}
				});
	}
}
