/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.service;

import javax.servlet.http.HttpSession;
import matrix.db.Context;
import org.springframework.stereotype.Service;

/**
 *
 * @author Administrator
 */
@Service
public class CustomAuthenticationServiceImpl implements CustomAuthenticationService {

    @Override
    public boolean isContextExist(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String plmKey = (String) session.getAttribute("plmKey");
        String securityContext = (String) session.getAttribute("securityContext");
        Context context = (Context) session.getAttribute("context");
        System.out.println("context : " + context);
        if (!(context == null) && !(username == null) && !(plmKey == null) && !(securityContext == null)) {
            System.out.println("authenticated");
            return true;
        }
        System.out.println("unauthenticated");
        return false;
    }
    
}
