package org.anderes.edu.dbunitburner5;

import static java.sql.Types.DATE;
import static java.sql.Types.INTEGER;
import static java.sql.Types.TIMESTAMP;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.dbunit.dataset.datatype.DataType;
import org.junit.jupiter.api.Test;

class DbUnitBurnerHelperTest {

    @Test
    void shouldBeTimestampDataType() {
        // when
        Optional<DataType> dataType = DbUnitBurnerHelper.createDataType(TIMESTAMP);
        
        // then
        assertThat(dataType).isNotNull().isPresent();
        assertThat(dataType.get()).isInstanceOf(CustomTimestampDataType.class);
    }
    
    @Test
    void shouldBeDateDataType() {
        // when
        Optional<DataType> dataType = DbUnitBurnerHelper.createDataType(DATE);
        
        // then
        assertThat(dataType).isNotNull().isPresent();
        assertThat(dataType.get()).isInstanceOf(CustomDateDataType.class);
    }
    
    @Test
    void shouldBeNotFoundDataType() {
        // when
        Optional<DataType> dataType = DbUnitBurnerHelper.createDataType(INTEGER);
        
        // then
        assertThat(dataType).isNotNull().isNotPresent();
    }
}
