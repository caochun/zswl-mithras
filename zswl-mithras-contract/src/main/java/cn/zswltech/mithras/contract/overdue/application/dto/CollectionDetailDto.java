package cn.zswltech.mithras.contract.overdue.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/22 10:52
 */
@Data
@ApiModel(value = "催收详情响应")
public class CollectionDetailDto {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty(value = "客户id")
    private Long clientId;
    @ApiModelProperty(value = "客户名称")
    private String clientName;
    @ApiModelProperty(value = "风险敞口")
    private Long riskExposure;
    @ApiModelProperty(value = "逾期租金")
    private Long overdueRent;
    @ApiModelProperty(value = "最大逾期天数")
    private Integer curMaxOverdueDays;
    @ApiModelProperty(value = "逾期罚息")
    private Long lateCharge;
    @ApiModelProperty(value = "催收动作列表")
    List<CollectionActionDto> collectionActionList;
}
