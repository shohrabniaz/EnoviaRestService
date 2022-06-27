package com.bjit.common.rest.app.service.mail;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TemplateParser {

    private static final Logger MAIL_PARSER_LOGGER = Logger.getLogger(TemplateParser.class);
    private String templateData;

    public String parse(String templateData, Object mailModel) {
        Method[] declaredMethods = mailModel.getClass().getDeclaredMethods();
        this.templateData = templateData;

        List<String> findThePlaceHolders;
        do {
            prepareMailMessageBody(declaredMethods, mailModel);
            findThePlaceHolders = findThePlaceHolders();
        } while (!NullOrEmptyChecker.isNullOrEmpty(findThePlaceHolders));

        return this.templateData;
    }

    private void prepareMailMessageBody(Method[] declaredMethods, Object mailModel) {
        Arrays.asList(declaredMethods).stream().filter((Method method) -> isGetter(method)).forEach((Method method) -> {
            try {
                String methodName = method.getName();
                String templatePlaceHolder = methodName.substring(3);
                templatePlaceHolder = makeTemplateString(templatePlaceHolder);

                String templatePlaceHolderValue = method.invoke(mailModel).toString();
                this.templateData = this.templateData.replace(templatePlaceHolder, templatePlaceHolderValue);
            } catch (IllegalAccessException | InvocationTargetException exp) {
                MAIL_PARSER_LOGGER.error(exp);
            }
        });
    }

    private String convertToUnderscored(String value) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return value.replaceAll(regex, replacement);
    }

    private List<String> findThePlaceHolders() {
        Pattern pattern = Pattern.compile("<%=(.*?)%>");
        Matcher matcher = pattern.matcher(this.templateData);
        List<String> placeHolderList = null;
        while (matcher.find()) {
            String data = matcher.group(1);
            if (!NullOrEmptyChecker.isNullOrEmpty(data)) {
                if (NullOrEmptyChecker.isNullOrEmpty(placeHolderList)) {
                    placeHolderList = new ArrayList<>();
                }
                placeHolderList.add(data);
            }
        }
        return placeHolderList;
    }

    private String makeTemplateString(String placeHolderName) {
        placeHolderName = "<%=" + convertToUnderscored(placeHolderName) + "%>";

        return placeHolderName.toUpperCase();
    }

    private boolean isGetter(Method method) {
        return (method.getName().startsWith("get") || method.getName().startsWith("is"))
                && method.getParameterCount() == 0 && !method.getReturnType().equals(void.class);
    }

    private boolean isSetter(Method method) {
        // identify set methods
        return method.getName().startsWith("set") && method.getParameterCount() == 1
                && method.getReturnType().equals(void.class);
    }

//    public static boolean isGetter(Method method){
//        if(!method.getName().startsWith("get"))      return false;
//        if(method.getParameterTypes().length != 0)   return false;
//        if(void.class.equals(method.getReturnType()) return false;
//        return true;
//    }
//
//    public static boolean isSetter(Method method){
//        if(!method.getName().startsWith("set")) return false;
//        if(method.getParameterTypes().length != 1) return false;
//        return true;
//    }
}
