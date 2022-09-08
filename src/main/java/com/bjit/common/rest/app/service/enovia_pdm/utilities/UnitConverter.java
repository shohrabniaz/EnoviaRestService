/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.utilities;

import com.digidemic.unitof.UnitOf;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */

@Component
public class UnitConverter implements IUnitConverter {
    
    @Override
    public Double unitConversion(String sourceUnit, Double valueToConvert) {
        if (sourceUnit.equalsIgnoreCase("m3")) {
            valueToConvert = new UnitOf.Volume().fromCubicMeters(valueToConvert).toCubicMeters();
        } else if (sourceUnit.equalsIgnoreCase("in3")) {
            valueToConvert = new UnitOf.Volume().fromCubicInches(valueToConvert).toCubicMeters();
        } else if (sourceUnit.equalsIgnoreCase("ft3")) {
            valueToConvert = new UnitOf.Volume().fromCubicFeet(valueToConvert).toCubicMeters();
        } else if (sourceUnit.equalsIgnoreCase("gal")) {
            valueToConvert = new UnitOf.Volume().fromGallonsUK(valueToConvert).toCubicMeters();
        } else if (sourceUnit.equalsIgnoreCase("l")) {
            valueToConvert = new UnitOf.Volume().fromLiters(valueToConvert).toCubicMeters();
        } else if (sourceUnit.equalsIgnoreCase("lb")) {
            valueToConvert = new UnitOf.Mass().fromPounds(valueToConvert).toKilograms();
        } else if (sourceUnit.equalsIgnoreCase("g")) {
            valueToConvert = new UnitOf.Mass().fromGrams(valueToConvert).toKilograms();
        } else if (sourceUnit.equalsIgnoreCase("kg")) {
            valueToConvert = new UnitOf.Mass().fromKilograms(valueToConvert).toKilograms();
        } else if (sourceUnit.equalsIgnoreCase("m")) {
            valueToConvert = new UnitOf.Length().fromMeters(valueToConvert).toMeters();
        } else if (sourceUnit.equalsIgnoreCase("mm")) {
            valueToConvert = new UnitOf.Length().fromMillimeters(valueToConvert).toMeters();
        } else if (sourceUnit.equalsIgnoreCase("ft")) {
            valueToConvert = new UnitOf.Length().fromFeet(valueToConvert).toMeters();
        } else if (sourceUnit.equalsIgnoreCase("in")) {
            valueToConvert = new UnitOf.Length().fromInches(valueToConvert).toMeters();
        } else if (sourceUnit.equalsIgnoreCase("mm2")) {
            valueToConvert = new UnitOf.Area().fromSquareMillimeters(valueToConvert).toSquareMeters();
        } else if (sourceUnit.equalsIgnoreCase("m2")) {
            valueToConvert = new UnitOf.Area().fromSquareMeters(valueToConvert).toSquareMeters();
        } else if (sourceUnit.equalsIgnoreCase("ft2")) {
            valueToConvert = new UnitOf.Area().fromSquareFeet(valueToConvert).toSquareMeters();
        } else if (sourceUnit.equalsIgnoreCase("in2")) {
            valueToConvert = new UnitOf.Area().fromSquareInches(valueToConvert).toSquareMeters();
        }

        return valueToConvert;
    }
    
    @Override
    public Double reverseUnitConversion(String sourceUnit, Double valueToConvert) {
        if (sourceUnit.equalsIgnoreCase("m3")) {
            valueToConvert = new UnitOf.Volume().fromCubicMeters(valueToConvert).toCubicMeters();
        } else if (sourceUnit.equalsIgnoreCase("in3")) {
            valueToConvert = new UnitOf.Volume().fromCubicMeters(valueToConvert).toCubicInches();
        } else if (sourceUnit.equalsIgnoreCase("ft3")) {
            valueToConvert = new UnitOf.Volume().fromCubicMeters(valueToConvert).toCubicFeet();
        } else if (sourceUnit.equalsIgnoreCase("gal")) {
            valueToConvert = new UnitOf.Volume().fromCubicMeters(valueToConvert).toGallonsUS();
        } else if (sourceUnit.equalsIgnoreCase("l")) {
            valueToConvert = new UnitOf.Volume().fromCubicMeters(valueToConvert).toLiters();
        } else if (sourceUnit.equalsIgnoreCase("lb")) {
            valueToConvert = new UnitOf.Mass().fromKilograms(valueToConvert).toPounds();
        } else if (sourceUnit.equalsIgnoreCase("g")) {
            valueToConvert = new UnitOf.Mass().fromKilograms(valueToConvert).toGrams();
        } else if (sourceUnit.equalsIgnoreCase("kg")) {
            valueToConvert = new UnitOf.Mass().fromKilograms(valueToConvert).toKilograms();
        } else if (sourceUnit.equalsIgnoreCase("m")) {
            valueToConvert = new UnitOf.Length().fromMeters(valueToConvert).toMeters();
        } else if (sourceUnit.equalsIgnoreCase("mm")) {
            valueToConvert = new UnitOf.Length().fromMeters(valueToConvert).toMillimeters();
        } else if (sourceUnit.equalsIgnoreCase("ft")) {
            valueToConvert = new UnitOf.Length().fromMeters(valueToConvert).toFeet();
        } else if (sourceUnit.equalsIgnoreCase("in")) {
            valueToConvert = new UnitOf.Length().fromMeters(valueToConvert).toInches();
        } else if (sourceUnit.equalsIgnoreCase("mm2")) {
            valueToConvert = new UnitOf.Area().fromSquareMeters(valueToConvert).toSquareMillimeters();
        } else if (sourceUnit.equalsIgnoreCase("m2")) {
            valueToConvert = new UnitOf.Area().fromSquareMeters(valueToConvert).toSquareMeters();
        } else if (sourceUnit.equalsIgnoreCase("ft2")) {
            valueToConvert = new UnitOf.Area().fromSquareMeters(valueToConvert).toSquareFeet();
        } else if (sourceUnit.equalsIgnoreCase("in2")) {
            valueToConvert = new UnitOf.Area().fromSquareMeters(valueToConvert).toSquareInches();
        }

        return valueToConvert;
    }
}
