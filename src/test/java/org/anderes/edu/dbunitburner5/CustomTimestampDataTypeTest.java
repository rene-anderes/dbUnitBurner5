package org.anderes.edu.dbunitburner5;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Timestamp;

import org.dbunit.dataset.datatype.TypeCastException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomTimestampDataTypeTest {
    
    private CustomTimestampDataType datatype;
    
    @BeforeEach
    public void setup() {
        datatype = new CustomTimestampDataType();
    }
    
    @Test
    void shouldBeTimestampWithSeconds() throws TypeCastException {
        final Timestamp expectedTimestamp = Timestamp.valueOf("2015-01-22 23:03:20");
        final Timestamp timestamp = datatype.typeCast("22.01.2015 23:03:20");
        
        assertThat(timestamp).isNotNull().isEqualTo(expectedTimestamp);
    }
    
    @Test
    void shouldBeTimestampWithoutSeconds() throws TypeCastException {
        final Timestamp expectedTimestamp = Timestamp.valueOf("2015-01-22 23:03:00");
        final Timestamp timestamp = datatype.typeCast("22.01.2015 23:03");
        
        assertThat(timestamp).isNotNull().isEqualTo(expectedTimestamp);
    }
    
    @Test
    void shouldBeTimestampWithoutTime() throws TypeCastException {
        final Timestamp expectedTimestamp = Timestamp.valueOf("2015-01-22 00:00:00");
        final Timestamp timestamp = datatype.typeCast("22.01.2015");
        
        assertThat(timestamp).isNotNull().isEqualTo(expectedTimestamp);
    }
    
    @Test
    void shouldBeWrongString() throws TypeCastException {
        assertThrows(TypeCastException.class, () -> { datatype.typeCast("31.12-2015"); });
    }

}
