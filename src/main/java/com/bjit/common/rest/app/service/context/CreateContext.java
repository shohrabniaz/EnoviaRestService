package com.bjit.common.rest.app.service.context;

import com.bjit.common.code.utility.context.ContextGeneration;
import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.ewc18x.utils.PropertyReader;;
import matrix.db.Context;
import matrix.util.MatrixException;
import com.bjit.ewc18x.context.Passport;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class CreateContext {

    private static final Logger CREATE_CONTEXT_LOGGER = Logger.getLogger(CreateContext.class);

    public Context createNoCasContext(String userId, String password, String host) throws MatrixException {
        try {
            CREATE_CONTEXT_LOGGER.debug("Connecting to no cas context");
            matrix.db.Context context = new matrix.db.Context(host);
            context.setUser(userId);
            context.setPassword(password);
            context.connect();
            CREATE_CONTEXT_LOGGER.debug("Connected to no cas context");
            return context;
        } catch (MatrixException exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }
//    public Context createCasContext(String userId, String password, String host) throws Exception {
//        //String passport = "";
//        try {
//            CREATE_CONTEXT_LOGGER.debug("Connecting to cas context");
//            Context context = new Context(host + Passport.getTicket(host, userId, password).split(";")[0]);
//            context.connect();
//            CREATE_CONTEXT_LOGGER.debug("Connected to cas context");
//            return context;
//        } catch (MatrixException exp) {
//            exp.printStackTrace(System.out);
//            throw exp;
//        }
//    }

    public Context createCasContext(String userId, String password, String host) throws Exception {
        //String passport = "";
        try {
            CREATE_CONTEXT_LOGGER.debug("Connecting to cas context");
            ContextGeneration generateContext = new ContextGeneration();
            Context context = generateContext.createContext(host, userId, password);
            CREATE_CONTEXT_LOGGER.info("Connected to cas context. User is : " + context.getUser());
            return context;
        } catch (Exception exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

//    public Context createCasContext(String userId, String password, String host) throws Exception {
//        //String passport = "";
//
//        Context context = null;
//        String ticket = "";
//        try {
//            System.out.println("Host Url :::: " + host);
//            String[] passport = Passport.getTicket(host, userId, password).split(";");//java.net.ConnectException: Connection timed out: connect
//            ticket = passport[0];
//            String jsessionId = passport[1];
//            System.out.println("CTX PT::::::> " + ticket);
//            System.out.println("jsessionId: " + jsessionId);
//        } catch (Exception exp) {
//            exp.printStackTrace(System.out);
//            exp.printStackTrace();
//        }
//        try {
//            context = new Context(host + ticket);
//            context.setUser(userId);
//            context.setRole("ctx::VPLMProjectLeader.Valmet.Common Space");
//            context.connect();
//
//            System.out.println("User : " + context.getUser());
//        } catch (MatrixException exp) {
//            exp.printStackTrace(System.out);
//            exp.printStackTrace();
//            throw exp;
//        }
//        return context;
//
//    }
    public matrix.db.Context getUserCredentialsFromHeader(HttpServletRequest request) throws Exception {
        try {

            String checkCasEnv = request.getHeader("isCas");
            checkCasEnv = (checkCasEnv == null || checkCasEnv.equalsIgnoreCase("")) ? PropertyReader.getProperty("matrix.context.env.connection.isCas") : checkCasEnv;
            boolean isCas = Boolean.parseBoolean(checkCasEnv);

            String userId = request.getHeader("userId");
            String password = request.getHeader("password");

            if (isCas) {
                return this.createCasContext(userId, password, PropertyReader.getProperty("matrix.context.cas.connection.host"));
            }
            return this.createNoCasContext(userId, password, PropertyReader.getProperty("matrix.context.nocas.connection.host"));
        } catch (Exception exp) {
            exp.printStackTrace(System.out);
            CREATE_CONTEXT_LOGGER.error(exp.getMessage());
            throw new Exception("The credentials you have used is not authentic or the context creation service may not be up and running");
        }
    }

    public matrix.db.Context getContext(String userId, String password, String host, boolean isCasContext) throws Exception {
        System.out.println("\n");
        CREATE_CONTEXT_LOGGER.debug("--------------- ||| Context Generation process has been started ||| ---------------");
        CREATE_CONTEXT_LOGGER.debug("###################################################################################");
        try {
            if (isCasContext) {
                return this.createCasContext(userId, password, host);
            }
            return this.createNoCasContext(userId, password, host);
        } catch (Exception exp) {
            CREATE_CONTEXT_LOGGER.error(exp.getMessage());
            exp.printStackTrace(System.out);
            throw exp;
        } finally {
            CREATE_CONTEXT_LOGGER.debug("--------------- ||| Context Generation process has been completed ||| ---------------");
            CREATE_CONTEXT_LOGGER.debug("#####################################################################################");
            System.out.println("\n");
        }
    }

    public matrix.db.Context getContext(String userId, String password, String host, boolean isCasContext, boolean showUserCredentials) throws Exception {
        System.out.println("\n");
        CREATE_CONTEXT_LOGGER.debug("--------------- ||| Context Generation process has been started ||| ---------------");
        CREATE_CONTEXT_LOGGER.debug("###################################################################################");
        try {
            if (showUserCredentials) {
                //System.out.println("User id : " + userId);
                //System.out.println("Application host : " + host);
                //System.out.println("Passport context : " + isCasContext);
            }
            return this.getContext(userId, password, host, isCasContext);
        } catch (Exception exp) {
            CREATE_CONTEXT_LOGGER.error(exp.getMessage());
            exp.printStackTrace(System.out);
            throw exp;
        } finally {
            CREATE_CONTEXT_LOGGER.debug("--------------- ||| Context Generation process has been completed ||| ---------------");
            CREATE_CONTEXT_LOGGER.debug("#####################################################################################");
            System.out.println("\n");
        }
    }

    public matrix.db.Context getContext(String userId, String password, String host, boolean isCasContext, boolean showUserCredentials, boolean showUserPassword) throws Exception {
        System.out.println("\n");
        CREATE_CONTEXT_LOGGER.debug("--------------- ||| Context Generation process has been started ||| ---------------");
        CREATE_CONTEXT_LOGGER.debug("###################################################################################");
        try {
            Context getContext = getContext(userId, password, host, isCasContext, showUserCredentials);
            if (showUserPassword) {
                //System.out.println("User password : " + password);
            }
            return getContext;
        } catch (Exception exp) {
            CREATE_CONTEXT_LOGGER.error(exp.getMessage());
            exp.printStackTrace(System.out);
            throw exp;
        } finally {
            CREATE_CONTEXT_LOGGER.debug("--------------- ||| Context Generation process has been completed ||| ---------------");
            CREATE_CONTEXT_LOGGER.debug("#####################################################################################");
            System.out.println("\n");
        }
    }

    public matrix.db.Context getContext(String userId, String password) throws Exception {
        CREATE_CONTEXT_LOGGER.debug("--------------- ||| Context Generation process has been started ||| ---------------");
        CREATE_CONTEXT_LOGGER.debug("###################################################################################");
        try {
            String host = PropertyReader.getProperty("matrix.context.cas.connection.host");
            return this.createCasContext(userId, password, host);
        } catch (Exception exp) {
            CREATE_CONTEXT_LOGGER.error(exp.getMessage());
            throw exp;
        } finally {
            CREATE_CONTEXT_LOGGER.debug("--------------- ||| Context Generation process has been completed ||| ---------------");
            CREATE_CONTEXT_LOGGER.debug("#####################################################################################");
        }
    }

    public matrix.db.Context getAdminContext() throws Exception {
        CREATE_CONTEXT_LOGGER.debug("--------------- ||| Context Generation process has been started ||| ---------------");
        CREATE_CONTEXT_LOGGER.debug("###################################################################################");
        try {
            ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();
            String userId = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
            String password = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));
            String host = PropertyReader.getProperty("matrix.context.cas.connection.host");
            Context context = createCasContext(userId,password,host);
            context.connect();
            return context;
        } catch (Exception exp) {
            CREATE_CONTEXT_LOGGER.error(exp.getMessage());
            throw exp;
        } finally {
            CREATE_CONTEXT_LOGGER.debug("--------------- ||| Context Generation process has been completed ||| ---------------");
            CREATE_CONTEXT_LOGGER.debug("#####################################################################################");
        }
    }

    public matrix.db.Context getContext() throws Exception {
        Context context = null;
        try {
            ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();
            String user = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
            String pass = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));
            context = getContext(user, pass);
        } catch (Exception exp) {
            CREATE_CONTEXT_LOGGER.error(exp.getMessage());
            throw exp;
        }
        return context;
    }
}
