/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.maturity.mvStateChange;

import com.bjit.common.code.utility.context.ContextGeneration;
import com.bjit.common.code.utility.http.client.CSRFTokenGenerationService;
import com.bjit.common.code.utility.http.client.model.CSRFToken;
import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.common.code.utility.statechange.impl.StateChange;
import com.bjit.common.code.utility.statechange.impl.StateChangeImpl;
import com.bjit.common.code.utility.statechange.model.Data;
import com.bjit.common.code.utility.statechange.model.ErrorReport;
import com.bjit.common.code.utility.statechange.model.Response;
import com.bjit.common.code.utility.statechange.model.State;
import com.bjit.common.rest.app.service.model.MaturityChange.MaturityChangeResponse;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import matrix.db.Context;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author Fazley Rabbi-11372 Date: 17-06-2022
 *
 */
@Service
public class MVStateChangeServiceImpl implements MVStateChangeService {

    private static final Logger MV_StateChangeService_Impl_Logger = Logger.getLogger(MVStateChangeServiceImpl.class);
    private List<String> stateList;
    private CSRFToken csrfToken;
    private CSRFTokenGenerationService service;
    private ContextPasswordSecurity contextPasswordSecurity;
    private Response response;
    private String hostUrl;
    private String passportUrl;
    private String securityContext;
    private MaturityChangeResponse maturityChangeResponse;

    @Override
    public MaturityChangeResponse changeStateLifecycle(String objPhyId, String targatedState) throws Exception {
        this.securityContext = PropertyReader.getProperty("preferred.security.context.dslc");
        this.contextPasswordSecurity = new ContextPasswordSecurity();
        String user = this.contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
        String pass = this.contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));
        String nextState = "";
        String currentState = "";

        //csrf token service call
        CSRFToken csrf = getCSRFToken(user, pass);

        List<Header> reqHeaders = new ArrayList<>();
        reqHeaders.add(new BasicHeader(csrf.getName(), csrf.getValue()));
        reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
        reqHeaders.add(new BasicHeader("Accept", "application/json"));

        ContextGeneration cg = new ContextGeneration();
        Context context = cg.createContext(this.hostUrl, user, pass);

        try {
            TNR tnr = new TNR();
            BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
            //get TNR by object physical id
            tnr = businessObjectOperations.getObjectTNR(context, objPhyId);

            CommonSearch commonSearch = new CommonSearch();

            List<String> selectDataList = new ArrayList<String>();
            selectDataList.add("current");

            stateList = new ArrayList<>();
            stateList.add("Preliminary");
            stateList.add("Product Management");
            stateList.add("Design Engineering");
            stateList.add("Review");
            stateList.add("Release");
            stateList.add("Obsolete");

            StateChange fetch = new StateChangeImpl(this.hostUrl,
                    this.service.getSimpleHttpClient().getCookieStore(), reqHeaders);

            List<HashMap<String, String>> attributeList = new ArrayList<>();

            //search item current state
            attributeList = commonSearch.searchItem(context, tnr, selectDataList);

            currentState = attributeList.get(0).get("current");
            MV_StateChangeService_Impl_Logger.info("CurrentState :" + currentState);

            if (stateList.contains(targatedState) && stateList.indexOf(targatedState) > stateList.indexOf(currentState)) {
                while (!currentState.equalsIgnoreCase(targatedState)) {
                    nextState = stateList.get(stateList.indexOf(currentState) + 1);
                    MV_StateChangeService_Impl_Logger.info("NextState :" + nextState);

                    //maturity state change service call
                    this.response = doStateChange(objPhyId, nextState, fetch);
                    currentState = this.response.getResults().get(0).getMaturityState();
                    MV_StateChangeService_Impl_Logger.info("CurrentState :" + currentState);
                }
                createMaturityResponse(this.response, "");
            } else {
                this.response = null;
                String message = "Maturity change operation is not successful for " + objPhyId;
                createMaturityResponse(this.response, message);
                return this.maturityChangeResponse;
            }
        } catch (Exception e) {
            MV_StateChangeService_Impl_Logger.error(e.getMessage());
        }
        context.close();
        return this.maturityChangeResponse;
    }

    public Response doStateChange(String objPhyId, String nextState, StateChange fetch) throws Exception {
        Data data = new Data();
        data.setId(objPhyId);
        data.setNextState(nextState);
        ArrayList<Data> dataList = new ArrayList<>();
        dataList.add(data);
        State state = new State();
        state.setData(dataList);
        try {
            //Maturity state change service call
            this.response = fetch.changeState(state);
        } catch (Exception e) {
            MV_StateChangeService_Impl_Logger.error(e.getMessage());
        }
        return this.response;
    }

    public CSRFToken getCSRFToken(String user, String pass) {
        this.hostUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
        this.passportUrl = PropertyReader.getProperty("matrix.context.cas.connection.passport");
        try {
            this.service = new CSRFTokenGenerationService(this.hostUrl, this.passportUrl, user, pass);
            this.csrfToken = service.getCSRFToken();

        } catch (Exception e) {
            MV_StateChangeService_Impl_Logger.error(e.getMessage());
        }
        return this.csrfToken;
    }

    public void createMaturityResponse(Response res, String message) {
        this.maturityChangeResponse = new MaturityChangeResponse();
        try {
            if (res == null) {
                this.maturityChangeResponse.setMessage(message);
            } else {
                this.maturityChangeResponse.setId(res.getResults().get(0).getId());
                this.maturityChangeResponse.setState(res.getResults().get(0).getMaturityState());
            }
        } catch (Exception e) {
            MV_StateChangeService_Impl_Logger.error(e.getMessage());
        }
    }
}
