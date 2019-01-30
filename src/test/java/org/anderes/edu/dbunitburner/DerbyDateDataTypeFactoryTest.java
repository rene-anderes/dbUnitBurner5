package org.anderes.edu.dbunitburner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.datatype.IntegerDataType;
import org.junit.jupiter.api.Test;

import static java.sql.Types.*;

public class DerbyDateDataTypeFactoryTest {

    
    @Test
    public void shouldBeCorrectDateDataType() throws DataTypeException {
        // given
        IDataTypeFactory factory = new DerbyDateDataTypeFactory();
        // when
        DataType dataType = factory.createDataType(DATE, "");
        // then
        assertThat(dataType, is(not(nullValue())));
        assertThat(dataType, instanceOf(CustomDateDataType.class));
    }
    
    @Test
    public void shouldBeOtherDataType() throws DataTypeException {
        // given
        IDataTypeFactory factory = new DerbyDateDataTypeFactory();
        // when
        DataType dataType = factory.createDataType(INTEGER, "");
        // then
        assertThat(dataType, is(not(nullValue())));
        assertThat(dataType, instanceOf(IntegerDataType.class));
    }
}
