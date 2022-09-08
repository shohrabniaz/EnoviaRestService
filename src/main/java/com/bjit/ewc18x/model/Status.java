/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.model;

/**
 *
 * @author Ashikur Rahman
 */
public enum Status {
    OK("OK"), FAILED("FAILED");
         
        private String status;
        
        Status(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }    
}
