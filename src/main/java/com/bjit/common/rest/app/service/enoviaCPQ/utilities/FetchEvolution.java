/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enoviaCPQ.utilities;

import com.matrixone.apps.domain.util.MqlUtil;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class FetchEvolution {

    private static final Logger FETCH_LOGGER = Logger.getLogger(FetchEvolution.class.getName());

    public boolean hasEvolution(String pid, Context context) throws Exception {
        System.out.println(" ++++++++++++++ hasEvolution +++++++++++++++ ");
        String queryResult = this.evolutionQueryResult(pid, context);

        if (queryResult == null || queryResult.isEmpty()) {
            return false;
        }
        System.out.println(" -------------- hasEvolution --------------- ");
        return true;
    }

    private String evolutionQueryResult(String pid, Context context) throws Exception {
        String queryFormat = "expand bus {0} from relationship VPLMrel/PLMConnection/V_Owner type VPMCfgEffectivity select bus physicalid dump |";
        String query = MessageFormat.format(queryFormat, pid);
        
        System.out.println("Evolution query   " + query );
        return FetchEvolution.getQueryResult(query, context);
    }

    public Map<String, List<String>> getEvolution(String id, Context context) throws Exception {
        Map<String, List<String>> instanceEvolutionMap = new HashMap<>();
        if (!this.hasEvolution(id, context)) {
            return instanceEvolutionMap;
        }

        String resultVPMCfgEffectivity = evolutionQueryResult(id, context);

        List<String> idOfVPMCfgEffectivity = this.getVPMCfgEffectivityId(resultVPMCfgEffectivity);

        List<String> pathsInfo = this.getPathsInfoOfVPMCfgEffectivity(idOfVPMCfgEffectivity, context);

        instanceEvolutionMap = this.getEvolutionMap(pathsInfo);

        return instanceEvolutionMap;
    }

    private Map<String, List<String>> getEvolutionMap(List<String> pathsInfo) {
        System.out.println(" +++++++++++++++ getEvolutionMap ++++++++++++++ ");
        Map<String, List<String>> map = new HashMap<>();

        // each path has one bom-rel id
        for (String path : pathsInfo) {
            // System.out.println(path);
            String[] pathChunks = path.split("\\|");

            String relationshipPid = "";
            List<String> connectedMVs = new ArrayList<>();

            // this loop is for evolution of single rel id
            for (String pathChunk : pathChunks) {
                //System.out.println(pathChunk);

                if (pathChunk.contains("DELFmiFunctionIdentifiedInstance")) {
                    // getting relationship pid
                    relationshipPid = pathChunk.substring(pathChunk.indexOf("DELFmiFunctionIdentifiedInstance,"))
                            .split("\\,")[1];
                } else if (pathChunk.contains("Products")) {
                    connectedMVs.add(pathChunk.substring(pathChunk.indexOf("Products,")).split("\\,")[1]);
                }
            }
            map.put(relationshipPid, connectedMVs);
        }
        System.out.println(map.toString());
        System.out.println(" --------------- getEvolutionMap -------------- ");
        return map;
    }
    
  

    private List<String> getPathsInfoOfVPMCfgEffectivity(List<String> idListOfVPMCfgEffectivity, Context context)
            throws Exception {
        System.out.println(" ++++++++++++ getPathsInfoOfVPMCfgEffectivity ++++++++++++++ ");
        String pathsQueryFormat = "print bus {0} select paths.path dump |";

        List<String> paths = new ArrayList<>();
        for (String id : idListOfVPMCfgEffectivity) {
            String query = MessageFormat.format(pathsQueryFormat, id);
            System.out.println("Query: " + query);
            String queryResult = FetchEvolution.getQueryResult(query, context);
            paths.add(queryResult);
        }
        System.out.println(" ------------ getPathsInfoOfVPMCfgEffectivity -------------- ");
        return paths;
    }

    private List<String> getVPMCfgEffectivityId(String resultVPMCfgEffectivity) {
        List<String> idList = new ArrayList<>();

        String[] linesVPMCfgEffectivity = resultVPMCfgEffectivity.split("\\n");
        System.out.println(Arrays.toString(linesVPMCfgEffectivity));

        for (String e : linesVPMCfgEffectivity) {
            String[] chunks = e.split("\\|");
            String physicalId = chunks[chunks.length - 1];
            System.out.println("VPMCfgEffectivity physical id:" + physicalId);
            idList.add(physicalId);
        }

        return idList;
    }

    public boolean hasConfigContext(String pid, Context context) throws Exception {
        System.out.println("++++++++++++++++++ hasConfigContext +++++++++++++++++");
        String hasConfigQueryFormat = "expand bus {0} from relationship VPLMrel/PLMConnection/V_Owner type VPMCfgContext  select bus physicalid dump |";
        String query = MessageFormat.format(hasConfigQueryFormat, pid);
        String queryResults = null;

        queryResults = MqlUtil.mqlCommand(context, query);

        if (queryResults == null || queryResults.isEmpty()) {
            return false;
        }

        System.out.println("------------------ hasConfigContext -----------------");
        return true;
    }

    public List<String> fetchConfigContext(String pid, Context context) throws Exception {
        List<String> configContexts = new ArrayList<>();
        if (!this.hasConfigContext(pid, context)) {
            return configContexts;
        }
        String queryResult = executeToFetchVPMCfgContext(pid, context);
        // execute query to get VPMCfgContext physical id
        String pidVPMCfgContext = this.getVPMCfgContextByParsingDumpQueryResult(queryResult);

        String pathsInfo = this.executeQueryToFetchPathsInfo(pidVPMCfgContext, context);
        configContexts = this.getConfigContextsByParsingDumpQueryResult(pathsInfo);
        return configContexts;
    }

    private List<String> getConfigContextsByParsingDumpQueryResult(String queryResult) {
        System.out.println("+++++++++++ getConfigContextsByParsingDumpQueryResult ++++++++++++++++");
        List<String> configContexts = new ArrayList<>();

        System.out.println(queryResult);
        String[] lines = queryResult.split("\\|");
        System.out.println(Arrays.toString(lines));

        for (String modelLine : lines) {
            System.out.println(modelLine);
            if (modelLine == null || modelLine.equals("") || !modelLine.contains("Model,")) {
                continue;
            }
            String physicalIDModel = modelLine.substring(modelLine.indexOf("Model,")).split("\\,")[1];
            System.out.println("Model Physical ID: " + physicalIDModel);
            configContexts.add(physicalIDModel);
        }
        System.out.println("--------- getConfigContextsByParsingDumpQueryResult ----------");
        return configContexts;
    }

    private String executeQueryToFetchPathsInfo(String pidVPMCfgContext, Context context) throws Exception {
        String pathsQueryFormat = "print bus {0}  select paths.path dump |,";
        String pathQuery = MessageFormat.format(pathsQueryFormat, pidVPMCfgContext);;

        // execute mql and get response
        String queryResult = getQueryResult(pathQuery, context);

        //pathsInfo = "SemanticRelation^businessobject,Model,3AAB758B0000703C6220866A000060F8,3AAB758B0000703C6220866A000060FA,3AAB758B0000703C6220866A000060F8,3AAB758B0000703C6220866A000060FC,FALSE|,SemanticRelation^businessobject,Model,3AAB758B0000703C622868C0000064AC,3AAB758B0000703C622868C0000064AE,3AAB758B0000703C622868C0000064AC,3AAB758B0000703C622868C0000064B0,FALSE";
        System.out.println(queryResult);
        return queryResult;
    }

    private String executeToFetchVPMCfgContext(String pid, Context context) throws Exception {
        String queryFormat = "expand bus {0} from relationship VPLMrel/PLMConnection/V_Owner type VPMCfgContext  select bus physicalid dump |";
        String query = MessageFormat.format(queryFormat, pid);
        System.out.println(query);

        String queryResult = getQueryResult(query, context);

        // queryResult =
        // "1|VPLMrel/PLMConnection/V_Owner|to|VPMCfgContext|3AAB758B00008DB4623829F900002494||3AAB758B00008DB4623829F900002494";
        return queryResult;
    }

    private String getVPMCfgContextByParsingDumpQueryResult(String queryResult) {
        // parsing and get VPMCfgContext physical id

        String[] chunks = queryResult.split("\\|");
        System.out.println(Arrays.toString(chunks));
        String pidVPMCfgContext = chunks[chunks.length - 1];

        return pidVPMCfgContext;
    }

    public static String getQueryResult(String query, Context context) throws Exception {
        MQLCommand objMQL = new MQLCommand();

        try {
            objMQL.open(context);
            String result = MqlUtil.mqlCommand(context, objMQL, query);

            // objMQL.close(context);
            return result;
        } catch (MatrixException e) {

            System.out.println("Query String : {0}" + query);

            throw e;
        }
    }
}
