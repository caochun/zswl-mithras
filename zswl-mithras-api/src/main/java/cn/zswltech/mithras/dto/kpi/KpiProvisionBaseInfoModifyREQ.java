package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 绩效-拨备表
 * 支持手工编辑 剩余本金、保证金余额、（敞口 自动计算）、计提比例、（本月风险金余额 自动计算）、（本月风险金计提/转回 自动计算）
 * @date 2023-06-19
 */
@Data
@ApiModel("绩效-拨备表编辑-请求体")
public class KpiProvisionBaseInfoModifyREQ {

    /**
    * id
    */
    @NotNull(message = "id不能为空")
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 剩余本金
     */
    @ApiModelProperty("剩余本金")
    private Long remainingPrincipal;

    /**
     * 保证金余额
     */
    @ApiModelProperty("保证金余额")
    private Long earnestBalance;

//    /**
//    * 计提比例-手动修改
//    */
//    @NotNull(message = "计提比例不能为空")
//    @ApiModelProperty(value = "计提比例-手动修改")
//    private Long withdrawalRatio;

}
