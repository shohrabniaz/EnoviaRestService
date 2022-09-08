/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.notifier;

/**
 *
 * @author BJIT
 * @param <T>
 */
public interface INotifier<T> {

    <T> void data(T data);
    <T, K extends Enum> void data(T data, K notifyingType);
    Boolean send() throws Exception;
}
