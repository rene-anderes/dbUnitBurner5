package org.anderes.edu.dbunitburner5;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.sql.Timestamp;

import org.dbunit.dataset.datatype.TypeCastException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CustomDateDataTypeTest {

    private CustomDateDataType datatype;
    
    @BeforeEach
    public void setUp() {
        datatype = new CustomDateDataType();
    }
    
    @Test
    public void shouldBeDateByDateString() throws TypeCastException {
        final Date expectedDate = Date.valueOf("2015-01-31");
        final Date date = datatype.typeCast("31.01.2015");
        
        assertThat(date).isNotNull();
        assertThat(date).isEqualTo(expectedDate);
    }
    
    @Test
    public void shouldBeDateByDatetimeString() throws TypeCastException {
        final Date expectedDate = new Date(1422734400000L);
        final Date date = datatype.typeCast("31.1.2015 21:00:00");
        
        assertThat(date).isNotNull();
        assertThat(date).isEqualTo(expectedDate);
    }
    
    @Test
    public void shouldBeDateByTimestamp() throws TypeCastException {
        Timestamp timestamp = new Timestamp(1390428200000L);
        final Date expectedDate = new Date(timestamp.getTime());
        final Date date = datatype.typeCast(timestamp);
        
        assertThat(date).isNotNull();
        assertThat(date).isEqualTo(expectedDate);
    }
    
    @Test
    public void shouldBeDateBySqlDateFormat() throws TypeCastException {
        final Date expectedDate = Date.valueOf("2015-01-31");
        final Date date = datatype.typeCast("2015-1-31");
        
        assertThat(date).isNotNull();
        assertThat(date).isEqualTo(expectedDate);
    }
    
    @Test
    public void shouldBeWrongString() throws TypeCastException {
        assertThrows(TypeCastException.class, () -> { datatype.typeCast("2015-12.31"); });
    }
}
