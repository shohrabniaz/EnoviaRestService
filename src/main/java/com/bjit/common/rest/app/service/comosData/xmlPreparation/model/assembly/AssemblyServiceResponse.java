package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.ComosCommonResponse;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class AssemblyServiceResponse extends ComosCommonResponse {
    private AssemblyData data;
}
