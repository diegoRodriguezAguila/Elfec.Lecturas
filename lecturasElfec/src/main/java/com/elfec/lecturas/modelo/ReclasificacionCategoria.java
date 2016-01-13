package com.elfec.lecturas.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

/**
 * Almacena la información sobre las reclasificaciones de categorias, de la
 * tabla ERP_ELFEC.CATEG_RECLASIF de la BD Oracle
 * 
 * @author drodriguez
 */
@Table(name = "ReclasificacionesCategorias")
public class ReclasificacionCategoria extends Model {

	public ReclasificacionCategoria() {
		super();
		inicializarTablaOperadores();
	}

	public ReclasificacionCategoria(ResultSet rs) throws SQLException {
		super();
		inicializarTablaOperadores();
		IdCategoriaOrigen = rs.getString("IDCATEG_ORIG");
		IdCategoriaDestino = rs.getString("IDCATEG_DEST");
		Condicion = rs.getString("CONDICION");
		Valor = rs.getInt("VALOR");
		RecategSiglaDescripcion = rs.getString("RECSGLDES");
	}

	@Column(name = "IDCATEG_ORIG")
	public String IdCategoriaOrigen;

	@Column(name = "IDCATEG_DEST")
	public String IdCategoriaDestino;

	@Column(name = "CONDICION")
	public String Condicion;

	@Column(name = "VALOR")
	public int Valor;

	@Column(name = "RECSGLDES")
	public String RecategSiglaDescripcion;

	/**
	 * Accede a la base de datos y obtiene todas las reclasificaciones
	 * correspondientes a una categoria
	 * 
	 * @param idCategoria
	 *            , id de la categoria origen
	 * @return Lista de ReclasificacionCategoria
	 */
	public static List<ReclasificacionCategoria> obtenerReclasifParaCategoria(
			String idCategoria) {
		return new Select().from(ReclasificacionCategoria.class)
				.where("IDCATEG_ORIG = ?", idCategoria).execute();
	}

	public boolean esNecesarioRecategorizar(String idCategoria,
			int consumoFacturado) {
		return (idCategoria.equals(IdCategoriaOrigen) && operadores.get(
				Condicion).evaluar(consumoFacturado, Valor));
	}

	/**
	 * ELimina todas las reclasificacions de categorias
	 */
	public static void eliminarTodo() {
		new Delete().from(ReclasificacionCategoria.class).execute();
	}

	// ------------------------ OPERADORES DE CONDICION---------------------
	/**
	 * Guarda los operadores logicos para poder ejecutar la <b>Condicion</b> que
	 * se tiene como cadena
	 */
	private Hashtable<String, OperadorLogico> operadores = new Hashtable<String, ReclasificacionCategoria.OperadorLogico>();

	/**
	 * Pone los operadores y sus respectivas clases del tipo OperadorLogico, con
	 * la Condicion como llave
	 */
	private void inicializarTablaOperadores() {
		operadores.put(">", new Mayor());
		operadores.put(">=", new MayorOIgual());
		operadores.put("<", new Menor());
		operadores.put("<=", new MenorOIgual());
		operadores.put("=", new Igual());
		operadores.put("<>", new Diferente());
	}

	/**
	 * Provee una interfaz de operadores lógicos para ser usada en el método
	 * <b>esNecesarioRecategorizar</b>
	 * 
	 * @author drodriguez
	 *
	 */
	private interface OperadorLogico {
		public boolean evaluar(int op1, int op2);
	}

	private class Mayor implements OperadorLogico {
		@Override
		public boolean evaluar(int op1, int op2) {
			return op1 > op2;
		}
	}

	private class MayorOIgual implements OperadorLogico {
		@Override
		public boolean evaluar(int op1, int op2) {
			return op1 >= op2;
		}
	}

	private class Menor implements OperadorLogico {
		@Override
		public boolean evaluar(int op1, int op2) {
			return op1 < op2;
		}
	}

	private class MenorOIgual implements OperadorLogico {
		@Override
		public boolean evaluar(int op1, int op2) {
			return op1 <= op2;
		}
	}

	private class Igual implements OperadorLogico {
		@Override
		public boolean evaluar(int op1, int op2) {
			return op1 == op2;
		}
	}

	private class Diferente implements OperadorLogico {
		@Override
		public boolean evaluar(int op1, int op2) {
			return op1 != op2;
		}
	}

}
