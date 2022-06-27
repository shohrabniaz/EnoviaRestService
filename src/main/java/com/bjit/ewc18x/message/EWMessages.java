package com.bjit.ewc18x.message;

import java.io.Serializable;

import static com.bjit.ewc18x.message.MessageType.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Kayum-603
 */
public final class EWMessages implements Serializable, Iterable<EWMessageCode> {

    private final EWMessageType resultMessageType;
    private final List<EWMessageCode> codeList = new ArrayList<>();

    public EWMessages(EWMessageType resultMessageType) {
        this(resultMessageType, null);
        //this.resultMessageType = resultMessageType;
    }

    public EWMessages(EWMessageType resultMessageType, String code) {
        if (resultMessageType == null) {
            throw new IllegalArgumentException("type must not be null!");
        }
        System.out.println("type: " + resultMessageType);
        this.resultMessageType = resultMessageType;
        if (code != null) {
            System.out.println("messages: " + code);
            add(code);
        }
    }

    public EWMessages add(EWMessageCode message) {
        if (message != null) {
            System.out.println("Add: " + message);
            this.codeList.add(message);
        } else {
            throw new IllegalArgumentException("message must not be null");
        }
        return this;
    }

    public EWMessages add(String code) {
        if (code != null) {
            this.add(EWMessageCode.getMsgCode(code));
        } else {
            throw new IllegalArgumentException("messages must not be null");
        }
        return this;
    }

    public EWMessages addMsgText(String msgText) {
        if (msgText != null) {
            this.add(EWMessageCode.getMsgTest(msgText));
        } else {
            throw new IllegalArgumentException("messages must not be null");
        }
        return this;
    }
    
    public static EWMessages success() {
        return new EWMessages(SUCCESS);
    }

    public static EWMessages info() {
        return new EWMessages(INFO);
    }

    public static EWMessages warning() {
        return new EWMessages(WARNING);
    }

    public static EWMessages error() {
        return new EWMessages(ERROR);
    }

    public static EWMessages danger() {
        return new EWMessages(DANGER);
    }

    public EWMessageType getType() {
        return resultMessageType;
    }

    public String getValue() {
        return resultMessageType.getValue();
    }

    public List<EWMessageCode> getCodeList() {
        return codeList;
    }

    @Override
    public String toString() {
        return "EWReusltMessages{" + "resultMessageType=" + resultMessageType + ", list=" + codeList + '}';
    }

    @Override
    public Iterator<EWMessageCode> iterator() {
        return codeList.iterator();
    }

}
