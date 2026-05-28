package cn.zswltech.mithras.dto.metric;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */
@Data
public class RiskMetricModifyReq {

    @NotNull
    private Long id;


    /**
     * 指标名称
     */
    @ApiModelProperty("指标名称")
    private String metricName;

    /**
     * 指标编号
     */
    @ApiModelProperty("指标编号")
    private String metricCode;

    /**
     * 所属机构id
     */
    @ApiModelProperty("所属机构Code")
    private String belongOrgCode;


    /**
     * 单位
     */
    @ApiModelProperty("计量单位")
    private String unit;

    /**
     * 指标类型
     */
    @ApiModelProperty("指标类型")
    private String metricType;

    /**
     * 指标大类
     */
    @ApiModelProperty("指标大类")
    private String metricMainClass;

    /**
     * 指标小类
     */
    @ApiModelProperty("指标小类")
    private String metricSubClass;


    /**
     * 报送频率
     */
    @ApiModelProperty("报送频率")
    private String frequency;

    /**
     * 币种
     */
    @ApiModelProperty("币种")
    private String currency;


    @ApiModelProperty("是否报送，默认：是")
    private Boolean needReport = true;


}
