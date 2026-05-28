package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/9/5 12:02
 */
@Data
@ApiModel("风险因子修改请求体")
public class AssetClassifyClientRiskFactorModifyREQ {
    @ApiModelProperty("id")
    @NotNull
    private Long id;

    @ApiModelProperty("风险因子")
    private List<RiskFactorWrapper> riskFactors;

}
