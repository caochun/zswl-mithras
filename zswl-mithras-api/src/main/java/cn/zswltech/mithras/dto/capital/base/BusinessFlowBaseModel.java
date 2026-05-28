package cn.zswltech.mithras.dto.capital.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangxiong
 * @date 2024/9/6/15:04
 * @description 自动核销需要统一标准，后续新增的业务流水都要实现改类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessFlowBaseModel {

    @ApiModelProperty(value = "现金流项目")
    private String cashFlowItem;

    @ApiModelProperty(value = "现金流项目名称")
    private String cashFlowItemName;

    @ApiModelProperty(value = "应收时间/应付时间")
    private String shouldPayTime;

    @ApiModelProperty(value = "应收金额/应付金额")
    private Long shouldPayAmount;

    @ApiModelProperty(value = "未收金额/未付金额")
    private Long noPayAmount;

    @ApiModelProperty(value = "本次核销金额")
    private Long thisWriteOffAmount;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty("配置一个源表ID，自动核销使用，业务功能可忽略")
    private Long sourceId;
}
