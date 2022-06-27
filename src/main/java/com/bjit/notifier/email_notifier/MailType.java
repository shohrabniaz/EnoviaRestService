/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.notifier.email_notifier;

/**
 *
 * @author BJIT
 */
public enum MailType {
    Html {
    @Override
    public String toString() {
      return "text/html; charset=utf-8";
    }
  },
    
    Plain {
    @Override
    public String toString() {
      return "text/plain; charset=utf-8";
    }
  }
}
