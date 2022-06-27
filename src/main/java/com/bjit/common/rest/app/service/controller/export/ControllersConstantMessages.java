/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export;

/**
 *
 * @author BJIT
 */
public enum ControllersConstantMessages {
    STARTING_TRANSACTION {
        @Override
        public String toString() {
            return "Starting Transaction";
        }
    },
    STARTED_TRANSACTION {
        @Override
        public String toString() {
            return "Transaction Started";
        }
    },
    COMMITTING_TRANSACTION {
        @Override
        public String toString() {
            return "Committing Transaction";
        }
    },
    COMMITTED_TRANSACTION {
        @Override
        public String toString() {
            return "Transaction Committed";
        }
    },
    ABORTING_TRANSACTION {
        @Override
        public String toString() {
            return "Aborting Transaction";
        }
    },
    ABORTED_TRANSACTION {
        @Override
        public String toString() {
            return "Transaction Aborted";
        }
    }
}
