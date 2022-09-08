/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.autoname.service;

import java.util.List;
import matrix.db.Context;

/**
 * @created 2021-04-06
 * @author Sudeepta
 */
public interface AutonameGenerateService {

    /**
     *
     * @param context
     */
    public void setContext(Context context);

    /**
     *
     * @param type
     * @return
     * @throws Exception
     */
    public String getAutonameByType(String type) throws Exception;

    /**
     *
     * @param type
     * @param prefix
     * @return
     * @throws Exception
     */
    public String getAutonameByType(String type, String prefix) throws Exception;

    /**
     *
     * @param type
     * @param prefix
     * @param affix
     * @return
     * @throws Exception
     */
    public String getAutonameByType(String type, String prefix, String affix) throws Exception;

    /**
     *
     * @param type
     * @param prefix
     * @param affix
     * @param suffix
     * @return
     * @throws Exception
     */
    public String getAutonameByType(String type, String prefix, String affix, String suffix) throws Exception;

    /**
     *
     * @param type
     * @param count
     * @return
     * @throws Exception
     */
    public List<String> getAutonameListByType(String type, int count) throws Exception;

    /**
     *
     * @param type
     * @param prefix
     * @param count
     * @return
     * @throws Exception
     */
    public List<String> getAutonameListByType(String type, String prefix, int count) throws Exception;

    /**
     *
     * @param type
     * @param prefix
     * @param affix
     * @param count
     * @return
     * @throws Exception
     */
    public List<String> getAutonameListByType(String type, String prefix, String affix, int count) throws Exception;

    /**
     * ddd dfasdf adsfaseads
     * 
     * @param type
     * @param prefix
     * @param affix
     * @param suffix
     * @param count
     * @return teste
     * @throws Exception
     */
    public List<String> getAutonameListByType(String type, String prefix, String affix, String suffix, int count) throws Exception;
}
