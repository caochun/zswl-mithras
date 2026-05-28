package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/19/11:23
 * @description
 */
@Data
public class BankFlowProcessingCenterPhaseListRSP {

    @ApiModelProperty(value = "现金流ID")
    private Long cashFlowId;

    @ApiModelProperty(value = "期项")
    private Integer phase;

    @ApiModelProperty(value = "现金流编号列表")
    private List<String> cashFlowCodeList;
}
