package org.anderes.edu.dbunitburner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.TypeCastException;

public class CustomTimestampDataType extends AbstractDataType {

    private final String formats[] = { "dd.MM.yyyy HH:mm:ss", "dd.MM.yyyy HH:mm", "dd.MM.yyyy" };
    
    public CustomTimestampDataType() {
        super("TIMESTAMP", Types.TIMESTAMP, Timestamp.class, false);
    }

    @Override
    public final Timestamp typeCast(final Object value) throws TypeCastException {
        if (value == null || value == ITable.NO_VALUE) {
            return null;
        }
        if (value instanceof java.sql.Timestamp) {
            return (Timestamp)value;
        }
        if (value instanceof java.util.Date) {
            final Date date = (Date) value;
            return new Timestamp(date.getTime());
        }
        if (value instanceof Long) {
            Long date = (Long) value;
            return new Timestamp(date.longValue());
        }
        if (value instanceof String) {
            return handleStringValue((String)value);
        }
        throw new TypeCastException(value, this);
    }

    private Timestamp handleStringValue(final String value) throws TypeCastException {
        try {
            long time = java.sql.Date.valueOf(value).getTime();
            return new Timestamp(time);
        } catch (IllegalArgumentException e) {}
        
        try {
            return new Timestamp(DateUtils.parseDate(value, formats).getTime());
        } catch (ParseException e) {
            throw new TypeCastException(value, this);
        }
    }

    @Override
    public boolean isDateTime() {
        return true;
    }

    @Override
    public Object getSqlValue(int column, ResultSet resultSet) throws SQLException, TypeCastException {
        Timestamp value = resultSet.getTimestamp(column);
        if (value == null || resultSet.wasNull()) {
            return null;
        }
        return value;
    }

    @Override
    public void setSqlValue(Object value, int column, PreparedStatement statement) throws SQLException, TypeCastException {
        statement.setTimestamp(column, (java.sql.Timestamp) typeCast(value));
    }

}
