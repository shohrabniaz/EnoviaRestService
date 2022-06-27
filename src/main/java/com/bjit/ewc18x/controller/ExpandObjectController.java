/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.controller;

import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Kayum-603
 */
@Controller
public class ExpandObjectController {
    private static final Logger logger = Logger.getLogger(ExpandObjectController.class);
    @RequestMapping(value = "expand-object", method = RequestMethod.GET)
    public String plmKeyForm(HttpSession httpSession, Model model) {
        System.out.println("Expand obejct controller has been called.");
        logger.info("At expand object controller");
        return "expand-object";
    }
}
