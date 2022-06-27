/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.context;

/**
 *
 * @author Kayum-603
 */

import com.bjit.ewc18x.utils.PropertyReader;
import matrix.db.Context;
import matrix.util.MatrixException;

public class MatrixContext {
  private static Context context = null;
  public Context getContext(String userName, String password, boolean isPassport, boolean isNewContext)
    throws Exception {
    try
    {
      if ((context != null) && (!isNewContext)) {
        return context;
      }
      String frameworkClassName = PropertyReader.getProperty("com.matrixone.servlet.Framework");
      System.out.println("frame work class name : " + frameworkClassName);
      Class frameworkClass = Class.forName(frameworkClassName);
      if (frameworkClass != null)
      {
        if (isPassport) {
          context = getContextForPassport(null, userName, password);
        } else {
          context = getNoCasContext(null, userName, password);
        }
        context.connect();
        System.out.println("Context Connected :" + context
          .checkContext());
      }
      else
      {
        throw new Exception("eMatrixRMIServlet jar was not added in project class path !! ");
      }
    }
    catch (Exception e)
    {
      System.out.println("Context was not created ");
      e.printStackTrace();
      throw new Exception(e.getMessage());
    }
    return context;
  }
  
  public Context getContext(ContextProperties contextProperties) throws Exception {
    try
    {
      if ((!contextProperties.isNewContext) && (context != null)) {
        return context;
      }
      String frameworkClassName = PropertyReader.getProperty("com.matrixone.servlet.Framework");
      Class frameworkClass = Class.forName(frameworkClassName);
      if (frameworkClass != null)
      {
        if (contextProperties.isPassport) {
          context = getContextForPassport(contextProperties.casHostPath, contextProperties
          
            .getUserName(), contextProperties
            .getPassword());
        } else {
          context = getNoCasContext(contextProperties.noCasHostPath, contextProperties
            .getUserName(), contextProperties
            .getPassword());
        }
        context.connect();
        System.out.println("Context Connected :" + context.checkContext());
      }
      else
      {
        throw new Exception("eMatrixRMIServlet jar was not added in project class path !! ");
      }
    }
    catch (Exception e)
    {
      System.out.println("Context was not created ");      
      e.printStackTrace();
      throw new Exception("Context was not created: "+e);
    }
    return context;
  }
  
  private  Context getNoCasContext(String hostPath, String userName, String password)
    throws MatrixException, Exception {
    String host = null;
    if ((hostPath == null) || (hostPath.equals(""))) {
      host = PropertyReader.getProperty("matrix.context.nocas.connection.host");
    }
    if ((host != null) && (!host.equals(""))) {
      context = new Context(host);
    } else {
      throw new Exception("Host path not found in properties file for No CAS login !!");
    }
    context.setUser(userName);
    if ((password != null) && (!password.equals(""))) {
      context.setPassword(password);
    }
    return context;
  }
  
  private Context getContextForPassport(String hostPath, String userName, String password)
    throws MatrixException, Exception {
    Context context = null;
    String host = null;
    if ((hostPath == null) || (hostPath.equals(""))) {
      host = PropertyReader.getProperty("matrix.context.cas.connection.host"); 
    }
    if ((host != null) && (!host.equals(""))) {
      context = new Context(host + Passport.getTicket(host, userName, password));
    } else {
      throw new Exception("Host path not found in properties for CAS login !!");
    }
    return context;
  }
  
//  public static void connect()
//    throws Exception
//  {
//    Context ctx = null;
//    try
//    {
//      ctx = getContext("jklalrahab", "jklalrahab", false, true);
//      
//      System.out.println(ctx.checkContext());
//      ctx.connect();
//      
//      System.out
//        .println(ctx.checkContext() + "**--" + ctx.getUser());
//      
//      String[] initargs = new String[0];
//      HashMap params = new HashMap();
//      params.put("objectId", "12451");
//      String newJporeturnedValue = (String)JPO.invoke(ctx, "TomalOnTest", initargs, "getStringTest", 
//      
//        JPO.packArgs(params), String.class);
//      System.out.println(newJporeturnedValue);
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace();
//    }
//  }

   
}
