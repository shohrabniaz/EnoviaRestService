/**
 *
 */
package com.bjit.mapper.mapproject.expand;

import java.util.List;
import java.util.Map;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;
import org.apache.log4j.Logger;

/**
 * @author TAREQ SEFATI
 *
 */
public class ExpandObjectTest {

    private static final Logger log = Logger.getLogger(ExpandObjectTest.class);

    /**
     * Returns type pattern for expanding object
     *
     * @return Pattern
     */
//    private Pattern buildTypePattern() {
//        ObjectTypesAndRelationsTest typesAndRelations = new ObjectTypesAndRelationsTest();
//        List<String> typeList = typesAndRelations.getTypeNames();
//        StringBuilder typePatternBuilder = new StringBuilder();
//        for (int i = 0; i < typeList.size(); i++) {
//            if (i > 0) {
//                typePatternBuilder.append(",");
//            }
//            String typeName = typeList.get(i);
//            if (typeName.contains(" ")) {
//                typePatternBuilder.append("\"");
//                typePatternBuilder.append(typeName);
//                typePatternBuilder.append("\"");
//            } else {
//                typePatternBuilder.append(typeName);
//            }
//
//        }
//        Pattern typePattern;
//        if (typePatternBuilder.length() > 0) {
//            typePattern = new Pattern(typePatternBuilder.toString());
//        } else {
//            typePattern = new Pattern("*");
//        }
//        log.debug("Type pattern: " + typePattern.getPattern());
//        return typePattern;
//    }

    private Pattern buildTypePattern(ObjectTypesAndRelationsTest typesAndRelations) {
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
//    private Pattern buildRelPattern() {
//        ObjectTypesAndRelationsTest typesAndRelations = new ObjectTypesAndRelationsTest();
//        List<String> relList = typesAndRelations.getRelationshipNames();
//        StringBuilder relPatternBuilder = new StringBuilder();
//        for (int i = 0; i < relList.size(); i++) {
//            if (i > 0) {
//                relPatternBuilder.append(",");
//            }
//            String relName = relList.get(i);
//            if (relName.contains(" ")) {
//                relPatternBuilder.append("\"");
//                relPatternBuilder.append(relName);
//                relPatternBuilder.append("\"");
//            } else {
//                relPatternBuilder.append(relName);
//            }
//        }
//        Pattern relPattern;
//        if (relPatternBuilder.length() > 0) {
//            relPattern = new Pattern(relPatternBuilder.toString());
//        } else {
//            relPattern = new Pattern("*");
//        }
//        log.debug("Relation pattern: " + relPattern.getPattern());
//        return relPattern;
//    }

    private Pattern buildRelPattern(ObjectTypesAndRelationsTest typesAndRelations) {
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
//    private SelectList buildBusStmnts() {
//        ObjectTypesAndRelationsTest typesAndRelations = new ObjectTypesAndRelationsTest();
//        Map<String, String> typeSelectablesWithOutputName = typesAndRelations.getTypeSelectablesWithOutputName();
//        SelectList selectBusStmts = new SelectList();
//        for (String typeSelectable : typeSelectablesWithOutputName.keySet()) {
//            selectBusStmts.add(typeSelectable);
//        }
//        log.debug("Select bus statements: " + selectBusStmts.toString());
//        return selectBusStmts;
//    }

    private SelectList buildBusStmnts(ObjectTypesAndRelationsTest typesAndRelations) {
        Map<String, String> typeSelectablesWithOutputName = typesAndRelations.getTypeSelectablesWithOutputName();
        SelectList selectBusStmts = new SelectList();
        typeSelectablesWithOutputName.keySet().forEach((typeSelectable) -> {
            selectBusStmts.add(typeSelectable);
        });
        log.debug("Select bus statements: " + selectBusStmts.toString());
        return selectBusStmts;
    }

//    private SelectList buildRelStmnsts() {
//        ObjectTypesAndRelationsTest typesAndRelations = new ObjectTypesAndRelationsTest();
//        Map<String, String> relationSelectablesWithOutputName = typesAndRelations.getRelationSelectablesWithOutputName();
//        SelectList selectRelStmts = new SelectList();
//        for (String relationSelectable : relationSelectablesWithOutputName.keySet()) {
//            selectRelStmts.add(relationSelectable);
//        }
//        log.debug("Select relation statements: " + selectRelStmts.toString());
//        return selectRelStmts;
//    }

    private SelectList buildRelStmnsts(ObjectTypesAndRelationsTest typesAndRelations) {
        Map<String, String> relationSelectablesWithOutputName = typesAndRelations.getRelationSelectablesWithOutputName();
        SelectList selectRelStmts = new SelectList();
        relationSelectablesWithOutputName.keySet().forEach((relationSelectable) -> {
            selectRelStmts.add(relationSelectable);
        });
        log.debug("Select relation statements: " + selectRelStmts.toString());
        return selectRelStmts;
    }

//    @SuppressWarnings("deprecation")
//    public ExpansionWithSelect expand(Context context, BusinessObject businessObject) {
//        ObjectTypesAndRelationsTest typesAndRelations = new ObjectTypesAndRelationsTest();
//
//        Pattern typePattern = buildTypePattern();
//        Pattern relPattern = buildRelPattern();
//        SelectList selectBusStmts = buildBusStmnts();
//        SelectList selectRelStmts = buildRelStmnsts();
//        short expandLevel = (short) typesAndRelations.getExpandLevel();
//        boolean expandUp = typesAndRelations.getExpandUp();
//        boolean expandDown = typesAndRelations.getExpandDown();
//        String busWhereExpression = typesAndRelations.getBusWhereClause();
//        String relWhereExpression = typesAndRelations.getRelWhereClause();
//        ExpansionWithSelect expandResult = null;
//
//        try {
//            expandResult = businessObject.expandSelect(context, relPattern.getPattern(), typePattern.getPattern(),
//                    selectBusStmts, selectRelStmts, expandUp, expandDown, // Get from .... true, false -> get to
//                    expandLevel, busWhereExpression, relWhereExpression, false);
//            log.debug("Expand the root object successfully.");
//        } catch (MatrixException e) {
//            log.error("Failed to expand the root object.");
//            log.error(e.getMessage());
//        }
//        return expandResult;
//    }

    @SuppressWarnings("deprecation")
    public ExpansionWithSelect expand(Context context, BusinessObject businessObject, ObjectTypesAndRelationsTest typesAndRelations) {
        Pattern typePattern = buildTypePattern(typesAndRelations);
        Pattern relPattern = buildRelPattern(typesAndRelations);
        SelectList selectBusStmts = buildBusStmnts(typesAndRelations);
        SelectList selectRelStmts = buildRelStmnsts(typesAndRelations);
        short expandLevel = (short) typesAndRelations.getExpandLevel();
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
}
