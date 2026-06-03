package cn.zswltech.mithras.kpi.mapper.query;

import cn.zswltech.mithras.service.mapper.query.PageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KpiProjectDistributionQuery extends PageQuery {
    private Integer distributionStatus;
    private String contractCode;
    private String projName;
    private Long sponsorUserId;
    private Long deptId;
    private String approvalStatus;
    private List<Long> currentUserBizDeptId;
    private Long currentUserId;
    private String currentUserIdStr;
    private List<Long> contractIds;

    private Long sponsorUserIdWeight;
    private Long projCosponsorUserIdWeight;
    private Long bizDeptIdWeight;
    private Integer weightFlag = 0;
}
