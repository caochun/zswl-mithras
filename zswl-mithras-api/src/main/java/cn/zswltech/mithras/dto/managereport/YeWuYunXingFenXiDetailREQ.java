package cn.zswltech.mithras.dto.managereport;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/12/10
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class YeWuYunXingFenXiDetailREQ extends PageReq {
    @ApiModelProperty("流程类型")
    private List<String> processModelTypeList;
    @NotNull(message = "查询开始时间不能为空")
    @ApiModelProperty("流程开始日期-起，格式：yyyy-MM-dd")
    private LocalDate processStartDateFrom;
    @NotNull(message = "查询结束时间不能为空")
    @ApiModelProperty("流程开始日期-止，格式：yyyy-MM-dd")
    private LocalDate processStartDateTo;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    @ApiModelProperty("业务类型，PUBLIC_CATEGORY-公用，INDUSTRY_CATEGORY-产业")
    private String businessCategory;
    @ApiModelProperty("审批状态，RUNNING-审批中，FINISH-审批完成")
    private String processStatus;
    @ApiModelProperty("租赁类型，hui_zu-回租，zhi_zu-直租")
    private String leaseType;
}
