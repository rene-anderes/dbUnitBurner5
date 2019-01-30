package org.anderes.edu.dbunitburner;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;

import org.apache.commons.lang3.time.DateUtils;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.TypeCastException;

public class CustomDateDataType extends AbstractDataType {

    private final String formats[] = { "dd.MM.yyyy HH:mm:ss", "dd.MM.yyyy HH:mm", "dd.MM.yyyy" };
    
    public CustomDateDataType() {
        super("DATE", Types.DATE, Date.class, false);
    }

    @Override
    public Date typeCast(Object value) throws TypeCastException {
        if (value == null || value == ITable.NO_VALUE) {
            return null;
        }
        if (value instanceof java.sql.Timestamp) {
            return new Date(((Timestamp)value).getTime());
        }
        if (value instanceof java.util.Date) {
            return (Date) value;
        }
        if (value instanceof String) {
            return handleStringValue((String)value);
        }
        throw new TypeCastException(value, this);
    }

    private Date handleStringValue(final String value) throws TypeCastException {
        try {
            return java.sql.Date.valueOf(value);
        } catch (IllegalArgumentException e) {}

        try {
            return new Date(DateUtils.parseDate(value, formats).getTime());
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
        final Date value = resultSet.getDate(column);
        if (value == null || resultSet.wasNull()) {
            return null;
        }
        return value;
    }

    @Override
    public void setSqlValue(Object value, int column, PreparedStatement statement) throws SQLException, TypeCastException {
        statement.setDate(column, (java.sql.Date) typeCast(value));
    }

}
