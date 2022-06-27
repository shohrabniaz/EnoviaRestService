package com.bjit.common.rest.pdm_enovia.bom.comparison.service.conversion;
/**
 * @author Ashikur Rahman / BJIT
 */
@FunctionalInterface
public interface Conversion<T> {
	public T conversion(T input) throws Exception;
}