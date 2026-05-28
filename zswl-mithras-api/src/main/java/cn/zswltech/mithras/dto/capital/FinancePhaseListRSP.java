package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/19/11:23
 * @description
 */
@Data
public class FinancePhaseListRSP {

    @ApiModelProperty(value = "idKey")
    private String idKey;

    @ApiModelProperty(value = "期项")
    private Integer phase;
}
