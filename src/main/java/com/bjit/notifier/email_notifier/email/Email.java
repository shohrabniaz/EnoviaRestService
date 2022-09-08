/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.notifier.email_notifier.email;

import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.notifier.email_notifier.EmailData;
import com.bjit.notifier.utilities.ObjectUtility;
import com.bjit.notifier.INotifier;
import com.bjit.notifier.email_notifier.MailType;
import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 * @param <T>
 */
public class Email<T> implements INotifier<T> {

    private String mailHost;
    private String mailPort;
    private String mailProtocol;
    private String mailType;
    private Boolean enableTls;
    private Boolean enableSmtpAuth;
    private String mailUserId;
    private String mailPassword;
    private EmailData emailData;

    private static final Logger EMAIL_LOGGER = Logger.getLogger(Email.class);

    protected String getMailHost() {
        EMAIL_LOGGER.debug("Getting Mail Server: " + mailHost);
        return mailHost;
    }

    protected void setMailHost(String mailHost) {
        EMAIL_LOGGER.debug("Setting Mail Server: " + mailHost);
        this.mailHost = mailHost;
    }

    protected String getMailPort() {
        EMAIL_LOGGER.debug("Getting Mail Server Port: " + mailPort);
        return mailPort;
    }

    protected void setMailPort(String mailPort) {
        EMAIL_LOGGER.debug("Setting Mail Server Port: " + mailPort);
        this.mailPort = mailPort;
    }

    public String getMailProtocol() {
        return mailProtocol;
    }

    public void setMailProtocol(String mailProtocol) {
        this.mailProtocol = mailProtocol;
    }

    protected String getMailType() {
        EMAIL_LOGGER.debug("Getting Mail Type: " + mailType);
        return mailType;
    }

    protected void setMailType(String mailType) {
        EMAIL_LOGGER.debug("Setting Mail Type: " + mailType);
        this.mailType = mailType;
    }

    protected Boolean getEnableTls() {
        EMAIL_LOGGER.debug("Getting TLS : " + enableTls.toString());
        return enableTls;
    }

    protected void setEnableTls(Boolean enableTls) {
        EMAIL_LOGGER.debug("Enabling TLS : " + enableTls.toString());
        this.enableTls = enableTls;
    }

    protected Boolean getEnableSmtpAuth() {
        return enableSmtpAuth;
    }

    protected void setEnableSmtpAuth(Boolean enableSmtpAuth) {
        this.enableSmtpAuth = enableSmtpAuth;
    }

    protected String getMailUserId() {
        EMAIL_LOGGER.debug("Getting Mail User Id : " + mailUserId);
        return mailUserId;
    }

    protected void setMailUserId(String mailUserId) {
        EMAIL_LOGGER.debug("Setting Mail User Id : " + mailUserId);
        this.mailUserId = mailUserId;
    }

    protected String getMailPassword() {
        EMAIL_LOGGER.debug("Getting Mail Password : *************");
        return mailPassword;
    }

    protected void setMailPassword(String mailPassword) {
        EMAIL_LOGGER.debug("Getting Mail Password : *************");
        this.mailPassword = mailPassword;
    }

    protected EmailData getEmailData() {
        return emailData;
    }

    protected void setEmailData(EmailData emailData) {
        this.emailData = emailData;
    }

    protected void validateEmailData(EmailData emailData) {
        //Check mail user id
        if (ObjectUtility.isNullOrEmpty(emailData.getFrom())) {
            String errorMessage = "Mail User Id Not Found";
            EMAIL_LOGGER.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        //Check receiver info
        List<String> receipentList = emailData.getTo();
        if (receipentList != null && !receipentList.isEmpty()) {
            try {

                EMAIL_LOGGER.info("Validating Recipients Email Addresses");
                receipentList.forEach(this::isValidMailAddress);
            } catch (Exception exp) {
                String errorMessage = exp.getMessage() + " found in the receipent (To) list";
                EMAIL_LOGGER.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } else {
            String errorMessage = "There is no receiver of this mail";
            EMAIL_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        //Check cc info
        if (!ObjectUtility.isNullOrEmpty(emailData.getCc())) {
            try {
                EMAIL_LOGGER.info("Validating Carbon Copy Recipients Email Addresses");
                emailData.getCc().forEach(this::isValidMailAddress);
            } catch (Exception exp) {
                String errorMessage = exp.getMessage() + " found in the carbon copy (Cc) list";
                EMAIL_LOGGER.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        }

        //Check bcc info
        if (!ObjectUtility.isNullOrEmpty(emailData.getBcc())) {
            try {
                EMAIL_LOGGER.info("Validating Blind Carbon Copy Recipients Email Addresses");
                emailData.getBcc().forEach(this::isValidMailAddress);
            } catch (Exception exp) {
                String errorMessage = exp.getMessage() + " found in the blind carbon copy (Bcc) list";
                EMAIL_LOGGER.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        }

        //Check subject
        if (ObjectUtility.isNullOrEmpty(emailData.getSubject())) {
            String errorMessage = "Subject not found in for this mail";
            EMAIL_LOGGER.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        //Check valid file
        if (!ObjectUtility.isNullOrEmpty(emailData.getAttachment())) {
            try {
                emailData.getAttachment().forEach(this::isValidFile);
            } catch (Exception exp) {
                EMAIL_LOGGER.error(exp.getMessage());
                throw new RuntimeException(exp.getMessage());
            }
        }

        setEmailData(emailData);
    }

    private void isValidFile(String fileName) {
        File attachment = new File(fileName);
        
        if (attachment.isDirectory()) {
            String errorMessage = attachment + " is not a valid file for attachment.";
            EMAIL_LOGGER.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        
        if (!attachment.exists()) {
            String errorMessage = attachment + " is not a valid file for attachment.";
            EMAIL_LOGGER.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    public boolean isValidMailAddress(String email) {
        EMAIL_LOGGER.debug("Email Address : " + email);
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";
        Boolean result;

        Pattern pat = Pattern.compile(emailRegex);
        if (ObjectUtility.isNullOrEmpty(email)) {
            String errorMessage = "Email Address Is Null or Empty";
            EMAIL_LOGGER.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        result = pat.matcher(email).matches();
        if (result) {
            return true;
        } else {
            String errorMessage = "Invalid email address '" + email + "'";
            EMAIL_LOGGER.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private MimeMessage addAttachments(MimeMessage message) throws MessagingException {
        Multipart multipart = new MimeMultipart();

        BodyPart messageTextPart = new MimeBodyPart();
        messageTextPart.setContent(this.getEmailData().getData(), this.getMailType());
        multipart.addBodyPart(messageTextPart);

        this.getEmailData().getAttachment().forEach((String filePath) -> {
            try {
                BodyPart messageFilePart = new MimeBodyPart();

                File file = new File(filePath);
                String filename = file.getName();

                DataSource source = new FileDataSource(filePath);
                messageFilePart.setDataHandler(new DataHandler(source));
                messageFilePart.setFileName(filename);
                multipart.addBodyPart(messageFilePart);
            } catch (MessagingException exp) {
                EMAIL_LOGGER.error(exp.getMessage());
                throw new RuntimeException(exp);
            }
        });

        message.setContent(multipart);
        return message;
    }

    private MimeMessage addBlindCarbonCopy(MimeMessage message) {
        List<String> bccList = this.getEmailData().getBcc();
        if (ObjectUtility.isNullOrEmpty(bccList)) {
            return message;
        }

        bccList.forEach(blindCarbonCopy -> {
            try {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(blindCarbonCopy));
            } catch (MessagingException exp) {
                EMAIL_LOGGER.error(exp.getMessage());
                throw new RuntimeException(exp);
            }
        });

        return message;
    }

    private MimeMessage addCarbonCopy(MimeMessage message) {
        List<String> ccList = this.getEmailData().getCc();
        if (ObjectUtility.isNullOrEmpty(ccList)) {
            return message;
        }

        ccList.forEach(carbonCopy -> {
            try {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(carbonCopy));
            } catch (MessagingException exp) {
                EMAIL_LOGGER.error(exp.getMessage());
                throw new RuntimeException(exp);
            }
        });

        return message;
    }

    private MimeMessage addRecipients(MimeMessage message) {
        this.getEmailData().getTo().forEach(receipent -> {
            try {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(receipent));
            } catch (MessagingException exp) {
                EMAIL_LOGGER.error(exp.getMessage());
                throw new RuntimeException(exp);
            }
        });

        return message;
    }

    private Properties setMailProperties() {
        Properties properties = System.getProperties();
//        properties.setProperty("mail.smtp.auth", Boolean.toString(this.getEnableSmtpAuth()));
//        properties.setProperty("mail.smtp.starttls.enable", Boolean.toString(this.getEnableTls()));
//        properties.setProperty("mail.smtp.host", this.getMailHost());
//        properties.setProperty("mail.smtp.port", this.getMailPort());
//        properties.setProperty("mail.smtp.userId", this.getFrom());
//        properties.setProperty("mail.smtp.user.password", this.getMailPassword());

        this.setMailHost(PropertyReader.getProperty("mail.host"));
        //this.setMailPort(PropertyReader.getProperty("mail.port"));
        this.setMailProtocol(PropertyReader.getProperty("mail.transport.protocol"));

        properties.setProperty("mail.host", this.getMailHost());
        properties.setProperty("mail.transport.protocol", this.getMailProtocol());
        properties.setProperty("mail.from", this.getEmailData().getFrom());

        return properties;
    }

    @Override
    public <T> void data(T emailData) {
        data(emailData, MailType.Plain);
    }

    @Override
    public <T, K extends Enum> void data(T emailData, K mailType) {
        this.setEmailData((EmailData) emailData);
        this.setMailType(mailType.toString());
    }

    @Override
    public Boolean send() throws Exception {
        try {
            validateEmailData(this.getEmailData());

            Properties properties = setMailProperties();
//            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
//                @Override
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication(properties.getProperty("mail.smtp.userId"), properties.getProperty("mail.smtp.user.password"));
//                }
//            });

            Session session = Session.getDefaultInstance(properties);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(properties.getProperty("mail.from")));
            message = addRecipients(message);
            message = addCarbonCopy(message);
            message = addBlindCarbonCopy(message);
            message.setSubject(this.getEmailData().getSubject());

            if (ObjectUtility.isNullOrEmpty(this.getEmailData().getAttachment())) {
                message.setContent(this.getEmailData().getData(), this.getMailType());
            } else {
                message = addAttachments(message);
            }

            Transport.send(message);
            return Boolean.TRUE;

        } catch (MessagingException exp) {
            EMAIL_LOGGER.error(exp.getMessage());
            throw new Exception(exp);
        } catch (Exception exp) {
            EMAIL_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }
}
