/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.validator;

import com.bjit.ewc18x.model.ExpandObjectForm;
import com.bjit.ewc18x.model.UpdateObjectForm;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

/**
 *
 * @author Kayum-603
 */
public class CustomValidator {

    private static final Logger LOGGER = Logger.getLogger(CustomValidator.class);

    public void validateData(Object targetObj, Errors errors, HttpSession httpSession) {
        LOGGER.debug("At Custome validator class");
        if (targetObj instanceof ExpandObjectForm) {
            ExpandObjectForm expandObjectForm = (ExpandObjectForm) targetObj;

            //if (!StringUtils.hasText(expandObjectForm.getPassword()) && ( httpSession.getAttribute("securityContext") == null || httpSession.getAttribute("context") == null)) {
            if (!StringUtils.hasText(expandObjectForm.getPassword()) || (httpSession.getAttribute("context") == null)) {
                errors.rejectValue("password", "NotEmpty.expandObjectForm.password");
            }
            String type = expandObjectForm.getType();
            if (!StringUtils.hasText(type)) {
                errors.rejectValue("type", "NotEmpty.expandObjectForm.type");
            }
            if (!StringUtils.hasText(expandObjectForm.getName())) {
                errors.rejectValue("name", "NotEmpty.expandObjectForm.name");
            }
            if (expandObjectForm.getSelectedItem() == null || expandObjectForm.getSelectedItem().isEmpty()) {
                LOGGER.debug("SelectedItem is empty");
                errors.rejectValue("selectedItem", "NotEmpty.expandObjectForm.selectedItem");
            }
            if (expandObjectForm.getSelectedTypeList() == null || expandObjectForm.getSelectedTypeList().isEmpty()) {
                System.out.println();
                LOGGER.debug("SelectedTypeList is empty");
                errors.rejectValue("selectedTypeList", "NotEmpty.expandObjectForm.selectedTypeList");
            }
            if (expandObjectForm.getRecursionLevel() == null) {
                LOGGER.debug("RecursionLevel is empty");
                errors.rejectValue("recursionLevel", "NotNull.expandObjectForm.recursionLevel");
            }
        } else if (targetObj instanceof UpdateObjectForm) {
            UpdateObjectForm updateObjectForm = (UpdateObjectForm) targetObj;
            LOGGER.debug("user name: "+updateObjectForm.getUserID());
            LOGGER.debug("user pass: "+updateObjectForm.getPassword());
            if (!StringUtils.hasText(updateObjectForm.getPassword()) && (httpSession.getAttribute("securityContext") == null || httpSession.getAttribute("context") == null)) {
                errors.rejectValue("password", "NotEmpty.expandObjectForm.password");
            }
        }

        // do "complex" validation here
    }

    /**
     * null or empty checker for String
     *
     * @param value
     * @return boolean
     */
    public boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
