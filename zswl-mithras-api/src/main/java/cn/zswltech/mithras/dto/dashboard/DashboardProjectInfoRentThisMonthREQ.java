package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
@Data
public class DashboardProjectInfoRentThisMonthREQ {
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("本期应收日期-起")
    private LocalDate planCollectionDateFrom;
    @ApiModelProperty("本期应收日期-止")
    private LocalDate planCollectionDateTo;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    private List<Long> ids;
    @ApiModelProperty("可见范围 workbenchProjectDataRange")
    @NotNull
    private String permissionType;
}
