package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CollectionFlowCenterClientContractMarginRSP {

    @ApiModelProperty("保证金id")
    private Long id;

    /**
     * 编号
     */
    @ApiModelProperty("保证金编号")
    private String marginCode;

    /**
     * 合同id
     */
    @ApiModelProperty("合同id")
    private Long contractId;

    /**
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("保证金余额")
    private Long earnestMoneyBalance;

}
