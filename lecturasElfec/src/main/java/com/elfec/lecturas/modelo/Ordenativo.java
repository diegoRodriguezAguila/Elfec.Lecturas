package com.elfec.lecturas.modelo;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Almacena la información sobre los ordenativos, de la tabla
 * ERP_ELFEC.TIPOS_NOV_SUM de la BD Oracle
 *
 * @author drodriguez
 */
@Table(name = "Ordenativos")
public class Ordenativo extends Model {

    /**
     * Código del ordenativo de casa demolida
     */
    public static final int ORD_CASA_DEMOLIDA = 31;
    /**
     * Código del ordenativo de lectura adelantada
     */
    public static final int ORD_LECT_ADELANTADA = 68;
    /**
     * Código del ordenativo de volteo de medidor
     */
    public static final int ORD_VOLTEO_MEDIDOR = 85;

    public Ordenativo() {
        super();
    }

    public Ordenativo(ResultSet rs) throws SQLException {
        super();
        Codigo = rs.getInt("IDNOVEDAD");
        Descripcion = rs.getString("DESCRIPCION");
        TipoNovedad = rs.getString("TIPO_NOV_MOV");
    }

    @Column(name = "IDNOVEDAD", index = true)
    public int Codigo;

    @Column(name = "DESCRIPCION")
    public String Descripcion;

    @Column(name = "TIPO_NOV")
    public String TipoNovedad;

    /**
     * Accede a la base de datos y obtiene todos los ordenativos
     *
     * @return Lista de ordenativos en orden por codigo
     */
    public static List<Ordenativo> obtenerTodos() {
        return new Select().from(Ordenativo.class)
                .orderBy("IDNOVEDAD").execute();
    }

    /**
     * Accede a la base de datos y obtiene todos los ordenativos que no sean de
     * impedimento ni automaticos en orden por codigo
     *
     * @return Lista de ordenativos en orden por codigo
     */
    public static List<Ordenativo> obtenerTodosLosOrdenativos() {
        return new Select().from(Ordenativo.class)
                .where("TIPO_NOV<>'IMPEDIMENTO' AND TIPO_NOV<>'AUTOMATICA'")
                .orderBy("IDNOVEDAD").execute();
    }

    /**
     * Accede a la base de datos y obtiene todos los ordenativos de impedimento
     * en orden por codigo
     *
     * @return Lista de ordenativos de impedimento de lectura en orden por
     * codigo
     */
    public static List<Ordenativo> obtenerOrdenativosDeImpedimento() {
        return new Select().from(Ordenativo.class)
                .where("TIPO_NOV='IMPEDIMENTO'").orderBy("IDNOVEDAD").execute();
    }

    /**
     * Accede a la base de datos y obtiene el ordenativo correspondiente al
     * código solicitado
     *
     * @return Ordenativo correspondiente al código solicitado, o null si no
     * existe
     */
    public static Ordenativo obtenerOrdenativoPorCodigo(int codigo) {
        return new Select().from(Ordenativo.class).where("IDNOVEDAD=?", codigo)
                .executeSingle();
    }

    /**
     * Accede a la base de datos y obtiene el ordenativo correspondiente a
     * Lectura Estimada
     *
     * @return Ordenativo correspondiente a lectura estimada, o null si no
     * existe
     */
    public static Ordenativo obtenerOrdenativoLecturaEstimada() {
        int codigo = 9; // en caso de que el codigo del ordenativo de lectura
        // cambiase se debe cambiar este codigo
        return new Select().from(Ordenativo.class).where("IDNOVEDAD=?", codigo)
                .executeSingle();
    }

    /**
     * Accede a la base de datos y obtiene el ordenativo correspondiente a
     * Lectura Reintentar
     *
     * @return Ordenativo correspondiente a lectura reintentar, o null si no
     * existe
     */
    public static Ordenativo obtenerOrdenativoLecturaReintentar() {
        int codigo = 23; // en caso de que el codigo del ordenativo de lectura
        // cambiase se debe cambiar este codigo
        return new Select().from(Ordenativo.class).where("IDNOVEDAD=?", codigo)
                .executeSingle();
    }

    /**
     * ELimina todos los ordenativos guardados
     */
    public static void eliminarTodosLosOrdenativos() {
        new Delete().from(Ordenativo.class).execute();
    }

}
