package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 资产减值预测表
 * @author vico
 * @date 2025-10-14
 */
@Data
@ApiModel("资产减值预测表列表-返回体")
public class EclExecutePredictBaseInfoListRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

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
    @ApiModelProperty(value = "拨备预测日期开始")
    private LocalDate predictDataBegan;

    @ApiModelProperty(value = "拨备预测日期结束")
    private LocalDate predictDataEnd;

    /**
    * 拨备预测来源 0自动创建 1 手工添加
    */
    @ApiModelProperty(value = "拨备预测来源 0自动创建 1 手工添加")
    private Integer source;

    @ApiModelProperty(value = "create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "update_time")
    private LocalDateTime updateTime;

}
