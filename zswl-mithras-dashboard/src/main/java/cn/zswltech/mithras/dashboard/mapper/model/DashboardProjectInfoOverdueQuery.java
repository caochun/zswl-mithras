package cn.zswltech.mithras.dashboard.mapper.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectInfoOverdueQuery extends CommonAuthQuery {
    private String projName;
    private String contractCode;
    private Long clientId;
    private Long bizDeptId;
    private Long projSponsorUserId;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private List<Long> ids;
    private String permissionType;

}
