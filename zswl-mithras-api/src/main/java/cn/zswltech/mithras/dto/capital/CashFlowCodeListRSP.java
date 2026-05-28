package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/30/09:49
 * @description
 */
@Data
public class CashFlowCodeListRSP {

    @ApiModelProperty(value = "借据ID")
    private Long cashFlowId;

    @ApiModelProperty(value = "现金流编号")
    private String cashFlowCode;
}
