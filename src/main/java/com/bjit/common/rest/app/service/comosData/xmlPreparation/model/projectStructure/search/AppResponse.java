package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.search;

import com.bjit.common.rest.app.service.payload.common_response.Status;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class AppResponse {
    Status status;
    Project data;
}