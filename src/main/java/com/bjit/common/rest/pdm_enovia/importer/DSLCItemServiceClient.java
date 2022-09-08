package com.bjit.common.rest.pdm_enovia.importer;

/**
 * @author Tohidul-571
 *
 */
public interface DSLCItemServiceClient {

    public abstract <T extends Object> T call() throws Exception;

}
