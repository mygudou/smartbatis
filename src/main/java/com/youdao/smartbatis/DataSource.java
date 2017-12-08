package com.youdao.smartbatis;

import java.lang.annotation.*;

/**
 *
 * @author liugang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface DataSource {

    DataSourceType type() default DataSourceType.WRITE;
}
