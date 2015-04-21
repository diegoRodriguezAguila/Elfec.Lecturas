package com.lecturas.elfec.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.lecturas.elfec.modelo.estadoslectura.*;

/**
 * Almacena la información sobre las rutas asignadas a un usuario de la tabla MOVILES.USUARIO_ASIGNACION
 * @author drodriguez
 */
@Table(name = "AsignacionesRutas")
public class AsignacionRuta extends Model {

	private static final long CONST_CUENTA = 100000;
	public AsignacionRuta()
	{
		super();
	}
	public AsignacionRuta(ResultSet rs) throws SQLException
	{
		super();
		UsuarioAsignado = rs.getString("USUARIO");
		Ruta = rs.getInt("RUTA");
		Dia = rs.getInt("DIA");
		Mes = rs.getInt("MES");
		Anio = rs.getInt("ANIO");
		OrdenInicio = rs.getInt("ORDEN_INICIO");
		OrdenFin = rs.getInt("ORDEN_FIN");
		Estado = rs.getInt("ESTADO");
		Estado++;//si es 1(ruta pendiente) se vuelve 2(ruta cargada a movil) y si es 6(relectura asignada) se vuelve 7 (relectura cargada a movil)
		CantidadLecturasRecibidas = rs.getInt("CANT_LEC_REC");
	}
	
	@Column(name = "UsuarioAsignado")
	public String UsuarioAsignado;
	
	@Column(name = "Ruta", notNull=true)
	public int Ruta;
	
	@Column(name = "Dia", notNull=true)
	public int Dia;
	
	@Column(name = "Anio", notNull=true)
	public int Anio;
	
	@Column(name = "Mes",  notNull=true)
    public int Mes;
	
	@Column(name = "OrdenInicio")
	public int OrdenInicio;
	
	@Column(name = "OrdenFin")
	public int OrdenFin;
	
	@Column(name = "Estado")
	public int Estado;
	
	@Column(name = "CantidadLecturasRecibidas")
	public int CantidadLecturasRecibidas;
	
	public int cantLecturasEnviadas;
	
	/**
	 * Obtiene la cuenta de inicio de la ruta
	 * @return
	 */
	public long obtenerCuentaInicio()
	{
		long rutaRes = Ruta*CONST_CUENTA;
		rutaRes+=OrdenInicio;
		return rutaRes;
	}
	
	/**
	 * Obtiene la cuenta de fin de la ruta
	 * @return
	 */
	public long obtenerCuentaFin()
	{
		long rutaRes = Ruta*CONST_CUENTA;
		rutaRes+=OrdenFin;
		return rutaRes;
	}
	
	/**
	 * Actualiza el estado de la asignacion de ruta segun el estado de las lecturas que pertenecen a ella
	 */
	public void asignarEstadoActualRuta()
	{
		List<Lectura> lecturasRuta = Lectura.obtenerLecturasRealizadasDeRuta(Ruta);
		int tam = lecturasRuta.size();
		int lecturasReintentar = 0;
		boolean hayLecReintentar = false;
		for (int i = 0; i < tam; i++) 
		{
			if(lecturasRuta.get(i).getEstadoLectura() instanceof ReIntentar)
			{
				hayLecReintentar=true;
				Estado = 4;
				lecturasReintentar++;
			}
		}
		cantLecturasEnviadas = tam-lecturasReintentar;
		if(!hayLecReintentar)
			Estado++;//si es 2(ruta cargada a movil) se vuelve 3(ruta descargada del movil) y si es 7(relectura cargada a movil) se vuelve 8 (relectura descargada del movil)
	}
	
	/**
	 * Obtiene todas las rutas asignadas al usuario
	 * @param usuario
	 * @return
	 */
	public static List<AsignacionRuta> obtenerRutasDeUsuario(String usuario)
	{
		return new Select().from(AsignacionRuta.class).where("UsuarioAsignado='"+usuario+"'").execute();
	}
	
	/**
	 * Obtiene todas las rutas de la base de datos SQLite
	 * @return
	 */
	public static List<AsignacionRuta> obtenerTodasLasRutas()
	{
		return new Select().from(AsignacionRuta.class).execute();
	}
	
	/**
	 * Obtiene la ruta pero con el formato de cuenta, es decir que una ruta 3320 devolvería 03-320
	 * @return
	 */
	public String obtenerRutaFormatoCuenta()
	{
		StringBuilder ruta = new StringBuilder(""+Ruta);
		if(ruta.length()<5)
		{
			ruta.insert(0, "0");
		}
		ruta.insert(2, "-");
		return ruta.toString();
	}
}
