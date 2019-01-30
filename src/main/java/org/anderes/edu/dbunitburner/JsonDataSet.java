package org.anderes.edu.dbunitburner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.dbunit.DatabaseUnitRuntimeException;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Diese Klasse wandelt einen JOSN-String in eine entsprechenden Liste von {@code ITable} um.
 * 
 * @author René Anderes
 *
 */
public class JsonDataSet extends AbstractDataSet {
    private final JsonITableParser tableParser = new JsonITableParser();

    private List<ITable> tables;

    public JsonDataSet(final String filename) throws IOException {
        Validate.notNull(filename);
        final URL url = this.getClass().getResource(filename);
        if (url == null) {
            final String msg = "Could not find file named = '" + filename + "'";
            throw new DatabaseUnitRuntimeException(msg);
        }
        tables = tableParser.getTables(url.openStream());
    }

    public JsonDataSet(URL url) throws IOException {
        Validate.notNull(url);
        tables = tableParser.getTables(url.openStream());
    }

    @Override
    protected ITableIterator createIterator(boolean reverse) throws DataSetException {
        return new DefaultTableIterator((ITable[]) tables.toArray(new ITable[tables.size()]));
    }

    private class JsonITableParser {

        private final ObjectMapper mapper = new ObjectMapper();

        @SuppressWarnings({ "unchecked" })
        public List<ITable> getTables(InputStream jsonStream) throws IOException {
            Validate.notNull(jsonStream);

            final ArrayList<ITable> tables = new ArrayList<ITable>();
            final Map<String, Object> dataset = mapper.readValue(jsonStream, Map.class);
            for (Map.Entry<String, Object> entry : dataset.entrySet()) {
                final List<Map<String, Object>> rows = (List<Map<String, Object>>) entry.getValue();
                final ITableMetaData meta = getMetaData(entry.getKey(), rows);
                final DefaultTable table = new DefaultTable(meta);
                int rowIndex = 0;
                for (Map<String, Object> row : rows) {
                    fillRow(table, row, rowIndex++);
                }
                tables.add(table);
            }
            return tables;
        }

        private ITableMetaData getMetaData(String tableName, List<Map<String, Object>> rows) {
            final LinkedHashSet<String> columns = new LinkedHashSet<String>();
            for (Map<String, Object> row : rows) {
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    columns.add(column.getKey());
                }
            }
            final List<Column> list = columns.stream().map(s -> new Column(s, DataType.UNKNOWN)).collect(Collectors.toList());
            return new DefaultTableMetaData(tableName, (Column[]) list.toArray(new Column[list.size()]));
        }

        private void fillRow(DefaultTable table, Map<String, Object> row, int rowIndex) {
            try {
                table.addRow();
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    table.setValue(rowIndex, column.getKey(), column.getValue());
                }
            } catch (Exception e) {
                throw new DatabaseUnitRuntimeException(e.getMessage(), e);
            }
        }
    }
}
