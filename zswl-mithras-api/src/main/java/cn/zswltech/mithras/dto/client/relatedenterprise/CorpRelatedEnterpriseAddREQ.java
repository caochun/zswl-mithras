package cn.zswltech.mithras.dto.client.relatedenterprise;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author luyi
 */
@Data
@ApiModel("关联企业-请求体")
public class CorpRelatedEnterpriseAddREQ {

    @NotNull
    @ApiModelProperty(value = "客户id", required = true)
    private Long clientId;

    /**
     * 关联企业名称
     */
    @NotBlank
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

    @ApiModelProperty("投资金额，单位都是元*10000倍")
    private Long investAmount;

    @NotBlank
    @ApiModelProperty("存续状态")
    private String continuousStatus;

    @ApiModelProperty("成立日期")
    private LocalDate establishDate;

    @ApiModelProperty("行业")
    private String industryType;
}
