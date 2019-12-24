package org.anderes.edu.dbunitburner5;

import static org.apache.commons.lang3.StringUtils.containsNone;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.dbunit.database.DatabaseConfig.PROPERTY_DATATYPE_FACTORY;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.dbunit.Assertion;
import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.IOperationListener;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.CsvDataFileLoader;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.dbunit.util.fileloader.XlsDataFileLoader;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbUnitExtension implements BeforeEachCallback, AfterEachCallback {

    private Logger logger = LoggerFactory.getLogger(DbUnitExtension.class);
    private IDatabaseTester databaseTester;
    private final Optional<Connection> connection;
        
    public DbUnitExtension() {
        this.connection = Optional.empty();
    }
    
    public DbUnitExtension(final Connection connection) {
        this.connection = Optional.of(connection);
    }
    
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        logger.trace("afterEach");
        after(context);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        logger.trace("beforeEach");
        
        try {
            final Connection connection = getConnection(context);
            DatabaseConnection databaseConnection = new DatabaseConnection(connection);
            databaseConnection.getConfig().setProperty(PROPERTY_DATATYPE_FACTORY, DbUnitBurnerHelper.resolveDataTypeFactory(connection));
            databaseTester = new DefaultDatabaseTester(databaseConnection);
            before(context);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private Connection getConnection(ExtensionContext context) throws Exception {
        if (connection.isPresent()) {
            return connection.get();
        }
        final Class<?> testClass = context.getRequiredTestClass();
        Optional<Field> fieldFound = Arrays.stream(testClass.getDeclaredFields()).filter(f -> f.getType() == DataSource.class).findFirst();
        if (fieldFound.isPresent()) {
            Field field = fieldFound.get();
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            final Connection connection = ((DataSource) field.get(context.getRequiredTestInstance())).getConnection();
            if (connection == null) {
                throw new RuntimeException("Connection not initialized correctly");
            }
            return connection;
        } else {
            fieldFound = Arrays.stream(testClass.getDeclaredFields()).filter(f -> f.getType() == Connection.class).findFirst();
            if (fieldFound.isPresent()) {
                Field field = fieldFound.get();
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                final Connection connection = (Connection) field.get(context.getRequiredTestInstance());
                if (connection == null) {
                    throw new RuntimeException("Connection not initialized correctly");
                }
                return connection;
            }
        }
        throw new RuntimeException("no connection !!");
    }

    private void before(final ExtensionContext context) throws Exception {
        final UsingDataSet usingDataSet = extractUsingDataSet(context);
        final CleanupUsingScript cleanupUsingScript = extractCleanupUsingScript(context);
        final UsingDataSetScript usingDataSetScript = extractUsingDataSetScript(context);
        DatabaseOperation databaseOperation = DatabaseOperation.CLEAN_INSERT;
        if (cleanupUsingScript != null) {
            processCleanupScripts(cleanupUsingScript);
        }
        if (usingDataSet != null) {
            processUsingDataSet(usingDataSet, databaseOperation);
        } else if (usingDataSetScript != null) {
            processUsingDataSetScript(usingDataSetScript);
        }
    }

    private UsingDataSetScript extractUsingDataSetScript(final ExtensionContext context) {
        
        Optional<UsingDataSetScript> script = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), UsingDataSetScript.class);
        if (script.isPresent()) {
            return script.get();
        }
        
        script = AnnotationSupport.findAnnotation(context.getRequiredTestClass(), UsingDataSetScript.class);
        if (script.isPresent()) {
            return script.get();
        }
        
        return null;
    }

    private CleanupUsingScript extractCleanupUsingScript(final ExtensionContext context) {
        
        Optional<CleanupUsingScript> script = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), CleanupUsingScript.class);
        if (script.isPresent()) {
            return script.get();
        }
        
        script = AnnotationSupport.findAnnotation(context.getRequiredTestClass(), CleanupUsingScript.class);
        if (script.isPresent()) {
            return script.get();
        }
        
        return null;
    }

    private UsingDataSet extractUsingDataSet(final ExtensionContext context) {
        
        Optional<UsingDataSet> script = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), UsingDataSet.class);
        if (script.isPresent()) {
            return script.get();
        }
        
        script = AnnotationSupport.findAnnotation(context.getRequiredTestClass(), UsingDataSet.class);
        if (script.isPresent()) {
            return script.get();
        }
        
        return null;
    }

    private void processUsingDataSet(final UsingDataSet usingDataSet, final DatabaseOperation databaseOperation) throws DataSetException, Exception, SQLException {
        logger.trace("processUsingDataSet: " + usingDataSet.toString());
        final String[] dataSetFiles = usingDataSet.value();
        final CompositeDataSet dataSet = buildDataSet(dataSetFiles);
        final IDatabaseConnection databaseConnection = databaseTester.getConnection();
        final IDataSet filteredDataSet = new FilteredDataSet(new DatabaseSequenceFilter(databaseConnection), dataSet);
        databaseTester.setOperationListener(IOperationListener.NO_OP_OPERATION_LISTENER);
        databaseTester.setSetUpOperation(databaseOperation);
        databaseTester.setDataSet(filteredDataSet);
        databaseTester.onSetup();
    }
    
    private void processCleanupScripts(final CleanupUsingScript cleanupUsingScript) throws Exception {
        logger.trace("processCleanupScripts: " + cleanupUsingScript.toString());
        final String[] cleanupFiles = cleanupUsingScript.value();
        processSqlScript(cleanupFiles);
    }

    private void processSqlScript(final String[] cleanupFiles) throws IOException, SQLException, Exception {
        logger.trace("processSqlScript");
        for (String cleanupFile : cleanupFiles) {
            final Collection<String> commands = SqlHelper.extractSqlCommands(Paths.get(cleanupFile));
            int[] results = SqlHelper.execute(databaseTester.getConnection().getConnection(), commands);
            resultToLogIsEnabled(commands, results);
        }
    }

    private void resultToLogIsEnabled(final Collection<String> commands, int[] values) {
        if (logger.isInfoEnabled()) {
            final String[] commandArray = commands.toArray(new String[commands.size()]);
            for (int index = 0 ; index < commands.size(); index++) {
                logger.info(commandArray[index] + ", Result: " + values[index]);
            }
        }
    }

    private void processUsingDataSetScript(final UsingDataSetScript usingDataSetScript) throws Exception {
        logger.trace("processUsingDataSetScript");
        final String[] usingDataSetScriptFiles = usingDataSetScript.value();
        processSqlScript(usingDataSetScriptFiles);
    }

    /*package*/ CompositeDataSet buildDataSet(String[] dataSetFiles) throws DataSetException {
        final List<IDataSet> dataSets = new ArrayList<IDataSet>(dataSetFiles.length);
        for (String dataSetFile : dataSetFiles) {
            DataFileLoader loader = identifyLoader(dataSetFile);
            IDataSet dataset = loader.load(dataSetFile);
            dataSets.add(dataset);
        }
        return new CompositeDataSet(dataSets.toArray(new IDataSet[dataSets.size()]));
    }

    private DataFileLoader identifyLoader(String dataSetFile) {
        DataFileLoader loader;
        if (dataSetFile.endsWith(".xml")) {
            loader = new FlatXmlDataFileLoader();
        } else if (dataSetFile.endsWith(".csv")) {
            loader = new CsvDataFileLoader();
        } else if (dataSetFile.endsWith(".xls")) {
            loader = new XlsDataFileLoader();
        } else if (dataSetFile.endsWith(".json")) {
            loader = new JsonDataFileLoader();
        } else {
            throw new IllegalStateException("DbUnitRule only supports XLS, CSV, JSON or Flat XML data sets for the moment");
        }
        return loader;
    }
    
    private void after(final ExtensionContext context) throws Exception {
        final Optional<ShouldMatchDataSet> annotation = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ShouldMatchDataSet.class);
        if (annotation.isPresent()) {
            compareDatabase(annotation.get());
            databaseTester.setTearDownOperation(DatabaseOperation.NONE);
            databaseTester.onTearDown();
        }
    }

    private void compareDatabase(final ShouldMatchDataSet annotation) throws Exception {
        final String[] dataSetFiles = annotation.value();
        final CompositeDataSet expectedDataSet = buildDataSet(dataSetFiles);
        final IDatabaseConnection databaseConnection = databaseTester.getConnection();
        final IDataSet databaseDataSet = databaseConnection.createDataSet();
      
        for (String tablename : expectedDataSet.getTableNames()) {
            final ITable expectedTable = buildFilteredAndSortedTable(expectedDataSet.getTable(tablename), annotation);
            final ITable actualTable = buildFilteredAndSortedTable(databaseDataSet.getTable(tablename), annotation);
            Assertion.assertEquals(expectedTable, actualTable);
        }
    }
    
    /*package*/ Map<String, String[]> buildMapFromStringArray(final String[] array) {
        final Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (String value : array) {
            if (containsNone(value, ".")) {
                continue;
            }
            final String table = substringBefore(value, ".").toUpperCase();
            final String column = substringAfter(value, ".").toUpperCase();
            if (map.containsKey(table)) {
                map.get(table).add(column);
            } else {
                List<String> list = new ArrayList<String>();
                list.add(column);
                map.put(table, list);
            }
        }
        final Map<String, String[]> returnValue = new HashMap<String, String[]>(map.size());
        for (String tablename : map.keySet()) {
            returnValue.put(tablename, map.get(tablename).toArray(new String[0]));
        }
        return returnValue;
    }
    
    private ITable buildFilteredAndSortedTable(final ITable originalTable, final ShouldMatchDataSet annotation) throws DataSetException {
        final Map<String, String[]> excludeColumns = buildMapFromStringArray(annotation.excludeColumns());
        final Map<String, String[]> orderBy = buildMapFromStringArray(annotation.orderBy());
        final String tablename = originalTable.getTableMetaData().getTableName().toUpperCase();
        ITable table = sortTable(orderBy, tablename, originalTable); 
        table = filterTable(table, excludeColumns, tablename);
        return table;
    }

    private ITable sortTable(final Map<String, String[]> orderBy, final String tablename, ITable table) throws DataSetException {
        if (orderBy.containsKey(tablename)) {
            final SortedTable sortedTable = new SortedTable(table, orderBy.get(tablename));
            sortedTable.setUseComparable(true); 
            return sortedTable;
        }
        return table;
    }

    private ITable filterTable(final ITable originalTable, final Map<String, String[]> excludeColumns, final String tablename) throws DataSetException {
        ITable table;
        if (excludeColumns.containsKey(tablename)) {
            table = DefaultColumnFilter.excludedColumnsTable(originalTable, excludeColumns.get(tablename));
        } else {
            table = originalTable;
        }
        return table;
    }
}
