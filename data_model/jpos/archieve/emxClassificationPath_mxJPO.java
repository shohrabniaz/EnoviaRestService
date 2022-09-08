/*
 *  emxLibraryCentralClassificationPathBase.java
 *
 * Copyright (c) 1992-2015 Dassault Systemes.
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * MatrixOne, Inc.  Copyright notice is precautionary only and does
 * not evidence any actual or intended publication of such program.
 *
 *  static const RCSID [] = "$Id: emxLibraryCentralClassificationPathBase.java.rca 1.18.2.1 Thu Dec 18 04:36:55 2008 ds-arsingh Experimental $";
 */
// TODO: think aobut the case where the Part Family does not have an mxsysInterface associated
import matrix.db.*;
import matrix.util.*;
import java.util.*;
import java.util.List;

import com.matrixone.apps.domain.*;
import com.matrixone.apps.common.*;
import com.matrixone.apps.domain.util.*;
import com.matrixone.apps.common.util.*;
import com.matrixone.apps.library.LibraryCentralConstants;
import com.matrixone.apps.library.Classification;
import java.util.regex.Matcher;

/**
 * The <code>emxLibraryCentralClassificationPathBase</code> class. This Class
 * manages the Interface paths and the Classification Paths and also provides
 * API for Dispaly of the Classification paths.
 *
 * @exclude
 */
public class emxClassificationPath_mxJPO
        implements LibraryCentralConstants {

    private static String FAIL_STATUS = "FAIL";
    private static String SUCCESS_STATUS = "SUCCESS";
    private static String OK_STATUS = "OK";
    private static String DUPLICATE_CLASSIFICATION_PATH_FOUND = "Duplicate Classification Path Found. paths:";
    private static String INVALID_CLASSIFICATION_PATH_FOUND = "Invalid Classification Path Found. paths:";

    private int _ifPathCacheHits = 0;
    private int _ifPathCacheMisses = 0;
    private int _clsPathCacheHits = 0;
    private int _clsPathCacheMisses = 0;
    private final String SUCCESS = "Success";
    private final String ERROR = "Error";
    private final String CLASSIFICATION_PATH_NOT_EXITS = "Classification path not found";

    // key=interface name; value=InterfacePath
    public HashMap _ifName2ifPath = new HashMap();

    // key=interface name; value=ClassificationPath
    public HashMap _ifName2clsPath = new HashMap();

    // key=strClassId; value=interface name
    public HashMap _clsId2ifName = new HashMap();

    /**
     * Creates the emxLibraryCentralClassificationPathBase Object.
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds no arguments
     * @throws Exception if the operation fails
     */
    public emxClassificationPath_mxJPO(Context context,
            String[] args) throws Exception {

    }

    /* Some of the following nested classes may appear to be
     * superfluous, but they make the main class code easier to
     * understand as compared to if it operated on more generic
     * classes like List.
     */
    /**
     * Each String in a List of this type is the name of a classification
     * interface. The root is first in the least, the leaf last. The path is NOT
     * meant to include the CLASSIFICATION_TAXONOMIES name, it starts below
     * that.
     */
    public class InterfacePath extends StringList {
    }

    /**
     * Similar to InterfacePath, but each entry in the List is a
     * ClassificationInfo
     */
    public class ClassificationPath extends Vector {
    }

    /**
     * Stores some basic info about a Classification. Not a full DomainObject.
     */
    public class ClassificationInfo {

        String name = null;
        String id = null;

        /**
         * Creates a ClassificationInfo Object given the Object Id and Object
         * Name
         *
         * @param id ObjectId
         * @param Name the Name of the Object
         */
        public ClassificationInfo(String id, String name) {
            setId(id);
            setName(name);
        }

        /**
         * Gets the ObjectId
         *
         * @return String the ObjectId
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the ObjectId
         *
         * @param id the ObjectId
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Gets the Object Name
         *
         * @return String the Object Name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the Object name
         *
         * @param id the object Name
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * This Comparator is used for sorting Vectors of ClassificationPaths
     */
    public Comparator _comparePaths = new Comparator() {
        // a and b are ClassificationPaths
        // Note that we are not sorting inside the
        // ClassificationPaths themselves, we are
        // comparing the paths to order them within the
        // list.

        /**
         * Compares two Objects
         *
         * @param _a object
         * @param _b object
         * @return int the Compare result
         */
        public int compare(Object _a, Object _b) {
            ClassificationPath a = (ClassificationPath) _a;
            ClassificationPath b = (ClassificationPath) _b;
            Iterator iterA = a.iterator();
            Iterator iterB = b.iterator();
            while (iterA.hasNext() && iterB.hasNext()) {
                ClassificationInfo elementA = (ClassificationInfo) iterA.next();
                ClassificationInfo elementB = (ClassificationInfo) iterB.next();
                String aName = elementA.getName();
                String bName = elementB.getName();
                int cmp = aName.compareTo(bName);
                if (cmp != 0) {
                    return cmp;
                }
            }
            return b.size() - a.size();
        }
    };

    /**
     * Returns the InterfacePath representing all nodes from
     * CLASSIFICATION_TAXONOMIES down to the given interfaceName
     *
     * @param context the eMatrix <code>Context</code> object
     * @param interfaceName
     * @return InterfacePath
     * @throws FrameworkException if the operation fails
     */
    public InterfacePath getInterfacePath(Context context, String interfaceName)
            throws FrameworkException {
        InterfacePath result = new InterfacePath();

        // Build a path from CLASSIFICATION_TAXONOMIES (exclusive) to interfaceName
        // BUT keep a cache (HashMap) of interfaces for which the path has
        // already been looked up, to avoid redundant queries. This is important,
        // because within one pageful of data fed to a table,
        // the same paths tend to show up again and again.  The cache needs to
        // be a class instance member variable, to be reused across calls to
        // this method.
        // We could actually cache the parent interface of each individual
        // interface, and build up paths from previously stored elements,
        // but if you need to get even one node in the path, you might
        // as well ask for the whole path, as it's not that much more
        // expensive; interfaces are cached in the core, so require no trips to
        // Oracle. If we were to walk the Subclass relationship instead,
        // then that would be a different story, and caching at the path
        // element level would be a must, as relathioships DO require a trip
        // to the SQL DB every time.
        // First off, check for cached result
        InterfacePath cachedResult = (InterfacePath) _ifName2ifPath.get(interfaceName);
        if (cachedResult != null) {
            _ifPathCacheHits++;
            return cachedResult;
        }

        // Failing, that, query the core
        _ifPathCacheMisses++;

        //    Here's how the query works:
        //    % mql print interface {Wing Screws.1115318266179} select
        //      allparents.name allparents.kindof\[Classification Taxonomies] dump;
        //    Screws.1115318224024,Fasteners.1115318151854,Mechanical
        //    Parts.1115317777426,Classification Taxonomies,Physical Dimensions,
        //    Classification Attribute Groups,Thread,Wing,TRUE,TRUE,TRUE,TRUE,FALSE,
        //    FALSE,FALSE,FALSE
        //
        //    The parents come out in depth-first order, which we will exploit;
        //    if that were not the case, then the query would be more complex,
        //    something like
        //    "print interface Xyz select allparents.derivative"
        //    and would require some tedious parsing.
        //
        //      So, then:
        //    - split the list in half (maybe it could just have been two queries)
        //    - walk both halves in parallel
        //    - skip the FALSEs, keep the TRUEs, and you've built the path to the root
        //    - to build a path downwards from root, walk sublists backward instead
        String strParentsCmd = "print interface $1 select $2 $3 dump $4";
        String strParentsResult = MqlUtil.mqlCommand(context, strParentsCmd, true,
                interfaceName,
                "allparents.name",
                "allparents.kindof[" + DomainConstants.INTERFACE_CLASSIFICATION_TAXONOMIES + "]",
                ","
        ).trim();
        System.out.println("strParentsResult: " + strParentsResult);
        StringList lstParentsAndBooleans = FrameworkUtil.split(strParentsResult, ",");
        int size = lstParentsAndBooleans.size();
        List lstParents = lstParentsAndBooleans.subList(0, size / 2);
        List lstBooleans = lstParentsAndBooleans.subList(size / 2, size);
        // Walk backwards (i--) so ancestors come out earlier than descendants
        // in path
        for (int i = size / 2 - 1; i >= 0; i--) {
            if (lstBooleans.get(i).toString().equalsIgnoreCase("TRUE")) {
                result.add(lstParents.get(i).toString());
            }
        }

        if (!result.isEmpty() && result.get(0).toString().equals(INTERFACE_CLASSIFICATION_TAXONOMIES)) {
            // remove TAXONOMIES root interface
            result.remove(0);
        }

        // add interfaceName as the tail; it is not returned as part of
        // the mql query
        result.add(interfaceName);
        System.out.println("getInterfacePath result: " + result.toString());
        // cache the result
        _ifName2ifPath.put(interfaceName, result);

        //System.out.println("getInterfacePath(" + interfaceName + ") COMPUTED: " + FrameworkUtil.join(result, "->"));
        return result;
    }

    /**
     * Converts an InterfacePath to a ClassificationPath
     *
     * @param context the eMatrix <code>Context</code> object
     * @param interfacePath
     * @return ClassificationPath
     * @throws FrameworkException if the operation fails
     */
    public ClassificationPath convertInterfacePathToClassPath(Context context, InterfacePath interfacePath)
            throws FrameworkException {
        ClassificationPath result = new ClassificationPath();

        String tailIf = (String) interfacePath.get(interfacePath.size() - 1);

        // First off, check for cached result
        ClassificationPath cachedResult
                = (ClassificationPath) _ifName2clsPath.get(tailIf);
        if (cachedResult != null) {
            _clsPathCacheHits++;
            return cachedResult;
        }
        _clsPathCacheMisses++;

        // Again, keep a cache (another HashMap) of interface names for which
        // the corresponding class name has already been looked up.
        Map ifName2clsInfo = new HashMap();
        String getClsCmd = "temp query bus '$1' $2 $3 where \"$4\" select $5 $6 $7 dump $8 recordsep $9";
        StringBuffer sbtypes = new StringBuffer(TYPE_LIBRARIES).append(",").append(TYPE_CLASSIFICATION);
        emxLibraryCentralUtil_mxJPO.
                commaPipeQueryToMapSkipTNR(context, getClsCmd, true, ifName2clsInfo,
                        sbtypes.toString(),
                        "*",
                        "*",
                        "attribute[" + ATTRIBUTE_MXSYS_INTERFACE + "] matchlist '" + FrameworkUtil.join(interfacePath, ",") + "' ','",
                        "attribute[" + ATTRIBUTE_MXSYS_INTERFACE + "]",
                        "id",
		                "attribute[Title]",
                        ",",
                        "|"
                );
        System.out.println("Class path map for interface path :" + ifName2clsInfo.toString());
        Iterator ifNameIter = interfacePath.iterator();
        while (ifNameIter.hasNext()) {
            String strIfName = (String) ifNameIter.next();
            System.out.println("strIfName : " + strIfName);
            StringList lstClsFields = (StringList) ifName2clsInfo.get(strIfName);
            System.out.println("lstClsFields : " + lstClsFields);
            String strId = (String) lstClsFields.get(0);
            System.out.println("strId : " + strId);
            String strName = (String) lstClsFields.get(1);
            System.out.println("strName : " + strName);
            ClassificationInfo cls = new ClassificationInfo(strId, strName);
            result.add(cls);
        }

        for (int i = 0; i < result.size(); i++) {
            ClassificationInfo cls = (ClassificationInfo) result.get(i);
            String clsId = cls.getId();
            String ifName = (String) interfacePath.get(i);
            _clsId2ifName.put(clsId, ifName);
        }

        _ifName2clsPath.put(tailIf, result);
        System.out.println("convertInterfacePathToClassPath result : " + result.toString());
        return result;
    }

    /**
     * Returns Vector of ClassificationPath's. Each ClassificationPath in the
     * Vector represents one classification path for the given endItemId. Each
     * ClassificationInfo in each ClassificatinoPath in the Vector represents an
     * element in the path (a Classification or Libaries) and stores name and id
     * of the classification or lib object. The entire Vector is sorted by the
     * Comparator above.
     *
     * @param context the eMatrix <code>Context</code> object
     * @param endItemId the objectId
     * @return a Vector of ClassificationPath's
     * @throws Exception
     */
    public Vector getEndItemClassificationPaths(Context context, String endItemId)
            throws Exception {

        // get obj's classification interfaces
        StringList lstInterfaces = emxLibraryCentralUtil_mxJPO.
                getClassificationInterfaces(context, endItemId);
        System.out.println("Classification Interfaces: " + lstInterfaces);
        // For each interface, get a path up to CLASSIFICATION_TAXONOMIES (exclusive).
        // Each path is a StringList of interface names, starting with the library's
        // interface, and ending with the interface containing the end item
        Vector vecClsInfoPaths = new Vector(lstInterfaces.size());
        Iterator ifIter = lstInterfaces.iterator();
        while (ifIter.hasNext()) {
            String strInterfaceName = (String) ifIter.next();
            System.out.println("strInterfaceName===" + strInterfaceName);
            InterfacePath interfacePath = getInterfacePath(context, strInterfaceName);
            System.out.println("interfacePath===" + interfacePath);
            ClassificationPath clsInfoPath = convertInterfacePathToClassPath(
                    context, interfacePath);
            vecClsInfoPaths.add(clsInfoPath);
        }

        // Sort the result
        Collections.sort(vecClsInfoPaths, _comparePaths);

        return vecClsInfoPaths;
    }

    /**
     * This method differs from getEndItemClassificationPaths in that the former
     * applies to end items, and this one applies to classifications.
     * Classification objects may have only one path, whereas end items may have
     * any number of them.
     *
     * @param context the eMatrix <code>Context</code> object
     * @param clsObjId the Classification object Id
     * @return ClassificationPath
     */
    public ClassificationPath getClassificationPath(Context context, String strClsObjId)
            throws FrameworkException {
        Classification cls = (Classification) DomainObject.newInstance(context, strClsObjId, LIBRARY);
        String strInterfaceName = cls.getInterfaceName(context);
        System.out.println("strInterfaceName: " + strInterfaceName);
        if (strInterfaceName.trim().isEmpty()) {
            return new ClassificationPath();
        }
        InterfacePath interfacePath = getInterfacePath(context, strInterfaceName);
        ClassificationPath clsInfoPath = convertInterfacePathToClassPath(context, interfacePath);
        return clsInfoPath;
    }

    /**
     * **********************************************************************
     */
    /**
     * Members above this point are clean business logic *
     */
    /**
     * Members below are HTML generating for use in programHtmlOutput *
     */
    /**
     * (presentation) *
     */
    /**
     * **********************************************************************
     */
    // separator between path elements. prefers breaking before arrow, rather
    // than after
    public String _elemSeparatorHTML = "<img style=\"padding-left:2px; padding-right:2px;\" src=\"../common/images/iconTreeToArrow.gif\"/>";

    // separator between one path and the next
    public String _pathSeparatorHTML = "<br/>";

    //  separator between relevant paths and others
    public String _relevanceSeparatorHTML = "<br/>";

    // some HTML rendering building blocks
    /**
     * Renders the path element
     *
     * @param strName the Object name
     * @param strObjectId the objectId
     * @param reportFormat the report format
     * @return String the rendered element
     */
    public String renderPathElem(Context context, String strName, String strObjectId, String reportFormat) {
        if ("HTML".equals(reportFormat) || "CSV".equals(reportFormat)) {
            return strName;
        } else {
            //Modified for Bug Id -363882 replace # by javascript:void(0) which was causing error in IE
            return "<a href=\"javascript:void(0)\" " + "onClick=\"javascript:showModalDialog("
                    + "'../common/emxTree.jsp?objectId=" + XSSUtil.encodeForURL(context, strObjectId) + "',"
                    + "'860','520');\"" + ">" + XSSUtil.encodeForXML(context, strName) + "</a>";
        }
    }

    /**
     * Renders an entire path by rendering its constituent elements separated by
     * the separator HTML
     *
     * @param pathElems the list of elements
     * @param reportFormat the report format
     * @return the rendered path
     */
    public String renderPath(Context context, ClassificationPath pathElems, String reportFormat) {
        StringList elemHtmlList = new StringList();
        Iterator i = pathElems.iterator();
        while (i.hasNext()) {
            ClassificationInfo elem = (ClassificationInfo) i.next();
            elemHtmlList.add(renderPathElem(context, elem.getName(), elem.getId(), reportFormat));
        }
        String elemSeperator = _elemSeparatorHTML;
        if ("CSV".equalsIgnoreCase(reportFormat)) {
            elemSeperator = "->";
        }
        return FrameworkUtil.join(elemHtmlList, elemSeperator);
    }

    /**
     * Given a vector of ClassificationPath's, render each one with HTML using
     * all appropriate separators
     *
     * @param vecPaths the paths
     * @param forUseInForm not used; was previously needed to allow different
     * rendering in UIForm vs. UITable, and could still be used for that purpose
     * in a customization
     * @param reportFormat the report format
     * @return String the rendered paths
     */
    public String renderPaths(Context context, Vector vecPaths, boolean forUseInForm, String reportFormat) {
        StringList lstPathHtml = new StringList();
        Iterator pathIter = vecPaths.iterator();
        while (pathIter.hasNext()) {
            ClassificationPath lstPath = (ClassificationPath) pathIter.next();
            System.out.println("lstPath: " + lstPath.toString());
            lstPathHtml.add(renderPath(context, lstPath, reportFormat));
        }
        String pathSeperator = _pathSeparatorHTML;
        if ("CSV".equalsIgnoreCase(reportFormat)) {
            pathSeperator = "\n";
        }
        System.out.println("lstPathHtml======" + lstPathHtml);
        String strPathHtml = FrameworkUtil.join(lstPathHtml, pathSeperator);
        System.out.println("strPathHtml: " + strPathHtml);
        return strPathHtml;
    }

    // The methods below are the JPO entry points used by UI components
    /**
     * This method operates on a single object, whose oid is given in args. If
     * that object is a Classification, this method calls getClassificationPath
     * for that object, and then renders the resulting path as HTML. If the
     * object is not a Classification, then it is treated as an end-item; in
     * that case, this method calls getEndItemClassificationPaths and renders
     * the resulting paths as HTML.
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds the following arguments 0 - objectId the objectId 1 -
     * pfMode printer friendly mode\ 2 -
     * @return String the Classification path HTML
     * @throws FrameworkException
     */
    public String getClassificationPathsHTML(Context context, String[] args)
            throws FrameworkException {
        try {
            String reportFormat = "json";
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            String objectId = (String) programMap.get("objectId");

            DomainObject domainObject = new DomainObject(objectId);
            if (domainObject.isKindOf(context, TYPE_CLASSIFICATION)) {
                System.out.println("Type classification: ");
                ClassificationPath path = getClassificationPath(context, objectId);
                //System.out.println("path=====" + path.toString());
                String htmlOutput = renderPath(context, path, reportFormat);
                System.out.println("htmlOutput: " + htmlOutput);
                return parseHTMLClassificationPath(htmlOutput);
            } else {
                System.out.println("not Type classification: ");
                Vector vecPaths = getEndItemClassificationPaths(context, objectId);
                String htmlOutput = renderPaths(context, vecPaths, false, reportFormat);
                System.out.println("htmlOutput: " + htmlOutput);
                return parseHTMLClassificationPath(htmlOutput);
            }
        } catch (Exception e) {
            throw new FrameworkException(e);
        }
    }

    public void printVector(Vector vector) {
        Enumeration e = vector.elements();

        // let us print all the elements available in enumeration
        System.out.println("Numbers in the enumeration are :- ");
        while (e.hasMoreElements()) {
            System.out.println("Number = " + e.nextElement().toString());
        }
    }

    public String parseHTMLClassificationPath(String classificationPathHtml) {
        String output = "";
        String[] classificationPathListAsHTML = classificationPathHtml.split("<br/>");
        for (int i = 0; i < classificationPathListAsHTML.length; i++) {
            boolean isFirstElement = true;
            String inputHtml = classificationPathListAsHTML[i];
            System.out.println("Input html: " + inputHtml);
            if (i != 0) {
                output += ";";
            }
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(";\">(.+?)</a>");
            Matcher m = p.matcher(inputHtml);
            while (m.find()) {
                if (!isFirstElement) {
                    output += "|";
                } else {
                    isFirstElement = false;
                }
                System.out.println(m.group(1));
                output += m.group(1);
                System.out.println("output: " + output);

            }
        }
        System.out.println("finam output: " + output);
        return output;
    }

    /**
     * Update Objects classification path
     *
     * @param context
     * @param args
     * @return String
     */
    public static String updateClassificationPath(Context context, String[] args) {
        try {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            String parentId = (String) programMap.get("objectId");
            String className = (String) programMap.get("classNames");
            HashMap resultMap = updateClassificationPath(context, parentId, className);
            if (resultMap.get("status").toString().equals(OK_STATUS)) {
                return "Success:: Classification Path changed successfully";
            } else {
                String failedMessage = resultMap.get("message") != null ? resultMap.get("message").toString() : "";
                return "Error:: Cannot Update classification path, cause: " + failedMessage;
            }
        } catch (Exception e) {
            return "Error:: Cannot Update classification path, cause: " + e.getMessage();
        }
    }

    /**
     * Getting Proper General Class Object Id From Classification Path list
     *
     * @param context
     * @param args
     * @return String generalClassObjectId
     */
    public static String getGeneralClassObjectIdFromClassificationPath(Context context, String classificationPath) throws Exception {
        String generalClassObjectId = null;
        MQLCommand objMQL = new MQLCommand();
        objMQL.open(context);

        String generalClassName = classificationPath.substring(classificationPath.lastIndexOf("|") + 1, classificationPath.length());
        String sMQLStatement = "temp query bus 'General Class' * * where \"name matchlist '" + generalClassName + "' ','\" select id dump :;";
        String result = MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
        if (!result.isEmpty() && result.split("\n").length >= 1) {
            String[] generalClasses = result.split("\n");
            for (String generalClassLine : generalClasses) {
                // General Class:Headbox GC3:D8B9017C000052185FF6965400001013:42848.60610.62350.14399
                String[] generalClassLineSplitted = generalClassLine.split(":");
                if (generalClassLineSplitted.length >= 4) {
                    String objectType = generalClassLineSplitted[0];
                    String objectName = generalClassLineSplitted[1];
                    String parentPhysicalId = generalClassLineSplitted[2];
                    String fullClassificationPath = objectName + ":";

                    while (!objectType.equals("General Library")) {
                        sMQLStatement = "print bus " + parentPhysicalId + " select type name dump :;";
                        result = MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
                        // General Class|Headbox GC2
                        String[] parentObjectSplitted = result.split(":");
                        objectName = parentObjectSplitted[1];
                        objectType = parentObjectSplitted[0];
                        if (objectType.equals("General Class")) {
                            fullClassificationPath += objectName + ":";
                        } else {
                            fullClassificationPath += objectName;
                        }

                        sMQLStatement = "expand bus " + parentPhysicalId + " to relationship \"Subclass\" recurse to 1 select relationship dump :;";
                        result = MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
                        if (!result.isEmpty()) {
                            // 1|Subclass|from|General Class|Headbox GC1|56A1839400000A1C5D9442D0000000D1|
                            // 1|Subclass|from|General Library|Headbox GL|-|
                            String[] expandResultSplitted = result.split(":");
                            objectType = expandResultSplitted[3];
                            objectName = expandResultSplitted[4];
                            if (objectType.equals("General Class")) {
                                fullClassificationPath += objectName + ":";
                                parentPhysicalId = expandResultSplitted[5];
                            } else {
                                fullClassificationPath += objectName;
                            }
                        }
                    }
                    String[] reversed = reverse(fullClassificationPath.split(":"));
                    String originalClassificationPath = "";
                    for (String string : reversed) {
                        originalClassificationPath += string + "|";
                    }
                    originalClassificationPath = originalClassificationPath.substring(0, originalClassificationPath.length() - 1);
                    if (originalClassificationPath.equals(classificationPath)) {
                        generalClassObjectId = generalClassLineSplitted[3];
                        break;
                    }
                }

            }

        }

        return generalClassObjectId;
    }

    /**
     * This function get disconnects old classification paths with object
     *
     * @param oldClassificationPathList
     * @param newClassificationPathList
     * @param sMQLStatement
     * @param context
     * @param objMQL
     * @return matrix.util.List<String> finalClassificationPathList
     * @throws FrameworkException, Exception
     */
    private static matrix.util.List<String> disconnect(String[] oldClassificationPathList, matrix.util.List<String> newClassificationPathList, String sMQLStatement, Context context, MQLCommand objMQL) throws FrameworkException, Exception {
        matrix.util.List remainingClassificationPaths = newClassificationPathList;
        try {
            for (String oldClassificationPath : oldClassificationPathList) {
                if (!oldClassificationPath.isEmpty()) {
                    // 1:Classified Item:from:General Class:Headbox GC3:D8B9017C000052185FF6965400001013:42848.60610.1922.31250
                    String[] splittedLine = oldClassificationPath.split(":");
                    String objectType = splittedLine[3];
                    String objectName = splittedLine[4];
                    String parentPhysicalId = splittedLine[5];
                    String connectionId = splittedLine[6];
                    String fullClassificationPath = objectName + ":";

                    while (!objectType.equals("General Library")) {
                        sMQLStatement = "print bus " + parentPhysicalId + " select type name dump :;";
                        String result = MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
                        // General Class|Headbox GC2
                        String[] parentObjectSplitted = result.split(":");
                        objectName = parentObjectSplitted[1];
                        objectType = parentObjectSplitted[0];
                        if (objectType.equals("General Class")) {
                            fullClassificationPath += objectName + ":";
                        } else {
                            fullClassificationPath += objectName;
                        }

                        sMQLStatement = "expand bus " + parentPhysicalId + " to relationship \"Subclass\" recurse to 1 select relationship dump :;";
                        result = MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
                        if (!result.isEmpty()) {
                            // 1|Subclass|from|General Class|Headbox GC1|56A1839400000A1C5D9442D0000000D1|
                            // 1|Subclass|from|General Library|Headbox GL|-|
                            String[] expandResultSplitted = result.split(":");
                            objectType = expandResultSplitted[3];
                            objectName = expandResultSplitted[4];
                            if (objectType.equals("General Class")) {
                                fullClassificationPath += objectName + ":";
                                parentPhysicalId = expandResultSplitted[5];
                            } else {
                                fullClassificationPath += objectName;
                            }
                        }
                    }
                    String[] reversed = reverse(fullClassificationPath.split(":"));
                    String originalClassificationPath = "";
                    for (String string : reversed) {
                        originalClassificationPath += string + "|";
                    }
                    originalClassificationPath = originalClassificationPath.substring(0, originalClassificationPath.length() - 1);
                    if (remainingClassificationPaths.contains(originalClassificationPath)) {
                        System.out.println("Ignoring: " + originalClassificationPath);
                        remainingClassificationPaths.remove(originalClassificationPath);
                    } else {
                        System.out.println("Disconnecting: " + originalClassificationPath);
                        sMQLStatement = "disconnect connection " + connectionId;
                        MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
                    }
                }
            }
            objMQL.close(context);
        } catch (Exception e) {
            e.printStackTrace();
            return remainingClassificationPaths;
        }
        return remainingClassificationPaths;
    }

    /**
     * This function get connects classification paths with object
     *
     * @param sMQLStatement
     * @param newClassificationPathList
     * @param context
     * @param objMQL
     * @param objectId
     * @return HashMap resultMap
     * @throws FrameworkException, Exception
     */
    private static HashMap connect(String sMQLStatement, matrix.util.List<String> newClassificationPathList, Context context, MQLCommand objMQL, String objectId) throws FrameworkException, Exception {
        HashMap resultMap = new HashMap();
        List<String> addedClassPaths = new ArrayList<String>();
        List<String> failedClassPaths = new ArrayList<String>();
        System.out.println("From connect");
        try {
            // temp query bus 'General Class' * * where "name matchlist 'DEF,Headbox GC,XYZ' ','" select id dump;
            // sMQLStatement = "temp query bus * * * where \"name matchlist '";
            sMQLStatement = "temp query bus 'General Class' * * where \"name matchlist '"; // allow only 'General Class' for classification path
            for (int j = 0; j < newClassificationPathList.size(); j++) {
                if (j == newClassificationPathList.size() - 1) {
                    sMQLStatement += newClassificationPathList.get(j);
                    break;
                }
                sMQLStatement += newClassificationPathList.get(j) + ",";
            }
            sMQLStatement += "' ','\" select id dump;";
            System.out.println(sMQLStatement);
            String allId = MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
            System.out.println("RETURNED VALUE:::" + allId);

            if (allId.trim().length() == 0) {
                failedClassPaths.addAll(newClassificationPathList);
                resultMap.put("status", FAIL_STATUS);
                resultMap.put("messages", failedClassPaths);
                resultMap.put("data", addedClassPaths);
                return resultMap;
            }

            String[] splited = allId.split("\n");

            for (int i = 0; i < splited.length; i++) {
                String addedClassPathName = splited[i].split(",")[1];

                System.out.println("s" + splited[i].split(",")[3]);
                String relId = splited[i].split(",")[3];
                System.out.println(relId);

                System.out.println("To be connected Object Id ::" + relId + "::");
                sMQLStatement = "connect bus " + objectId + " rel \"Classified Item\" from " + relId + ";";
                String non = MqlUtil.mqlCommand(context, objMQL, sMQLStatement);

                addedClassPaths.add(addedClassPathName);
                newClassificationPathList.remove(addedClassPathName); // remove from requested list
            }

            for (int i = 0; i < newClassificationPathList.size(); i++) {
                failedClassPaths.add(newClassificationPathList.get(i));
            }
        } catch (Exception e) {
            System.out.println("Class path, Exception: " + e.getMessage());
            throw new Exception("Exception occured during connect, cause: " + e.getMessage());
        }
        if (failedClassPaths.size() > 0) {
            resultMap.put("status", FAIL_STATUS);
            resultMap.put("messages", failedClassPaths);
            resultMap.put("data", addedClassPaths);
        } else {
            resultMap.put("status", OK_STATUS);
            resultMap.put("data", addedClassPaths);
        }
        return resultMap;
    }

     /**
     * This function get Classification Path from user
     *
     * @param context
     * @param args
     * @return Map<String, List<String>> allUserEmail
     * @throws java.lang.Exception
     */
    public List<String> getClassificationPath(Context context, String[] args) throws Exception {

   
        Map<String, String> object = new HashMap<String, String>();
        List<String> response = new ArrayList<>();
   
        List<String> objectIdList = JPO.unpackArgs(args);
        for (String objectId : objectIdList) {
            try {
                String classificationPath = getClassificationPathsTitle(context, objectId);
          
                    if (classificationPath.isEmpty() || classificationPath == null || classificationPath=="") {
                        object = buildResponseObject(objectId, null, ERROR, CLASSIFICATION_PATH_NOT_EXITS);
                    } else {
                        object = buildResponseObject(objectId, classificationPath, SUCCESS, null);
                    }
                
            } catch (MatrixException ex) {

                object = buildResponseObject(objectId, null, ERROR, ex.toString());
            }
            response.add(object.toString());
        }

        return response;
    }

    /**
     * This function build hashMap response object
     *
     * @param user
     * @param email
     * @param status
     * @param message
     * @return HashMap<String,String> object
     */
    public Map<String, String> buildResponseObject(String objectId, String classificationPath, String status, String message) {
        Map<String, String> object = new HashMap<String, String>();
        object.put("objectId", objectId);
        object.put("classificationPath", classificationPath);
        object.put("status", status);
        object.put("message", message);
        return object;

    }

    public String getClassificationPaths(Context context, String objectId)
            throws FrameworkException {
        try {
            String reportFormat = "json";

            DomainObject domainObject = new DomainObject(objectId);
            if (domainObject.isKindOf(context, TYPE_CLASSIFICATION)) {
                System.out.println("Type classification: ");
                ClassificationPath path = getClassificationPath(context, objectId);
                //System.out.println("path=====" + path.toString());
                String htmlOutput = renderPath(context, path, reportFormat);
                System.out.println("htmlOutput: " + htmlOutput);
                return parseHTMLClassificationPath(htmlOutput);
            } else {
                System.out.println("not Type classification: ");
                Vector vecPaths = getEndItemClassificationPaths(context, objectId);
                String htmlOutput = renderPaths(context, vecPaths, false, reportFormat);
                System.out.println("htmlOutput: " + htmlOutput);
                return parseHTMLClassificationPath(htmlOutput);
            }
        } catch (Exception e) {
            throw new FrameworkException(e);
        }
    }

    /**
     * This function update Classification path by object id if duplicate
     * classification path or invalid classification path found then message
     * will be added
     *
     * @param context
     * @param args
     * @return Map<String, Object> resultMap
     */
    public static HashMap updateClassificationPath(Context context, String objectId, String classificationPaths) throws Exception {
        HashMap resultMap = new HashMap();
        resultMap.put("objectId", objectId);
        String status = OK_STATUS;
        List<String> validClassificationPaths = new ArrayList<String>();
        List<String> invalidClassificationPaths = new ArrayList<String>();
        List<String> duplicateClassificationPaths = new ArrayList<String>();

        List<Map<String, Object>> requestedClassificationPathMapList = new ArrayList<Map<String, Object>>();
        matrix.util.List<String> requestedClassificationPaths = new matrix.util.List<>();
        System.out.println("Requested General Class Object Id List: ");
        // sample classificationPath: Headbox GL2|Headbox GC1\nHeadbox GL2|Headbox GC1|Headbox GC2
        if (!classificationPaths.isEmpty()) {
            String[] classificationPathList = classificationPaths.split("\n");

            for (String classificationPath : classificationPathList) {
                classificationPath = classificationPath.trim();
                String generalClassObjectId = getGeneralClassObjectIdFromClassificationPath(context, classificationPath);
                if (generalClassObjectId != null) {
                    System.out.println(classificationPath + " : " + generalClassObjectId);
                    if (requestedClassificationPaths.contains(classificationPath)) {
                        System.out.println(DUPLICATE_CLASSIFICATION_PATH_FOUND);
                        duplicateClassificationPaths.add(classificationPath);
                        status = FAIL_STATUS;
                        continue;
                    }
                    requestedClassificationPaths.add(classificationPath);

                    Map<String, Object> requestedClassificationPathMap = new LinkedHashMap<String, Object>();
                    requestedClassificationPathMap.put("generalClassObjectId", generalClassObjectId);
                    requestedClassificationPathMap.put("classificationPath", classificationPath);
                    requestedClassificationPathMapList.add(requestedClassificationPathMap);
                    validClassificationPaths.add(classificationPath);
                } else {
                    invalidClassificationPaths.add(classificationPath);
                    status = FAIL_STATUS;
                }
            }
            if (invalidClassificationPaths.size() == classificationPathList.length) {
                resultMap.put("status", FAIL_STATUS);
                resultMap.put("messages", invalidClassificationPaths);
                resultMap.put("message", INVALID_CLASSIFICATION_PATH_FOUND + invalidClassificationPaths);
                return resultMap;
            }
        }
        MQLCommand objMQL = new MQLCommand();
        objMQL.open(context);
        String sMQLStatement = "expand bus " + objectId + " to relationship \"Classified Item\" recurse to 1 select relationship id dump :;";
        String result = MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
        String[] oldClassificationPathList = result.split("\n");
        requestedClassificationPaths = disconnect(oldClassificationPathList, requestedClassificationPaths, status, context, objMQL);

        // connect remaining classification paths
        if (!requestedClassificationPaths.isEmpty()) {
            for (Map<String, Object> requestedClassificationPathMap : requestedClassificationPathMapList) {
                String generalClassObjectId = (String) requestedClassificationPathMap.get("generalClassObjectId");
                String classificationPath = (String) requestedClassificationPathMap.get("classificationPath");
                if (requestedClassificationPaths.contains(classificationPath)) {
                    sMQLStatement = "connect bus " + objectId + " rel \"Classified Item\" from " + generalClassObjectId + ";";
                    MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
                    System.out.println("Connecting : " + classificationPath);
                }
            }

            resultMap.put("objectId", objectId);
            validClassificationPaths.addAll(requestedClassificationPaths);
        }
        // success:Headbox MG2, failed: ABC,XYZ
        resultMap.put("data", validClassificationPaths);
        resultMap.put("status", status);
        if (status.equalsIgnoreCase(FAIL_STATUS)) {
            StringBuilder messageBuilder = new StringBuilder();
            if (duplicateClassificationPaths.size() > 0) {
                messageBuilder.append(DUPLICATE_CLASSIFICATION_PATH_FOUND + duplicateClassificationPaths + " ");
            }
            if (invalidClassificationPaths.size() > 0) {
                messageBuilder.append(INVALID_CLASSIFICATION_PATH_FOUND + invalidClassificationPaths);
            }
            resultMap.put("messages", invalidClassificationPaths.addAll(duplicateClassificationPaths));
            resultMap.put("message", messageBuilder.toString());
        }
        return resultMap;
    }

    public static String[] reverse(String a[]) {
        String[] b = new String[a.length];
        int j = a.length;
        for (int i = 0; i < a.length; i++) {
            b[j - 1] = a[i];
            j = j - 1;
        }
        return b;
    }

    /**
     * This function update mass Classification path
     *
     * @param context
     * @param args
     * @return Map<String, Object> resultMap
     */
    public static HashMap updateMassClassificationPath(Context context, String[] args) {
        HashMap resultMap = new HashMap();
        ArrayList resultList = new ArrayList();
        try {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            ArrayList objectList = (ArrayList) programMap.get("data");
            for (Object element : objectList) {
                HashMap object = (HashMap) element;
                String objectId = (String) object.get("objectId");
                String classificationPath = (String) object.get("classificationPath");
                HashMap result = updateClassificationPath(context, objectId, classificationPath);
                resultList.add(result);
            }
            resultMap.put("status", SUCCESS_STATUS);
            resultMap.put("data", resultList);
        } catch (Exception e) {
            resultMap.put("status", FAIL_STATUS);
            resultMap.put("message", e.getMessage());
            resultMap.put("data", resultList);
            return resultMap;
        }
        return resultMap;
    }
	
	public String getClassificationPathsTitle(Context context, String objectId)
            throws FrameworkException {
        try {
//			MatrixSession ms=new MatrixSession();
//			ms.setUserName("creator");
//			Context context=  new Context(ms);
            String reportFormat = "json";
//            HashMap programMap = (HashMap) JPO.unpackArgs(args);
//            HashMap paramMap = (HashMap) programMap.get("paramMap");
//            String objectId = (String) paramMap.get("objectId");
//            HashMap requestMap = (HashMap)programMap.get("requestMap");
//            String pfMode = (String)requestMap.get("PFmode");
//            String reportFormat = pfMode == null ? null : "HTML";
//            if (pfMode != null && pfMode.equalsIgnoreCase("true")) {
//                reportFormat = "HTML";
//            } else {
//                reportFormat = null;
//            }

            DomainObject domainObject = new DomainObject(objectId);
            if (domainObject.isKindOf(context, TYPE_CLASSIFICATION)) {
                System.out.println("--------------------------------Type classification: ");
                ClassificationPath path = getClassificationPath(context, objectId);
                //System.out.println("path=====" + path.toString());
                String htmlOutput = renderPathTitle(context, path, reportFormat);
                System.out.println("------------------------------------htmlOutput: "+htmlOutput);
                return parseHTMLClassificationPath(htmlOutput);
            } else {
                System.out.println("--------------------------------not Type classification: ");
                Vector vecPaths = getEndItemClassificationPaths(context, objectId);
                String htmlOutput = renderPathsTitle(context, vecPaths, false, reportFormat);
                System.out.println("------------------------------------htmlOutput: "+htmlOutput);
                return parseHTMLClassificationPath(htmlOutput);
            }
        } catch (Exception e) {
            throw new FrameworkException(e);
        }
    }
	
	/**
     * Renders an entire path by rendering its constituent elements separated by
     * the separator HTML
     *
     * @param pathElems the list of elements
     * @param reportFormat the report format
     * @return the rendered path
     */
    public String renderPathTitle(Context context, ClassificationPath pathElems, String reportFormat) {
        StringList elemHtmlList = new StringList();
        Iterator i = pathElems.iterator();
        while (i.hasNext()) {
            ClassificationInfo elem = (ClassificationInfo) i.next();
            elemHtmlList.add(renderPathTitleElem(context, elem.getName(), elem.getId(), reportFormat));
        }
        String elemSeperator = _elemSeparatorHTML;
        if ("CSV".equalsIgnoreCase(reportFormat)) {
            elemSeperator = "->";
        }
        return FrameworkUtil.join(elemHtmlList, elemSeperator);
    }
	
	 /**
     * Renders the path element
     *
     * @param strName the Object name
     * @param strObjectId the objectId
     * @param reportFormat the report format
     * @return String the rendered element
     */
    public String renderPathTitleElem(Context context, String strName, String strObjectId, String reportFormat) {
        if ("HTML".equals(reportFormat) || "CSV".equals(reportFormat)) {
            return strName;
        } else {
            //Modified for Bug Id -363882 replace # by javascript:void(0) which was causing error in IE
            return "<a href=\"javascript:void(0)\" " + "onClick=\"javascript:showModalDialog("
                    + "'../common/emxTree.jsp?objectId=" + strObjectId + "',"
                    + "'860','520');\"" + ">" + strName + "</a>";
        }
    }
	
	/**
     * Given a vector of ClassificationPath's, render each one with HTML using
     * all appropriate separators
     *
     * @param vecPaths the paths
     * @param forUseInForm not used; was previously needed to allow different
     * rendering in UIForm vs. UITable, and could still be used for that purpose
     * in a customization
     * @param reportFormat the report format
     * @return String the rendered paths
     */
    public String renderPathsTitle(Context context, Vector vecPaths, boolean forUseInForm, String reportFormat) {
        StringList lstPathHtml = new StringList();
        Iterator pathIter = vecPaths.iterator();
        while (pathIter.hasNext()) {
            ClassificationPath lstPath = (ClassificationPath) pathIter.next();
            System.out.println("------------------------------------------------lstPath: "+lstPath.toString());
            lstPathHtml.add(renderPathTitle(context, lstPath, reportFormat));
        }
        String pathSeperator = _pathSeparatorHTML;
        if ("CSV".equalsIgnoreCase(reportFormat)) {
            pathSeperator = "\n";
        }
        System.out.println("lstPathHtml======" + lstPathHtml);
        String strPathHtml = FrameworkUtil.join(lstPathHtml, pathSeperator);
        System.out.println("------------------------------------------------strPathHtml: "+strPathHtml);
        return strPathHtml;
    }
}
