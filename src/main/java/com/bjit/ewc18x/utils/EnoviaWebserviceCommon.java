/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.utils;



import com.bjit.ewc18x.context.ContextProperties;
import com.bjit.ewc18x.context.MatrixContext;
import com.bjit.ewc18x.model.ExpandObjectForm;
import com.bjit.ewc18x.model.UserContextForm;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Scanner;
import javax.servlet.http.HttpSession;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 *
 * @author Kayum-603
 */
public class EnoviaWebserviceCommon {

    private static final Logger LOGGER = Logger.getLogger(EnoviaWebserviceCommon.class);
    MatrixContext matrixContext = new MatrixContext();
    UserContextForm contextForm = new UserContextForm();
  
    public Context getContext(ExpandObjectForm expandObjectForm) throws CustomException {
        try {
        return getContext(expandObjectForm.getUserID(), expandObjectForm.getPassword(), false, "");
         } catch (Exception ex) {
           LOGGER.error("Error occured: " + ex);
           throw new CustomException(ex.getMessage());     
        }
    }

    public Context getContext(String userID, String password, boolean isRole, String securityContext) throws CustomException {
        LOGGER.debug("Called getContext method");
         LOGGER.debug("Security context: "+securityContext);
        Context context = null;
        try {
            /*
            For connecting to a context
             */
            String host = PropertyReader.getProperty("matrix.context.nocas.connection.host");
            LOGGER.debug("Found Host: " + host);
            // for unsecure connection
            context = new Context(host);
            context.setUser(userID);
            if (password != null && !password.equals("")) {
                context.setPassword(password);
                if(isRole) {
                  // context.setRole(securityContext);
                }
            }
            context.connect();
            LOGGER.debug("Context created?: " + context.checkContext());
            return context;
        } catch (Exception ex) {
            LOGGER.error("Error while creating context: " + ex.getMessage());
            throw new CustomException(ex.getMessage());
        }
    }
     
    public Context getSecureContext(ExpandObjectForm expandObjectForm) throws CustomException, Exception {
        try {
             return getSecureContext(expandObjectForm.getUserID(), expandObjectForm.getPassword());
        } catch (Exception ex) {
          LOGGER.error("Error occured: " + ex);
          throw new CustomException(ex.getMessage());     
        }
       
    }
    
    /**
     * Used to generate SecureContext
     *
     * @param userID
     * @param userPassword
     * @return context
     * @throws com.bjit.ewc.utils.CustomException
     */
    public Context getSecureContext(String userID, String userPassword) throws CustomException {
        LOGGER.debug("Called getContext method");
        Context context = null;
        try {
        String host = PropertyReader.getProperty("matrix.context.cas.connection.host");
        System.out.println("found host : " + host);
        LOGGER.debug("Found Host: " + host);
        ContextProperties contextProperties = new ContextProperties();
        contextProperties.setCasHostPath(host);
        contextProperties.setUserName(userID);
        contextProperties.setPassport(true);
        contextProperties.setNewContext(false);

        if (userPassword != null && !userPassword.equals("")) {
            contextProperties.setPassword(userPassword);
        }
        context = matrixContext.getContext(userID, userPassword, true, true);
        contextForm.setUserContext(context);
        LOGGER.debug("Context created and set to pojo: "+contextForm.getUserContext().checkContext());
        //context = MatrixContext.getContext(contextProperties);
        //for secure --end
        LOGGER.debug("Context created?: " + context.checkContext());
        } catch (NullPointerException ex) {
            LOGGER.error("Error while creating context: " + ex.getMessage());
            throw new CustomException("Invalid Credential !");
        } catch (Exception ex) {
            LOGGER.error("Error while creating context: " + ex.getMessage());
            throw new CustomException(ex.getMessage());
        }
        return context;
    }

    public Context getUnSecureContext(ExpandObjectForm expandObjectForm) throws CustomException {
        try {
            return getUnSecureContext(expandObjectForm.getUserID(), expandObjectForm.getPassword());
        } catch (Exception ex) {
          LOGGER.error("Error occured: " + ex);
          throw new CustomException(ex.getMessage());    
        }
    }
    public Context getUnSecureContext(String userID, String userPassword) throws Exception {
        Context context = null;
        try {
            LOGGER.debug("Called getContext method");
            String host = PropertyReader.getProperty("matrix.context.nocas.connection.host");
            LOGGER.debug("Found Host: " + host);

            context = new Context(host);
            context.setUser(userID);
            if (userPassword != null && !userPassword.equals("")) {
                context.setPassword(userPassword);
            }
            context.connect();
            LOGGER.debug("Context created?: " + context.checkContext());

        } catch (MatrixException ex) {    
          LOGGER.error("Error while creating unsecured context: " + ex.getMessage());
          throw new Exception(ex);    
        }
        return context;
    }

    public boolean containsCaseInsensitive(String s, List<String> list) {
        for (String string : list) {
            if (string.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public int findIndexOfAttribute(File file, String atrName) {

        FileReader fr = null;
        LineNumberReader lnr = null;
        String str;
        int i;

        try {

            // create new reader
            fr = new FileReader(file);
            lnr = new LineNumberReader(fr);

            // read lines till the end of the stream
            while ((str = lnr.readLine()) != null) {
                i = lnr.getLineNumber();
                //  System.out.print("(" + i + ")");

                // prints string
                // System.out.println(str);
                if (str.equals("Classification Path")) {
                    LOGGER.debug("Index of classification path: " + i);
                    return i;
                }
            }

        } catch (Exception e) {

            // if any error occurs
            e.printStackTrace();
        } finally {

            try {
                // closes the stream and releases system resources
                if (fr != null) {
                    fr.close();
                }
                if (lnr != null) {
                    lnr.close();
                }
            } catch (IOException ex) {
                LOGGER.error("IOException occured: " + ex);
            }
        }
        return 0;
    }
    /**
     * Used to get cbpKey from configuration file
     *
     * @param userName
     * @return cbpKey string
     */
    public String getCbpKey(String userName) {
        LOGGER.debug("In getCbpKey() method.");
        String cbpKey = null;
        try {
            LOGGER.debug("Reading File for getCbpKey.");
            File file = new File(getClass().getResource("/cbpKeyList.conf").getFile());
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.charAt(0) == '#') {
                    continue;
                }

                String[] temp = line.split(":");
                if(temp[0].compareTo(userName) == 0) {
                    cbpKey = temp[1];
                    LOGGER.debug("Got cbpKey from file. " + cbpKey);
                    break;
                }
            }
            input.close();
        } catch (FileNotFoundException | ArrayIndexOutOfBoundsException ex) {
            LOGGER.error(ex.getMessage());
        }
        LOGGER.debug("Returning value of cbpKey: " + cbpKey);
        return cbpKey;
    }
    
        /**
     * used to set session attribute which will be used throughout the
     * application
     *
     * @param session
     * @param username
     * @param plmKey
     * @param securityContext
     * @param context
     */
    public void setSessionAttributes(HttpSession session, String username, String plmKey, String securityContext, Context context) {
        session.setAttribute("username", username);
        session.setAttribute("plmKey", plmKey);
        session.setAttribute("securityContext", securityContext);
        session.setAttribute("context", context);
    }

    public void removeSessionAttributes(HttpSession httpSession) {
        httpSession.removeAttribute("username");
        httpSession.removeAttribute("plmKey");
        httpSession.removeAttribute("securityContext");
        httpSession.removeAttribute("context");
    }
}
