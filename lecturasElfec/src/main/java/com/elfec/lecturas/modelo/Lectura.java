package com.elfec.lecturas.modelo;

import android.database.Cursor;
import android.location.Location;
import android.os.Handler;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.elfec.lecturas.controlador.TomarLectura;
import com.elfec.lecturas.helpers.ManejadorDeCamara;
import com.elfec.lecturas.logica_negocio.web_services.ManejadorConexionRemota;
import com.elfec.lecturas.modelo.backuptexto.IModeloBackupableTexto;
import com.elfec.lecturas.modelo.enums.EstadoExportacion;
import com.elfec.lecturas.modelo.estados.lectura.EstadoLecturaFactory;
import com.elfec.lecturas.modelo.estados.lectura.IEstadoLectura;
import com.elfec.lecturas.modelo.eventos.EventoAlObtenerResultado;
import com.elfec.lecturas.modelo.eventos.EventoAlObtenerUbicacion;
import com.elfec.lecturas.modelo.interfaces.IExportable;
import com.elfec.lecturas.modelo.validaciones.AdvConsumoBajo;
import com.elfec.lecturas.modelo.validaciones.AdvConsumoElevado;
import com.elfec.lecturas.modelo.validaciones.AdvIndiceMenorActual;
import com.elfec.lecturas.modelo.validaciones.IValidacionLectura;
import com.elfec.lecturas.modelo.validaciones.ValidacionLecturaCorrecta;
import com.elfec.lecturas.settings.VariablesDeEntorno;
import com.elfec.lecturas.settings.VariablesDeSesion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Almacena la información sobre las lecturas, de la tabla Lecturas de la BD
 * Moviles de Oracle (MOVILES.LECTURAS)
 *
 * @author drodriguez
 */
@Table(name = "Lecturas")
public class Lectura extends Model implements EventoAlObtenerUbicacion,
        EventoAlObtenerResultado, IModeloBackupableTexto, IExportable {

    private static final String INSERT_QUERY = "INSERT INTO ERP_ELFEC.SGC_MOVIL_LECTURAS VALUES (%d, %d, %d, %d, %d, '%s', %d, %s, %s, "
            + "TO_DATE('%s', 'dd/mm/yyyy hh24:mi:ss'), %s, %s, UPPER('%s'), %d, %s, USER, SYSDATE, %s, %d)";
    private IEstadoLectura EstadoLectura;

    // Atributos nuevos
    @Column(name = "EstadoLectura", index = true, notNull = true)
    private int Estado;

    @Column(name = "Consumo")
    public int Consumo;

    @Column(name = "ConsumoFacturado")
    public int ConsumoFacturado;

    @Column(name = "Recordatorio")
    public String Recordatorio;

    @Column(name = "PotenciaLectura", index = true, notNull = false, onDelete = ForeignKeyAction.SET_NULL)
    public Potencia PotenciaLectura;

    private EvolucionConsumo EvolucionConsumoLectura;

    public EvolucionConsumo obtenerEvolucionConsumoLectura() {
        return (EvolucionConsumoLectura == null ? EvolucionConsumo
                .obtenerEvolucionConsumo(Suministro, Mes, Anio)
                : EvolucionConsumoLectura);
    }

    @Column(name = "GPSLatitud")
    public double GPSLatitud;

    @Column(name = "GPSLongitud")
    public double GPSLongitud;

    /**
     * Cuando fue enviado por 3g tiene el estado 1, 0 en caso contrario , 2 si
     * es que se envió al servidor por otro medio
     */
    @Column(name = "Enviado3G")
    public int Enviado3G;
    /**
     * Cantidad de fotos que se han tomado para esta lectura
     */
    @Column(name = "NumFotosTomadas")
    public int NumFotosTomadas;
    /**
     * Cantidad de veces que se uso la opcion modificar sobre esta lectura
     */
    @Column(name = "NumModificaciones")
    public int NumModificaciones;

    /**
     * El usuario que insertó la lectura
     */
    @Column(name = "UsuarioAuditoria")
    public String UsuarioAuditoria;

    public Lectura() {
        super();
        EstadoLectura = EstadoLecturaFactory.crearEstado(Estado);
    }

    public Lectura(ResultSet rs) throws SQLException {
        super();
        Dia = rs.getShort("LEMARE");
        Ruta = rs.getInt("LEMRUT");
        Anio = rs.getShort("LEMANO");
        Mes = rs.getByte("LEMMES");
        CodigoCLiente = rs.getLong("LEMCLI");
        Suministro = rs.getLong("LEMSUM");
        Estado = 0;// Valor de Pendiente
        EstadoLectura = EstadoLecturaFactory.crearEstado(Estado);
        OrdenTPL = rs.getInt("LEMORDTPL");
        Remesa = rs.getShort("LEMREM");
        NombreCliente = rs.getString("LEMNOM");
        DireccionSuministro = rs.getString("LEMDIR");
        Cuenta = rs.getString("LEMCTAANT");
        TipoCliente = rs.getString("LEMTIPCLI");
        GrunLectura = rs.getByte("LEMGRUNLE");
        NumeroConsumosBajos = rs.getShort("LEMNCOBAJ");
        Categoria = rs.getString("LEMCTG");
        TipoMedidor = rs.getShort("LEMTIPMED");
        NumeroMedidor = rs.getString("LEMNROMED");
        NumDigitosMedidor = rs.getByte("LEMNRODIG");
        FactorMultiplicador = rs.getBigDecimal("LEMFACMUL");
        FechaLecturaAnterior = rs.getDate("LEMFECANT");
        FechaLecturaActual = rs.getDate("LEMFECLEC");
        HoraLectura = rs.getString("LEMHORLEC");
        UltimoIndice = rs.getInt("LEMULTIND");
        ConsumoPromedio = rs.getInt("LEMCONPRO");
        ConsumoAcumulado = rs.getInt("LEMCONACU");
        LecturaNueva = rs.getInt("LEMNVALEC");
        LegLec = rs.getInt("LEMLEGLEC");
        TipoLectura = rs.getShort("LEMTIPLEC");
        SiglaCategoria = rs.getString("LEMSGL");
        OrdenSecuencialTPL = rs.getInt("LEMORDSEQ");
        ObservacionLectura = rs.getShort("LEMOBSL");
        LecLec = rs.getShort("LEMLECLEC");
        LecMob = rs.getShort("LEMLECMOB");
        LecRut = rs.getShort("LEMLECRUT");
        TagImpresionAviso = rs.getByte("LEMIMPFAC");
        ImporteTap = rs.getBigDecimal("LEMIMPTAP");
        ImporteAseo = rs.getBigDecimal("LEMIMPASE");
        ImporteCargoFijo = rs.getBigDecimal("LEMCARFIJ");
        ImportePorEnergia = rs.getBigDecimal("LEMIMPEN");
        ImportePorPotencia = rs.getBigDecimal("LEMIMPPOT");
        ImporteDescuentoTarifaDignidad = rs.getBigDecimal("LEMDESTDI");
        ImporteLey1886 = rs.getBigDecimal("LEMLEY1886");
        MesesAdeudados = rs.getString("LEMMESDEU");
        PerdidasHierroKwh = rs.getBigDecimal("LEMPERHIE");
        PerdidasCobrePorcentaje = rs.getBigDecimal("LEMPERCOB");
        LeePotencia = rs.getByte("LEMLEEPOT");
        FactorCarga = rs.getBigDecimal("LEMFACCAR");
        HoraMesPotencia = rs.getInt("LEMHRAMES");
        CotizaAseo = rs.getByte("LEMCOTASE");
        PorcentajeTAP = rs.getBigDecimal("LEMTAP");
        PotenciaContratada = rs.getInt("LEMPOTCON");
        PotenciaFacturada = rs.getInt("LEMPOTFAC");
        NitCliente = rs.getLong("LEMCLINIT");
        NusCliente = rs.getInt("LEMNUS");
        FechaPosibleCorte = rs.getDate("LEMFECCOR");
        FechaVencimiento = rs.getDate("LEMFECVTO");
        FechaProximaEmision = rs.getDate("LEMFECPRO");
        ImporteFacturasAdeudadas = rs.getBigDecimal("LEMIMPDEU");
        TopeMedidor = rs.getBigDecimal("LEMTOPE");
        TagLey1886 = rs.getByte("LEMLEYTAG");
        TipoTAP = rs.getByte("LEMTPOTAP");
        LecturaAnterior = rs.getInt("LEMLECANT");
        PorcentajeLey1886 = rs.getFloat("LEMLEYPOR");
        LeeReactiva = rs.getByte("LEMLEEREA");
        TagRecategorizacion = rs.getByte("LEMRECATE");
        TagTarifaDignidad = rs.getByte("LEMTAGTAR");
        ImporteTotal = rs.getBigDecimal("LEMIMPTOT");
        CategoriaRecategorizada = rs.getString("LEMRECCAT");
        TagCalculaPotencia = rs.getByte("LEMPOTTAG");
        ImpresionesAvisoCobro = rs.getShort("LEMIMPAVI");
        KhwAdicionales = rs.getInt("LEMKWHADI");
    }

    // Campos tabla oracle
    @Column(name = "LEMDIA", notNull = true)
    public int Dia;

    @Column(name = "LEMRUT", index = true, notNull = true)
    public int Ruta;

    @Column(name = "LEMANO", index = true, notNull = true)
    public int Anio;

    @Column(name = "LEMMES", index = true, notNull = true)
    public int Mes;

    @Column(name = "LEMCLI", notNull = true)
    public long CodigoCLiente;

    @Column(name = "LEMSUM", index = true, notNull = true)
    public long Suministro;

    @Column(name = "LEMORDTPL")
    public int OrdenTPL;

    @Column(name = "LEMREM")
    public int Remesa;

    @Column(name = "LEMNOM")
    public String NombreCliente;

    @Column(name = "LEMDIR")
    public String DireccionSuministro;

    @Column(name = "LEMCTAANT", index = true)
    public String Cuenta;

    @Column(name = "LEMTIPCLI")
    public String TipoCliente;

    @Column(name = "LEMGRUNLE")
    public int GrunLectura;// revisar que es?

    @Column(name = "LEMNCOBAJ")
    public int NumeroConsumosBajos;

    @Column(name = "LEMCTG")
    public String Categoria;

    @Column(name = "LEMTIPMED")
    public int TipoMedidor;

    @Column(name = "LEMNROMED", index = true)
    public String NumeroMedidor;

    @Column(name = "LEMNRODIG")
    public int NumDigitosMedidor;

    @Column(name = "LEMFACMUL")
    public BigDecimal FactorMultiplicador;

    @Column(name = "LEMFECANT")
    public Date FechaLecturaAnterior;

    @Column(name = "LEMFECLEC")
    public Date FechaLecturaActual;

    @Column(name = "LEMHORLEC")
    public String HoraLectura;

    @Column(name = "LEMULTIND")
    public int UltimoIndice;

    @Column(name = "LEMCONPRO")
    public int ConsumoPromedio;

    @Column(name = "LEMCONACU")
    public int ConsumoAcumulado;

    @Column(name = "LEMNVALEC")
    public int LecturaNueva;

    @Column(name = "LEMLEGLEC")
    public int LegLec; // que es?

    @Column(name = "LEMTIPLEC")
    public int TipoLectura;

    @Column(name = "LEMSGL")
    public String SiglaCategoria;

    @Column(name = "LEMORDSEQ")
    public int OrdenSecuencialTPL;

    @Column(name = "LEMOBSL")
    public int ObservacionLectura;

    @Column(name = "LEMLECLEC")
    public int LecLec;// que es?

    @Column(name = "LEMLECMOB")
    public int LecMob;// que es?

    @Column(name = "LEMLECRUT")
    public int LecRut;// que es?

    @Column(name = "LEMIMPFAC")
    public int TagImpresionAviso;

    @Column(name = "LEMIMPTAP")
    public BigDecimal ImporteTap;

    @Column(name = "LEMIMPASE")
    public BigDecimal ImporteAseo;

    @Column(name = "LEMCARFIJ")
    public BigDecimal ImporteCargoFijo;

    @Column(name = "LEMIMPEN")
    public BigDecimal ImportePorEnergia;

    @Column(name = "LEMIMPPOT")
    public BigDecimal ImportePorPotencia;

    @Column(name = "LEMDESTDI")
    public BigDecimal ImporteDescuentoTarifaDignidad;

    @Column(name = "LEMLEY1886")
    public BigDecimal ImporteLey1886;

    @Column(name = "LEMMESDEU")
    public String MesesAdeudados;

    @Column(name = "LEMPERHIE")
    public BigDecimal PerdidasHierroKwh;

    @Column(name = "LEMPERCOB")
    public BigDecimal PerdidasCobrePorcentaje;

    @Column(name = "LEMLEEPOT")
    public int LeePotencia;

    @Column(name = "LEMFACCAR")
    public BigDecimal FactorCarga;

    @Column(name = "LEMHRAMES")
    public int HoraMesPotencia;

    @Column(name = "LEMCOTASE")
    public int CotizaAseo;

    @Column(name = "LEMTAP")
    public BigDecimal PorcentajeTAP;

    @Column(name = "LEMPOTCON")
    public int PotenciaContratada;

    @Column(name = "LEMPOTFAC")
    public int PotenciaFacturada;

    @Column(name = "LEMCLINIT")
    public long NitCliente;

    @Column(name = "LEMNUS")
    public int NusCliente;

    @Column(name = "LEMFECCOR")
    public Date FechaPosibleCorte;

    @Column(name = "LEMFECVTO")
    public Date FechaVencimiento;

    @Column(name = "LEMFECPRO")
    public Date FechaProximaEmision;

    @Column(name = "LEMIMPDEU")
    public BigDecimal ImporteFacturasAdeudadas;

    @Column(name = "LEMTOPE")
    public BigDecimal TopeMedidor;

    @Column(name = "LEMLEYTAG")
    public int TagLey1886;

    @Column(name = "LEMTPOTAP")
    public int TipoTAP;

    @Column(name = "LEMLECANT")
    public int LecturaAnterior;

    @Column(name = "LEMLEYPOR")
    public float PorcentajeLey1886;

    @Column(name = "LEMLEEREA")
    public int LeeReactiva;

    @Column(name = "LEMRECATE")
    public int TagRecategorizacion;

    @Column(name = "LEMTAGTAR")
    public int TagTarifaDignidad;

    @Column(name = "LEMIMPTOT")
    public BigDecimal ImporteTotal;

    @Column(name = "LEMRECCAT")
    public String CategoriaRecategorizada;

    @Column(name = "LEMPOTTAG")
    public int TagCalculaPotencia;

    @Column(name = "LEMIMPAVI")
    public int ImpresionesAvisoCobro;

    @Column(name = "LEMKWHADI")
    public int KhwAdicionales;

    /**
     * Obtiene la insert query SQL remota de la lectura
     *
     * @return query {@link Lectura#INSERT_QUERY}
     */
    public String toRemoteInsertQuery() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaHora = df.format(FechaLecturaActual) + " " + HoraLectura;
        return String
                .format(Locale.getDefault(),
                        INSERT_QUERY,
                        Ruta,
                        Anio,
                        Mes,
                        Dia,
                        Suministro,
                        NumeroMedidor,
                        LecturaNueva,
                        ((PotenciaLectura == null || PotenciaLectura.LecturaNuevaPotencia == null) ? "NULL"
                                : PotenciaLectura.LecturaNuevaPotencia
                                .toPlainString().replace(',', '.')),
                        ((PotenciaLectura == null || PotenciaLectura.Reactiva == null) ? "NULL"
                                : PotenciaLectura.Reactiva.toPlainString()
                                .replace(',', '.')), fechaHora,
                        (("" + GPSLatitud).replace(',', '.')),
                        (("" + GPSLongitud).replace(',', '.')),
                        UsuarioAuditoria, getEstadoLectura().getEstadoEntero(),
                        (Recordatorio == null ? "NULL"
                                : ("'" + Recordatorio.replace("'", "''") + "'")),
                        (ImporteTotal != null ? ImporteTotal.toPlainString()
                                .replace(',', '.') : "NULL"),
                        ((PotenciaLectura == null) ? -1
                                : PotenciaLectura.ConsumoFacturado));
    }

    /**
     * Obtiene el estado de la lectura en cadena
     *
     * @return Estado lectura
     */
    public String obtenerEstadoLectura() {
        return getEstadoLectura().getEstadoCadena();
    }

    /**
     * Accede a la base de datos y obtiene todas las lecturas en orden por
     * cuentas
     *
     * @return Lista de Lecturas en orden de cuentas
     */
    public static List<Lectura> obtenerTodasLasLecturas() {
        return new Select().from(Lectura.class).orderBy("LEMCTAANT").execute();
    }

    /**
     * Convierte la Cuenta que es un integer a una cadena con el formato.
     *
     * @return Cuenta con el formato 22-202-654-12
     */
    public String obtenerCuentaConFormato() {
        StringBuilder cuenta = new StringBuilder(Cuenta);
        if (cuenta.length() == 9) {
            cuenta.insert(0, "0");
        }
        cuenta.insert(2, "-");
        cuenta.insert(6, "-");
        cuenta.insert(10, "-");
        return cuenta.toString();
    }

    /**
     * Llama al método de EstadoLectura, para mostrarla en una vista del tipo
     * TomarLectura
     *
     * @param tomarLectura
     */
    public void mostrarLecturaEnTomarLectura(TomarLectura tomarLectura) {
        getEstadoLectura().mostrarLectura(tomarLectura, this);
    }

    /**
     * Llama al método de EstadoLectura, para mostrar los menus en una vista del
     * tipo TomarLectura
     *
     * @param tomarLectura
     */
    public void mostrarMenuEnTomarLectura(TomarLectura tomarLectura) {
        getEstadoLectura().mostrarMenuLectura(tomarLectura, this);
    }

    /**
     * Asigna la implementación de la interfaz EstadoLectura, correcta según el
     * int de estado que se le haya pasado, utiliza el factory para esto
     *
     * @param estado
     */
    public void setEstadoLectura(int estado) {
        Estado = estado;
        EstadoLectura = EstadoLecturaFactory.crearEstado(estado);
    }

    /**
     * Retorna la implementación de la interfaz EstadoLectura, correspondiente
     * al entero Estado, adicionalmente verifica si el entero esta sincronizado
     * con la clase de estado.
     *
     * @return EstadoLectura
     */
    public IEstadoLectura getEstadoLectura() {
        if (EstadoLectura.getEstadoEntero() != Estado) {
            EstadoLectura = EstadoLecturaFactory.crearEstado(Estado);
        }
        return EstadoLectura;
    }

    /**
     * Elimina toda la información general de lecturas que pertenezcan a la
     * asignación de ruta dada
     *
     * @param asignacionRuta
     */
    public static void eliminarLecturasDeRutaAsignada(
            AsignacionRuta asignacionRuta) {
        new Delete()
                .from(Lectura.class)
                .where("LEMRUT = ? AND LEMDIA = ? AND LEMMES = ? AND LEMANO = ?",
                        asignacionRuta.Ruta, asignacionRuta.Dia,
                        asignacionRuta.Mes, asignacionRuta.Anio)
                .where("LEMCTAANT BETWEEN "
                        + asignacionRuta.obtenerCuentaInicio() + " AND "
                        + asignacionRuta.obtenerCuentaFin()).execute();
    }

    /**
     * Obtiene las lecturas que pertenezcan a la
     * asignación de ruta dada
     *
     * @param asignacionRuta
     * @return lecturas que pertenecen a la ruta
     */
    public static List<Lectura> obtenerLecturasDeRutaAsignada(
            AsignacionRuta asignacionRuta) {
        return new Select().from(Lectura.class)
                .where("LEMRUT = ? AND LEMDIA = ? AND LEMMES = ? AND LEMANO = ?",
                        asignacionRuta.Ruta, asignacionRuta.Dia,
                        asignacionRuta.Mes, asignacionRuta.Anio)
                .where("LEMCTAANT BETWEEN "
                        + asignacionRuta.obtenerCuentaInicio() + " AND "
                        + asignacionRuta.obtenerCuentaFin()).execute();
    }

    /**
     * Accede a la base de datos y obtiene todas las lecturas de la <b>ruta</b>
     * en orden por cuentas
     *
     * @param ruta
     * @return Lista de Lecturas en orden de cuentas
     */
    public static List<Lectura> obtenerLecturasDeRuta(int ruta) {
        return new Select().from(Lectura.class).where("LEMRUT=?", ruta)
                .orderBy("LEMCTAANT").execute();
    }

    /**
     * Accede a la base de datos y obtiene todas las lecturas realizadas (No
     * pendientes ni postergadas) de la <b>ruta</b> en orden por cuentas
     *
     * @param ruta
     * @return Lista de Lecturas en orden de cuentas de la ruta
     */
    public static List<Lectura> obtenerLecturasRealizadasDeRuta(int ruta) {
        return new Select()
                .from(Lectura.class)
                .where("LEMRUT=" + ruta
                        + " AND EstadoLectura<>0 AND EstadoLectura<>3")
                .execute();
    }

    /**
     * Accede a la base de datos y obtiene todas las lecturas que tengan el
     * <b>estado</b> en orden por cuentas
     *
     * @param estado
     * @return Lista de Lecturas en orden de cuentas
     */
    public static List<Lectura> obtenerLecturasPorEstado(int estado) {
        return new Select().from(Lectura.class)
                .where("EstadoLectura=?", estado).orderBy("LEMCTAANT")
                .execute();
    }

    /**
     * Accede a la base de datos y obtiene todas las lecturas que tengan el
     * <b>estado</b> y la <b>ruta</b> en orden por cuentas
     *
     * @param estado
     * @return Lista de Lecturas en orden de cuentas
     */
    public static List<Lectura> obtenerLecturasPorEstadoYRuta(int estado,
                                                              int ruta) {
        return new Select().from(Lectura.class)
                .where("EstadoLectura=" + estado + " AND LEMRUT=" + ruta)
                .orderBy("LEMCTAANT").execute();
    }

    /**
     * Busca una Lectura por los ultimos digitos de la cuenta, la cuenta de
     * busqueda debe estar en formato 00500 (sin guiones). debe ser de 5
     * digitos, caso contrario la busqueda retornará resultados inesperados
     *
     * @param ultimosDigitosCuenta
     * @return Lista de lecturas cuyas cuentas terminan en los digitos
     * seleccionados
     */
    public static List<Lectura> buscarPorUltimosDigitosCuenta(
            String ultimosDigitosCuenta) {
        return new Select().from(Lectura.class)
                .where("LEMCTAANT LIKE '%" + ultimosDigitosCuenta + "'")
                .execute();
    }

    /**
     * Busca una Lectura por su numero de cuenta, la cuenta de busqueda debe
     * estar en formato 111100500 (sin guiones)
     *
     * @param cuenta
     * @return Lectura correspondiente a la cuenta, o null en caso de no
     * encontrarse
     */
    public static Lectura buscarPorCuenta(String cuenta) {
        return new Select().from(Lectura.class).where("LEMCTAANT=?", cuenta)
                .executeSingle();
    }

    /**
     * Busca una Lectura por su numero de medidor
     *
     * @param numMedidor
     * @return Lectura correspondiente al numero de medidor, o null en caso de
     * no encontrarse
     */
    public static Lectura buscarPorNumeroMedidor(String numMedidor) {
        return new Select().from(Lectura.class)
                .where("LEMNROMED=?", numMedidor).executeSingle();
    }

    /**
     * Busca una Lectura por su nus
     *
     * @param nus
     * @return Lectura correspondiente al numero de medidor, o null en caso de
     * no encontrarse
     */
    public static Lectura buscarPorNUS(long nus) {
        return new Select().from(Lectura.class).where("LEMSUM=?", nus)
                .executeSingle();
    }

    /**
     * Verifica si el resultado de la resta de la lectura nueva menos la
     * anterior es elevado.
     *
     * @return true, false
     */
    public boolean esConsumoElevado() {
        if (ConsumoPromedio == 0)
            return false;
        return (calcularConsumoFacturado(LecturaAnterior, LecturaNueva) > (ConsumoPromedio * 2));
    }

    /**
     * Verifica si la variable <b>ConsumoFacturado</b> representa un valor
     * elevado.
     *
     * @return true, false
     */
    public boolean consumoFacturadoAsignadoElevado() {
        if (ConsumoPromedio == 0)
            return false;
        return (ConsumoFacturado > (ConsumoPromedio * 2));
    }

    /**
     * Verifica si el consumo es bajo.
     *
     * @return true, false
     */
    public boolean esConsumoBajo() {
        if (ConsumoPromedio == 0)
            return false;
        return (calcularConsumoFacturado(LecturaAnterior, LecturaNueva) < (ConsumoPromedio / 2));
    }

    /**
     * Calcula el consumo facturado a partir de las lecturas anterior y nueva.
     * No actualiza las variables de Consumo ni ConsumoFacturado
     *
     * @param lectAnterior
     * @param lectNueva
     * @return consumo facturado
     */
    private int calcularConsumoFacturado(int lectAnterior, int lectNueva) {
        int consumo = ((new BigDecimal(lectNueva - lectAnterior))
                .multiply(FactorMultiplicador)).setScale(0,
                RoundingMode.HALF_UP).intValue();
        int consumoFacturado = consumo + KhwAdicionales;
        if (PerdidasCobrePorcentaje != null) {
            consumoFacturado += (PerdidasCobrePorcentaje.multiply(
                    new BigDecimal(consumo)).setScale(0, RoundingMode.HALF_UP)
                    .intValue());
        }
        if (PerdidasHierroKwh != null) {
            consumoFacturado += PerdidasHierroKwh.intValue();
        }
        return consumoFacturado;
    }

    /**
     * Verifica si la lectura actual es menor a la anterior.
     *
     * @return true, false
     */
    public boolean indiceActualEsMenor() {
        return (LecturaNueva < LecturaAnterior);
    }

    /**
     * Valida la lectura
     *
     * @return resultado de la validacion
     */
    public IValidacionLectura validarLectura() {
        if (indiceActualEsMenor())
            return new AdvIndiceMenorActual();
        if (esConsumoBajo())
            return new AdvConsumoBajo();
        if (esConsumoElevado())
            return new AdvConsumoElevado();
        return new ValidacionLecturaCorrecta();
    }

    /**
     * Accede a la base de datos y obtiene todos los ordenativos asignados a la
     * lectura
     *
     * @return lista de ordenativos
     */
    public List<OrdenativoLectura> obtenerOrdenativosLectura() {
        return getMany(OrdenativoLectura.class, "Lectura");
    }

    /**
     * Accede a la base de datos y obtiene todos los ordenativos que aun no
     * fueron asignados a la lectura
     *
     * @return lista de ordenativos
     */
    public List<Ordenativo> obtenerOrdenativosNoUsadosLectura() {
        return new Select()
                .from(Ordenativo.class)
                .where("TIPO_NOV<>'IMPEDIMENTO' AND TIPO_NOV<>'AUTOMATICA' AND ID NOT IN (SELECT Ordenativo FROM OrdenativosLectura WHERE Lectura="
                        + this.getId() + ")").orderBy("IDNOVEDAD").execute();
    }

    /**
     * Accede a la base de datos y obtiene los conceptos asignados a la lectura
     *
     * @return lista de conceptos
     */
    public List<ConceptoLectura> obtenerConceptosLectura() {
        return getMany(ConceptoLectura.class, "Lectura");
    }

    /**
     * Accede a la base de datos y obtiene los conceptos asignados a la lectura,
     * en orden de impresión
     *
     * @return lista de conceptos
     */
    public List<ConceptoLectura> obtenerConceptosLecturaOrdenImpresion() {
        return new Select().from(ConceptoLectura.class)
                .where("LEMSUM=?", Suministro).where("LEMMES=?", Mes)
                .where("LEMANO=?", Anio).orderBy("OrdenImpresion").execute();
    }

    /**
     * Asigna los valores de la lecturaNueva, asi como la fecha de la lectura y
     * cambia su estado a <b>Leida</b>, asigna el consumo, no realiza el
     * guardado en base de datos asigna el usuario de VariablesDeSesion como
     * usuario de auditoria que realiza la lectura.
     *
     * @param lecturaNueva , el valor del medidor de la nueva lectura
     * @param fechaLec     , fecha en que se realiza la lectura
     */
    public void leerLectura(int lecturaNueva, Date fechaLec) {
        UsuarioAuditoria = VariablesDeSesion.getUsuarioLogeado();
        setEstadoLectura(1);// leida
        LecturaNueva = lecturaNueva;
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        FechaLecturaActual = fechaLec;
        HoraLectura = df.format(fechaLec);
        int lecAnteriorCalculable = LecturaAnterior;
        if (lecturaNueva < LecturaAnterior) {
            lecAnteriorCalculable = (-1)
                    * (TopeMedidor.intValue() - LecturaAnterior);
        }
        Consumo = ((new BigDecimal(LecturaNueva - lecAnteriorCalculable))
                .multiply(FactorMultiplicador)).setScale(0,
                RoundingMode.HALF_UP).intValue();
        ConsumoFacturado = Consumo + KhwAdicionales;
        if (PerdidasCobrePorcentaje != null) {
            ConsumoFacturado += (PerdidasCobrePorcentaje.multiply(
                    new BigDecimal(Consumo)).setScale(0, RoundingMode.HALF_UP)
                    .intValue());
        }
        if (PerdidasHierroKwh != null) {
            ConsumoFacturado += PerdidasHierroKwh.intValue();
        }
    }

    /**
     * Obtiene el valor de la estimación de la lectura, no guarda ni cambia
     * ningun estado
     *
     * @return el valor de la lectura estimada
     */
    public int obtenerLecturaActivaEstimada() {
        int consumoPromedioNeto = (new BigDecimal(ConsumoPromedio).divide(
                FactorMultiplicador, 2, RoundingMode.HALF_UP)).intValue();
        return LecturaAnterior + consumoPromedioNeto;// cambiar esto en caso de
        // que se cambie la
        // forma de estimacion
    }

    /**
     * Obtiene el valor de la estimación de la demanda, no guarda ni cambia
     * ningun estado.
     *
     * @return el valor de la demanda estimada
     */
    public BigDecimal obtenerLecturaDemandaEstimada() {
        // cambiar esto en caso de que se cambie la forma de estimacion
        return (new BigDecimal(ConsumoFacturado).divide(
                (FactorCarga.multiply(new BigDecimal(HoraMesPotencia))), 10,
                RoundingMode.HALF_UP)).setScale(0, RoundingMode.HALF_EVEN);
    }

    /**
     * A partir del valor del consumo facturado, busca en la tabla de
     * recategorizaciones si es que hay alguna recategorizacion para su
     * categoria, y en caso de cumplir las condiciones de recategorización, la
     * recategoriza llena los valores de <b>CategoriaRecategorizada</b> con la
     * nueva categoria y pone <b>TagRecategorizacion</b> en 1. y se guarda con
     * la nueva categoria, llamar despues de haber llamado a <b>leerLectura</b>
     *
     * @return retorna true, si es que se realizó una recategorización, false en
     * caso contrario
     */
    public boolean recategorizarSiEsNecesario() {
        List<ReclasificacionCategoria> reclasifsCat = ReclasificacionCategoria
                .obtenerReclasifParaCategoria(Categoria);
        for (ReclasificacionCategoria reclCat : reclasifsCat) {
            if (reclCat.esNecesarioRecategorizar(Categoria, ConsumoFacturado)) {
                CategoriaRecategorizada = reclCat.IdCategoriaDestino;
                SiglaCategoria = reclCat.RecategSiglaDescripcion;
                TagRecategorizacion = 1;
                this.save();
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina los conceptos generados para el aviso de cobranza, los
     * Ordenativos y los datos de Consumo, ConsumoFacturado, ImporteTap,
     * ImporteAseo, ImporteCargoFijo, ImportePorEnergia, ImportePorPotencia,
     * ImporteDescuentoTarifaDignidad, ImporteLey1886, NumFotosTomadas,
     * Enviado3G, GPSLongitud, GPSLatitud LecturaNueva, FechaLecturaActual,
     * HoraLectura y en caso de tener, de la PotenciaFacturada ,
     * FechaLecturaPotencia, HoraLecturaPotencia, EnergiaReactiva y Demanda.
     * Tambien cambia el estado de la lectura a <b>Pendiente</b> , todos estos
     * cambios se guardan en la base de datos Se utiliza para poder dejar la
     * lectura en estado modificable.
     */
    public void resetearLectura() {
        NumModificaciones++;
        ObservacionLectura = 0;
        Consumo = 0;
        ConsumoFacturado = 0;
        ImporteTap = null;
        ImporteAseo = null;
        ImporteCargoFijo = null;
        ImportePorEnergia = null;
        ImportePorPotencia = null;
        ImporteDescuentoTarifaDignidad = null;
        ImporteLey1886 = null;
        LecturaNueva = 0;
        FechaLecturaActual = null;
        HoraLectura = null;
        NumFotosTomadas = 0;
        Enviado3G = 0;
        GPSLatitud = 0;
        GPSLongitud = 0;
        if (PotenciaLectura != null) {
            if (LeePotencia == 1 || TagCalculaPotencia == 1) {
                PotenciaLectura.resetearLecturaPotencia();
            }
            if (LeeReactiva == 1) {
                PotenciaLectura.Reactiva = null;
            }
            PotenciaLectura.save();
        }
        eliminarConceptosEliminables();
        eliminarOrdenativosLectura();
        ManejadorDeCamara.borrarTodasLasFotosDeLectura(Suministro);
        setEstadoLectura(0);// pone la lectura en Pendiente
        this.save();
    }

    /**
     * Verifica si aun se pueden agregar ordenativos a la lectura, dependiendo
     * del numero maximo de ordenativos asignado en las variables de entorno
     */
    public boolean sePuedeAgregarOrdenativos() {
        int cantOrds = this.obtenerOrdenativosLectura().size();
        return (cantOrds < VariablesDeEntorno.numMaxOrdenativosPorLectura);
    }

    /**
     * Elimina todos los ordenativos asignados a esta lectura
     */
    private void eliminarOrdenativosLectura() {
        List<OrdenativoLectura> ordenativosLectura = obtenerOrdenativosLectura();
        for (OrdenativoLectura ordLec : ordenativosLectura) {
            ordLec.delete();
        }
    }

    /**
     * Elimina los conceptos de la lectura que se crearon al momento de generar
     * el aviso de cobranza, es decir, los que tienen el campo EsEliminable=1
     */
    private void eliminarConceptosEliminables() {
        List<ConceptoLectura> conceptosLectura = obtenerConceptosLectura();
        for (ConceptoLectura cptoLec : conceptosLectura) {
            if (cptoLec.EsEliminable == 1) {
                cptoLec.delete();
            }
        }
    }

    /**
     * Verifica si la lectura tiene un recordatorio, si el Recordatorio es null,
     * vacio o esta compuesto de puros espacios devuelve false
     *
     * @return
     */
    public boolean tieneRecordatorio() {
        return !(Recordatorio == null || Recordatorio.isEmpty() || contarEspacios(Recordatorio) == Recordatorio
                .length());
    }

    /**
     * Cuenta los espacios de una cadena es usado por la funcion
     * <b>tieneRecordatorio</b>
     *
     * @param cadena
     * @return
     */
    private int contarEspacios(String cadena) {
        int cont = 0;
        for (int i = 0; i < cadena.length(); i++) {
            if (cadena.charAt(i) == ' ')
                cont++;
        }
        return cont;
    }

    /**
     * Guarda los datos de Latitud y Longitud en la base de datos
     *
     * @param ubicacionActual
     */
    public void guardarUbicacion(Location ubicacionActual) {
        GPSLatitud = ubicacionActual.getLatitude();
        GPSLongitud = ubicacionActual.getLongitude();
        this.save();
        guardarLecturaRemota();
    }

    /**
     * Envia la lectura a guardarse remotamente y en caso de que no estuviera
     * lista, programa la tarea para reintentarse en 40 segundos
     */
    public void guardarLecturaRemota() {
        try {
            ManejadorConexionRemota.guardarLectura(this);
        } catch (Exception e) {
            Handler retryController = new Handler();
            Runnable retryRun = new Runnable() {

                @Override
                public void run() {
                    try {
                        ManejadorConexionRemota.guardarLectura(Lectura.this);
                    } catch (Exception e) {
                    }
                }
            };
            retryController.postDelayed(retryRun, 40000);
        }
    }

    @Override
    public void ejecutarTarea(Location ubicacionObtenida) {
        guardarUbicacion(ubicacionObtenida);
    }

    @Override
    public void ejecturaSiTimeout() {
        guardarLecturaRemota();
    }

    /**
     * Obtiene todas las lecturas realizadas (No pendientes ni postergadas), que
     * tienen el atributo Enviado3G=0
     *
     * @return lista de lecturas que no se enviaron por 3g
     */
    public static List<Lectura> obtenerLecturasNoEnviadas3G() {
        return new Select().from(Lectura.class)
                .where("Enviado3G=0 AND EstadoLectura<>0 AND EstadoLectura<>3")
                .orderBy("LEMCTAANT").execute();
    }

    /**
     * Verifica que todas las lecturas se hayan realizado, y no quede ninguna
     * pendiente ni postergada
     *
     * @return true, si no existe ninguna lectura pendiente ni postergada
     */
    public static boolean seRealizaronTodasLasLecturas() {
        Cursor mCount = Cache
                .openDatabase()
                .rawQuery(
                        "SELECT COUNT(1) FROM "
                                + "(select count(*) NUM from Lecturas WHERE EstadoLectura NOT IN (0, 3)) AS LEIDAS, "
                                + "(select count(*) NUM from Lecturas) AS TODAS "
                                + "WHERE LEIDAS.NUM=TODAS.NUM AND TODAS.NUM>0",
                        null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        return count == 1;
    }

    /**
     * Realiza un count de las lecturas que tienen uno o mas estados
     *
     * @param ruta    si es -1 se hace de todas las rutas
     * @param estados
     * @return
     */
    public static int countLecturasPorEstadoYRuta(int ruta, int... estados) {
        boolean hayEstados = estados.length > 0;
        String[] estadosStr = new String[estados.length];
        StringBuilder inClause = new StringBuilder("(");
        if (hayEstados) {
            for (int i = 0; i < estados.length; i++) {
                estadosStr[i] = "" + estados[i];
                inClause.append((i == (estados.length - 1) ? "?" : "?, "));
            }
            inClause.append(")");
        }
        Cursor mCount = Cache
                .openDatabase()
                .rawQuery(
                        "SELECT COUNT(*) FROM Lecturas "
                                + (hayEstados ? ("WHERE EstadoLectura IN " + inClause.toString())
                                : "")
                                + (ruta != -1 ? ((hayEstados ? " AND"
                                : " WHERE") + " LEMRUT=" + ruta) : ""),
                        (hayEstados ? estadosStr : null));
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        return count;
    }

    @Override
    public void procesarResultado(double resultado) {
        if (resultado == 1.0) {
            Enviado3G = 1;
            this.save();
        }
    }

    @Override
    public String obtenerLineaTextoBackup() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaHora = df.format(this.FechaLecturaActual) + " "
                + this.HoraLectura;
        StringBuilder str = new StringBuilder("");
        str.append(Ruta)
                .append("|")
                .append(Anio)
                .append("|")
                .append(Mes)
                .append("|")
                .append(Dia)
                .append("|")
                .append(Suministro)
                .append("|")
                .append(Cuenta)
                .append("|")
                .append(NumeroMedidor)
                .append("|")
                .append(LecturaNueva)
                .append("|")
                .append(PotenciaLectura != null ? PotenciaLectura.Reactiva
                        : "-")
                .append("|")
                .append(PotenciaLectura != null ? PotenciaLectura.LecturaNuevaPotencia
                        : "-").append("|").append(fechaHora).append("|")
                .append(Estado).append("|").append(UsuarioAuditoria);
        return str.toString();
    }

    @Override
    public String obtenerNombreArchivoBackup() {
        return "lecturas_backup";
    }

    @Override
    public String obtenerCabeceraBackup() {
        return "Ruta|Anio|Mes|Dia|Suministro|Cuenta|Numero Medidor|Lectura Activa|Lectura Reactiva|Lectura Potencia|Fecha y Hora Lectura|Estado|Usuario";
    }

    @Override
    public void setExportStatus(EstadoExportacion estadoExportacion) {
        Enviado3G = estadoExportacion == EstadoExportacion.EXPORTADO ? 2 : 0;
    }

    @Override
    public String getRegistryResume() {
        return "Lectura con suministro: <b>" + Suministro + "</b>";
    }
}
