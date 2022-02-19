package org.anderes.edu.dbunitburner5;

import java.util.Optional;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mittels dieser Data-Type-Factory ist es möglich ein Zeitstempel in folgenden Formaten im Datenbankfile abzulegen:<br>
 * dd.MM.yyyy HH:mm:ss     (Nanosekunden sind 0)<br>
 * dd.MM.yyyy HH:mm        (Sekunden und Nanosekunden sind 0)<br>
 * dd.MM.yyyy              (Stunden, Minuten, Sekunden und Nanosekunden sind 0)<br>
 * <p>
 * 
 * @author René Anderes
 *
 */
public class MySqlDateDataTypeFactory extends MySqlDataTypeFactory {

    private Logger logger = LoggerFactory.getLogger(MySqlDateDataTypeFactory.class);
    
    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        Optional<DataType> optionalDatType = DbUnitBurnerHelper.createDataType(sqlType);
        if (optionalDatType.isPresent()) {
            final DataType dataType = optionalDatType.get();
            if (logger.isDebugEnabled()) {
                final String msg = String.format("Für den SQL-Type '%s' wird Klasse '%s' eingesetzt.", sqlTypeName, dataType.getClass().getName());
                logger.debug(msg);
            }
            return dataType;
        }
        return super.createDataType(sqlType, sqlTypeName);
    }
    
}
