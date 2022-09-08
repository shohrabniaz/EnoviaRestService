
package com.bjit.common.rest.app.service.comosData.project_structure.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author omour faruq
 */

@Component
@Scope("prototype")
@Data
@ToString
public class ProjectDataBean {

    @SerializedName("TableData")
    @Expose
    private List<ComosActivitiesBean> tableData = null;
    
    @SerializedName("Error")
    @Expose
    private String error;
}
