/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.notifier.email_notifier.email;

import com.bjit.notifier.email_notifier.MailType;

/**
 *
 * @author BJIT
 * @param <T>
 */
public class HtmlEmail<T> extends Email<T> {
     @Override
    public <T> void data(T emailData) {
        data(emailData, MailType.Html);
    }
}
