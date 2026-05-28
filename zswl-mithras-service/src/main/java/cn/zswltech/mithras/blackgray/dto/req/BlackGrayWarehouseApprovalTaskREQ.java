package cn.zswltech.mithras.blackgray.dto.req;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("黑灰名单任务审批列表入参")
public class BlackGrayWarehouseApprovalTaskREQ extends PageReq {

    // 点击单条查看时传入id
    private Long id;

    @ApiModelProperty(value = "任务编号")
    private String taskNum;

    @ApiModelProperty(value = "数据时点，即用户提交任务时所在的月份")
    private String timePoint;

    @ApiModelProperty(value = "机构code")
    private String orgCode;

    @ApiModelProperty(value = "审批状态 0=wait， 1=ing， 2=withdraw， 3=reject， 4=finish")
    private Integer auditStatus;

    @ApiModelProperty(name = "业务来源 BlackGraySourceEnum")
    private String businessSource;

    @ApiModelProperty(value = "当前操作人")
    private String currentOperator;


}
