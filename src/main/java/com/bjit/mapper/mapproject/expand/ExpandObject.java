/**
 *
 */
package com.bjit.mapper.mapproject.expand;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.List;
import java.util.Map;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectItr;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;
import org.apache.log4j.Logger;

/**
 * @author TAREQ SEFATI
 *
 */
public class ExpandObject {

    private static final Logger log = Logger.getLogger(ExpandObject.class);

    /**
     * Returns type pattern for expanding object
     *
     * @return Pattern
     */
    private Pattern buildTypePattern() {
        ObjectTypesAndRelations typesAndRelations = new ObjectTypesAndRelations();
        List<String> typeList = typesAndRelations.getTypeNames();
        StringBuilder typePatternBuilder = new StringBuilder();
        for (int i = 0; i < typeList.size(); i++) {
            if (i > 0) {
                typePatternBuilder.append(",");
            }
            String typeName = typeList.get(i);
            if (typeName.contains(" ")) {
                typePatternBuilder.append("\"");
                typePatternBuilder.append(typeName);
                typePatternBuilder.append("\"");
            } else {
                typePatternBuilder.append(typeName);
            }

        }
        Pattern typePattern;
        if (typePatternBuilder.length() > 0) {
            typePattern = new Pattern(typePatternBuilder.toString());
        } else {
            typePattern = new Pattern("*");
        }
        log.debug("Type pattern: " + typePattern.getPattern());
        return typePattern;
    }

    //private Pattern buildTypePattern(String mapsAbsoluteDirectory, ObjectTypesAndRelations typesAndRelations) {
    private Pattern buildTypePattern(ObjectTypesAndRelations typesAndRelations) {
        //ObjectTypesAndRelations typesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory);
        List<String> typeList = typesAndRelations.getTypeNames();
        StringBuilder typePatternBuilder = new StringBuilder();
        for (int i = 0; i < typeList.size(); i++) {
            if (i > 0) {
                typePatternBuilder.append(",");
            }
            String typeName = typeList.get(i);
            if (typeName.contains(" ")) {
                typePatternBuilder.append("\"");
                typePatternBuilder.append(typeName);
                typePatternBuilder.append("\"");
            } else {
                typePatternBuilder.append(typeName);
            }

        }
        Pattern typePattern;
        if (typePatternBuilder.length() > 0) {
            typePattern = new Pattern(typePatternBuilder.toString());
        } else {
            typePattern = new Pattern("*");
        }
        log.debug("Type pattern: " + typePattern.getPattern());
        return typePattern;
    }

    /**
     * Builds relationship patterns for expanding object
     *
     * @return Pattern
     */
    private Pattern buildRelPattern() {
        ObjectTypesAndRelations typesAndRelations = new ObjectTypesAndRelations();
        List<String> relList = typesAndRelations.getRelationshipNames();
        StringBuilder relPatternBuilder = new StringBuilder();
        for (int i = 0; i < relList.size(); i++) {
            if (i > 0) {
                relPatternBuilder.append(",");
            }
            String relName = relList.get(i);
            if (relName.contains(" ")) {
                relPatternBuilder.append("\"");
                relPatternBuilder.append(relName);
                relPatternBuilder.append("\"");
            } else {
                relPatternBuilder.append(relName);
            }
        }
        Pattern relPattern;
        if (relPatternBuilder.length() > 0) {
            relPattern = new Pattern(relPatternBuilder.toString());
        } else {
            relPattern = new Pattern("*");
        }
        log.debug("Relation pattern: " + relPattern.getPattern());
        return relPattern;
    }

    //private Pattern buildRelPattern(String mapsAbsoluteDirectory, ObjectTypesAndRelations typesAndRelations) {
    private Pattern buildRelPattern(ObjectTypesAndRelations typesAndRelations) {
        //ObjectTypesAndRelations typesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory);
        List<String> relList = typesAndRelations.getRelationshipNames();
        StringBuilder relPatternBuilder = new StringBuilder();
        for (int i = 0; i < relList.size(); i++) {
            if (i > 0) {
                relPatternBuilder.append(",");
            }
            String relName = relList.get(i);
            if (relName.contains(" ")) {
                relPatternBuilder.append("\"");
                relPatternBuilder.append(relName);
                relPatternBuilder.append("\"");
            } else {
                relPatternBuilder.append(relName);
            }
        }
        Pattern relPattern;
        if (relPatternBuilder.length() > 0) {
            relPattern = new Pattern(relPatternBuilder.toString());
        } else {
            relPattern = new Pattern("*");
        }
        log.debug("Relation pattern: " + relPattern.getPattern());
        return relPattern;
    }

    /**
     * Creates the bus statement by all the properties and attributes declared
     * in the XML map. These lists generated at the beginning of the program
     * execution.
     *
     * @return
     */
    private SelectList buildBusStmnts() {
        ObjectTypesAndRelations typesAndRelations = new ObjectTypesAndRelations();
        Map<String, String> typeSelectablesWithOutputName = typesAndRelations.getTypeSelectablesWithOutputName();
        SelectList selectBusStmts = new SelectList();
        for (String typeSelectable : typeSelectablesWithOutputName.keySet()) {
            selectBusStmts.add(typeSelectable);
        }
        log.debug("Select bus statements: " + selectBusStmts.toString());
        return selectBusStmts;
    }

    //private SelectList buildBusStmnts(String mapsAbsoluteDirectory, ObjectTypesAndRelations typesAndRelations) {
    private SelectList buildBusStmnts(ObjectTypesAndRelations typesAndRelations) {
        //ObjectTypesAndRelations typesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory);
        Map<String, String> typeSelectablesWithOutputName = typesAndRelations.getTypeSelectablesWithOutputName();
        SelectList selectBusStmts = new SelectList();
        for (String typeSelectable : typeSelectablesWithOutputName.keySet()) {
            selectBusStmts.add(typeSelectable);
        }
        log.debug("Select bus statements: " + selectBusStmts.toString());
        return selectBusStmts;
    }

    private SelectList buildRelStmnsts() {
        ObjectTypesAndRelations typesAndRelations = new ObjectTypesAndRelations();
        Map<String, String> relationSelectablesWithOutputName = typesAndRelations.getRelationSelectablesWithOutputName();
        SelectList selectRelStmts = new SelectList();
        for (String relationSelectable : relationSelectablesWithOutputName.keySet()) {
            selectRelStmts.add(relationSelectable);
        }
        log.debug("Select relation statements: " + selectRelStmts.toString());
        return selectRelStmts;
    }

    //private SelectList buildRelStmnsts(String mapsAbsoluteDirectory, ObjectTypesAndRelations typesAndRelations) {
    private SelectList buildRelStmnsts(ObjectTypesAndRelations typesAndRelations) {
        //ObjectTypesAndRelations typesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory);
        Map<String, String> relationSelectablesWithOutputName = typesAndRelations.getRelationSelectablesWithOutputName();
        SelectList selectRelStmts = new SelectList();
        for (String relationSelectable : relationSelectablesWithOutputName.keySet()) {
            selectRelStmts.add(relationSelectable);
        }
        log.debug("Select relation statements: " + selectRelStmts.toString());
        return selectRelStmts;
    }

    @SuppressWarnings("deprecation")
    public ExpansionWithSelect expand(Context context, BusinessObject businessObject, String expandLevelStr) {
        ObjectTypesAndRelations typesAndRelations = new ObjectTypesAndRelations();

        Pattern typePattern = buildTypePattern();
        Pattern relPattern = buildRelPattern();
        SelectList selectBusStmts = buildBusStmnts();
        SelectList selectRelStmts = buildRelStmnsts();
        //short expandLevel = (short) typesAndRelations.getExpandLevel();
        short expandLevel = Short.valueOf(expandLevelStr);
        boolean expandUp = typesAndRelations.getExpandUp();
        boolean expandDown = typesAndRelations.getExpandDown();
        String busWhereExpression = typesAndRelations.getBusWhereClause();
        String relWhereExpression = typesAndRelations.getRelWhereClause();
        ExpansionWithSelect expandResult = null;

        try {
            expandResult = businessObject.expandSelect(context, relPattern.getPattern(), typePattern.getPattern(),
                    selectBusStmts, selectRelStmts, expandUp, expandDown, // Get from .... true, false -> get to
                    expandLevel, busWhereExpression, relWhereExpression, false);
            log.debug("Expand the root object successfully.");
        } catch (MatrixException e) {
            log.error("Failed to expand the root object.");
            log.error(e.getMessage());
        }
        return expandResult;
    }

    @SuppressWarnings("deprecation")
    //public ExpansionWithSelect expand(Context context, BusinessObject businessObject, String expandLevelStr, String mapsAbsoluteDirectory) {
    public ExpansionWithSelect expand(Context context, BusinessObject businessObject, String expandLevelStr, ObjectTypesAndRelations typesAndRelations) {
        //ObjectTypesAndRelations typesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory);

//        Pattern typePattern = buildTypePattern(mapsAbsoluteDirectory, typesAndRelations);
//        Pattern relPattern = buildRelPattern(mapsAbsoluteDirectory, typesAndRelations);
//        SelectList selectBusStmts = buildBusStmnts(mapsAbsoluteDirectory, typesAndRelations);
//        SelectList selectRelStmts = buildRelStmnsts(mapsAbsoluteDirectory, typesAndRelations);
        Pattern typePattern = buildTypePattern(typesAndRelations);
        Pattern relPattern = buildRelPattern(typesAndRelations);
        SelectList selectBusStmts = buildBusStmnts(typesAndRelations);
        SelectList selectRelStmts = buildRelStmnsts(typesAndRelations);
        //short expandLevel = (short) typesAndRelations.getExpandLevel();
        short expandLevel = Short.valueOf(expandLevelStr);
        boolean expandUp = typesAndRelations.getExpandUp();
        boolean expandDown = typesAndRelations.getExpandDown();
        String busWhereExpression = typesAndRelations.getBusWhereClause();
        String relWhereExpression = typesAndRelations.getRelWhereClause();
        ExpansionWithSelect expandResult = null;

        try {
            expandResult = businessObject.expandSelect(context, relPattern.getPattern(), typePattern.getPattern(),
                    selectBusStmts, selectRelStmts, expandUp, expandDown, // Get from .... true, false -> get to
                    expandLevel, busWhereExpression, relWhereExpression, false);
        } catch (Exception e) {
            log.error("Root object expansion failed.");
            log.error(e.getMessage());
        }
        return expandResult;
    }

    public BusinessObjectWithSelectList getChildObjectFromExpansion(Context context, BusinessObject bus, List<String> typeList, String rel, ObjectTypesAndRelations typesAndRelations) throws MatrixException {
        BusinessObjectWithSelectList childBusWithSelectList = new BusinessObjectWithSelectList();
        BusinessObjectWithSelect childBusWithSelect = null;
        SelectList selectBusStmts = buildBusStmnts(typesAndRelations);
        SelectList selectRelStmts = buildRelStmnsts(typesAndRelations);
        Pattern typePattern = new Pattern(String.join(",", typeList));
        Pattern relPattern = new Pattern(rel);
        String busWhereExpression = "";
        String relWhereExpression = "";
        ExpansionWithSelect expandResult;
        RelationshipWithSelectItr relItr = null;
        Short expandLevel = new Short("1");
        expandResult = bus.expandSelect(context, relPattern.getPattern(), typePattern.getPattern(), selectBusStmts, selectRelStmts,
                false, true, expandLevel, busWhereExpression, relWhereExpression, false);
        relItr = new RelationshipWithSelectItr(expandResult.getRelationships());
        while (!NullOrEmptyChecker.isNull(relItr) && relItr.next()) {
            RelationshipWithSelect relSelect = relItr.obj();
            childBusWithSelect = relSelect.getTarget();
            //  return childBusWithSelect;
            childBusWithSelectList.add(childBusWithSelect);

        }
        return childBusWithSelectList;
    }

    public BusinessObjectWithSelectList getDrawingObjectsFromExpansion(Context context, BusinessObject bus, String type, String rel, ObjectTypesAndRelations typesAndRelations) throws MatrixException {
        BusinessObjectWithSelectList childBusWithSelectList = new BusinessObjectWithSelectList();
        BusinessObjectWithSelect childBusWithSelect = null;
        SelectList selectBusStmts = buildBusStmnts(typesAndRelations);
        SelectList selectRelStmts = buildRelStmnsts(typesAndRelations);
        Pattern typePattern = new Pattern(type);
        Pattern relPattern = new Pattern(rel);
        String busWhereExpression = "";
        String relWhereExpression = "";
        ExpansionWithSelect expandResult;
        RelationshipWithSelectItr relItr = null;
        Short expandLevel = new Short("1");
        expandResult = bus.expandSelect(context, relPattern.getPattern(), typePattern.getPattern(), selectBusStmts, selectRelStmts,
                false, true, expandLevel, busWhereExpression, relWhereExpression, false);
        relItr = new RelationshipWithSelectItr(expandResult.getRelationships());
        while (!NullOrEmptyChecker.isNull(relItr) && relItr.next()) {
            RelationshipWithSelect relSelect = relItr.obj();
            childBusWithSelect = relSelect.getTarget();
            childBusWithSelectList.add(childBusWithSelect);
        }
        return childBusWithSelectList;
    }
}
