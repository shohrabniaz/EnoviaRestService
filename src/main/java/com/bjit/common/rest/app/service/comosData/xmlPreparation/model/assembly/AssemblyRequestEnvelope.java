package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.CommonRequestEnvelope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@ToString
@Component
@Scope("prototype")
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyRequestEnvelope  extends CommonRequestEnvelope {
    private AssemblyRequestData assemblyRequestData;
}
