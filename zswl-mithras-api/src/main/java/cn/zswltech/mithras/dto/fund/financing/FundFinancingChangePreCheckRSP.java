package cn.zswltech.mithras.dto.fund.financing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/2/24
 * @description
 */
@Data
@ApiModel("融资管理-贷后变更-预检查-返回体")
public class FundFinancingChangePreCheckRSP {
    @ApiModelProperty("是否处于贷后变更中")
    private Boolean isChanging;

    @ApiModelProperty("变更类型")
    private String changeSubType;
}
