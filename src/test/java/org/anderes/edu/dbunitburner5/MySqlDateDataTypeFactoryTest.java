package org.anderes.edu.dbunitburner5;

import static java.sql.Types.DATE;
import static java.sql.Types.INTEGER;
import static org.assertj.core.api.Assertions.assertThat;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.datatype.IntegerDataType;
import org.junit.jupiter.api.Test;

public class MySqlDateDataTypeFactoryTest {

    
    @Test
    public void shouldBeCorrectDateDataType() throws DataTypeException {
        // given
        IDataTypeFactory factory = new MySqlDateDataTypeFactory();
        // when
        DataType dataType = factory.createDataType(DATE, "");
        // then
        assertThat(dataType).isNotNull();
        assertThat(dataType).isInstanceOf(CustomDateDataType.class);
    }
    
    @Test
    public void shouldBeOtherDataType() throws DataTypeException {
        // given
        IDataTypeFactory factory = new MySqlDateDataTypeFactory();
        // when
        DataType dataType = factory.createDataType(INTEGER, "");
        // then
        assertThat(dataType).isNotNull();
        assertThat(dataType).isInstanceOf(IntegerDataType.class);
    }
}
