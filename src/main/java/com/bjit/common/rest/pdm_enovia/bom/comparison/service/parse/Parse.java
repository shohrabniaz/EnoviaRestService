package com.bjit.common.rest.pdm_enovia.bom.comparison.service.parse;
/**
 * @author Ashikur Rahman / BJIT
 */
@FunctionalInterface
public interface Parse<T> {
	public T parse() throws Exception;
}