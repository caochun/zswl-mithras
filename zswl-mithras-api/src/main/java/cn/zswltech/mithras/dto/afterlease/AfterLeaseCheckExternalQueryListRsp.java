package cn.zswltech.mithras.dto.afterlease;
import cn.zswltech.mithras.dto.ListBaseRSP;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 租后检查外部查询任务
 * @author zhaozhengkang
 * @date 2022-11-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租后检查外部查询任务列表-返回体")
public class AfterLeaseCheckExternalQueryListRsp extends ListBaseRSP {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "项目主办用户姓名")
    private String projSponsorUserName;

    @ApiModelProperty(value = "合同最终到期日")
    private LocalDate deadline;

    @ApiModelProperty(value = "审批通过时间")
    private LocalDateTime approvalPassTime;

    @ApiModelProperty(value = "状态")
    private String approvalStatus;

}
