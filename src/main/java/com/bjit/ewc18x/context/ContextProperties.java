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

public class ContextProperties
{
  public String casHostPath = null;
  public String noCasHostPath = null;
  private String userName = null;
  private String password = null;
  public boolean isPassport;
  public boolean isNewContext;
  
  public String getUserName()
  {
    return this.userName;
  }
  
  public void setUserName(String userName)
  {
    this.userName = userName;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public boolean isPassport()
  {
    return this.isPassport;
  }
  
  public void setPassport(boolean isPassport)
  {
    this.isPassport = isPassport;
  }
  
  public boolean isNewContext()
  {
    return this.isNewContext;
  }
  
  public void setNewContext(boolean isNewContext)
  {
    this.isNewContext = isNewContext;
  }
  
  public String getCasHostPath()
  {
    return this.casHostPath;
  }
  
  public void setCasHostPath(String casHostPath)
  {
    this.casHostPath = casHostPath;
  }
  
  public String getNoCasHostPath()
  {
    return this.noCasHostPath;
  }
  
  public void setNoCasHostPath(String noCasHostPath)
  {
    this.noCasHostPath = noCasHostPath;
  }
}
