package com.bjit.ewc18x.message;

import java.io.Serializable;
import org.springframework.util.Assert;

/**
 *
 * @author Kayum-603
 */
public class EWMessageCode implements Serializable{

    private static final long serialVersionUID = -2020904640866275166L;
    private static final Object[] EMPTY_ARRAY = new Object[0];

    private final String code;
    private final String msgText;
    private  Object[] args;
    
    public EWMessageCode(String code, String msgText) {
        this.code = code;
        this.msgText = msgText;
        System.out.println("Code: "+code);
    }
    

    public String getCode() {
        return code;
    }
   
    public String getMsgTest() {
        return msgText;
    }

       public static EWMessageCode getMsgCode(String code, Object... args) {
        Assert.notNull(code, "message code must not be null");
        return new EWMessageCode(code, null);
    } 

    public static EWMessageCode getMsgTest(String msgText, Object... args) {
        Assert.notNull(msgText, "message code must not be null");
        return new EWMessageCode(null, msgText);
    } 

    @Override
    public String toString() {
        return "EWMessageCode{" + "code=" + code + ", msgText=" + msgText + '}';
    }

    
       
       
}
