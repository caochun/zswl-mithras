package cn.zswltech.mithras.third.tianyancha.application.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author junke
 */
@Data
@Accessors(chain = true)
public class MithrasRelatedEnterpriseInfo {

    /**
     * 关联企业名称
     */
    @ApiModelProperty(value = "关联企业名称", required = true)
    private String enterpriseName;

    /**
     * 关联关系
     */
    @ApiModelProperty("关联关系")
    private String relationship;

    /**
     * 注册资本
     */
    @ApiModelProperty("注册资本")
    private Long registerCapital;

    /**
     * 持股比例
     */
    @ApiModelProperty("持股比例")
    private Long shareholdingRatio;

    /**
     * 投资金额（万元）
     */
    @ApiModelProperty("投资金额（万元）")
    private Long investAmount;

    @ApiModelProperty("存续状态")
    private String continuousStatus;

    @ApiModelProperty("成立日期")
    private LocalDate establishDate;

    @ApiModelProperty("行业")
    private String industryType;

    @ApiModelProperty("行业名称")
    private String industryTypeName;


}
