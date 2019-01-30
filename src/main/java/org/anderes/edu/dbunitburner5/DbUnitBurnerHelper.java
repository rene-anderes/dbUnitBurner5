package org.anderes.edu.dbunitburner5;

import static java.sql.Types.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Optional;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DbUnitBurnerHelper {

    private static Logger logger = LoggerFactory.getLogger(DbUnitBurnerHelper.class);
    
    public static Optional<DataType> createDataType(int sqlType, String sqlTypeName) {
        if (sqlType == TIMESTAMP) {
            return Optional.of(new CustomTimestampDataType());
        } else if (sqlType == DATE) {
            return Optional.of(new CustomDateDataType());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Uses the {@code DatabaseMetaData.getDatabaseProductName()}, and optionally the major version, to try and
     * determine the correct DbUnit {@code IDataTypeFactory} to use.
     * <p>
     * If the database name does not match any of the heuristics applied, a {@code DefaultDataTypeFactory} is returned
     * and a warning is logged. Depending on the database, the default data type factory may result in test failures.
     *
     * @param connection a JDBC connection to obtain {@code DatabaseMetaData}
     * @return the data type factory to use, which will never be {@code null}
     * @throws java.sql.SQLException if metadata cannot be retrieved
     */
    public static IDataTypeFactory resolveDataTypeFactory(final Connection connection) throws SQLException {
        final DatabaseMetaData metaData = connection.getMetaData();
        final String databaseName = metaData.getDatabaseProductName();

        IDataTypeFactory factory;
        if ("HSQL Database Engine".equals(databaseName)) {
            logger.debug("Using HSQL DataTypeFactory");
            factory = new HsqldbDataTypeFactory();
        } else if ("H2".equals(databaseName)) {
            logger.debug("Using H2 DataTypeFactory");
            factory = new H2DataTypeFactory();
        } else if ("MySQL".equals(databaseName)) {
            logger.debug("Using MySQL DataTypeFactory");
            factory = new MySqlDataTypeFactory();
        } else if ("PostgreSQL".equals(databaseName)) {
            logger.debug("Using Postgres DataTypeFactory");
            factory = new PostgresqlDataTypeFactory();
        } else if (databaseName.startsWith("Microsoft SQL Server")) {
            logger.debug("Using SQL Server DataTypeFactory");
            factory = new MsSqlDataTypeFactory();
        } else if ("Oracle".equals(databaseName)) {
            if (metaData.getDatabaseMajorVersion() < 10) {
                logger.debug("Using Oracle DataTypeFactory for 10g and later");
                factory = new OracleDataTypeFactory();
            } else {
                logger.debug("Using Oracle DataTypeFactory for 9i and earlier");
                factory = new Oracle10DataTypeFactory();
            }
        } else if ("Derby".equalsIgnoreCase(databaseName) || "Apache Derby".equalsIgnoreCase(databaseName) ) {
            logger.debug("Using Derby DataTypeFactory (DefaultDataTypeFactory)");
            factory = new DefaultDataTypeFactory();
        } else {
            logger.warn("No IDataTypeFactory was resolved for {}. Using default DataTypeFactory. This may result in " +
                    "test failures. If so, please update {} with an explicit DataTypeFactory for this database.",
                    databaseName, DbUnitBurnerHelper.class);
            return new DefaultDataTypeFactory();
        }

        return factory;
    }
}
