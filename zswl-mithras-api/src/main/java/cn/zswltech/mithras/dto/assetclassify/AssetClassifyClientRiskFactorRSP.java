package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/9/5 12:02
 */
@Data
@ApiModel("风险因子响应")
public class AssetClassifyClientRiskFactorRSP {
    @ApiModelProperty("templateId")
    private Long templateId;

    @ApiModelProperty("type")
    private String type;

    @ApiModelProperty("risk_factor")
    private String riskFactor;

    @ApiModelProperty("has_risk")
    private Boolean hasRisk;

    @ApiModelProperty("remark")
    private String remark;
}
