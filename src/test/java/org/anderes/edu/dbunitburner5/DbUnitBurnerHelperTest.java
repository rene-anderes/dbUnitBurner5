package org.anderes.edu.dbunitburner5;

import static java.sql.Types.DATE;
import static java.sql.Types.INTEGER;
import static java.sql.Types.TIMESTAMP;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.dbunit.dataset.datatype.DataType;
import org.junit.jupiter.api.Test;

public class DbUnitBurnerHelperTest {

    @Test
    public void shouldBeTimestampDataType() {
        // when
        Optional<DataType> dataType = DbUnitBurnerHelper.createDataType(TIMESTAMP, "");
        
        // then
        assertThat(dataType).isNotNull();
        assertThat(dataType.isPresent()).isTrue();
        assertThat(dataType.get()).isInstanceOf(CustomTimestampDataType.class);
    }
    
    @Test
    public void shouldBeDateDataType() {
        // when
        Optional<DataType> dataType = DbUnitBurnerHelper.createDataType(DATE, "");
        
        // then
        assertThat(dataType).isNotNull();
        assertThat(dataType.isPresent()).isTrue();
        assertThat(dataType.get()).isInstanceOf(CustomDateDataType.class);
    }
    
    @Test
    public void shouldBeNotFoundDataType() {
        // when
        Optional<DataType> dataType = DbUnitBurnerHelper.createDataType(INTEGER, "");
        
        // then
        assertThat(dataType).isNotNull();
        assertThat(dataType.isPresent()).isFalse();
    }
}
