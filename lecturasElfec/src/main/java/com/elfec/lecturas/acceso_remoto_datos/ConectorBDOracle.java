package com.elfec.lecturas.acceso_remoto_datos;

import android.content.Context;
import android.nfc.FormatException;

import com.elfec.lecturas.helpers.ManejadorJSON;
import com.elfec.lecturas.modelo.AsignacionRuta;
import com.elfec.lecturas.modelo.BaseCalculo;
import com.elfec.lecturas.modelo.BaseCalculoConcepto;
import com.elfec.lecturas.modelo.Concepto;
import com.elfec.lecturas.modelo.ConceptoCategoria;
import com.elfec.lecturas.modelo.ConceptoLectura;
import com.elfec.lecturas.modelo.ConceptoTarifa;
import com.elfec.lecturas.modelo.EvolucionConsumo;
import com.elfec.lecturas.modelo.Lectura;
import com.elfec.lecturas.modelo.MedidorEntreLineas;
import com.elfec.lecturas.modelo.Ordenativo;
import com.elfec.lecturas.modelo.OrdenativoLectura;
import com.elfec.lecturas.modelo.PermisoRestriccion;
import com.elfec.lecturas.modelo.Potencia;
import com.elfec.lecturas.modelo.PreferenciaUI;
import com.elfec.lecturas.modelo.ReclasificacionCategoria;
import com.elfec.lecturas.modelo.TokenServicioWeb;
import com.elfec.lecturas.modelo.Usuario;
import com.elfec.lecturas.modelo.enums.EstadoAsignacionRuta;
import com.elfec.lecturas.modelo.excepciones.OracleBDConexionException;
import com.elfec.lecturas.modelo.excepciones.OracleBDConfiguracionException;
import com.elfec.lecturas.modelo.resultados.ResultadoTipado;
import com.elfec.lecturas.settings.ConstantesDeEntorno;
import com.elfec.lecturas.settings.VariablesDeSesion;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Se encarga de la conexión con la base de datos oracle, y la importación y
 * exportación de ésta.
 *
 * @author drodriguez
 */
public class ConectorBDOracle {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private DateTime currentDate = DateTime.now();

    /**
     * Crea una nueva conexion con la base de datos Oracle, usando la
     * información de conexión del archivo JSON de configuración que se
     * encuentra en la carpeta <b>assets</b> o con la configuración configurada
     * desde el dispositivo. Para crear una nueva conexión debe usar el método
     * {@link ConectorBDOracle#crear(Context, boolean)}
     *
     * @param contexto     contexto, necesario para poder acceder a la carpeta
     *                     <b>assets</b>
     * @param habilitarRol , indica si se debe habilitar o no el rol de lecturas para
     *                     esta conexión
     * @throws OracleBDConexionException
     */
    private ConectorBDOracle(Context contexto, boolean habilitarRol)
            throws OracleBDConexionException, OracleBDConfiguracionException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            JSONObject configBD = LectorConfigBD.obtenerConfiguracion(contexto);
            String url = "jdbc:oracle:thin:@//" + configBD.getString("ip")
                    + ":" + configBD.getString("puerto") + "/"
                    + configBD.getString("servicio");
            conn = DriverManager.getConnection(url,
                    VariablesDeSesion.getUsuarioLogeado(),
                    VariablesDeSesion.getPasswordUsuario());
            stmt = conn.createStatement();
            if (habilitarRol)
                habilitarRolLecturas(contexto);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new OracleBDConexionException();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OracleBDConexionException();
        } catch (JSONException e) {
            e.printStackTrace();
            throw new OracleBDConfiguracionException();
        }
    }

    /**
     * Crea una nueva instancia de el conector a la base de datos, no es un
     * singletone, es un factory
     *
     * @param contexto
     * @param habilitarRol
     * @return {@link ResultadoTipado} que contiene como resultado el
     * {@link ConectorBDOracle}
     */
    public static ResultadoTipado<ConectorBDOracle> crear(Context contexto,
                                                          boolean habilitarRol) {
        ResultadoTipado<ConectorBDOracle> result = new ResultadoTipado<>();
        try {
            result.setResultado(new ConectorBDOracle(contexto, habilitarRol));
        } catch (FormatException e) {
            result.agregarError(e);
        } catch (OracleBDConexionException e) {
            result.agregarError(e);
        }
        return result;
    }

    /**
     * Hbailita el rol de LECTURAS para el usuario logeado con el que se realiza
     * la conexión este metodo se llama con el parametro del constructor y
     * deberia activarse para cualquier conexion a la base de datos oracle que
     * requiera acceso a las tablas especÃ­ficas de lecturas
     *
     * @param contexto , la actividad de la que se llama el conector, necesaria para
     *                 poder acceder a la carpeta <b>assets</b>
     * @throws FormatException
     * @throws SQLException
     * @throws JSONException
     */
    private void habilitarRolLecturas(Context contexto)
            throws OracleBDConfiguracionException, OracleBDConexionException {
        String errorWhileEnablingRole = "Error al activar el rol: ";
        try {
            JSONObject configBD = LectorConfigBD.obtenerConfiguracion(contexto);
            String query = "SET ROLE "
                    + configBD.getString("rol")
                    + (configBD.getString("password").equals("") ? ""
                    : (" IDENTIFIED BY \""
                    + configBD.getString("password") + "\""));
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            throw new OracleBDConexionException(errorWhileEnablingRole);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new OracleBDConfiguracionException(errorWhileEnablingRole);
        }
    }

    /**
     * Importa todas las lecturas correspondientes a la ruta propocionada. Toma
     * en cuenta el dia, mes y año para realizar la importación y lo realiza de
     * la tabla MOVILES.LECTURAS
     *
     * @param ruta
     * @return
     * @throws SQLException
     */
    public List<Lectura> obtenerLecturasPorRuta(AsignacionRuta ruta)
            throws SQLException {
        List<Lectura> lista = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        if (ruta.getEstado() == EstadoAsignacionRuta.ASIGNADA) {
            query = new StringBuilder(
                    "SELECT * FROM MOVILES.LECTURAS WHERE LEMRUT=");
            query.append(ruta.Ruta).append(" AND LEMARE=").append(ruta.Dia)
                    .append(" AND LEMMES=").append(ruta.Mes)
                    .append(" AND LEMANO=").append(ruta.Anio)
                    .append(" AND TO_NUMBER(LEMCTAANT,'9999999999')>=")
                    .append(ruta.obtenerCuentaInicio())
                    .append(" AND TO_NUMBER(LEMCTAANT,'9999999999')<=")
                    .append(ruta.obtenerCuentaFin());
        }
        if (ruta.getEstado() == EstadoAsignacionRuta.RELECTURA_ASIGNADA) {
            query = new StringBuilder(
                    "SELECT * FROM MOVILES.LECTURAS a WHERE EXISTS (SELECT 1 FROM ERP_ELFEC.SGC_MOVIL_LECTURAS b"
                            + " WHERE a.LEMSUM=b.NUS AND a.LEMMES=b.MES AND a.LEMANO=b.ANIO AND a.LEMRUT=b.RUTA AND b.ESTADO=4) AND LEMRUT=");
            query.append(ruta.Ruta)
                    .append(" AND TO_NUMBER(LEMCTAANT,'9999999999')>=")
                    .append(ruta.obtenerCuentaInicio())
                    .append(" AND TO_NUMBER(LEMCTAANT,'9999999999')<=")
                    .append(ruta.obtenerCuentaFin());
        }
        rs = stmt.executeQuery(query.toString());
        while (rs.next()) {
            lista.add(new Lectura(rs));
        }
        return lista;
    }

    /**
     * Importa todas las potencias asociadas a lecturas correspondientes a la
     * ruta propocionada. Toma en cuenta el dia, mes y año para realizar la
     * importación y lo realiza de la tabla MOVILES.LECTURASP
     *
     * @param ruta
     * @param listLecCondicion
     * @return
     * @throws SQLException
     */
    public List<Potencia> obtenerPotenciasPorRuta(int ruta,
                                                  String listLecCondicion) throws SQLException {
        List<Potencia> lista = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT * FROM MOVILES.LECTURASP WHERE LEMRUT=");
        rs = stmt.executeQuery(query.append(ruta).append(" AND LEMMES=")
                .append(currentDate.getMonthOfYear())
                .append(" AND LEMANO=").append(currentDate.getYear())
                .append(" AND LEMSUM IN ").append(listLecCondicion).toString());
        while (rs.next()) {
            lista.add(new Potencia(rs));
        }
        return lista;
    }

    public List<ConceptoLectura> obtenerConceptosPorRuta(int ruta,
                                                         String listLecCondicion) throws SQLException {
        List<ConceptoLectura> lista = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT * FROM MOVILES.LECTURASCONCEPTOS WHERE LEMRUT=");
        rs = stmt.executeQuery(query.append(ruta).append(" AND LEMMES=")
                .append(currentDate.getMonthOfYear())
                .append(" AND LEMANO=").append(currentDate.getYear())
                .append(" AND LEMSUM IN ").append(listLecCondicion).toString());
        while (rs.next()) {
            lista.add(new ConceptoLectura(rs));
        }
        return lista;
    }

    public List<EvolucionConsumo>
    obtenerEvolucionConsumosPorRuta(int ruta, String listLecCondicion)
            throws SQLException {
        List<EvolucionConsumo> lista = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT * FROM MOVILES.LECTURASC WHERE LEMRUT=");
        rs = stmt.executeQuery(query.append(ruta).append(" AND LEMMES=")
                .append(currentDate.getMonthOfYear())
                .append(" AND LEMANO=").append(currentDate.getYear())
                .append(" AND LEMSUM IN ").append(listLecCondicion).toString());
        while (rs.next()) {
            lista.add(new EvolucionConsumo(rs));
        }
        return lista;
    }

    /**
     * Obtiene los ordenativos remotamente
     *
     * @return lista ordenativos
     * @throws SQLException
     */
    public List<Ordenativo> obtenerOrdenativos() throws SQLException {
        List<Ordenativo> lista = new ArrayList<Ordenativo>();
        rs = stmt
                .executeQuery("SELECT * FROM ERP_ELFEC.TIPOS_NOV_SUM WHERE ESTADO='A'");
        while (rs.next()) {
            lista.add(new Ordenativo(rs));
        }
        return lista;
    }

    public List<ConceptoCategoria> obtenerConceptosCategorias()
            throws SQLException {
        List<ConceptoCategoria> lista = new ArrayList<>();
        int idCuadroTarifario = ((currentDate.getYear() - 2000) * 100)
                + currentDate.getMonthOfYear();
        rs = stmt
                .executeQuery("SELECT * FROM ERP_ELFEC.CPTOS_CATEGORIAS WHERE IDCUADRO_TARIF = "
                        + idCuadroTarifario
                        + " AND IDCONCEPTO >=10000 AND IDCONCEPTO<12000");
        while (rs.next()) {
            lista.add(new ConceptoCategoria(rs));
        }
        return lista;
    }

    public List<ConceptoTarifa> obtenerConceptosTarifas() throws SQLException {
        List<ConceptoTarifa> lista = new ArrayList<>();
        int idCuadroTarifario = ((currentDate.getYear() - 2000) * 100)
                + currentDate.getMonthOfYear();
        rs = stmt
                .executeQuery("SELECT * FROM ERP_ELFEC.CONCEPTOS_TARIFAS WHERE IDCUADRO_TARIF = "
                        + idCuadroTarifario);
        while (rs.next()) {
            lista.add(new ConceptoTarifa(rs));
        }
        return lista;
    }

    public List<BaseCalculo> obtenerBasesCalculo() throws SQLException {
        List<BaseCalculo> lista = new ArrayList<>();
        rs = stmt
                .executeQuery("SELECT IDBASE_CALCULO,DESCRIPCION,"
                        + "(SELECT ORDEN_IMPRESION FROM ERP_ELFEC.GBASES_CALC_IMP I WHERE I.IDBASE_CALCULO=B.IDBASE_CALCULO) AS ORDEN_IMPRESION "
                        + "FROM ERP_ELFEC.GBASES_CALC B");
        while (rs.next()) {
            lista.add(new BaseCalculo(rs));
        }
        return lista;
    }

    public List<BaseCalculoConcepto> obtenerBasesCalculoConceptos()
            throws SQLException {
        List<BaseCalculoConcepto> lista = new ArrayList<>();
        rs = stmt
                .executeQuery("SELECT * FROM ERP_ELFEC.GBASES_CALC_CPTOS WHERE IDCONCEPTO>=10000 AND IDCONCEPTO<14000");
        while (rs.next()) {
            lista.add(new BaseCalculoConcepto(rs));
        }
        return lista;
    }

    public List<Concepto> obtenerConceptos() throws SQLException {
        List<Concepto> lista = new ArrayList<>();
        rs = stmt
                .executeQuery("SELECT * FROM ERP_ELFEC.CONCEPTOS WHERE IDCONCEPTO>=10000 AND IDCONCEPTO<12000");
        while (rs.next()) {
            lista.add(new Concepto(rs));
        }
        return lista;
    }

    public List<ReclasificacionCategoria> obtenerReclasificacionCategorias()
            throws SQLException {
        List<ReclasificacionCategoria> lista = new ArrayList<>();
        rs = stmt
                .executeQuery("SELECT CR.IDCATEG_ORIG,CR.IDCATEG_DEST,CR.CONDICION,CR.VALOR,FAM.RECSGLDES "
                        + "FROM ERP_ELFEC.CATEG_RECLASIF CR, MOVILES.RECATEGORIZACION FAM "
                        + "WHERE CR.IDCATEG_ORIG=FAM.RECCATORI AND CR.IDCATEG_DEST=FAM.RECCATDES AND CR.VALOR=FAM.RECVAL");
        while (rs.next()) {
            lista.add(new ReclasificacionCategoria(rs));
        }
        return lista;
    }

    /**
     * Se conecta al servidor Oracle y obtiene la fecha y hora actuales
     *
     * @return Date
     * @throws SQLException
     * @throws ParseException
     */
    public Date obtenerFechaDelServidor() throws SQLException, ParseException,
            NullPointerException {
        rs = stmt.executeQuery("SELECT SYSDATE FROM dual");
        while (rs.next()) {
            String fecha = rs.getString("SYSDATE");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S",
                    Locale.getDefault());
            return df.parse(fecha);
        }
        return null;
    }

    /**
     * Exporta una lectura al servidor, no guarda ni realiza cambios localmente,
     * se deberÃ­a actualizar su estado en caso de exportarse exitosamente
     *
     * @param lec
     * @return 1 si el insert se hizo correctamente
     * @throws SQLException
     */
    public int exportarLectura(Lectura lec) throws SQLException {
        return stmt.executeUpdate(lec.toRemoteInsertQuery());
    }

    /**
     * Exporta un simple ordenativo, no guarda ni realiza cambios localmente, se
     * deberÃ­a actualizar su estado en caso de exportarse exitosamente
     *
     * @param ordLec
     * @return 1 si se importó exitosamente
     * @throws SQLException
     */
    public int exportarOrdenativoLectura(OrdenativoLectura ordLec)
            throws SQLException {
        return stmt.executeUpdate(ordLec.toRemoteInsertQuery());
    }

    /**
     * Exporta una lectura entre lineas al servidor, no guarda ni realiza
     * cambios localmente, se deberÃ­a actualizar su estado en caso de exportarse
     * exitosamente
     *
     * @param lecEL
     * @return 1 si el insert se hizo correctamente
     * @throws SQLException
     */
    public int exportarLecturaEntreLineas(MedidorEntreLineas lecEL)
            throws SQLException {
        return stmt.executeUpdate(lecEL.toRemoteInsertQuery());
    }

    /**
     * Importa las variables parametrizables de las tabla
     * ERP_ELFEC.SGC_MOVIL_PARAM y las guarda en archivos JSON para su uso por
     * la clase estatica VariablesDeEntorno
     *
     * @return
     * @throws SQLException
     * @throws JSONException
     */
    public boolean obtenerParametrosGenerales() throws SQLException,
            JSONException {
        rs = stmt
                .executeQuery("SELECT * FROM ERP_ELFEC.SGC_MOVIL_PARAM WHERE ESTADO=1");
        if (rs.next()) {
            JSONObject parametrosGenerales = ManejadorJSON
                    .convertirResultSetAJSON(rs);
            return ManejadorJSON.guardarJSONEnArchivo(
                    parametrosGenerales.toString(),
                    ConstantesDeEntorno.archivoParametrosGrales);
        }
        return false;
    }

    /**
     * Importa las variables parametrizables de las tabla
     * ERP_ELFEC.SGC_MOVIL_PARAM_COD_ORD_RES y las guarda en archivos JSON para
     * su uso por la clase estatica VariablesDeEntorno
     *
     * @return
     * @throws SQLException
     * @throws JSONException
     */
    public boolean obtenerParamCodOrdenativosResumen() throws SQLException,
            JSONException {
        rs = stmt
                .executeQuery("SELECT CODIGO FROM ERP_ELFEC.SGC_MOVIL_PARAM_COD_ORD_RES WHERE ESTADO=1");
        JSONArray params = new JSONArray();
        while (rs.next()) {
            JSONObject paramResumenOrdenativos = ManejadorJSON
                    .convertirResultSetAJSON(rs);
            params.put(paramResumenOrdenativos);
        }
        return ManejadorJSON.guardarJSONEnArchivo(params.toString(),
                ConstantesDeEntorno.archivoCodsResumenOrdenativos);
    }

    /**
     * Importa las variables parametrizables de las tabla
     * ERP_ELFEC.SGC_MOVIL_PARAM_CATEG_NO_CFIJO y las guarda en archivos JSON
     * para su uso por la clase estatica VariablesDeEntorno
     *
     * @return
     * @throws SQLException
     * @throws JSONException
     */
    public boolean obtenerParamCategsNoMuestraCargoFijo() throws SQLException,
            JSONException {
        rs = stmt
                .executeQuery("SELECT CATEGORIA FROM ERP_ELFEC.SGC_MOVIL_PARAM_CATEG_NO_CFIJO WHERE ESTADO=1");
        JSONArray params = new JSONArray();
        while (rs.next()) {
            JSONObject paramCategsNoMostrarCargoFijo = ManejadorJSON
                    .convertirResultSetAJSON(rs);
            params.put(paramCategsNoMostrarCargoFijo);
        }
        return ManejadorJSON.guardarJSONEnArchivo(params.toString(),
                ConstantesDeEntorno.archivoCategsNoMostrarCargoFijo);
    }

    /**
     * Se conecta a la base de datos oracle y obtiene al usuario solitictado, en
     * caso de no existir, existira un error de conexion
     *
     * @return
     */
    public Usuario obtenerUsuario(String username) throws SQLException {
        rs = stmt
                .executeQuery("SELECT * FROM MOVILES.USUARIO_APP WHERE APLICACION='Lecturas Moviles' AND UPPER(USUARIO)=UPPER('"
                        + username + "')");
        while (rs.next()) {
            return new Usuario(rs);
        }
        return null;
    }

    /**
     * Se conecta a la base de datos oracle y obtiene los permisos y
     * restricciones del perfil solitictado de la tabla
     * MOVILES.PERFIL_APP_OPCIONESy los guarda
     *
     * @return
     */
    public boolean importarPermisosPerfilUsuario(String perfil) {
        try {
            rs = stmt
                    .executeQuery("SELECT * FROM MOVILES.PERFIL_APP_OPCIONES WHERE APP='Lecturas Moviles' AND PERFIL='"
                            + perfil + "' AND ESTADO=1");
            while (rs.next()) {
                (new PermisoRestriccion(rs)).save();
            }
            return true;
        } catch (SQLException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Se conecta a la base de datos oracle y obtiene las preferencias de UI del
     * usuario solitictado de la tabla MOVILES.USUARIO_PREFERENCIAS_UI y los
     * guarda.
     *
     * @return
     */
    public boolean importarPreferenciasUIUsuario(String usuario) {
        try {
            rs = stmt
                    .executeQuery("SELECT * FROM MOVILES.USUARIO_PREFERENCIAS_UI WHERE UPPER(USUARIO)=UPPER('"
                            + usuario + "') AND ESTADO=1");
            while (rs.next()) {
                (new PreferenciaUI(rs)).save();
            }
            return true;
        } catch (SQLException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Se conecta a la base de datos oracle y obtiene los tokens para la
     * conexión al servicio web a partir de la fecha de sincronizacion de la
     * cuenta del usuario hasta el rango de dias que tiene asignados, el rango
     * de dias no puede ser cero. del usuario, de la vista MOVILES.V_TOKEN_LEC
     *
     * @param usuario
     * @return
     */
    public boolean importarTokensUsuario(Usuario usuario) {
        try {
            for (int i = 0; i < usuario.RangoDias; i++) {
                StringBuilder query = new StringBuilder(
                        "SELECT MOVILES.f_token(");
                query.append(i).append(") AS TOKEN, (SYSDATE+").append(i)
                        .append(") AS FECHA, USER AS USUARIO FROM DUAL");
                rs = stmt.executeQuery(query.toString());
                while (rs.next()) {
                    (new TokenServicioWeb(rs)).save();
                }
            }
            return true;
        } catch (SQLException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Se conecta a la base de datos oracle y verifica si el imei propocionado
     * esta aurorizado para usar la aplicación para ello accede a la tabla
     * MOVILES.IMEI_APP y ve que exista ese IMEI y que tenga estado 1 es decir
     * que esté activo
     *
     * @param imei
     * @return
     * @throws SQLException
     */
    public boolean validarExistenciaIMEI(String imei) throws SQLException {
        rs = stmt
                .executeQuery("SELECT * FROM MOVILES.IMEI_APP WHERE APLICACION='Lecturas Moviles' AND IMEI='"
                        + imei + "' AND ESTADO=1");
        while (rs.next()) {
            return true;
        }
        return false;
    }

    /**
     * Se conecta a la base de datos oracle y obtiene las rutas asignadas al
     * usuario para ese dia
     *
     * @param usuario
     * @return
     * @throws SQLException
     */
    public List<AsignacionRuta> obtenerRutasAsignadas(String usuario)
            throws SQLException {
        List<AsignacionRuta> lista = new ArrayList<>();
        rs = stmt
                .executeQuery("SELECT * FROM MOVILES.USUARIO_ASIGNACION WHERE UPPER(USUARIO)=UPPER('"
                        + usuario
                        + "') AND DIA_ASIG_CARGA="
                        + currentDate.getDayOfMonth()
                        + " AND MES="
                        + currentDate.getMonthOfYear()
                        + " AND ANIO="
                        + currentDate.getYear()
                        + " AND (ESTADO=1 OR ESTADO=6)");
        while (rs.next()) {
            lista.add(new AsignacionRuta(rs));
        }
        return lista;
    }

    /**
     * Actualiza masivamente los estados de una lista de rutas
     *
     * @param listaRutas
     * @param actualizarCantLecturasRecibidas
     * @throws SQLException
     */
    public void actualizarEstadoRutas(List<AsignacionRuta> listaRutas,
                                      boolean actualizarCantLecturasRecibidas) throws SQLException {
        for (AsignacionRuta asignacionRuta : listaRutas) {
            actualizarEstadoRuta(asignacionRuta,
                    actualizarCantLecturasRecibidas);
        }
    }

    /**
     * Se conecta a la base de datos y pone el estado de la ruta de la base de
     * datos SQLite a las rutas de la tabla MOVILES.USUARIO_ASIGNACION, segun la
     * ruta proporcionada
     *
     * @param ruta
     * @param actualizarCantLecturasRecibidas
     * @throws SQLException
     */
    public void actualizarEstadoRuta(AsignacionRuta ruta,
                                     boolean actualizarCantLecturasRecibidas) throws SQLException {
        String query;
        String datos;
        String condicion;
        query = "UPDATE MOVILES.USUARIO_ASIGNACION";
        datos = " SET ESTADO="
                + ruta.getEstado().toShort()
                + (actualizarCantLecturasRecibidas ? ", CANT_LEC_REC="
                + ruta.cantLecturasEnviadas : "");
        condicion = " WHERE UPPER(USUARIO)=UPPER('" + ruta.UsuarioAsignado
                + "') AND DIA=" + ruta.Dia + " AND MES=" + ruta.Mes
                + " AND ANIO=" + ruta.Anio + " AND RUTA=" + ruta.Ruta;
        stmt.executeUpdate(query + datos + condicion);
    }

    /**
     * busca una asignación de ruta segÃºn los parÃ¡metros especificados
     *
     * @param ruta
     * @param ordenInicio
     * @param ordenFin
     * @param anio
     * @param mes
     * @param estado
     * @return
     * @throws SQLException
     */
    public AsignacionRuta buscarAsignacionRuta(int ruta, int ordenInicio,
                                               int ordenFin, int anio, int mes, int estado) throws SQLException {
        AsignacionRuta asigRuta = null;
        StringBuilder query = new StringBuilder(
                "SELECT * FROM MOVILES.USUARIO_ASIGNACION WHERE RUTA=");
        query.append(ruta).append(" AND MES=").append(mes)
                .append(" AND ORDEN_INICIO=").append(ordenInicio)
                .append(" AND ORDEN_FIN=").append(ordenFin)
                .append(" AND ANIO=").append(anio).append(" AND ESTADO=")
                .append(estado);
        rs = stmt.executeQuery(query.toString());
        while (rs.next()) {
            asigRuta = new AsignacionRuta(rs);
        }
        return asigRuta;
    }

    /**
     * Se conecta a la base de datos y actualiza el estado de las asignaciones
     * de rutas originales (Estado 4) al estado de completadas (Estado 3) de las
     * cuales salieron las asignaciones de reintentar de la tabla
     * MOVILES.USUARIO_ASIGNACION, segun la lista de rutas proporcionada.
     * proporcionada
     *
     * @param listaRutas
     * @throws SQLException
     */
    public void actualizarEquivalentesReintentar(List<AsignacionRuta> listaRutas)
            throws SQLException {
        List<AsignacionRuta> listaRutasEquivalentes = new ArrayList<>();
        for (AsignacionRuta ruta : listaRutas) {
            if (ruta.getEstado() == EstadoAsignacionRuta.RELECTURA_EXPORTADA) {
                AsignacionRuta asignacionEquivalente = buscarAsignacionRuta(
                        ruta.Ruta, ruta.OrdenInicio, ruta.OrdenFin, ruta.Anio,
                        ruta.Mes, 4);
                if (asignacionEquivalente != null) {
                    asignacionEquivalente.cantLecturasEnviadas = asignacionEquivalente.CantidadLecturasRecibidas
                            + ruta.cantLecturasEnviadas;
                    asignacionEquivalente
                            .setEstado(EstadoAsignacionRuta.EXPORTADA);
                    listaRutasEquivalentes.add(asignacionEquivalente);
                }
            }
        }
        actualizarEstadoRutas(listaRutasEquivalentes, true);
    }

}
