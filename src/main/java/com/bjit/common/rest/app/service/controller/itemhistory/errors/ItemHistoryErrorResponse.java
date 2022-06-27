package com.bjit.common.rest.app.service.controller.itemhistory.errors;


import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @author Touhidul Islam
 */
public interface ItemHistoryErrorResponse {

    public abstract ResponseEntity getResponse(List<String> messages);
}
