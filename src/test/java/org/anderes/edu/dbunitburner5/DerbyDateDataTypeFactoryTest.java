package org.anderes.edu.dbunitburner5;

import static java.sql.Types.DATE;
import static java.sql.Types.INTEGER;
import static org.assertj.core.api.Assertions.assertThat;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.datatype.IntegerDataType;
import org.junit.jupiter.api.Test;

class DerbyDateDataTypeFactoryTest {

    
    @Test
    void shouldBeCorrectDateDataType() throws DataTypeException {
        // given
        IDataTypeFactory factory = new DerbyDateDataTypeFactory();
        // when
        DataType dataType = factory.createDataType(DATE, "");
        // then
        assertThat(dataType).isNotNull().isInstanceOf(CustomDateDataType.class);
    }
    
    @Test
    void shouldBeOtherDataType() throws DataTypeException {
        // given
        IDataTypeFactory factory = new DerbyDateDataTypeFactory();
        // when
        DataType dataType = factory.createDataType(INTEGER, "");
        // then
        assertThat(dataType).isNotNull().isInstanceOf(IntegerDataType.class);
    }
}
