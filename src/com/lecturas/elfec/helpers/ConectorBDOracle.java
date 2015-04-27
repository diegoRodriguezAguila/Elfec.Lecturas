package com.lecturas.elfec.helpers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.lecturas.elfec.modelo.AsignacionRuta;
import com.lecturas.elfec.modelo.BaseCalculo;
import com.lecturas.elfec.modelo.BaseCalculoConcepto;
import com.lecturas.elfec.modelo.Concepto;
import com.lecturas.elfec.modelo.ConceptoLectura;
import com.lecturas.elfec.modelo.ConceptoCategoria;
import com.lecturas.elfec.modelo.ConceptoTarifa;
import com.lecturas.elfec.modelo.EvolucionConsumo;
import com.lecturas.elfec.modelo.Lectura;
import com.lecturas.elfec.modelo.MedidorEntreLineas;
import com.lecturas.elfec.modelo.Ordenativo;
import com.lecturas.elfec.modelo.OrdenativoLectura;
import com.lecturas.elfec.modelo.PermisoRestriccion;
import com.lecturas.elfec.modelo.Potencia;
import com.lecturas.elfec.modelo.PreferenciaUI;
import com.lecturas.elfec.modelo.ReclasificacionCategoria;
import com.lecturas.elfec.modelo.TokenServicioWeb;
import com.lecturas.elfec.modelo.Usuario;

/**
 * Se encarga de la conexi�n con la base de datos oracle, y la importaci�n y exportaci�n de �sta.
 * @author drodriguez
 *
 */
public class ConectorBDOracle {
	private Connection conn=null;
	private Statement stmt=null;
	private ResultSet rs=null;
	private Calendar calendar = Calendar.getInstance();
	
	/**
	 * Crea una nueva conexion con la base de datos Oracle, usando la informaci�n de conexi�n del archivo JSON de configuraci�n
	 * que se encuentra en la carpeta <b>assets</b> o con la configuraci�n configurada desde el dispositivo
	 * @param contexto, la actividad de la que se llama el conector, necesaria para poder acceder a la carpeta <b>assets</b>
	 * @param habilitarRol, indica si se debe habilitar o no el rol de lecturas para esta conexi�n
	 */
	public ConectorBDOracle(Context contexto, boolean habilitarRol) 
	{
		try 
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			JSONObject configBD = LectorConfigBD.obtenerConfiguracion(contexto);
			String url = "jdbc:oracle:thin:@//"+configBD.getString("ip")+":"+configBD.getString("puerto")+"/"+configBD.getString("servicio");
			conn = DriverManager.getConnection(url, VariablesDeSesion.getUsuarioLogeado(), VariablesDeSesion.getPasswordUsuario());
			stmt = conn.createStatement();
			if(habilitarRol)
				habilitarRolLecturas(contexto);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Hbailita el rol de LECTURAS para el usuario logeado con el que se realiza la conexi�n
	 * este metodo se llama con el parametro del constructor y deberia activarse para cualquier 
	 * conexion a la base de datos oracle que requiera acceso a las tablas espec�ficas de lecturas
	 * @param contexto, la actividad de la que se llama el conector, necesaria para poder acceder a la carpeta <b>assets</b>
	 * @throws JSONException 
	 */
	private void habilitarRolLecturas(Context contexto) throws JSONException
	{
		try
		{
			JSONObject configBD = LectorConfigBD.obtenerConfiguracion(contexto);
			String query = "SET ROLE "+configBD.getString("rol")+(configBD.getString("password").equals("")?"":(" IDENTIFIED BY \""+configBD.getString("password")+"\""));
			stmt.executeUpdate(query);
		}
		catch(SQLException e){
			Log.d("com.lecturas.elfec", e.getCause()+": "+ e.getMessage());
		} 
	}
	
	
	/**
	 * Importa todos los datos diarios, son las tablas que corresponden a los modelos: Lectura, Potencia, Ordenativo,
	 * OrdenativoLectura, ConceptoLectura, EvolucionConsumo y MedidorEntreLinea. Tambi�n se encarga de actualizar la tabla
	 * de asignaciones MOVILES.USUARIO_ASIGNACION a los estados correspondientes.
	 * @return true, si se lograron importar todos los datos
	 */
	public boolean importarDatosDiariosDeOracle()
	{
		boolean rutasImportadas = importarRutasAsignadas();
		List<AsignacionRuta> rutas = AsignacionRuta.obtenerRutasDeUsuario(VariablesDeSesion.getUsuarioLogeado());
		return rutasImportadas && importarLecturasPotenciasConceptosYEvConsumos(rutas) && importarOrdenativos() 
				&& importarParametrizables();
	}
	
	/**
	 * Verifica el cuadro tarifario actual y si es necesario importa todos los datos mensuales, son las tablas que corresponden a los modelos: BaseCalculo, BaseCalculoConcepto, 
	 * Concepto, ConceptoCategoria y ReclasificacionCategoria.
	 * <br><br><b>NOTA.-</b> Se debe llamar antes de importar datos diarios dado que verifica
	 * que el cuadro tarifario sea el actual, si no lo es, elimina todos los datos mensuales y diarios.
	 * @return true, si se lograrion importar todos los datos
	 */
	public boolean importarDatosMensualesDeOracle()
	{
		if(!GestionadorBDSQLite.idCuadroTarifarioEsActual())
		{
			GestionadorBDSQLite.eliminarDatosDiariosExceptoUsuario();
			GestionadorBDSQLite.eliminarDatosMensuales();
			return importarConceptosCategoriasTarifasGruposYBasesCalculo() && importarReclasificacionCategorias();
		}
		return false;
	}
	
	/**
	 * Importa las lecturas y todos sus datos relativos, que son las potencias, conceptos y evolucion de consumos asociados
	 * a ella. Se obtiene segun las rutas proporcionadas
	 * @param rutas
	 * @return
	 */
	private boolean importarLecturasPotenciasConceptosYEvConsumos(List<AsignacionRuta> rutas)
	{
		try
		{
			List<Lectura> listaLec = new ArrayList<Lectura>();
			List<Potencia> listaPot = new ArrayList<Potencia>();
			List<ConceptoLectura> listaConceptos = new ArrayList<ConceptoLectura>();
			List<EvolucionConsumo> listaEvCons = new ArrayList<EvolucionConsumo>();
			for(AsignacionRuta ruta : rutas)
			{
				List<Lectura> sigListaLec = obtenerLecturasPorRuta(ruta);
				String listLecCondicion = convertirListaACondicion(sigListaLec);
				listaLec.addAll(sigListaLec);
				listaPot.addAll(obtenerPotenciasPorRuta(ruta.Ruta, listLecCondicion));
				listaConceptos.addAll(obtenerConceptosPorRuta(ruta.Ruta, listLecCondicion));
				listaEvCons.addAll(obtenerEvolucionConsumosPorRuta(ruta.Ruta, listLecCondicion));
			}
			ActiveAndroid.beginTransaction();
			for(Potencia pot : listaPot)
			{
				pot.save();
			}
			for(EvolucionConsumo evCons : listaEvCons)
			{
				evCons.save();
			}
			for(Lectura lec : listaLec)
			{
				lec.PotenciaLectura = Potencia.obtenerPotencia(lec.Suministro, lec.Mes, lec.Anio);
				lec.save();
			}
			for(ConceptoLectura conc : listaConceptos)
			{
				Lectura lectura=ConceptoLectura
						.obtenerLectura(conc.Suministro, conc.Mes, conc.Anio);
				BaseCalculoConcepto baseCalculoConcepto = BaseCalculoConcepto
						.obtenerBaseCalculoConcepto(conc.ConceptoCodigo);
				conc.Lectura = lectura;
				conc.OrdenImpresion = baseCalculoConcepto.BaseCalculo.OrdenImpresion;
				conc.AreaImpresion = baseCalculoConcepto.Concepto.AreaImpresion;
				conc.save();
			}
			ActiveAndroid.setTransactionSuccessful();
			return true;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(ActiveAndroid.inTransaction())
				ActiveAndroid.endTransaction();
		}
	}
	
	/**
	 * Convierte la lista de lecturas a una cadena que sirve de condicion en SQL
	 * esta cadena es una lista de suministros entre parentesis, separadas por comas (4324,23423,3555)
	 * @param sigListaLec
	 * @return
	 */
	private String convertirListaACondicion(List<Lectura> sigListaLec) {
		StringBuilder suministros = new StringBuilder("(");
		for (Lectura lectura : sigListaLec) 
		{
			suministros.append(lectura.Suministro).append(",");
		}
		suministros.setCharAt(suministros.length()-1, ')');
		return suministros.toString();
	}

	/**
	 * Importa los ordenativos de la base de datos oracle, ERP_ELFEC.TIPOS_NOV_SUM y los inserta en la base
	 * de datos local SQLite
	 * @return
	 */
	private boolean importarOrdenativos()
	{
		try 
		{
			List<Ordenativo> listaOrdenativos = obtenerOrdenativos();
			ActiveAndroid.beginTransaction();
			for(Ordenativo ord : listaOrdenativos)
			{
				ord.save();
			}
			ActiveAndroid.setTransactionSuccessful();
			return true;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(ActiveAndroid.inTransaction())
				ActiveAndroid.endTransaction();
		}
	}
	/**
	 * Importa todas las lecturas correspondientes a la ruta propocionada.
	 * Toma en cuenta el dia, mes y a�o para realizar la importaci�n y lo realiza de la tabla
	 * MOVILES.LECTURAS
	 * @param ruta
	 * @return
	 * @throws SQLException
	 */
	private List<Lectura> obtenerLecturasPorRuta(AsignacionRuta ruta) throws SQLException 
	{
		List<Lectura> lista = new ArrayList<Lectura>();
		StringBuilder query = new StringBuilder();
		if(ruta.Estado==2)
		{
			query  = new StringBuilder("SELECT * FROM MOVILES.LECTURAS WHERE LEMRUT=");
			query.append(ruta.Ruta) 
					.append(" AND LEMARE=").append(ruta.Dia)
					.append(" AND LEMMES=").append(ruta.Mes) 
					.append(" AND LEMANO=").append(ruta.Anio)
					.append(" AND TO_NUMBER(LEMCTAANT,'9999999999')>=").append(ruta.obtenerCuentaInicio())
					.append(" AND TO_NUMBER(LEMCTAANT,'9999999999')<=").append(ruta.obtenerCuentaFin());
		}
		if(ruta.Estado==7)
		{
			query = new StringBuilder("SELECT * FROM MOVILES.LECTURAS a WHERE EXISTS (SELECT 1 FROM ERP_ELFEC.SGC_MOVIL_LECTURAS b"+
				" WHERE a.LEMSUM=b.NUS AND a.LEMMES=b.MES AND a.LEMANO=b.ANIO AND a.LEMRUT=b.RUTA AND b.ESTADO=4) AND LEMRUT=");
			query.append(ruta.Ruta);
		}
		rs = stmt.executeQuery(query.toString());
		while (rs.next()) 
		{
			lista.add(new Lectura(rs));
		}
		return lista;
	}
	
	
	
	/**
	 * Importa todas las potencias asociadas a lecturas correspondientes a la ruta propocionada.
	 * Toma en cuenta el dia, mes y a�o para realizar la importaci�n y lo realiza de la tabla
	 * MOVILES.LECTURASP
	 * @param ruta
	 * @param listLecCondicion 
	 * @return
	 * @throws SQLException
	 */
	private List<Potencia> obtenerPotenciasPorRuta(int ruta, String listLecCondicion) throws SQLException
	{
		List<Potencia> lista = new ArrayList<Potencia>();
		StringBuilder query = new StringBuilder("SELECT * FROM MOVILES.LECTURASP WHERE LEMRUT=");
		rs = stmt.executeQuery(query.append(ruta).append(" AND LEMMES=").append((calendar.get(Calendar.MONTH)+1)) 
				.append(" AND LEMANO=").append(calendar.get(Calendar.YEAR))
				.append(" AND LEMSUM IN ").append(listLecCondicion).toString());
		while (rs.next()) 
		{
			lista.add(new Potencia(rs));
		}
		return lista;
	}
	
	private List<ConceptoLectura> obtenerConceptosPorRuta(int ruta, String listLecCondicion) throws SQLException
	{
		List<ConceptoLectura> lista = new ArrayList<ConceptoLectura>();
		StringBuilder query = new StringBuilder("SELECT * FROM MOVILES.LECTURASCONCEPTOS WHERE LEMRUT=");
		rs = stmt.executeQuery(query.append(ruta).append(" AND LEMMES=").append((calendar.get(Calendar.MONTH)+1)) 
				.append(" AND LEMANO=").append(calendar.get(Calendar.YEAR))
				.append(" AND LEMSUM IN ").append(listLecCondicion).toString());
		while (rs.next()) 
		{
			lista.add(new ConceptoLectura(rs));
		}
		return lista;
	}
	
	private List<EvolucionConsumo> obtenerEvolucionConsumosPorRuta(int ruta, String listLecCondicion) throws SQLException
	{
		List<EvolucionConsumo> lista = new ArrayList<EvolucionConsumo>();
		StringBuilder query = new StringBuilder("SELECT * FROM MOVILES.LECTURASC WHERE LEMRUT="); 
		rs = stmt.executeQuery(query.append(ruta).append(" AND LEMMES=").append((calendar.get(Calendar.MONTH)+1)) 
				.append(" AND LEMANO=").append(calendar.get(Calendar.YEAR))
				.append(" AND LEMSUM IN ").append(listLecCondicion).toString());
		while (rs.next()) 
		{
			lista.add(new EvolucionConsumo(rs));
		}
		return lista;
	}
	
	private List<Ordenativo> obtenerOrdenativos() throws SQLException
	{
		List<Ordenativo> lista = new ArrayList<Ordenativo>();
		rs = stmt.executeQuery("SELECT * FROM ERP_ELFEC.TIPOS_NOV_SUM");
		while (rs.next()) 
		{
			lista.add(new Ordenativo(rs));
		}
		return lista;
	}
	
	private boolean importarConceptosCategoriasTarifasGruposYBasesCalculo()
	{
		try 
		{
			List<Concepto> listaConceptos = obtenerConceptos();
			List<ConceptoCategoria> listaCategorias = obtenerConceptosCategorias();
			List<ConceptoTarifa> listaTarifas = obtenerConceptosTarifas();
			List<BaseCalculo> listaBasesCalculo = obtenerBasesCalculo();
			List<BaseCalculoConcepto> listaBasesCalculoConceptos = obtenerBasesCalculoConceptos();
			ActiveAndroid.beginTransaction();
			for(Concepto concep : listaConceptos)
			{
				concep.save();
			}
			for(ConceptoCategoria cat : listaCategorias)
			{
				cat.Concepto = Concepto.obtenerConcepto(cat.IdConcepto, cat.IdSubConcepto);
				cat.save();
			}
			for(ConceptoTarifa tarif : listaTarifas)
			{
				tarif.Concepto = Concepto.obtenerConcepto(tarif.IdConcepto, tarif.IdSubConcepto);
				tarif.save();
			}
			for(BaseCalculo basCalc : listaBasesCalculo)
			{
				basCalc.save();
			}
			for(BaseCalculoConcepto basCalcCon : listaBasesCalculoConceptos)
			{
				BaseCalculo baseCalc = BaseCalculo.obtenerBaseDeCalculo(basCalcCon.IdBaseCalculo);
				Concepto concepto = Concepto.obtenerConcepto(basCalcCon.IdConcepto, basCalcCon.IdSubConcepto);
				if(baseCalc!=null && concepto!=null)
				{
					basCalcCon.BaseCalculo = baseCalc;
					basCalcCon.Concepto = concepto;
					basCalcCon.save();
				}
			}
			ActiveAndroid.setTransactionSuccessful();
			return true;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(ActiveAndroid.inTransaction())
				ActiveAndroid.endTransaction();
		}
	}
	
	
	private List<ConceptoCategoria> obtenerConceptosCategorias() throws SQLException
	{
		List<ConceptoCategoria> lista = new ArrayList<ConceptoCategoria>();
		int idCuadroTarifario = ((calendar.get(Calendar.YEAR)-2000)*100)+(calendar.get(Calendar.MONTH)+1);
		rs = stmt.executeQuery("SELECT * FROM ERP_ELFEC.CPTOS_CATEGORIAS WHERE IDCUADRO_TARIF = "+idCuadroTarifario
				+" AND IDCONCEPTO >=10000 AND IDCONCEPTO<12000");
		while (rs.next()) 
		{
			lista.add(new ConceptoCategoria(rs));
		}
		return lista;
	}
	
	private List<ConceptoTarifa> obtenerConceptosTarifas() throws SQLException
	{
		List<ConceptoTarifa> lista = new ArrayList<ConceptoTarifa>();
		int idCuadroTarifario = ((calendar.get(Calendar.YEAR)-2000)*100)+(calendar.get(Calendar.MONTH)+1);
		rs = stmt.executeQuery("SELECT * FROM ERP_ELFEC.CONCEPTOS_TARIFAS WHERE IDCUADRO_TARIF = "+idCuadroTarifario);
		while (rs.next()) 
		{
			lista.add(new ConceptoTarifa(rs));
		}
		return lista;
	}
	
	private List<BaseCalculo> obtenerBasesCalculo() throws SQLException
	{
		List<BaseCalculo> lista = new ArrayList<BaseCalculo>();
		rs = stmt.executeQuery("SELECT IDBASE_CALCULO,DESCRIPCION,"+
					"(SELECT ORDEN_IMPRESION FROM ERP_ELFEC.GBASES_CALC_IMP I WHERE I.IDBASE_CALCULO=B.IDBASE_CALCULO) AS ORDEN_IMPRESION "+
				"FROM ERP_ELFEC.GBASES_CALC B");
		while (rs.next()) 
		{
			lista.add(new BaseCalculo(rs));
		}
		return lista;
	}
	
	private List<BaseCalculoConcepto> obtenerBasesCalculoConceptos() throws SQLException
	{
		List<BaseCalculoConcepto> lista = new ArrayList<BaseCalculoConcepto>();
		rs = stmt.executeQuery("SELECT * FROM ERP_ELFEC.GBASES_CALC_CPTOS WHERE IDCONCEPTO>=10000 AND IDCONCEPTO<14000");
		while (rs.next()) 
		{
			lista.add(new BaseCalculoConcepto(rs));
		}
		return lista;
	}
	
	
	private List<Concepto> obtenerConceptos() throws SQLException
	{
		List<Concepto> lista = new ArrayList<Concepto>();
		rs = stmt.executeQuery("SELECT * FROM ERP_ELFEC.CONCEPTOS WHERE IDCONCEPTO>=10000 AND IDCONCEPTO<12000");
		while (rs.next()) 
		{
			lista.add(new Concepto(rs));
		}
		return lista;
	}
	
	private boolean importarReclasificacionCategorias()
	{
		try 
		{
			List<ReclasificacionCategoria> listaReclasifCats = obtenerReclasificacionCategorias();
			ActiveAndroid.beginTransaction();
			for(ReclasificacionCategoria reclasifCat : listaReclasifCats)
			{
				reclasifCat.save();
			}
			ActiveAndroid.setTransactionSuccessful();
			return true;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(ActiveAndroid.inTransaction())
				ActiveAndroid.endTransaction();
		}
	}
	
	private List<ReclasificacionCategoria> obtenerReclasificacionCategorias() throws SQLException
	{
		List<ReclasificacionCategoria> lista = new ArrayList<ReclasificacionCategoria>();
		rs = stmt.executeQuery("SELECT CR.IDCATEG_ORIG,CR.IDCATEG_DEST,CR.CONDICION,CR.VALOR,FAM.RECSGLDES "
				+"FROM ERP_ELFEC.CATEG_RECLASIF CR, MOVILES.RECATEGORIZACION FAM "+
				"WHERE CR.IDCATEG_ORIG=FAM.RECCATORI AND CR.IDCATEG_DEST=FAM.RECCATDES AND CR.VALOR=FAM.RECVAL");
		while (rs.next()) 
		{
			lista.add(new ReclasificacionCategoria(rs));
		}
		return lista;
	}
	
	
	/**
	 * Se conecta al servidor Oracle y obtiene la fecha y hora actuales
	 * @return Date
	 * @throws SQLException
	 * @throws ParseException
	 */
	public Date obtenerFechaDelServidor() throws SQLException, ParseException, NullPointerException
	{
		rs = stmt.executeQuery("SELECT SYSDATE FROM dual");
		while (rs.next()) 
		{
			String fecha =  rs.getString("SYSDATE");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault());
			return df.parse(fecha); 
		}
		return null;
	}
	
	/**
	 * Se conecta a la base de datos Oracle y descarga en ella las lecturas, medidores entre lineas y ordenativos
	 * y actualiza el estado de las rutas al estado correspondiente segun las lecturas realizadas
	 * @return
	 */
	public boolean exportarInformacionAlServidor()
	{
		try
		{
			exportarLecturas();
			exportarOrdenativos();
			exportarLecturasEntreLineas();
			List<AsignacionRuta> listaRutas = AsignacionRuta.obtenerTodasLasRutas();
			int tam = listaRutas.size();
			for (int i = 0; i < tam; i++) 
			{
				listaRutas.get(i).asignarEstadoActualRuta();
			}
			actualizarEstadoRutas(listaRutas,true);
			actualizarEquivalentesReintentar(listaRutas);
			return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Exporta las lecturas a la tabla ERP_ELFEC.SGC_MOVIL_LECTURAS
	 * @throws SQLException 
	 */
	private void exportarLecturas() throws SQLException
	{
		List<Lectura> lecturas = Lectura.obtenerLecturasNoEnviadas3G();
		for(Lectura lec : lecturas)
		{
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
			String fechaHora = df.format(lec.FechaLecturaActual)+" "+lec.HoraLectura;
			String query = "INSERT INTO ERP_ELFEC.SGC_MOVIL_LECTURAS VALUES " +
					"("+lec.Ruta+", "+lec.Anio+", "+lec.Mes+", "+lec.Dia+", "+lec.Suministro+
				    ", '"+lec.NumeroMedidor+"', "+lec.LecturaNueva+", "+
				((lec.PotenciaLectura==null || lec.PotenciaLectura.LecturaNuevaPotencia==null)?"NULL":lec.PotenciaLectura.LecturaNuevaPotencia.toPlainString())+
				", "+((lec.PotenciaLectura==null || lec.PotenciaLectura.Reactiva==null)?"NULL":lec.PotenciaLectura.Reactiva.toPlainString())+
				", TO_DATE('"+fechaHora+"', 'dd/mm/yyyy hh24:mi:ss'), "+lec.GPSLatitud+", "+lec.GPSLongitud+", UPPER('"+lec.UsuarioAuditoria+"'), "+
				lec.getEstadoLectura().getEstadoEntero()+", "+(lec.Recordatorio==null?"NULL":("'"+lec.Recordatorio+"'"))+", "+
				"USER , SYSDATE, "+(lec.ImporteTotal!=null?lec.ImporteTotal.toString():"NULL")+", "+
				((lec.PotenciaLectura==null)?"-1":lec.PotenciaLectura.ConsumoFacturado)+")";
			stmt.executeUpdate(query);
			lec.Enviado3G = 2;//2 se envi� al servidor, no necesariamente por 3G
			lec.save();
		}
	}
	
	/**
	 * Exporta los ordenativos de las lecturas a la tabla ERP_ELFEC.SGC_MOVIL_ORDENATIVOS
	 * @throws SQLException 
	 */
	private void exportarOrdenativos() throws SQLException
	{
		List<OrdenativoLectura> ordslecturas = OrdenativoLectura.obtenerLecturasConOrdenativosNoEnviados3G();
		for(OrdenativoLectura ordLec : ordslecturas)
		{
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
			String fechaHora = df.format(ordLec.Fecha)+" "+ordLec.Hora;
			String query = "INSERT INTO ERP_ELFEC.SGC_MOVIL_ORDENATIVOS VALUES " +
				"("+ordLec.Lectura.Ruta+", "+ordLec.Lectura.Anio+", "+ordLec.Lectura.Mes+", "+ordLec.Lectura.Dia+", "+ordLec.Lectura.Suministro+
			    ", "+ordLec.Ordenativo.Codigo+", TO_DATE('"+
				fechaHora+"', 'dd/mm/yyyy hh24:mi:ss'), UPPER('"+ordLec.UsuarioAuditoria+"'), USER , SYSDATE)";
			stmt.executeUpdate(query);
			ordLec.Enviado3G = 2;//2 se envi� al servidor, no necesariamente por 3G
			ordLec.save();
		}
	}
	
	/**
	 * Exporta las lecturas de medidores entre lineas a la tabla ERP_ELFEC.SGC_MOVIL_LECT_ENTRE_LINEAS
	 * @throws SQLException 
	 */
	private void exportarLecturasEntreLineas() throws SQLException
	{
		List<MedidorEntreLineas> lecturasEntreLineas = MedidorEntreLineas.obtenerMedidoresEntreLineasNoEnviados3G();
		for(MedidorEntreLineas lecEL : lecturasEntreLineas)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lecEL.FechaLectura);
			int anio = calendar.get(Calendar.YEAR), mes = (calendar.get(Calendar.MONTH)+1), dia = calendar.get(Calendar.DAY_OF_MONTH);
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
			String fechaHora = df.format(lecEL.FechaLectura)+" "+lecEL.HoraLectura;
			String query = "INSERT INTO ERP_ELFEC.SGC_MOVIL_LECT_ENTRE_LINEAS VALUES " +
					"("+lecEL.Ruta+", "+anio+", "+mes+", "+dia+", NULL"+
				    ", '"+lecEL.NumeroMedidor+"', "+lecEL.LecturaNueva+", "+
				(lecEL.LecturaPotencia==null?"NULL":lecEL.LecturaPotencia.toPlainString())+
				", "+(lecEL.Reactiva==null?"NULL":lecEL.Reactiva.toPlainString())+", TO_DATE('"+
				fechaHora+"', 'dd/mm/yyyy hh24:mi:ss'), "+lecEL.GPSLatitud+", "+lecEL.GPSLongitud+", UPPER('"+lecEL.UsuarioAuditoria+"'), USER , SYSDATE)";
			stmt.executeUpdate(query);
			lecEL.Enviado3G = 2;//2 se envi� al servidor, no necesariamente por 3G
			lecEL.save();
		}
	}
	
	/**
	 * Importa las variables parametrizables de las tablas:  ERP_ELFEC.SGC_MOVIL_PARAM
	 * ERP_ELFEC.SGC_MOVIL_PARAM_CATEG_NO_CFIJO Y ERP_ELFEC.SGC_MOVIL_PARAM_COD_ORD_RES
	 * y las guarda en archivos JSON para su uso por la clase estatica VariablesDeEntorno
	 */
	public boolean importarParametrizables()
	{
		try 
		{
			return obtenerParametrosGenerales() && obtenerParamCodOrdenativosResumen() 
					&& obtenerParamCategsNoMuestraCargoFijo();
		} 
		catch (SQLException e) 
		{
			return false;
		} 
		catch (JSONException e) 
		{
			return false;
		}
		catch (Exception e) 
		{
			return false;
		}
	}
	
	/**
	 * Importa las variables parametrizables de las tabla  ERP_ELFEC.SGC_MOVIL_PARAM
	 * y las guarda en archivos JSON para su uso por la clase estatica VariablesDeEntorno
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	private boolean obtenerParametrosGenerales() throws SQLException, JSONException
	{
		rs = stmt.executeQuery("SELECT * FROM ERP_ELFEC.SGC_MOVIL_PARAM WHERE ESTADO=1");
		if(rs.next())
		{
			JSONObject parametrosGenerales = ManejadorJSON.convertirResultSetAJSON(rs);
			return ManejadorJSON.guardarJSONEnArchivo(parametrosGenerales.toString(), ConstantesDeEntorno.archivoParametrosGrales);
		}
		return false;
	}
	
	/**
	 * Importa las variables parametrizables de las tabla  ERP_ELFEC.SGC_MOVIL_PARAM_COD_ORD_RES
	 * y las guarda en archivos JSON para su uso por la clase estatica VariablesDeEntorno
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	private boolean obtenerParamCodOrdenativosResumen() throws SQLException, JSONException
	{
		rs = stmt.executeQuery("SELECT CODIGO FROM ERP_ELFEC.SGC_MOVIL_PARAM_COD_ORD_RES WHERE ESTADO=1");
		JSONArray params = new JSONArray();
		while(rs.next())
		{
			JSONObject paramResumenOrdenativos = ManejadorJSON.convertirResultSetAJSON(rs);
			params.put(paramResumenOrdenativos);
		}
		return ManejadorJSON.guardarJSONEnArchivo(params.toString(), ConstantesDeEntorno.archivoCodsResumenOrdenativos);
	}
	
	/**
	 * Importa las variables parametrizables de las tabla  ERP_ELFEC.SGC_MOVIL_PARAM_CATEG_NO_CFIJO
	 * y las guarda en archivos JSON para su uso por la clase estatica VariablesDeEntorno
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	private boolean obtenerParamCategsNoMuestraCargoFijo() throws SQLException, JSONException
	{
		rs = stmt.executeQuery("SELECT CATEGORIA FROM ERP_ELFEC.SGC_MOVIL_PARAM_CATEG_NO_CFIJO WHERE ESTADO=1");
		JSONArray params = new JSONArray();
		while(rs.next())
		{
			JSONObject paramCategsNoMostrarCargoFijo = ManejadorJSON.convertirResultSetAJSON(rs);
			params.put(paramCategsNoMostrarCargoFijo);
		}
		return ManejadorJSON.guardarJSONEnArchivo(params.toString(), ConstantesDeEntorno.archivoCategsNoMostrarCargoFijo);
	}
	
	/**
	 * Se conecta a la base de datos oracle y obtiene al usuario solitictado, en caso de no existir, existira un error de conexion
	 * @return
	 */
	public Usuario obtenerUsuario(String username) throws SQLException
	{
		rs = stmt.executeQuery("SELECT * FROM MOVILES.USUARIO_APP WHERE APLICACION='Lecturas Moviles' AND UPPER(USUARIO)=UPPER('"+username+"')");
		while(rs.next())
		{
			return new Usuario(rs);
		}
		return null;
	}
	
	/**
	 * Se conecta a la base de datos oracle y obtiene los permisos y restricciones del perfil solitictado de
	 * la tabla MOVILES.PERFIL_APP_OPCIONESy los guarda
	 * @return
	 */
	public boolean importarPermisosPerfilUsuario(String perfil)
	{
		try
		{
			rs = stmt.executeQuery("SELECT * FROM MOVILES.PERFIL_APP_OPCIONES WHERE APP='Lecturas Moviles' AND PERFIL='"+perfil+"' AND ESTADO=1");
			while(rs.next())
			{
				(new PermisoRestriccion(rs)).save();
			}
			return true;
		}
		catch (SQLException e) 
		{
			return false;
		} 
		catch (Exception e) 
		{
			return false;
		}
	}
	
	/**
	 * Se conecta a la base de datos oracle y obtiene las preferencias de UI del usuario solitictado de
	 * la tabla MOVILES.USUARIO_PREFERENCIAS_UI y los guarda.
	 * @return
	 */
	public boolean importarPreferenciasUIUsuario(String usuario)
	{
		try
		{
			rs = stmt.executeQuery("SELECT * FROM MOVILES.USUARIO_PREFERENCIAS_UI WHERE UPPER(USUARIO)=UPPER('"+usuario+"') AND ESTADO=1");
			while(rs.next())
			{
				(new PreferenciaUI(rs)).save();
			}
			return true;
		}
		catch (SQLException e) 
		{
			return false;
		} 
		catch (Exception e) 
		{
			return false;
		}
	}
	
	/**
	 * Se conecta a la base de datos oracle y obtiene los tokens para la conexi�n al servicio web 
	 * a partir de la fecha de sincronizacion de la cuenta del usuario hasta el rango de dias que
	 * tiene asignados, el rango de dias no puede ser cero.
	 * del usuario, de la vista MOVILES.V_TOKEN_LEC
	 * @param usuario
	 * @return
	 */
	public boolean importarTokensUsuario(Usuario usuario)
	{
		try
		{	
			for (int i = 0; i < usuario.RangoDias; i++) 
			{
				StringBuilder query = new StringBuilder("SELECT MOVILES.f_token(");
				query.append(i).append(") AS TOKEN, (SYSDATE+").append(i)
				.append(") AS FECHA, USER AS USUARIO FROM DUAL"); 
				rs = stmt.executeQuery(query.toString());
				while(rs.next())
				{
					(new TokenServicioWeb(rs)).save();
				}
			}
			return true;
		}
		catch (SQLException e) 
		{
			return false;
		} 
		catch (Exception e) 
		{
			return false;
		}
	}
	/**
	 * Se conecta a la base de datos oracle y verifica si el imei propocionado esta aurorizado para usar la aplicaci�n
	 * para ello accede a la tabla MOVILES.IMEI_APP y ve que exista ese IMEI y que tenga estado 1 es decir que est� activo
	 * @param imei
	 * @return
	 * @throws SQLException
	 */
	public boolean validarExistenciaIMEI(String imei) throws SQLException
	{
		rs = stmt.executeQuery("SELECT * FROM MOVILES.IMEI_APP WHERE APLICACION='Lecturas Moviles' AND IMEI='"+imei+"' AND ESTADO=1");
		while(rs.next())
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Realiza la importaci�n de las rutas asignadas para el usuario en la fecha actual y actualiza el estado
	 * de las asignaciones de rutas a 2 (Descargadas) o a 7 en caso de ser una ruta de relectura
	 * @return
	 */
	public boolean importarRutasAsignadas()
	{
		try 
		{
			List<AsignacionRuta> listaAsignacionRutas = obtenerRutasAsignadas(VariablesDeSesion.getUsuarioLogeado());
			ActiveAndroid.beginTransaction();
			for(AsignacionRuta asignRuta : listaAsignacionRutas)
			{
				asignRuta.save();
			}
			ActiveAndroid.setTransactionSuccessful();
			actualizarEstadoRutas(listaAsignacionRutas,false);
			return true;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(ActiveAndroid.inTransaction())
				ActiveAndroid.endTransaction();
		}
	}
	
	/**
	 * Se conecta a la base de datos oracle y obtiene las rutas asignadas al usuario para ese dia
	 * @param usuario
	 * @return
	 * @throws SQLException
	 */
	public List<AsignacionRuta> obtenerRutasAsignadas(String usuario) throws SQLException
	{
		List<AsignacionRuta> lista = new ArrayList<AsignacionRuta>();
		rs = stmt.executeQuery("SELECT * FROM MOVILES.USUARIO_ASIGNACION WHERE UPPER(USUARIO)=UPPER('" + usuario + 
				"') AND DIA_ASIG_CARGA="+calendar.get(Calendar.DAY_OF_MONTH)+" AND MES="+(calendar.get(Calendar.MONTH)+1)+ 
				" AND ANIO="+calendar.get(Calendar.YEAR)+" AND (ESTADO=1 OR ESTADO=6)");
		while (rs.next())
		{
			lista.add(new AsignacionRuta(rs));
		}
		return lista;
	}
	
	/**
	 * Se conecta a la base de datos y pone el estado de las rutas de la base de datos SQLite a las rutas de la 
	 * tabla MOVILES.USUARIO_ASIGNACION, segun la lista de rutas proporcionada
	 * proporcionado
	 * @param listaRutas
	 * @param actualizarCantLecturasRecibidas
	 * @throws SQLException 
	 */
	public void actualizarEstadoRutas(List<AsignacionRuta> listaRutas, boolean actualizarCantLecturasRecibidas) throws SQLException
	{
		String query;
		String datos;
		String condicion;
		for(AsignacionRuta ruta : listaRutas)
		{
			query = "UPDATE MOVILES.USUARIO_ASIGNACION";
			datos = " SET ESTADO="+ruta.Estado+(actualizarCantLecturasRecibidas?", CANT_LEC_REC="+ruta.cantLecturasEnviadas:"");
			condicion = " WHERE UPPER(USUARIO)=UPPER('" + ruta.UsuarioAsignado + 
					"') AND DIA="+ruta.Dia+" AND MES="+ruta.Mes+ 
					" AND ANIO="+ruta.Anio+" AND RUTA="+ruta.Ruta;
			stmt.executeUpdate(query+datos+condicion);
		}
	}
	
	/**
	 * busca una asignaci�n de ruta seg�n los par�metros especificados
	 * @param usuario
	 * @param ruta
	 * @param ordenInicio
	 * @param ordenFin
	 * @param anio
	 * @param mes
	 * @param estado
	 * @return
	 * @throws SQLException 
	 */
	public AsignacionRuta buscarAsignacionRuta(int ruta, int ordenInicio, int ordenFin, int anio, int mes, int estado ) throws SQLException
	{
		AsignacionRuta asigRuta=null;
		StringBuilder query = new StringBuilder("SELECT * FROM MOVILES.USUARIO_ASIGNACION WHERE RUTA=");
		query.append(ruta).append(" AND MES=").append(mes) 
				.append(" AND ORDEN_INICIO=").append(ordenInicio)
				.append(" AND ORDEN_FIN=").append(ordenFin)
				.append(" AND ANIO=").append(anio).append(" AND ESTADO=").append(estado);
		rs = stmt.executeQuery(query.toString());
		while (rs.next())
		{
			asigRuta = new AsignacionRuta(rs);
		}
		return asigRuta;
	}
	
	/**
	 * Se conecta a la base de datos y actualiza el estado de las asignaciones de rutas originales (Estado 4) al estado
	 * de completadas (Estado 3) de las cuales salieron las asignaciones de reintentar de la 
	 * tabla MOVILES.USUARIO_ASIGNACION, segun la lista de rutas proporcionada.
	 * proporcionada
	 * @param listaRutas
	 * @throws SQLException 
	 */
	private void actualizarEquivalentesReintentar(List<AsignacionRuta> listaRutas) throws SQLException 
	{
		List<AsignacionRuta> listaRutasEquivalentes = new ArrayList<AsignacionRuta>();
		for(AsignacionRuta ruta : listaRutas)
		{
			if(ruta.Estado == 8)
			{
				AsignacionRuta asignacionEquivalente = buscarAsignacionRuta(ruta.Ruta, ruta.OrdenInicio, ruta.OrdenFin, ruta.Anio, ruta.Mes, 4);
				if(asignacionEquivalente!=null)
				{
					asignacionEquivalente.cantLecturasEnviadas=asignacionEquivalente.CantidadLecturasRecibidas+ruta.cantLecturasEnviadas; 
					asignacionEquivalente.Estado = 3;
					listaRutasEquivalentes.add(asignacionEquivalente);
				}
			}
		}
		actualizarEstadoRutas(listaRutasEquivalentes, true);
	}


}
