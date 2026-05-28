package cn.zswltech.mithras.dto.budget;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;

/**
 * @description 资产减值预测表
 * @author vico
 * @date 2025-10-14
 */
@Data
@ApiModel("资产减值预测表新增-请求体")
public class EclExecutePredictBaseInfoAddREQ {

    /**
    * 预算计划id
    */
    @ApiModelProperty(value = "预算计划id")
    private Long budgetPlanId;

    /**
    * 拨备预测计划名称
    */
    @ApiModelProperty(value = "拨备预测计划名称")
    private String budgetPlanName;

    /**
    * 拨备预测日期
    */
    @ApiModelProperty(value = "拨备预测日期")
    private LocalDate predictDataFrom;

    @ApiModelProperty(value = "拨备预测日期")
    private LocalDate predictDataTo;

    /**
    * 拨备预测来源 0自动创建 1 手工添加
    */
    @ApiModelProperty(value = "拨备预测来源 0自动创建 1 手工添加")
    private Integer source;


}
