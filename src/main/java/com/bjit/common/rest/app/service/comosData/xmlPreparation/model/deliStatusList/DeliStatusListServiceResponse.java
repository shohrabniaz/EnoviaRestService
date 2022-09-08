package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.deliStatusList;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.ComosCommonResponse;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class DeliStatusListServiceResponse extends ComosCommonResponse {
    private List<DeliStatusServiceData> data;
}
