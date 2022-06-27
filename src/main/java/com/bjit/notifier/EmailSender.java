/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.notifier;

import com.bjit.notifier.email_notifier.EmailData;
import com.bjit.notifier.email_notifier.email.Email;
import com.bjit.notifier.email_notifier.email.HtmlEmail;
import org.apache.log4j.Logger;


/**
 *
 * @author BJIT
 */
public class EmailSender {
    private static final Logger Log = Logger.getLogger(EmailSender.class);

    public void testMail() {
        try { 
            /*emailData.setTo("omourfrq@gmail.com, sumon.faruq@bjitgroup.com");
            emailData.setCc("omourfrq@gmail.com, sumon.faruq@bjitgroup.com");
            emailData.setBcc("omourfrq@gmail.com, sumon.faruq@bjitgroup.com");
            emailData.setAttachment("C:\\Users\\BJIT\\Documents\\NetBeansProjects\\Notifier\\src\\main\\java\\com\\bjit\\notifier\\Email.java, C:\\Users\\BJIT\\Documents\\NetBeansProjects\\Notifier\\src\\main\\java\\com\\bjit\\notifier\\TextEmail.java");
            
            
            
            emailData.setData("Text Mail");
            emailData.setSubject("Text Mail Testing");
            TextEmail textEmail = new TextEmail(emailData);
            textEmail.send();
            
            emailData.setData("<h1>HTML Mail</h1>");
            emailData.setSubject("HTML Mail Testing");
            HtmlMail htmlMail = new HtmlMail(emailData);
            htmlMail.send();*/
            
            
            EmailData emailData = new EmailData();
            
            emailData.setFrom("test-admin.plm@valmet.com");
            
            emailData.setTo("omourfrq@gmail.com", "sumon.faruq@bjitgroup.com");
            emailData.setCc("omourfrq@gmail.com, sumon.faruq@bjitgroup.com");
            emailData.setBcc("omourfrq@gmail.com", "sumon.faruq@bjitgroup.com");
            emailData.setAttachment("/data/apps/plm/deplopment/apache-tomcat-9.0.1/tmpFiles/mail-test-file1.txt", "/data/apps/plm/deplopment/apache-tomcat-9.0.1/tmpFiles/mail-test-file2.txt");
            
            
            emailData.setData("Text Mail. <ol><li>One</li><li>Two</li><li>Three</li><li>Four</li></ol>");
            emailData.setSubject("Text Mail Testing");
            
            INotifier<EmailData> email = new Email();
            email.data(emailData);
            email.send();
            
            
            
            emailData.setData("<h1>HTML Mail Testing</h1><ol><li>One</li><li>Two</li><li>Three</li><li>Four</li></ol>");
            emailData.setSubject("HTML Mail Testing");
            
            INotifier<EmailData> htmlMail = new HtmlEmail();
            htmlMail.data(emailData);
            htmlMail.send();
            
        } catch (Exception ex) {
            Log.error("Mail could not be sent.");
            Log.error(ex.getMessage());
        } 
    }
    
    public void sendEmail(String data){
        try {
            
            EmailData emailData = new EmailData();
            
            emailData.setFrom("test-admin.plm@valmet.com");
            
            //emailData.setTo("omourfrq@gmail.com", "sumon.faruq@bjitgroup.com");
            emailData.setTo("tahmid.ali@bjitgroup.com");
//            emailData.setCc("omourfrq@gmail.com, sumon.faruq@bjitgroup.com");
//            emailData.setBcc("omourfrq@gmail.com", "sumon.faruq@bjitgroup.com");
//            emailData.setAttachment("/data/apps/plm/deplopment/apache-tomcat-9.0.1/tmpFiles/mail-test-file1.txt", "/data/apps/plm/deplopment/apache-tomcat-9.0.1/tmpFiles/mail-test-file2.txt");
            
            
            emailData.setData("Text Mail. <ol><li>One</li><li>Two</li><li>Three</li><li>Four</li></ol> Report Link: "+data);
            emailData.setSubject("Text Mail Testing");
            
            INotifier<EmailData> email = new Email();
            email.data(emailData);
            email.send();
            Log.debug("Mail sent successfully.");
            
        } catch (Exception ex) {
            Log.error("Mail could not be sent.");
            Log.error(ex.getMessage());
        }
    }
}
