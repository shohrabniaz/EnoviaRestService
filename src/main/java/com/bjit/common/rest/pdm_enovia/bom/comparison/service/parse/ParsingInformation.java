package com.bjit.common.rest.pdm_enovia.bom.comparison.service.parse;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * @author Ashikur Rahman / BJIT
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE })
public @interface ParsingInformation {
	public Type type() default Type.JSON;
	public SourceType sourceType() default SourceType.STRING;
	public String sourceInput() default "";
	public Class<?> sourceDataModel();
}