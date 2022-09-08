package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assigneeService;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.deliStatusList.DeliStatus;
import lombok.*;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Scope("prototype")
public class UserAndStatusList {
    @NonNull
    private String assignee;
    @NonNull
    private List<DeliStatus> deliStatusList;
    private Map<String, String> rootItemInfo;
    private Map<String, String> assigneeInformation;
}
