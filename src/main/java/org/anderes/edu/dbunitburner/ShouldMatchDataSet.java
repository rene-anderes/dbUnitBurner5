package org.anderes.edu.dbunitburner;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface ShouldMatchDataSet {
    String[] value();
    String[] excludeColumns() default { };
    String[] orderBy() default { }; 
}