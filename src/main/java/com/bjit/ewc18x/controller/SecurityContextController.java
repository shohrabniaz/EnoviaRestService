/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.controller;


import com.bjit.ewc18x.utils.CustomException;
import com.bjit.ewc18x.utils.EnoviaWebserviceCommon;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import matrix.db.Context; 
import matrix.db.JPO;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class SecurityContextController {
    private static final Logger logger = Logger.getLogger(SecurityContextController.class);
    EnoviaWebserviceCommon enoviaWebserviceCommon = new EnoviaWebserviceCommon();
     /**
     * Used to get Security Context for all users 
     * @param session
     * @param userName 
     * @param password
     * @param response
     * @return
     * @throws com.bjit.ewc.utils.CustomException
     * @throws java.io.IOException
     */
    @RequestMapping(value = "getNocasSecurityContext")
    public @ResponseBody String getNocasSecurityContext(HttpSession session, @RequestParam(value = "userName") String userName ,@RequestParam(value = "password") String password, HttpServletResponse response) throws CustomException, IOException {
        List<String> data =new ArrayList<String>();
        data.add("admin");
        JSONObject jassonObject = new JSONObject();
        System.out.println(userName+" ----------- "+jassonObject.toString());
        JSONArray jassonArray = new JSONArray();
        try {
        Map<String , List<String> >userContext = getUserContexts(session, userName, password, false);
        List <String> allContxt=userContext.get(userName);
        for (int i=0;i<allContxt.size();i++) {
            jassonArray.put(allContxt.get(i).toString());
        }
        jassonObject.put("userContext", jassonArray);
        } catch (CustomException | JSONException e) {
         logger.error("Exception while getting security context: "+e.getMessage());
         jassonObject.put("errorMessage", e.getMessage());
         response.setStatus(400);
         response.setContentType("application/json");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().write(jassonObject.toString()); 
        }
     return jassonObject.toString();
    }
    
   
     /**
     * Used to get cas Security Context for all users 
     * @param session
     * @param userName 
     * @param password
     * @param response
     * @return
     * @throws com.bjit.ewc.utils.CustomException
     * @throws java.io.IOException
     */
    @RequestMapping(value = "getCasSecurityContext")
    public @ResponseBody
    String getCasSecurityContext(HttpSession session, @RequestParam(value = "userName") String userName, @RequestParam(value = "password") String password, HttpServletResponse response) throws CustomException, IOException {
//       if(session.getAttribute("securityContext")!=null) {
//         System.out.println("Session Data: "+session.getAttribute("securityContext"));
//         return (String) session.getAttribute("securityContext");
//       } else{
        List<String> data = new ArrayList<String>();
        data.add("admin");
        JSONObject jassonObject = new JSONObject();
        System.out.println(userName + " ----------- " + jassonObject.toString());
        try {
                    JSONArray jassonArray = new JSONArray();

            Map<String, List<String>> userContext = getUserContexts(session, userName, password, true);
            List<String> allContxt = userContext.get(userName);
            for (int i = 0; i < allContxt.size(); i++) {
                jassonArray.put(allContxt.get(i).toString());
            }
            jassonObject.put("userContext", jassonArray);

        session.setAttribute("username", userName);
        session.setAttribute("securityContext", jassonObject.toString());
        } catch (CustomException | JSONException e) {
         logger.error("Exception while getting security context: "+e.getMessage());
         jassonObject.put("errorMessage", e.getMessage());
         response.setStatus(400);
         response.setContentType("application/json");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().write(jassonObject.toString());  
//         return jassonObject.toString();    
        }

        return jassonObject.toString();
    }
    
    /**
     * used to get Map of List which contains every user's cas security context as a list
     * @param session
     * @param userName
     * @param password
     * @param isSecured checks is it for secure context or for unsecure context 
     * @return Map of list
     * @throws com.bjit.ewc.utils.CustomException
     */
    public Map<String , List<String>> getUserContexts(HttpSession session,String userName,String password, boolean isSecured) throws CustomException {

        Context ctx = null;
        Map<String , List<String>> allUserContext=null;
        try {
//            ExpandObjectForm expandObjectForm = new ExpandObjectForm();
//            expandObjectForm.setUserID(userName);
//            expandObjectForm.setPassword(password);
            if(isSecured) {
            logger.debug("Creating secured context");
            ctx = enoviaWebserviceCommon.getSecureContext(userName, password);
            } else {
            logger.debug("Creating unsecured context");
            ctx = enoviaWebserviceCommon.getUnSecureContext(userName, password);
            }   
            if(ctx.checkContext()) {
              logger.debug("is context created: "+ctx.checkContext());
              session.setAttribute("context", ctx);
            }
            String initargs[] = {};
            HashMap params = new HashMap();
            params.put("userName", userName);
            allUserContext = (Map<String , List<String>>) JPO.invoke(ctx,
              "UserSecurityContext_All", initargs, "getUserSecurityContextWithDefault",
              JPO.packArgs(params), Map.class);
            
            Iterator it = allUserContext.entrySet().iterator();
               while (it.hasNext()) {
                   Map.Entry pair = (Map.Entry)it.next();
                   List<String> allContextList=(List<String>)pair.getValue();
                   System.out.println(pair.getKey() + " = " + pair.getValue());
               }

        } catch (MatrixException ex) {
            logger.trace("Exception occured:: " + ex);
            throw new CustomException(ex.getMessage());
        } catch (Exception ex) {
            logger.trace("Exception occured:: " + ex);
            throw new CustomException(ex.getMessage());
        } 
        return allUserContext;
    }
}
