package org.anderes.edu.dbunitburner;

import java.util.Optional;
import static java.sql.Types.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.dbunit.dataset.datatype.DataType;
import org.junit.jupiter.api.Test;

public class DbUnitBurnerHelperTest {

    @Test
    public void shouldBeTimestampDataType() {
        // when
        Optional<DataType> dataType = DbUnitBurnerHelper.createDataType(TIMESTAMP, "");
        
        // then
        assertThat(dataType, is(not(nullValue())));
        assertThat(dataType.isPresent(), is(true));
        assertThat(dataType.get(), instanceOf(CustomTimestampDataType.class));
    }
    
    @Test
    public void shouldBeDateDataType() {
        // when
        Optional<DataType> dataType = DbUnitBurnerHelper.createDataType(DATE, "");
        
        // then
        assertThat(dataType, is(not(nullValue())));
        assertThat(dataType.isPresent(), is(true));
        assertThat(dataType.get(), instanceOf(CustomDateDataType.class));
    }
    
    @Test
    public void shouldBeNotFoundDataType() {
        // when
        Optional<DataType> dataType = DbUnitBurnerHelper.createDataType(INTEGER, "");
        
        // then
        assertThat(dataType, is(not(nullValue())));
        assertThat(dataType.isPresent(), is(false));
    }
}
