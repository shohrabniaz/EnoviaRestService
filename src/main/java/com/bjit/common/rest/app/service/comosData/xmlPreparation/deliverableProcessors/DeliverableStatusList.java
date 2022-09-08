package com.bjit.common.rest.app.service.comosData.xmlPreparation.deliverableProcessors;

import com.bjit.common.code.utility.http.client.CSRFTokenGenerationService;
import com.bjit.common.code.utility.http.client.model.CSRFToken;
import com.bjit.common.code.utility.http.v2.HttpClientService;
import com.bjit.common.code.utility.http.v2.impl.SimpleHttpClientService;
import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.common.rest.app.service.comosData.exceptions.SubtasksStructureException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.IComosData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SessionModel;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assigneeService.*;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.deliStatusList.DeliStatusListRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.deliStatusList.DeliStatusListServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileReader;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.common.rest.app.service.utilities.IJSON;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.ewc18x.utils.PropertyReader;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Log4j
@Service
@RequestScope
@Qualifier("DeliverableStatusList")
public class DeliverableStatusList implements IStructurePreparation<List<TaskAssigneeRespondedData>, DeliStatusListServiceResponse, DeliStatusListRequestData> {
    @Autowired
    IJSON json;
    @Autowired
    @Qualifier("ComosFileReader")
    IFileReader fileReader;
    @Value("classpath:data/Task_Assignee.json")
    Resource resourceFile;
    @Autowired
    @Qualifier("DeliStatusListServiceConsumer")
    IComosData<DeliStatusListRequestData> deliStatusListServiceConsumer;
    @Autowired
    SessionModel sessionModel;
    @Autowired
    @Qualifier("CommonSearch")
    CommonSearch commonSearch;
    @Autowired
    BeanFactory beanFactory;
    Map<String, Map<String, String>> assigneesInformation = new HashMap<>();
    @Getter
    List<String> deliverableAndAssignee;
    

    Function<String, HashMap<String, String>> getObjectInformation = (String deliverableUID) -> {
        HashMap<String, String> whereClause = new HashMap<>();
        whereClause.put("attribute[PRJ_ComosActivityUID]", deliverableUID);
        try {
            List<HashMap<String, String>> deliverableIds = commonSearch.searchItem(sessionModel.getContext(),
                    new TNR("Task", "*", "*"),
                    whereClause,
                    List.of("type", "name", "revision", "id", "physicalId", "attribute[PRJ_ComosActivityUID]"));

            return Optional
                    .of(deliverableIds)
                    .filter(list -> !list.isEmpty())
                    .get()
                    .stream()
                    .findFirst()
                    .get();
        } catch (Exception e) {
            log.error(e);
            deliverableAndAssignee = Optional.ofNullable(deliverableAndAssignee).orElse(new ArrayList<>());
            deliverableAndAssignee.add(e.getMessage());
            return new HashMap<>();
//            throw new RuntimeException(e);
        }
    };
    BiFunction<Map<String, UserAndStatusList>, String, UserAndStatusList> getDeliIdInfo = (Map<String, UserAndStatusList> getAssigneeAnStatusList, String deliUid) -> {
        UserAndStatusList userAndStatusList = getAssigneeAnStatusList.get(deliUid);
        userAndStatusList.setRootItemInfo(getObjectInformation.apply(deliUid));
        return userAndStatusList;
    };
    Supplier<CSRFTokenGenerationService> getCsrfTokenService = () -> {
        try {
            ContextPasswordSecurity contextPassword = new ContextPasswordSecurity();
            String username = contextPassword.decryptPassword(PropertyReader.getProperty("context.name"));
            String password = contextPassword.decryptPassword(PropertyReader.getProperty("context.pass"));

            return new CSRFTokenGenerationService(
                    PropertyReader.getProperty("matrix.context.cas.connection.host.dslc"),
                    PropertyReader.getProperty("matrix.context.cas.connection.passport"), username, password);
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    };
    Function<CSRFToken, List<Header>> getHeaders = (CSRFToken token) -> {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader(token.getName(), token.getValue()));
        
        headers.add(new BasicHeader("SecurityContext",PropertyReader.getProperty("comos.assinee.security.context")));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        headers.add(new BasicHeader("Accept", "application/json"));
        return headers;
    };
    Function<CSRFTokenGenerationService, HttpClientService> getHttpClient = (CSRFTokenGenerationService service) -> {
        CookieStore cookieStore = service.getSimpleHttpClient().getCookieStore();
        HttpClientService client = new SimpleHttpClientService();
        client.setCookieStore(cookieStore);
        return client;
    };
    Function<String, Map<String, String>> getAssigneeInformation = (String assignee) -> {
        try {
            if (assigneesInformation.containsKey(assignee)) {
                return assigneesInformation.get(assignee);
            }

            List<HashMap<String, String>> assigneeInformationList = commonSearch.searchItem(sessionModel.getContext(),
                    new TNR("Person", assignee, "*"),
                    List.of("type", "name", "revision", "id", "physicalId", "attribute[Email Address]", "attribute[First Name]", "attribute[Last Name]"));

            HashMap<String, String> userInformation = Optional
                    .of(assigneeInformationList)
                    .filter(list -> !list.isEmpty())
                    .get()
                    .stream()
                    .findFirst()
                    .get();

            assigneesInformation.put(assignee, userInformation);

            return userInformation;
        } catch (Exception e) {
            log.error(e);
            deliverableAndAssignee = Optional.ofNullable(deliverableAndAssignee).orElse(new ArrayList<>());
            deliverableAndAssignee.add(e.getMessage());
            return new HashMap<>();
//            throw new RuntimeException(e);
        }
    };
    Function<DeliStatusListServiceResponse, Map<String, UserAndStatusList>> getAssigneeAnStatusList = (DeliStatusListServiceResponse serviceData) -> {
        return Optional.ofNullable(serviceData)
                .map(svcData -> svcData.getData())
                .stream().filter(data -> Optional.ofNullable(data).isPresent())
                .flatMap(List::stream)
                .collect(
                        Collectors.toMap(deliStatusServiceData -> deliStatusServiceData.getDeliUId(),
                                deliStatusServiceData -> {
                                    String assignee = deliStatusServiceData.getAssingnees();
                                    log.info("Assignee of '" + deliStatusServiceData.getDeliUId() + "' is: " + assignee);
                                    assignee = Optional.ofNullable(assignee).orElse("");
                                    return new UserAndStatusList(assignee, Optional.ofNullable(deliStatusServiceData.getStatusList()).orElse(new ArrayList<>()));
                                }
                        ));
    };
    Function<UserAndStatusList, DSAssigneeRequestModel> prepareDSServiceRequest = (userAndStatusList) -> {
        Map<String, String> rootItemInfo = userAndStatusList.getRootItemInfo();
//        Map<String, String> assigneeInformation = userAndStatusList.getAssigneeInformation();

        DSTaskAssigneeDataElements dsTaskAssigneeDataElements = beanFactory.getBean(DSTaskAssigneeDataElements.class);
        dsTaskAssigneeDataElements.setName(userAndStatusList.getAssignee());
//        dsTaskAssigneeDataElements.setFirstname(assigneeInformation.get("attribute[First Name]"));
//        dsTaskAssigneeDataElements.setLastname(assigneeInformation.get("attribute[Last Name]"));
//        dsTaskAssigneeDataElements.setFullname(assigneeInformation.get("attribute[First Name]") + " " + assigneeInformation.get("attribute[Last Name]"));
//        dsTaskAssigneeDataElements.setEmail(assigneeInformation.get("attribute[Email Address]"));

        DSAssigneeRequestData dsAssigneeRequestData = beanFactory.getBean(DSAssigneeRequestData.class);
//        dsAssigneeRequestData.setId(assigneeInformation.get("physicalid"));
//        dsAssigneeRequestData.setType(assigneeInformation.get("type"));

        dsAssigneeRequestData.setDataElements(dsTaskAssigneeDataElements);

        DSAssigneeRequestModel dsAssigneeRequestModel = beanFactory.getBean(DSAssigneeRequestModel.class);
        dsAssigneeRequestModel.setData(Optional.ofNullable(dsAssigneeRequestModel.getData()).orElse(new ArrayList<>()));
        dsAssigneeRequestModel.getData().add(dsAssigneeRequestData);

        return dsAssigneeRequestModel;
    };
    Function<UserAndStatusList, TaskAssigneeRespondedData> callTaskDSService = (userAndStatusList) -> {
        try {
            CSRFTokenGenerationService service = getCsrfTokenService.get();

            CSRFToken token = service.getCSRFToken();
            List<Header> headers = getHeaders.apply(token);
            HttpClientService client = getHttpClient.apply(service);

            String dsRequestURL = PropertyReader.getProperty("comos.task.assignee.service.url") + userAndStatusList.getRootItemInfo().get("physicalid") + "/assignees";

            JSON nullRemoverJson = new JSON(Boolean.FALSE);
            String dsRequestData = nullRemoverJson.serialize(prepareDSServiceRequest.apply(userAndStatusList));

            log.info("DS service url : " + dsRequestURL);
            log.info("DS service data : " + dsRequestData);

            HttpResponse response = client.doPostRequest(dsRequestURL, true, dsRequestData, headers);
            String responseStr = client.getResponseStr(response);
            log.info("DS response data : " + responseStr);
            DSAssigneeResponseModel deserialize = json.deserialize(responseStr, DSAssigneeResponseModel.class);

            TaskAssigneeRespondedData respondedModel = beanFactory.getBean(TaskAssigneeRespondedData.class);
            respondedModel.setRespondedModel(deserialize);
            respondedModel.setUserAndStatusList(userAndStatusList);

            return respondedModel;

        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    };

    @Override
    public List<TaskAssigneeRespondedData> prepareStructure(DeliStatusListRequestData requestData) throws IOException {
        DeliStatusListServiceResponse serviceData = getServiceData(requestData);

        Map<String, UserAndStatusList> getAssigneeAnStatusList = this.getAssigneeAnStatusList.apply(serviceData);
        System.out.println(getAssigneeAnStatusList);

        return Optional.of(getAssigneeAnStatusList)
                .map(Map::keySet)
                .stream()
                .flatMap(Set::stream)
                .filter(deliId -> {
                    String assigneeName = getAssigneeAnStatusList.get(deliId).getAssignee();
                    Boolean isAssigneeExists =  Optional
                            .of(assigneeName)
                            .map(String::trim)
                            .map(assignee -> !assignee.isEmpty())
                            .orElse(false);

                    if(!isAssigneeExists){
                        deliverableAndAssignee = Optional.ofNullable(deliverableAndAssignee).orElse(new ArrayList<>());
                        deliverableAndAssignee.add("Assignee of '" + deliId + "' item has not been found in the COMOS service response");
                    }
                    return isAssigneeExists;
                })
                .map(deliUid -> getDeliIdInfo.apply(getAssigneeAnStatusList, deliUid))
                .filter(userAndStatusList -> !userAndStatusList.getRootItemInfo().isEmpty())
//                .map(userAndStatusList -> {
//                    Map<String, String> assigneeInformation = getAssigneeInformation.apply(userAndStatusList.getAssignee());
//                    userAndStatusList.setAssigneeInformation(assigneeInformation);
//                    return userAndStatusList;
//                })
//                .filter(userAndStatusList -> !userAndStatusList.getAssigneeInformation().isEmpty())
                .peek(log::info)
                .map(callTaskDSService).collect(Collectors.toList());
    }

    @Override
    public DeliStatusListServiceResponse getServiceData(DeliStatusListRequestData requestData) throws IOException {
        Boolean readFromFile = Boolean.parseBoolean(PropertyReader.getProperty("comos.get.json.data.from.file"));
        String jsonData = readFromFile ? fileReader.readFile(resourceFile.getFile()) : deliStatusListServiceConsumer.getComosData(requestData);

        DeliStatusListServiceResponse comosResponseModel = getDeliverableStatusServiceResponse(jsonData);
        return comosResponseModel;
    }

    private DeliStatusListServiceResponse getDeliverableStatusServiceResponse(String jsonString) {
        try {
            DeliStatusListServiceResponse serviceResponse = json.deserialize(jsonString, DeliStatusListServiceResponse.class);
            Optional.ofNullable(serviceResponse.getData()).orElseThrow(() -> new SubtasksStructureException(serviceResponse.getMessage()));

            return serviceResponse;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }
}
