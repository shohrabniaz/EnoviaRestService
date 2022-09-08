/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.utilities;

/**
 *
 * @author BJIT
 */
public interface IUnitConverter {
    Double unitConversion(String sourceUnit, Double valueToConvert);
    Double reverseUnitConversion(String sourceUnit, Double valueToConvert);
}
