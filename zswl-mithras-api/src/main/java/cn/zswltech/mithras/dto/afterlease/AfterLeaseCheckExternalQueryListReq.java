package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 租后检查外部查询任务
 * @date 2022-11-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租后检查外部查询任务列表-请求体")
public class AfterLeaseCheckExternalQueryListReq extends PageReq {

    @ApiModelProperty("目标月份例如:2022-12-1")
    @NotNull
    private LocalDate targetMonth;

//    @ApiModelProperty("项目名称")
//    private String projName;
//
//    @ApiModelProperty("项目主办")
//    private Long projSponsorUserId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("主办")
    private Long sponsorId;
    
    @ApiModelProperty("审批状态")
    private String approvalStatus;

}
