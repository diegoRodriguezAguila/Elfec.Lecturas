package com.elfec.lecturas.helpers.sql;

import com.elfec.lecturas.helpers.utils.text.AttributePicker;
import com.elfec.lecturas.helpers.utils.text.ObjectListToSQL;
import com.elfec.lecturas.modelo.Lectura;

import java.util.List;

/**
 * Created by drodriguez on 12/07/2016.
 * SqlUtils
 */
public class SqlUtils {

    /**
     * Obtiene la clausula In de la información general de lecturas
     *
     * @param lecturas
     * @return clausula IN SQL de suministros de lecturas
     */
    public static String convertirAClausulaIn(List<Lectura> lecturas) {
        return ObjectListToSQL.convertToSQL(lecturas, "LEMSUM",
                new AttributePicker<String, Lectura>() {
                    @Override
                    public String pickAttribute(Lectura readingGeneralInfo) {
                        return "" + readingGeneralInfo.Suministro;
                    }
                });
    }
}
