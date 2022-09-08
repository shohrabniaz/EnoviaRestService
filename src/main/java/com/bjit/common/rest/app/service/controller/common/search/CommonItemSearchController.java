package com.bjit.common.rest.app.service.controller.common.search;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.controller.GTS.TranslationController;
import com.bjit.common.rest.app.service.controller.common.search.processor.DsFtsItemSearchProcessor;
import com.bjit.common.rest.app.service.controller.common.search.processor.PDMItemSearchProcessor;
import com.bjit.common.rest.app.service.controller.common.search.validator.CommonSearchValidator;
import com.bjit.common.rest.app.service.model.common.CommonItemSearchBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchDetailsResponseBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchErrorBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchRequestBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchResponseBean;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.search.DsFtsSearchService;
import com.bjit.common.rest.app.service.search.FtsSearchService;
import com.bjit.common.rest.app.service.search.SearchService;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.jsonOutput.Items;
import com.bjit.mapper.mapproject.util.Constants;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.db.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author BJIT
 */
@RestController
public class CommonItemSearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private DsFtsSearchService ftsSearchService;

    @Autowired
    private FtsSearchService ftsService;

    Context context;
    private static final org.apache.log4j.Logger COMMON_ITEM_SEARCH_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(TranslationController.class);

    @ResponseBody
    @PostMapping(value = "/common-search/item/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> commonItemSearch(HttpServletRequest httpRequest, HttpServletResponse response, @RequestBody final CommonItemSearchBean commonItemSearchBean) {

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        try {
            String user = httpRequest.getHeader("user");
            String pass = httpRequest.getHeader("pass");
            String host = PropertyReader.getProperty("matrix.context.cas.connection.host");
            if (NullOrEmptyChecker.isNullOrEmpty(user) || NullOrEmptyChecker.isNullOrEmpty(pass)) {
                try {
                    CreateContext createContext = new CreateContext();
                    context = createContext.getAdminContext();
                    if (!context.isConnected()) {
                        throw new Exception(Constants.CONTEXT_EXCEPTION);
                    }
                } catch (Exception exp) {
                    return new ResponseEntity<>(Constants.CONTEXT_EXCEPTION, HttpStatus.NOT_ACCEPTABLE);
                }
            } else {
                try {
                    CreateContext createContext = new CreateContext();
                    context = createContext.createCasContext(user, pass, host);
                    if (!context.isConnected()) {
                        throw new Exception(Constants.CONTEXT_EXCEPTION);
                    }
                } catch (Exception exp) {
                    return new ResponseEntity<>(Constants.CONTEXT_EXCEPTION, HttpStatus.NOT_ACCEPTABLE);
                }
            }
            Items responseForSearch = searchService.getItems(context, commonItemSearchBean);
            if (responseForSearch.getItems().isEmpty()) {
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(Constants.TYPE_NAME_BE_NULL_EXCEPTION).setStatus(Status.FAILED).buildResponse();
                COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.error(buildResponse);
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
            responseBuilder.setData(responseForSearch);
            buildResponse = responseBuilder.setStatus(Status.OK).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (Exception exp) {
            buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(Constants.INVALID_ATTRIBUTE_MESSAGE).setStatus(Status.FAILED).buildResponse();
            COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.error(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            if (context != null) {
                context.close();
                context = null;
            }
        }
    }

    @RequestMapping(value = "/common-search/item/search", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> commonItemSearchWithTNR(HttpServletRequest httpRequest,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "revision", required = false) String rev
    ) {

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        try {
            String user = httpRequest.getHeader("user");
            String pass = httpRequest.getHeader("pass");
            if (NullOrEmptyChecker.isNullOrEmpty(user) || NullOrEmptyChecker.isNullOrEmpty(pass)) {
                try {
                    CreateContext createContext = new CreateContext();
                    context = createContext.getAdminContext();
                    if (!context.isConnected()) {
                        throw new Exception(Constants.CONTEXT_EXCEPTION);
                    }
                } catch (Exception exp) {
                    return new ResponseEntity<>(Constants.CONTEXT_EXCEPTION, HttpStatus.NOT_ACCEPTABLE);
                }
            } else {
                try {
                    CreateContext createContext = new CreateContext();
                    context = createContext.getContext(user, pass);
                    if (!context.isConnected()) {
                        throw new Exception(Constants.CONTEXT_EXCEPTION);
                    }
                } catch (Exception exp) {
                    return new ResponseEntity<>(Constants.CONTEXT_EXCEPTION, HttpStatus.NOT_ACCEPTABLE);
                }
            }

            CommonItemSearchBean commonItemSearchBean = new CommonItemSearchBean(type, name, rev);

            Items responseForSearch = searchService.getItems(context, commonItemSearchBean);
            if (responseForSearch.getItems().isEmpty()) {
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(Constants.TYPE_NAME_BE_NULL_EXCEPTION).setStatus(Status.FAILED).buildResponse();
                COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.error(buildResponse);
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
            responseBuilder.setData(responseForSearch);
            buildResponse = responseBuilder.setStatus(Status.OK).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (Exception exp) {
            buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(Constants.INVALID_ATTRIBUTE_MESSAGE).setStatus(Status.FAILED).buildResponse();
            COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.error(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    @ResponseBody
    @PostMapping(path = "/valmet/enovia/api/v1/common-search/item/name/exist", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> itemSearchbyItemName(HttpServletRequest httpRequest, @RequestBody final ItemSearchRequestBean searchRequest) {
        Instant startServiceTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String response = "";
        Context searchContext = null;
        try {
            List<ItemSearchResponseBean> result = new ArrayList<>();
            List<ItemSearchErrorBean> errors = new ArrayList<>();
            searchContext = CommonSearchValidator.validateToken(httpRequest.getHeader("token"));
            COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.info("Executing Common search service========");
            PDMItemSearchProcessor searchProcessor = new PDMItemSearchProcessor();
            result = searchProcessor.searchItemsByName(searchContext, searchRequest, searchService, errors);

            if (!NullOrEmptyChecker.isNullOrEmpty(result) && !NullOrEmptyChecker.isNullOrEmpty(errors)) {
                response = responseBuilder.setData(result).addErrorMessage(errors).setStatus(Status.OK).buildResponse();
            } else if (!NullOrEmptyChecker.isNullOrEmpty(result) && NullOrEmptyChecker.isNullOrEmpty(errors)) {
                response = responseBuilder.setData(result).setStatus(Status.OK).buildResponse();
            } else {
                response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        } finally {

            if (searchContext != null) {
                searchContext.close();
                searchContext = null;
            }

            Instant endServiceTime = Instant.now();
            Duration timeTakenbyService = Duration.between(startServiceTime, endServiceTime);
            COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.info("Time taken by Search Service :" + timeTakenbyService.toMillis());
        }
    }

    @ResponseBody
    @PostMapping(path = "/valmet/enovia/api/v2/common-search/item/name/exist", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> itemSearchByItemName(HttpServletRequest httpRequest, @RequestBody final ItemSearchRequestBean searchRequest) {
        Instant startServiceTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String response;
        //final Context context = (Context) httpRequest.getAttribute("context");

        try {
            List<ItemSearchResponseBean> result = new ArrayList<>();
            List<ItemSearchErrorBean> errors = new ArrayList<>();
            COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.info(searchRequest.getFtsSearch());
            if (searchRequest.getFtsSearch().equals("true")) {
                COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.info("Executing DS FTS search service========");
                DsFtsItemSearchProcessor ftsSearchProcessor = new DsFtsItemSearchProcessor();
                result = ftsSearchProcessor.searchItemsByName(context, searchRequest, ftsSearchService, errors);
            } else {
                final Context context = CommonSearchValidator.validateToken(httpRequest.getHeader("token"));
                COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.info("Executing Common search service========");
                PDMItemSearchProcessor searchProcessor = new PDMItemSearchProcessor();
                result = searchProcessor.searchByName(context, searchRequest, searchService, errors);
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(result) && !NullOrEmptyChecker.isNullOrEmpty(errors)) {
                response = responseBuilder.setData(result).addErrorMessage(errors).setStatus(Status.OK).buildResponse();
            } else if (!NullOrEmptyChecker.isNullOrEmpty(result) && NullOrEmptyChecker.isNullOrEmpty(errors)) {
                response = responseBuilder.setData(result).setStatus(Status.OK).buildResponse();
            } else {
                response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            if (context != null) {
                context.close();
            }
            Instant endServiceTime = Instant.now();
            Duration timeTakenbyService = Duration.between(startServiceTime, endServiceTime);
            COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.info("Time taken by Search Service :" + timeTakenbyService.toMillis());
        }
    }

    @ResponseBody
    @PostMapping(path = "/valmet/enovia/api/v1/common-search/item/search/result", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> itemSearchDetailsByItemName(HttpServletRequest httpRequest, @RequestBody final ItemSearchRequestBean searchRequest) {
        Instant startServiceTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String response;
        final Context context = (Context) httpRequest.getAttribute("context");

        try {
            List<ItemSearchDetailsResponseBean> result = new ArrayList<>();
            List<ItemSearchErrorBean> errors = new ArrayList<>();

            COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.info("Executing DS FTS search details service========");
            DsFtsItemSearchProcessor ftsSearchProcessor = new DsFtsItemSearchProcessor();
            result = ftsSearchProcessor.getItemDetailsByName(context, searchRequest, ftsSearchService, errors);

            if (!NullOrEmptyChecker.isNullOrEmpty(result) && !NullOrEmptyChecker.isNullOrEmpty(errors)) {
                response = responseBuilder.setData(result).addErrorMessage(errors).setStatus(Status.OK).buildResponse();
            } else if (!NullOrEmptyChecker.isNullOrEmpty(result) && NullOrEmptyChecker.isNullOrEmpty(errors)) {
                response = responseBuilder.setData(result).setStatus(Status.OK).buildResponse();
            } else {
                response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            if (context != null) {
                context.close();
            }
            Instant endServiceTime = Instant.now();
            Duration timeTakenbyService = Duration.between(startServiceTime, endServiceTime);
            COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.info("Time taken by Search Service :" + timeTakenbyService.toMillis());
        }
    }

    @ResponseBody
    @PostMapping(path = "/valmet/enovia/api/v1/common-search/item/ftssearch/result", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> itemFTSSearchDetailsByItemName(HttpServletRequest httpRequest, @RequestBody final ItemSearchRequestBean searchRequest) {
        Instant startServiceTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String response;
        final Context context = (Context) httpRequest.getAttribute("context");

        try {
            List<ItemSearchDetailsResponseBean> result = new ArrayList<>();
            List<ItemSearchErrorBean> errors = new ArrayList<>();

            COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.info("Executing DS FTS search details service========");
            DsFtsItemSearchProcessor ftsSearchProcessor = new DsFtsItemSearchProcessor();

            result = ftsSearchProcessor.getFTSItemDetailsByName(context, searchRequest, ftsService, errors);

            if (!NullOrEmptyChecker.isNullOrEmpty(result) && !NullOrEmptyChecker.isNullOrEmpty(errors)) {
                response = responseBuilder.setData(result).addErrorMessage(errors).setStatus(Status.OK).buildResponse();
            } else if (!NullOrEmptyChecker.isNullOrEmpty(result) && NullOrEmptyChecker.isNullOrEmpty(errors)) {
                response = responseBuilder.setData(result).setStatus(Status.OK).buildResponse();
            } else {
                response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            if (context != null) {
                context.close();
            }
            Instant endServiceTime = Instant.now();
            Duration timeTakenbyService = Duration.between(startServiceTime, endServiceTime);
            COMMON_ITEM_SEARCH_CONTROLLER_LOGGER.info("Time taken by Search Service :" + timeTakenbyService.toMillis());
        }
    }
}