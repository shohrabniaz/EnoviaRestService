/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.autoNumber;

import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import lombok.extern.log4j.Log4j;
import matrix.db.Context;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Omour Faruq
 */
@Log4j
@RestController
@RequestMapping(path = "/autonumber")
public class NumberGeneratorController {

//    @Autowired
//    NumberGeneratorResponse numberGeneratorResponse;

    @Autowired
    BeanFactory beanFactory;

    @Autowired NumberGeneratorProcessor numberGeneratorProcessor;
    @Autowired CommonUtilities commonUtilities;

    @GetMapping("/generate/{type}/{format}/{objectCount}")
//    @GetMapping("/generate")
    @ResponseBody
//    public NumberGeneratorResponse generateNumber(HttpServletRequest httpRequest, @RequestParam String type, @RequestParam String format, @RequestParam Integer objectCount) throws Exception {
    public NumberGeneratorResponse generateNumber(HttpServletRequest httpRequest, @PathVariable("type") String type, @PathVariable("format") String format, @PathVariable("objectCount") Integer objectCount) throws Exception {
//        Context context = (Context) httpRequest.getAttribute("context");
        Context context = commonUtilities.generateContext();
        NumberGenerationModel numberGenerationModel = beanFactory.getBean(NumberGenerationModel.class);

        numberGenerationModel.setContext(context);
        numberGenerationModel.setType(type);
        numberGenerationModel.setObjectCount(objectCount);
        numberGenerationModel.setFormat(format);

        NumberGeneratorResponse bean = numberGeneratorProcessor.generateAutonumber(numberGenerationModel);

        return bean;
    }


}
